package com.t1.tripfy.service.chat;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

/* 메모 240520 1646
 * 
 * 자세한 설명 참조
 * https://velog.io/@wnguswn7/Project-SseEmitter%EB%A1%9C-%EC%95%8C%EB%A6%BC-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
 * 대충 중요한부분 - 하단 동작과정
 * SseEmitter 완료/타임아웃시 저장소에서 삭제
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
 * */

@Slf4j
@Service
public class SseEmitterService {
	private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
	
	private static final long TIMEOUT = 60 * 1000;
	private static final long RECONNECTION_AFTER_TIMEOUT = 1 * 1000;
	
	private static long connEventCount = 0L;
	private static long broadcastEventCount = 0L;
	
	public SseEmitter subscribe(String lastEventID, String userid) {
		SseEmitter emit = createEmitter();
		
		//SseEmitter에 이벤트 핸들러 등록
		emit.onTimeout(() -> {
			emitterMap.remove(userid);
			log.debug("timeout, SseEmitter, userid={}", userid);
			if(log.isDebugEnabled()) { debugPrintAllElementsOfEmitterMap(); }
		});
		emit.onError((e) -> {
			log.debug("error, SseEmitter, userid=" + userid, e);
		});
		emit.onCompletion(() -> {
			emitterMap.remove(userid);
			log.debug("completion, SseEmitter, userid={}", userid);
			if(log.isDebugEnabled()) { debugPrintAllElementsOfEmitterMap(); }
		});
		
		//기존 연결 emit 이 존재할 경우 삭제
		if(emitterMap.containsKey(userid)) {
			emitterMap.get(userid).complete();
		}
		//emit 저장
		emitterMap.put(userid, emit);
		
		//초기 연결시 한번 응답해준다(아마 TIMEOUT 관련인걸로 암)
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
					//여기서는 따로 JSON에 담기는게 아니니 그냥 꺼내도 될거임 아마
					.data("SSE connected - " + userid)
					//재접속 시도 대기시간
					//연결이 끊긴 경우 해당 시간 이후 재접속 시도
					.reconnectTime(RECONNECTION_AFTER_TIMEOUT)
					);
		} catch(IOException e) {
			//클라에서 냅다 연결을 끊어버렸을때 서버서 전송을 시도하면 여기로 온다
			//연결단계에서 오류 발생시 연결을 끊는다
			emit.completeWithError(e);
			emitterMap.remove(userid);
			log.debug("error while sending connection message, SseEmitter, userid=" + userid, e);
			if(log.isDebugEnabled()) { debugPrintAllElementsOfEmitterMap(); }
			return null;
		}
		
		log.debug("send, connected, SseEmitter, userid={}", userid);
		if(log.isDebugEnabled()) { debugPrintAllElementsOfEmitterMap(); }
		
		return emit;
	}
	
	private SseEmitter createEmitter() {
		return new SseEmitter(TIMEOUT);
	}
	
	private void debugPrintAllElementsOfEmitterMap() {
		log.debug("=============== emitterMap ===============");
		emitterMap.forEach((key, val) -> {
			log.debug("userid={} emit={}", key, val);
		});
		log.debug("================== done ==================\n");
	}
}
