package com.t1.tripfy.service.chat;

import java.util.ArrayList;

import com.t1.tripfy.domain.dto.chat.ChatDTO;

public interface ChatService {
	Long createChat(String userid, Long packagenum);
	ArrayList<ChatDTO> selectChatByUserid(String userid);
	// wip
}
