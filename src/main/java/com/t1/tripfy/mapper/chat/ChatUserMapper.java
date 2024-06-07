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
	/**
	 * <p><strong>SELECT</strong> : chatRoomIdx, userid로 채팅방의 상대 사용자 userid 가져오기
	 * <p>요청자를 제외하고 가져옴
	 * <p><strong>다대다 가능</strong>
	 * */
	List<String> selectOpponentUserid(Long chatRoomIdx, String userid);
	/**
	 * <p><strong>SELECT</strong> : chatRoomIdx, userid로 채팅방의 상대 사용자 chat_user 행 가져오기
	 * <p>요청자를 제외하고 가져옴
	 * <p><strong>다대다 가능</strong>
	 * */
	List<ChatUserDTO> selectOpponentUserInfo(Long chatRoomIdx, String userid);
	
	//userid로 chat_user 테이블 행 개수 가져오기
	Integer selectCountOfSpecificChatUserByUserid(String userid);
	
	
	//JOIN 사용해서 userid가 가입된 모든 채팅방중 최근 수신순 x ~ y 개의 chat_room_idx 가져오기
	// chat_detail과 JOIN함
	List<Map<String, Object>> selectSpecificRecentReceivedChatRoomIdx(Integer start, Integer end, String userid);
	
	/**
	 * <p><strong>SELECT</strong> : userid로 채팅방 가져오기
	 * <p>다만 닫힌 채팅방(chat_user.chat_user_is_quit = true, chat_room.chat_room_is_terminated)은 가져오지 않음
	 * <p>반환값은 각각 <br>chat_room_idx, chat_room_title, packagenum, chat_user_is_creator, regdate(채팅방 개설일), last_msg_date(chat_room_idx의 제일 최신 메시지 regdate)<br>로 이루어져 있음
	 * <p><strong>최신 내림차순 정렬</strong>
	 * */
	List<Map<String, Object>> selectAllByUserid(String userid);
	
	/**
	 * <p><strong>UPDATE</strong> : 마지막 확인 메시지 수정하기
	 * */
	Integer updateChatDetailIdx(Long chatRoomIdx, String userid, Long chatDetailIdx);
	/**
	 * <p><strong>UPDATE</strong> : 메시지 전부 확인처리
	 * */
	Integer updateChatDetailIdxToEnd(Long chatRoomIdx, String userid);
	
	//c
	
	/**
	 * <p><strong>INSERT</strong> : 유저 추가
	 * <p>userList의 각 요소들은 chatRoomIdx, userid, chatUserIsCreator가 초기화되어있어야 함
	 * @param userList : 삽입할 정보 리스트
	 * */
	Integer insertRow(List<ChatUserDTO> userList);
}
