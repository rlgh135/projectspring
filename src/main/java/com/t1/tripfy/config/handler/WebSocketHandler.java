package com.t1.tripfy.config.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatContentMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatRoomEnterMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatFailedMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatFailedMessagePayload.ChatFailReason;
import com.t1.tripfy.service.chat.ChatService;
import com.t1.tripfy.service.chat.SseEmitterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {
	
	
	// userid - WebSocketSession
	// chatRoomIdx < userid
	// userid < chatRoomIdx
	/*
	 * 일단 WEBSOCKET_SESSIONS는 동시로그인 처리는 안하게 할 거임
	 * 이건 로그인쪽이랑 얘기해봐야되서 보류 
	 * */
	//웹소켓 접속 유저 목록
	private static final HashMap<String, WebSocketSession> WEBSOCKET_SESSIONS = new HashMap<>();
	//채팅방 유저 목록
	private static final HashMap<Long, ArrayList<String>> OPENED_CHAT_INFO = new HashMap<>();
	//유저의 채팅방 접속 상태 목록
	private static final HashMap<String, ArrayList<Long>> OPENED_CHAT_REV = new HashMap<>();
	/*
	 * !!!!주의
	 * OPENED_CHAT_INFO 는 연결상태를 정의하지 않음
	 * 단순히 채팅방 유저 목록을 저장하는 목적만 가진 변수임
	 * 
	 * OPENED_CHAT_REV 가 연결 상태를 정의함
	 * */
	
	//jackson 직렬/역직렬화용
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired @Qualifier("chatServiceImpl")
	private ChatService chatServiceImpl;
	@Autowired
	private SseEmitterService sseService;
	
	// 웹소켓 연결시
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	//1. WebSocketSession에서 HttpSesson 추출, 거서 loginUser를 다시 추출
		String userid = (String) session.getAttributes().get("loginUser");
		
	//2. loginUser 검증
		if(userid == null) {
			session.close(CloseStatus.POLICY_VIOLATION);
			if(log.isDebugEnabled()) {
				log.debug("disconnect, unauthorized access(session.loginUser=null), WebSocket");
			}
			return;
		}
		
	//3. 웹소켓 기존 연결이 존재하는지 확인
		// 기존의 웹소켓 연결(WebSocketSession)이 살아있으면 끊어준다
		// 왠만해서는 임마가 실행되는 상황은 피해야함
		if(WEBSOCKET_SESSIONS.containsKey(userid)) {
			WebSocketSession tempSess = WEBSOCKET_SESSIONS.get(userid);
			if(tempSess != null) {
				tempSess.close(CloseStatus.SESSION_NOT_RELIABLE);
			}
			if(log.isDebugEnabled()) {
				log.debug("disconnect existing connection, new connection request, WebSocket, userid={}", userid);
			}
		}
		
	//4. 웹소켓 등록
		WEBSOCKET_SESSIONS.put(userid, session);
		if(log.isDebugEnabled()) {
			log.debug("connect, WebSocket, userid={}", userid);
		}
	
	//4-1. 기존 SSE 연결 해제
		
	}

	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		//우선 역직렬화
		/* domain.dto.MessagePayload, Message 참조*/
		MessageDTO<?> receivedMsg = objectMapper.readValue(message.getPayload(), 
				objectMapper.getTypeFactory()
						.constructParametricType(MessageDTO.class, MessagePayload.class));

		//발송자를 파악해 receivedMsg에 삽입
		String userid = (String) session.getAttributes().get("loginUser");
		receivedMsg.setSenderId(userid);

		if(log.isDebugEnabled()) {
			log.debug("msg received, WebSocket, userid={}, msg={}", receivedMsg.getSenderId(), receivedMsg);
			log.debug("msg ACT={}", receivedMsg.getAct());
			log.debug("msg value={}", receivedMsg);
		}
		
		//송신용 객체 생성
		MessageDTO<? extends MessagePayload> sendingMsg;
		
		//receivedMsg.act 값을 기준으로 분기
		switch(receivedMsg.getAct()) {
		case "chatRoomEnter": //  -> MessageDTO<ChatRoomEnterMessagePayload>
			//채팅방 진입 요청
			
			Long creRoomIdx = ((ChatRoomEnterMessagePayload)receivedMsg.getPayload()).getRoomidx();
			
			/*
			 * 기존에는 우선 맵에 저장 후 처리 -> 실패시 삭제의 구조였는데
			 * 갈아엎을거임
			 * 이제는 처리를 먼저 하고 결과에 따라 삽입하게 한다
			 * 
			 * 이거 채팅방 진입 여부
			 * 어쩌면 다른 모든 채팅방 가입자들한테 뿌려줘야 할 지도 모름
			 * 읽음 표시 관련해서dd
			 * */
			
			//열린 채팅방인지 확인
			if(!OPENED_CHAT_INFO.containsKey(creRoomIdx)) {
				//닫힌 경우 사용자 정보를 가져와야 함
				
				//서비스 조회
				if(null == (sendingMsg = chatServiceImpl.chatRoomEnterHandling(receivedMsg.setSenderId(userid), true))) {
					//조회 실패
					session.sendMessage(new TextMessage(
							objectMapper.writeValueAsString(failMessageBuilder(receivedMsg.getAct(), ChatFailReason.SERVER_FAIL))
					));
					return;
				}
				
				//채팅방 정보 맵에 저장
				//맵 키밸류 생성
				OPENED_CHAT_INFO.put(creRoomIdx, createHashMapValue());
				//상대 유저 저장
				for(String id : sendingMsg.getReceiverId()) {
					OPENED_CHAT_INFO.get(creRoomIdx).add(id);
				}
				//요청자 저장
				OPENED_CHAT_INFO.get(creRoomIdx).add(userid);
				//채팅방 접속 상태 저장
				if(!OPENED_CHAT_REV.containsKey(userid)) {
					OPENED_CHAT_REV.put(userid, createHashMapValue());
					OPENED_CHAT_REV.get(userid).add(creRoomIdx);
				} else {
					OPENED_CHAT_REV.get(userid).add(creRoomIdx);
				}
			} else {
				//열린 경우
				//서비스 조회
				if(null == (sendingMsg = chatServiceImpl.chatRoomEnterHandling(receivedMsg.setSenderId(userid), false))) {
					//조회 실패
					session.sendMessage(new TextMessage(
							objectMapper.writeValueAsString(failMessageBuilder(receivedMsg.getAct(), ChatFailReason.SERVER_FAIL))
					));
					return;
				}
				//채팅방 접속 상태 저장
				if(!OPENED_CHAT_REV.containsKey(userid)) {
					OPENED_CHAT_REV.put(userid, createHashMapValue());
					OPENED_CHAT_REV.get(userid).add(creRoomIdx);
				} else {
					OPENED_CHAT_REV.get(userid).add(creRoomIdx);
				}
			}
			
			//채팅방 진입 요청자에게 값 전달
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(sendingMsg)));
			break;
		case "chatRoomLeave":
			//채팅방 이탈 요청
			/*여기서는 요청받은 채팅방만 처리, afterConnClosed에서는 해당 세션의 모든 채팅방 처리
			 * 
			 * 웹소켓 연결 해제 여부는 SharedWorker에서 판단함
			 * */
			Long crlRoomidx = ((ChatRoomEnterMessagePayload)receivedMsg.getPayload()).getRoomidx();
			
			//우선 요청자 삭제
			OPENED_CHAT_REV.get(userid).remove(crlRoomidx);
			//해당 채팅방의 유저 중 요청자 제외 접속해있는 유저가 존재하는지 확인
			boolean shouldDelete = true;
			for(String id : OPENED_CHAT_INFO.get(crlRoomidx)) {
				if(OPENED_CHAT_REV.containsKey(id) && OPENED_CHAT_REV.get(id).contains(crlRoomidx)) {
					shouldDelete = false;
					break;
				}
			}
			//요청자 제외 접속자가 없으면 키밸류 삭제
			/* 나중에 채팅 캐시 만들면 여기서 DB로 insert */
			if(shouldDelete) {
				OPENED_CHAT_INFO.remove(crlRoomidx);
			}
			//해당 채팅방이 요청자가 접속해있었던 마지막 채팅방이면
			// 접속맵에서도 삭제
			if(OPENED_CHAT_REV.get(userid).isEmpty()) {
				OPENED_CHAT_REV.remove(userid);
			}
			
			//응답
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(receivedMsg)));
			break;
		case "sendChat":
			//메시지 전송 요청
			/*
			 * 일단 지금은 바로 DB 인서트하는걸ㄹ로
			 * */
			Long scRoomIdx = ((ChatContentMessagePayload)receivedMsg.getPayload()).getRoomidx();
			
			//OPENED_CHAT_INFO 에 채팅방 정보가 저장되어있는지 확인
			if(OPENED_CHAT_INFO.containsKey(scRoomIdx)) {
				//저장되어있는 경우
				
				//서비스로 값 넣고 결과값 받아오기
				if(null == (sendingMsg = chatServiceImpl.chatReceiveHandling(receivedMsg))) {
					//처리 실패시 처리
					session.sendMessage(new TextMessage(
							objectMapper.writeValueAsString(failMessageBuilder(receivedMsg.getAct(), ChatFailReason.SERVER_FAIL))
					));
					return;
				}
				
				//채팅방 가입자들에게 전송, 송신자에게 알림
				for(String id : OPENED_CHAT_INFO.get(scRoomIdx)) {
					System.out.println("id = " + id + " userid=" + userid);
					System.out.println(sendingMsg.getAct());
					if(id.equals(userid)) {
						//송신자에게 응답
						System.out.println("equals");
						session.sendMessage(new TextMessage(objectMapper.writeValueAsString(sendingMsg.setAct("sendChat"))));
					} else {
						//그외 채팅 참여자에게 broadcast
						if(WEBSOCKET_SESSIONS.containsKey(id)) {
							//웹소켓 연결이 있으면
							WEBSOCKET_SESSIONS.get(id).sendMessage(new TextMessage(objectMapper.writeValueAsString(sendingMsg.setAct("broadcastChat"))));
						} else {
							//없으면 SSE로
							sseService.broadcast(id, objectMapper.writeValueAsString(sendingMsg.setAct("broadcastChat")));
						}
					}
				}
			} else {
				//저장되어있지 않은 경우?
				//에러
				session.sendMessage(new TextMessage(
						objectMapper.writeValueAsString(failMessageBuilder(receivedMsg.getAct(), ChatFailReason.SERVER_FAIL))
				));
			}
			break;
		case "loadChat":
			//메시지 로드 요청
			/*
			 * 채팅방 진입시의 최초 로드와는 별개임
			 * 
			 * 일단 지금은 바로 DB 조회하는걸로
			 * */
			//서비스에서 값 가져오기
			if(null == (sendingMsg = chatServiceImpl.chatLoadHandling(receivedMsg))) {
				//조회 실패시 처리
				session.sendMessage(new TextMessage(
						objectMapper.writeValueAsString(failMessageBuilder("loadChat", ChatFailReason.SERVER_FAIL))
				));
				return;
			}
			
			//전송
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(sendingMsg)));
			break;
		default:
			//오류 처리 등?
			// 필요 없는 로직일 수도
			break;
		}
//		테스트를 위해 주석처리 - 240603
	}

	// 웹소켓 연결 해제시
	/* 이거 어디서 어떤 CloseStatus를 주건 여기로 오니까 log.debug()도 여기로 옮기고
	 * 세션맵 remove도 여기서 처리하게 하자
	 * */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		//세션 저장 객체에서 삭제해준다
		//채팅세션 저장 객체에서도 삭제
		//이거 사용자 로그아웃 시에도 우선적으로 호출하게 해야 함
		String userid = (String)session.getAttributes().get("loginUser");
		
		//채팅 접속 맵에서 삭제 - 마지막 접속자였으면 OCI에서도 삭제
		//해야할거
		// OCR에서 요청자 삭제
		// OCR에서 userid가 접속해있던 채팅방 목록 꺼내오기
		// OCI에서 꺼내온 roomidx들 순회하면서 접근해 각 채팅방의 유저 파악
		// 다시 OCR로 가서 각 채팅방별로 연결 유지중인 유저 수 파악
		//  연결 유지중인 유저가 없는 채팅방은 OCI에서 삭제
		//  각 유저들도 체크해 해
		//근데 이 모든건 OCR에 userid 가 있는 경우에만 해주면 됨(비정상적 연결해제 아마 사용자가 브라우저를 통째로 닫는다던가)
		if(OPENED_CHAT_REV.containsKey(userid)) {
			List<Long> roomList = OPENED_CHAT_REV.remove(userid);
			for(long keyIdx : roomList) {
				List<String> userList = OPENED_CHAT_INFO.get(keyIdx);
				boolean shouldDelete = true;
				for(String keyId : userList) {
					if(!keyId.equals(userid)) {
						if(OPENED_CHAT_REV.containsKey(keyId) && OPENED_CHAT_REV.get(keyId).contains(keyIdx)) {
							shouldDelete = false;
							break;
						}
					}
				}
				if(shouldDelete) {
					OPENED_CHAT_INFO.remove(keyIdx);
				}
			}
		}
		
		//웹소켓 맵에서 삭제
		if(WEBSOCKET_SESSIONS.containsValue(session)) {
			WEBSOCKET_SESSIONS.remove(userid);
		}
		
		if(log.isDebugEnabled())
			log.debug("disconnect, WebSocket, userid={}", userid);
	}
	
	/**
	 * <p>웹소켓 메시지 broadcast 용 함수
	 * <p>WebSocketHandler 밖에서 웹소켓 전파를 하기 위해 만들어짐
	 * @param action : MessageDTO.act 값
	 * @param payload : MessageDTO.payload 값 - MessagePayload 인터페이스의 구현체만 대입 가능
	 * @param targetRoomIdx : 전파할 채팅방 인덱스(cr.chat_room_idx)
	 * @param userThatExcept : 전파하지 않을 사용자 - 아마 이 함수를 호출하는 요청 처리 로직의 요청자를 넣으면 될 거임, 없으면 null 넣어라
	 * @throws Exception : d
	 */
	public void broadcastRequestHandler(String action, MessagePayload payload, long targetRoomIdx, String userThatExcept) throws Exception {
		//targetRoomIdx 값 검증은 안 넣음
		//action, payload 값 검증도 안 넣는다 어짜피 MessagePayload 구현체만 넘길 수 있긴 하니까 뭐

		//송신값 구성
		MessageDTO<ChatRoomEnterMessagePayload> message = new MessageDTO<>();
		message.setAct(action);
		message.setPayload((ChatRoomEnterMessagePayload)payload);
		
		MessageDTO<? extends MessagePayload> sex = message;
		
		//목표 채팅방의 가입자 목록 꺼내기
		List<String> userList = OPENED_CHAT_INFO.get(targetRoomIdx);
		
		//순회하면서 broadcast
		// 웹소켓 연결이 없는 유저는 SSE로 넘긴다
		for(String userid : userList) {
			if(WEBSOCKET_SESSIONS.containsKey(userid)) {
				WEBSOCKET_SESSIONS.get(userid).sendMessage(new TextMessage(objectMapper.writeValueAsString(userList)))
			} else {
				
			}
		}
	}
	
	//==========================================================================================
	//== AOP ===================================================================================
	private <T> ArrayList<T> createHashMapValue() {
		return new ArrayList<T>();
	}
	
	private HashMap<String, ArrayList<String>> createValueObjectOfOPENED_CHAT_INFO() {
		HashMap<String, ArrayList<String>> temp = new HashMap<>();
		temp.put("USER", new ArrayList<>());
		temp.put("CONN_USER", new ArrayList<>());
		return temp;
	}
	
	private MessageDTO<ChatFailedMessagePayload> failMessageBuilder(String act, String reason) {
		return new MessageDTO<ChatFailedMessagePayload>()
				.setAct(act)
				.setPayload(
						new ChatFailedMessagePayload()
								.setReason(reason)
				);
	}
}