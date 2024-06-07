package com.t1.tripfy.mapper.chat;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.chat.ChatRoomDTO;

@Mapper
public interface ChatRoomMapper {
	//chat_room_idx로 행 가져오기
	ChatRoomDTO selectRowByChatRoomIdx(Long chatRoomIdx);
	
	//c
//	Integer createRow()
}
