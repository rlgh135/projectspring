package com.t1.tripfy.service.chat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatContentDetailMessagePayload;

import lombok.extern.slf4j.Slf4j;

/* 메모 240520 1646
 * 
 * 자세한 설명 참조
 * https://velog.io/@wnguswn7/Project-SseEmitter%EB%A1%9C-%EC%95%8C%EB%A6%BC-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
 * 대충 중요한부분 - 하단 동작과정
 * SseEmitter 완료/타임아웃시 저장소에서 삭제
 * 전체적인 설명 + 캐시 사용한 설계 시나리오 근데 코틀린으로 작성한거같음
 * https://jsonobject.tistory.com/558
 * 
 * + 추가
 * *EventCount 관해서
 * 이거 그냥 Long값이 아니라 시간값을 넣으면 좋을거같은데
 * + 추가
 * 유저 userid를 emitterMap의 key로 하면 안됨
 * 한 유저가 여러 클라이언트(여러 브라우저건 탭이건)을 열 수도 있음
 * 그런 상황에서도 이왕이면 모든 탭에 이벤트를 쏴줘야 할 것 같음
 * 그러러면 중복될 수 있는 userid 대신 각 탭 <-> 서버 연결을 표시하는 UUID같은걸 주고
 * 별개의 Map 등의 객체로 userid <-> 해당 유저의 모든 연결 의 형태로 저장해야 함
 * 
 * !! 이새끼 목적
 * 채팅창이 닫혀 있는 상황(웹소켓 연결 x) 에서 안 읽은 채팅 개수를 클라에게 반환,
 * 새로운 메시지가 전달된 경우 이를 반영한 채팅 개수 반환
 * !! 추가 목적
 * 메시지 전달시 카톡처럼 간략한 메시지 전송자, 내용의 알림 반환해주기
 * 안할수도있음 아마 안할듯
 * 
 * 240520 현재 상태
 * 대충 구현중, 아직 이사중임
 * 로그인유저에게만 연결허가, 이벤트 메시지 시간값+userid 등의 형태로 변경, 웹소켓 로직과 연결등은
 * 아직 손도 못댐
 * 
 * 240521
 * 이제 SseEmitter를 가리키는 key 는 userid 가 아니라 랜덤부여된 UUID임
 * 한 유저가 여러 웹페이지에 여러 SSE 연결을 가질 수 있기에
 * 각 유저들의 UUID들은 별개의 객체 sseSessions에 저장됨
 * 
 * UUID는 재연결과 상관없이 매번 새로 발급해줄거임
 * 재연결/timeout 등 기존 연결이 종료될 때 마다 emitterMap.remove를 하듯이 sseSessions도 remove를 한다
 * */

@Slf4j
@Service
public class SseEmitterService {
	//userid <-> UUID 매핑 관리 객체
	private final Map<String, List<UUID>> sseSessions = new ConcurrentHashMap<>();
	
	//SSE 연결 객체(SseEmitter) 관리 객체
	//240521
	//이제부터 임마는 userid, SseEmitter가 아닌 UUID(클라 웹페이지별), SseEmitter로 구성
	private final Map<UUID, SseEmitter> emitterMap = new ConcurrentHashMap<>();
	
	//연결 TIMEOUT 설정
	private static final long TIMEOUT = 30 * 1000;
	//클라이언트 재접속 시도 유예기간
	private static final long RECONNECTION_AFTER_TIMEOUT = 1 * 1000;
	
	//이거 좀 생각해봐야됨 last-event-id로 들어가는, 해당 기능의 분별점이 되는 값이니까 이렇게 관리하면 안돼 - 240523
	private static long connEventCount = 0L;
	
	public SseEmitter subscribe(String lastEventID, String userid) {
		SseEmitter emit = createEmitter();
		
		//UUID 발급
		UUID uuid = createRandomUUID();
		
		//SseEmitter에 이벤트 핸들러 등록
		emit.onTimeout(() -> {
			if(log.isDebugEnabled()) { 
				log.info("timeout, SseEmitter, userid={}, UUID={}", userid, uuid);
				debugPrintAllElementsOfCache("onTimeout userid=" + userid + " UUID=" + uuid); 
			}
			emit.complete();
		});
		emit.onError((e) -> {
			/* 발생 가능한 오류 목록 출:https://jsonobject.tistory.com/558
			 * 
			 * 클라단 timeout시 재연결 시도 없이 503 Service Unavailable
			 * - 최초생성후 만료시까지 단 하나의 event도 보내지 않은 경우 발생
			 * = 연결시 event 전송
			 * 
			 * SseEmitter.send() 시 java.io.IOException
			 * - 클라단에서 새로고침/종료로 EventStream 연결이 종료된 경우 발생
			 * = 정상적인 상황이며 해당 SseEmitter 객체를 제거해주면 됨
			 * 
			 * SseEmitter.send() 시 java.lang.IllegalStateException
			 * - timeout 등의 이유로 EventStream 연결이 종료된 경우 발생
			 * = 정상적인 상황임 SseEmitter 객체를 제거해주면 됨
			 * 
			 * org.springframework.web.context.request.async.AsyncRequestTimeoutException
			 * - timeout 시 발생
			 * = 걍 두면 됨
			 * 
			 * 위의 오류들 받아서 처리(SseEmitter 삭제 등)을 해줘야 함
			 * */
			if(log.isDebugEnabled()) {
				log.info("error, SseEmitter, userid=" + userid + ", UUID=" + uuid, e);
			}
			emit.complete();
		});
		emit.onCompletion(() -> {
			deleteSseSession(userid, uuid);
			if(log.isDebugEnabled()) {
				log.info("completion, SseEmitter, userid={}, UUID={}", userid, uuid);
				debugPrintAllElementsOfCache("onCompletion userid=" + userid + " UUID=" + uuid); 
			}
		});
		
		//기존 연결 emit 이 존재할 경우 삭제
//		if(emitterMap.containsKey(userid)) {
//			emitterMap.get(userid).complete();
//		} SseEmitter 매핑 key를 UUID로 바꿔서 특정 SseEmitter만 찝어내기가 어려움
//		그냥 timeout에 맡긴다
		
		//emit, sseSessions 저장
		
		log.debug("userid={}", userid);
		
		emitterMap.put(uuid, emit);
		if(!sseSessions.containsKey(userid)) {
			sseSessions.put(userid, createNewSseSessionValue());
		}
		sseSessions.get(userid).add(uuid);
		
		//초기 연결시 한번 응답해준다(timeout 시 클라이언트 503 오류를 막기 위한 더미값 전송)
		connEventCount++;
		try {
			emit.send(SseEmitter.event()
					//메시지 값 적재
					
					//이벤트 타입
					//JS에서의 event.type과 매핑됨
					.name("connected")
					//이벤트 id
					//JS event.lastEventId
					//타임아웃 등으로 재연결시 이 값이 Last-Event-ID 헤더에 담겨옴
					.id("conn-" + connEventCount)
					//적재할 데이터
					//JS에서는 대충 JSON.parse(event.data).필드명 으로 접근함
					//여기서는 따로 JSON에 담기는게 아니니 그냥 event.data 로 접근해도 됨
					.data("SSE connected - " + userid, MediaType.TEXT_EVENT_STREAM)
					//재접속 시도 대기시간
					//연결이 끊긴 경우 해당 시간 이후 재접속 시도
					.reconnectTime(RECONNECTION_AFTER_TIMEOUT)
			);
		} catch(IOException e) {
			//클라에서 냅다 연결을 끊어버렸을때 서버서 전송을 시도하면 여기로 온다
			//연결단계에서 오류 발생시 연결을 끊는다

			//이거 UUID 지울때도 List에서 금마가 마지막 요소인지 체크해야함
			// 마지막거가 맞으면 key-value 쌍으로 지운다
			// 이부분 따로 메서드로 뽑는게 나을지도 모르겠음
			if(log.isDebugEnabled()) { 
				log.info("error while sending connection message, SseEmitter, userid=" + userid + ", UUID=" + uuid, e);
				debugPrintAllElementsOfCache("connected msg sending catch userid=" + userid + " UUID=" + uuid);
			}
			emit.completeWithError(e);
			return null;
		}
		
		if(log.isDebugEnabled()) { 
			log.info("send, connected, SseEmitter, userid={} UUID={}", userid, uuid);
			debugPrintAllElementsOfCache("after send connected msg userid=" + userid + " UUID=" + uuid); 
		}
		
		return emit;
	}
	
	public void broadcast(String userid, String msg) {
		/*
		 * 이거 상대 유저가 연결되어있지 않은 경우(null == sseSessions.get(userid)) 도 고려해야함
		 * */
		if(sseSessions.containsKey(userid)) {
			List<UUID> uuidList = sseSessions.get(userid);
			
			for(UUID uuid : uuidList) {
				try {
					emitterMap.get(uuid).send(SseEmitter.event()
							.name("broadcastChat")
							//일단 임시로 시간값을 보낸다
							.id("bcc-" + LocalDateTime.now().toString())
							.data(msg, MediaType.TEXT_EVENT_STREAM)
							.reconnectTime(RECONNECTION_AFTER_TIMEOUT)
					);
				} catch(IOException e) {
					if(log.isDebugEnabled()) {
						log.info("error whild sending broadcast message, SseEmitter, userid={}, UUID={}, e={}", userid, uuid, e);
					}
				}
			}
		}
	}
	
	//==========================================================================================
	//= AOP ====================================================================================
	
	private SseEmitter createEmitter() {
		return new SseEmitter(TIMEOUT);
	}
	
	private UUID createRandomUUID() {
		return UUID.randomUUID();
	}
	
	private List<UUID> createNewSseSessionValue() {
		return new ArrayList<>();
	}
	
	private void deleteSseSession(String userid, UUID uuid) {
		emitterMap.remove(uuid);
		//userid -> List.size() == 0 이면 해당 key-value를 지운다
		//혹은 왠지는 몰라도 value가 null일 경우에도 key-value를 삭제한다
		sseSessions.get(userid).remove(uuid);
		if(sseSessions.get(userid) == null || sseSessions.get(userid).size() == 0) {
			sseSessions.remove(userid);
		}
	}
	
	private synchronized void debugPrintAllElementsOfCache(String callFrom) {
		String logRes[] = {"\n======================================================================================"
				+ "\n== " + callFrom
				+ "\n-------------------- sseSessions -------------------"
				+ "\nuserid              UUID"};
		sseSessions.forEach((key, val) -> {
			if(val == null || val.isEmpty()) {
				//이 경우 key는 있는데 val - List<UUID>의 값이 없는 경우
				// 즉 뭔가 꼬인거임
				logRes[0] += "\n" + (key.length() <= 18 ? key : key.substring(0, 17) + "..")
						+ "\n                    none";
			} else {
				//key-value에서 value - List에 요소가 있는 경우(정상적인 경우)
				logRes[0] += "\n" + (key.length() <= 18 ? key : key.substring(0, 17) + "..");
				for(int i = 0; i < val.size(); i++) {
					logRes[0] += "\n                    " + val.get(i);
				}
			}
		});
		logRes[0] += "\n-------------------- emitterMap --------------------"
				+ "\nUUID                                  SseEmitter";
		emitterMap.forEach((key, val) -> {
			logRes[0] += "\n" + key + "  " + val;
		});
		logRes[0] += "\n======================== end =======================\n";
		
		log.debug(logRes[0]);
	}
}
