package com.t1.tripfy.mapper.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.chat.ChatUserDTO;

@Mapper
public interface ChatUserMapper {
	
//R
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
	
	/**
	 * <p>일반/일반 채팅중
	 * <br><strong>OTO</strong>
	 * <br><strong>userid, opponentuserid만이 가입</strong>
	 * <br>의 조건을 충족하는 채팅방에서 userid의 행을 뽑아오기
	 * 
	 * <p>채팅방의 is_terminated, is_quit 여부는 체크 안함
	 * */
	List<ChatUserDTO> selectOTOCommonChatUserInfoByUserids(String userid, String opponentuserid);
	
	//userid로 chat_user 테이블 행 개수 가져오기
	/**
	 * <p>userid로 chat_user 테이블의 행 개수 가져오기
	 * <p>즉 userid가 가입한 채팅방의 개수를 구하는 쿼리임
	 * <p>또한 cu.chat_user_is_quit=false인 행만 셈<br>나간 채팅은 세지 않음
	 * 
	 * @param userid : String 목표 유저의 userid
	 * */
	Integer selectCountOfSpecificChatUserByUserid(String userid);
	
	
	//JOIN 사용해서 userid가 가입된 모든 채팅방중 최근 수신순 x ~ y 개의 chat_room_idx 가져오기
	// chat_detail과 JOIN함
	List<Map<String, Object>> selectSpecificRecentReceivedChatRoomIdx(Integer start, Integer end, String userid);
	
	/**
	 * <p><strong>SELECT</strong> : userid로 채팅방 가져오기
	 * <p><s>다만 닫힌 채팅방(chat_user.chat_user_is_quit = true, chat_room.chat_room_is_terminated)은 가져오지 않음</s>
	 * <br>이제는 목표 사용자(userid)가 나간 채팅방(cu.chat_user_is_quit=true)만을 제외하고 전부 가져옴
	 * <p>반환값은 각각 
	 * <br>chat_room_idx,
	 * <br>chat_room_type,
	 * <br>chat_room_title,
	 * <br>packagenum,
	 * <br>chat_room_is_terminated,
	 * <br>chat_user_is_creator,
	 * <br>regdate(채팅방 개설일),
	 * <br>last_msg_date(chat_room_idx의 제일 최신 메시지 regdate)<br>로 이루어져 있음
	 * <p><strong>최신 내림차순 정렬</strong>
	 * */
	List<Map<String, Object>> selectAllByUserid(String userid);

//U	

	/**
	 * <p><strong>UPDATE</strong> : 마지막 확인 메시지 수정하기
	 * */
	Integer updateChatDetailIdx(Long chatRoomIdx, String userid, Long chatDetailIdx);
	/**
	 * <p><strong>UPDATE</strong> : 메시지 전부 확인처리
	 * */
	Integer updateChatDetailIdxToEnd(Long chatRoomIdx, String userid);
	/**
	 * <p><strong>UPDATE</strong> : 사용자 채팅방 이탈여부(chat_user_is_quit) 수정하기
	 * 
	 * @param isQuitValue : 수정값
	 * @param chatRoomIdx : 이탈여부를 수정할 채팅방
	 * @param userid : 이탈여부를 수정할 채팅방의 유저 userid
	 * */
	Integer updateIsQuit(boolean isQuitValue, long chatRoomIdx, String userid);
	
//c
	
	/**
	 * <p><strong>INSERT</strong> : 유저 추가
	 * <p>userList의 각 요소들은 chatRoomIdx, userid, chatUserIsCreator가 초기화되어있어야 함
	 * @param userList : 삽입할 정보 리스트
	 * */
	Integer insertRow(List<ChatUserDTO> userList);
}