package com.t1.tripfy.mapper.chat;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.chat.ChatUserDTO;

@Mapper
public interface ChatUserMapper {
	//userid로 chat_user 테이블 행 긁어오기
	ArrayList<ChatUserDTO> selectSpecificChatUserByUserid(String userid);
}
