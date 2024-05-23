package com.t1.tripfy.mapper.chat;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.chat.ChatUserDTO;

@Mapper
public interface ChatUserMapper {
	//userid로 chat_user 테이블 행 긁어오기
	ArrayList<ChatUserDTO> selectSpecificChatUserByUserid(String userid);
	//채팅방에 가입해있는 상대방의 userid를 가져오기
	/*1대1 채팅 전용*/
	String selectOpponentUserid(Long chatRoomIdx, String userid);
}
