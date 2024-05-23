package com.t1.tripfy.mapper.chat;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.chat.ChatDetailDTO;

@Mapper
public interface ChatDetailMapper {
	//특정 채팅방의 특정 메시지 이후의 메시지 개수 세기
	Integer selectSpecificCountOfChatDetail(Long chatRoomIdx, Long chatDetailIdx);
	//특정 채팅방의 모든 메시지 개수 세기
	Integer selectAllCountOfChatDetail(Long chatRoomIdx);
	//특정 채팅방의 마지막 메시지 가져오기
	ChatDetailDTO selectLastRowByChatRoomIdx(Long chatRoomIdx);
}
