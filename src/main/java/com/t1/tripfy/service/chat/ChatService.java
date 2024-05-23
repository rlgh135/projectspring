package com.t1.tripfy.service.chat;

import java.util.ArrayList;
import java.util.List;

import com.t1.tripfy.domain.dto.chat.ChatListPayloadDTO;

public interface ChatService {
	// 중요
	// 메서드를 쿼리 기준으로 나누지 말고
	// 비즈니스 로직 기준으로 나눌것!!
	Long createChat(String userid, Long packagenum);
	
	// 안 읽은 채팅 개수 가져오기
	Integer selectCountOfUnreadChatByUserid(String userid);
	
	// 채팅방 리스트 + 각 채팅방의 마지막 메시지 + 안 읽은 메시지 개수 가져오기
	List<ChatListPayloadDTO> selectChatList(String userid);
}
