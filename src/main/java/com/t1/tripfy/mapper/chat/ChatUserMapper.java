package com.t1.tripfy.mapper.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.chat.ChatUserDTO;

@Mapper
public interface ChatUserMapper {
	//userid로 chat_user 테이블 행 긁어오기
	ArrayList<ChatUserDTO> selectSpecificChatUserByUserid(String userid);
	//채팅방에 가입해있는 상대방의 userid를 가져오기
	/*1대1 채팅 전용*/
	String selectOpponentUserid(Long chatRoomIdx, String userid);
	
	//userid로 chat_user 테이블 행 개수 가져오기
	Integer selectCountOfSpecificChatUserByUserid(String userid);
	
	
	//JOIN 사용해서 userid가 가입된 모든 채팅방중 최근 수신순 x ~ y 개의 chat_room_idx 가져오기
	// chat_detail과 JOIN함
	List<Map<String, Object>> selectSpecificRecentReceivedChatRoomIdx(Integer start, Integer end, String userid);
}
