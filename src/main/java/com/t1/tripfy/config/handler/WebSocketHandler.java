package com.t1.tripfy.config.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatRoomEnterMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatFailedMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatFailedMessagePayload.ChatFailReason;
import com.t1.tripfy.service.chat.ChatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {
	
	//임마는 유저 <-> 서버간 1대1 세션(WebSocketSession 객체)를 저장하는 친구
	// 최소한 내가 이해하기로는 WebSocketSession 객체는 HttpSession처럼 각 유저별로 따로 존재함
	// WebSocketSession 임마로 sendMessage()등을 할 수 있으니 이렇게 따로 모아놓았다가
	// 메시지 전달(handleTextMessage) 시 임마를 기준으로 뿌려준다
	// 또한 ping/pong 시에도 임마를 순회하면서 쏴준다
	private static final HashMap<String, WebSocketSession> WEBSOCKET_SESSIONS = new HashMap<>();
	
	// userid - WebsocketSession
	// chatRoomIdx - userid
	private static final HashMap<Long, List<String>> OPENED_CHAT_INFO = new HashMap<>();
	
	//jackson 직렬/역직렬화용
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired @Qualifier("chatServiceImpl")
	private ChatService chatServiceImpl;
	
	/* !!중요 240524
	 * https://velog.io/@typo/sharing-websocket-connections-betwwen-tabs-and-windows
	 * 웹소켓도 SSE처럼 웹페이지별임
	 * 그리고 공유 워커? 얘기도 있음
	 * https://developer.mozilla.org/ko/docs/Web/API/Web_Workers_API
	 * https://developer.mozilla.org/ko/docs/Web/API/SharedWorker
	 * 
	 * 공유 워커에서 웹소켓 쓰기
	 * https://stackoverflow.com/questions/68081220/how-to-use-html5-websockets-within-sharedworkers
	 * 
	 * 웹워커/공유워커 간단 정리
	 * https://pks2974.medium.com/web-worker-%EA%B0%84%EB%8B%A8-%EC%A0%95%EB%A6%AC%ED%95%98%EA%B8%B0-4ec90055aa4d
	 * */
	
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
		
	
	//5. 안 읽은 채팅 개수 끌고오기
	//6. 채팅방 리스트와 안 읽은 채팅 개수 보내주기
		//이제 채팅방 리스트/안 읽은 채팅 개수쪽은 Ajax로 처리함
	}

	// 메시지 발송시
	/* 대충 해야할거
	 * 
	 * 유효성
	 * 송신자 확인
	 * 수신된 채팅방 확인
	 * 해당 채팅방의 다른 사용자가 웹소켓 연결되어있는지 확인
	 *   ㄴ분기                     -웹소켓 연결이 없는 경우
	 *     ㄴ웹소켓에 연결돼있는경우   SSE 연결 확인
	 *       전송                      ㄴ분기                    -바로 아래로 이동
	 *                                   ㄴSSE 연결 확인되는 경우
	 *                                     전송
	 * 다 끝났으면 DB에 저장
	 * 
	 * + 시간 관련 추가 - 240522
	 * 메시지 송신 시간의 기준
	 * 1. WebSocketHandler.handleTextMessage 시점
	 * 2. DB insert 시점(CURRENT_TIMESTAMP)
	 * 간단한건 DB insert 시점
	 * 일단 ChatDetailDTO의 시간 필드를 TimeStamp -> LocalDateTime으로 변경함
	 * */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		//우선 역직렬화
		/* domain.dto.MessagePayload, Message 참조*/
		MessageDTO<?> receivedMsg = objectMapper.readValue(message.getPayload(), 
				objectMapper.getTypeFactory()
						.constructParametricType(MessageDTO.class, MessagePayload.class));

		//발송자를 파악해 receivedMsg에 삽입
		receivedMsg.setSenderId((String) session.getAttributes().get("loginUser"));

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
			
			Long chatRoomIdx = ((ChatRoomEnterMessagePayload)receivedMsg.getPayload()).getRoomidx();
			
			//진입하려는 채팅방이 이미 열린(OPENED_CHAT_INFO에 있는지) 채팅방인지 확인
			if(!OPENED_CHAT_INFO.containsKey(chatRoomIdx)) {
				//닫혀있는 경우 키밸류 생성 후 senderId 삽입
				OPENED_CHAT_INFO.put(chatRoomIdx, createValueObjectOfOPENED_CHAT_INFO());
				OPENED_CHAT_INFO.get(chatRoomIdx).add(receivedMsg.getSenderId());
				//서비스 찍기
				if(null == (sendingMsg = chatServiceImpl.chatRoomEnterHandling(receivedMsg))) {
					//조회 실패시 처리
					// 이거 좀 정리하자 -240531
					session.sendMessage(new TextMessage(
							objectMapper.writeValueAsString(
									new MessageDTO<ChatFailedMessagePayload>()
										.setAct(receivedMsg.getAct())
										.setPayload(
												new ChatFailedMessagePayload()
													.setReason(
															ChatFailReason.SERVER_FAIL
													)
										)
							)
					));
					return;
				}
				//receiverId 삽입
				OPENED_CHAT_INFO.get(chatRoomIdx).add(sendingMsg.getReceiverId());
			} else {
				//열려있는 경우
				if(null == (sendingMsg = chatServiceImpl.chatRoomEnterHandling(receivedMsg))) {
					//조회 실패시 처리
					session.sendMessage(new TextMessage(
							objectMapper.writeValueAsString(
									new MessageDTO<ChatFailedMessagePayload>()
										.setAct(receivedMsg.getAct())
										.setPayload(
												new ChatFailedMessagePayload()
													.setReason(
															ChatFailReason.SERVER_FAIL
													)
										)
							)
					));
					return;
				}
			}
			/*테스트용*/
//			if(((ChatRoomEnterMessagePayload)receivedMsg.getPayload()).getRoomidx() == 6) {
//				session.sendMessage(new TextMessage(
//						objectMapper.writeValueAsString(
//								new MessageDTO<ChatFailedMessagePayload>()
//									.setAct(receivedMsg.getAct())
//									.setPayload(
//											new ChatFailedMessagePayload()
//												.setReason(
//														ChatFailReason.SERVER_FAIL
//												)
//									)
//						)
//				));
//				return;
//			}
			
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(sendingMsg)));
			break;
		case "chatRoomLeave":
			//채팅방 이탈 요청
			/*이거 여기(정상적 이탈)만이 아니라 afterConnClosed 쪽(비정상적 이탈) 시에도 처리해줘야할지도
			 * 이탈자 userid로 존재하는 모든 캐시 날리기 정도?*/
			break;
		default:
			//오류 처리 등?
			// 필요 없는 로직일 수도
			break;
		}
	}

	// 웹소켓 연결 해제시
	/* 이거 어디서 어떤 CloseStatus를 주건 여기로 오니까 log.debug()도 여기로 옮기고
	 * 세션맵 remove도 여기서 처리하게 하자
	 * */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		//세션 저장 객체에서 삭제해준다
		//이거 사용자 로그아웃 시에도 우선적으로 호출하게 해야 함
		if(WEBSOCKET_SESSIONS.containsValue(session)) {
			WEBSOCKET_SESSIONS.remove(session.getAttributes().get("loginUser"));
		}
	}
	
	//==========================================================================================
	//== AOP ===================================================================================
	private List<String> createValueObjectOfOPENED_CHAT_INFO() {
		return new ArrayList<String>();
	}
}