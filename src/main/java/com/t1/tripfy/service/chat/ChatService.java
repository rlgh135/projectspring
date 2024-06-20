package com.t1.tripfy.service.chat;

import java.util.List;

import com.t1.tripfy.domain.dto.chat.ChatListPayloadDTO;
import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatRoomEnterMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatContentDetailMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatDetailBulkMessagePayload;

public interface ChatService {
	// 중요
	// 메서드를 쿼리 기준으로 나누지 말고
	// 비즈니스 로직 기준으로 나눌것!!
	
	/* 서비스 나누기?
	 * https://jangjjolkit.tistory.com/62
	 * */
	
	/*
	 * 채팅방 만들기
	 * 각각
	 * chatRoomType == 	0 일반/일반(다대다 가능)
	 * 					1 패키지(문의)
	 * 					2 패키지(다대다)
	 * */
	ChatListPayloadDTO createChat(String userid, Long packagenum);
	ChatListPayloadDTO createChat(String userid, String title, List<String> invitee);
	ChatListPayloadDTO createChat(String userid, Long packagenum, String title, List<String> invitee);
	
	// 채팅방 진입
	// 나중에 캐시 추가하는 경우를 생각해 채팅방 진입과 채팅방 내용 가져오는 메서드를 분리함
	// + 여기서 DB 조회를 통해 유저 - 유저 매칭을 서버 메모리에 저장함 - 최소한 꺼내옴
	//   매번 메시지 수신시마다 DB를 찍을 수는 없으니까..
	// 일단 메서드는 그대로 분리하고 chatRoomIdx < userid 매핑을 WebSocket쪽에 추가함 <-이거 ChatServiceImpl로 옮길까?
	MessageDTO<ChatDetailBulkMessagePayload> chatRoomEnterHandling(MessageDTO<? extends MessagePayload> receivedMsg, boolean doesNeedToLoadUser);
	
	// 채팅 수신 처리
	MessageDTO<ChatContentDetailMessagePayload> chatReceiveHandling(MessageDTO<? extends MessagePayload> receivedMsg);
	
	// 채팅 로드 처리
	MessageDTO<ChatDetailBulkMessagePayload> chatLoadHandling(MessageDTO<? extends MessagePayload> receivedMsg);
	
	// 채팅방 정보 가져오고 chat_user.chat_detail_idx 갱신
//	List<ChatDetailDTO> getSpecificChatDetailByChatRoomIdx(Mess)
	
	// 안 읽은 채팅 개수 가져오기
	Integer selectCountOfUnreadChatByUserid(String userid);
	
	// 채팅방 리스트 + 각 채팅방의 마지막 메시지 + 안 읽은 메시지 개수 가져오기
	List<ChatListPayloadDTO> selectChatList(String userid);
	
	/**
	 * <p>채팅방 탈퇴 처리
	 * <p>모든 가입자가 탈퇴한 경우 채팅 또한 종료시킨다
	 * @param receivedMsg : 목표 chat_room_idx, userid를 포함한 수신 객체
	 * @return 채팅방 종료시 true, 요청자만 탈퇴시 false, 실패시 null
	 */
	Boolean leaveChatHandler(MessageDTO<? extends MessagePayload> receivedMsg);
	
	/**
	 * <p>채팅방 종료 처리
	 * <p>권한자(가이드) 에 의한 채팅방 종료 요청을 처리함
	 * <p><strong>채팅 종료와 채팅방 접근은 별개임</strong>
	 * @param receivedMsg : 목표 chat_room_idx와 요청자 userid를 포함한 수신 객체
	 * @return 종료 성공시 true, 실패시 false, 유효성 실패(userid)시 null
	 */
	Boolean terminateChatHandler(MessageDTO<? extends MessagePayload> receivedMsg);
}
