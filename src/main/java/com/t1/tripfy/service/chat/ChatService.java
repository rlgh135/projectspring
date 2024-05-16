package com.t1.tripfy.service.chat;

import java.util.ArrayList;

import com.t1.tripfy.domain.dto.chat.old.ChatDTO;

public interface ChatService {
	// 중요
	// 메서드를 쿼리 기준으로 나누지 말고
	// 비즈니스 로직 기준으로 나눌것!!
	Long createChat(String userid, Long packagenum);
	ArrayList<ChatDTO> selectChatByUserid(String userid);
	
	// 안 읽은 채팅 개수 가져오기
	Integer selectCountOfUnreadChatByUserid(String userid);
}
