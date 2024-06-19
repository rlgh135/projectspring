package com.t1.tripfy.mapper.chat;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.chat.ChatUserRegdateDTO;

@Mapper
public interface ChatUserRegdateMapper {
// C
	/**
	 * <p><strong>INSERT</strong> : 1행 기입
	 * <br>인수들은 전부 기입되어야 함
	 * @param chatRoomIdx : 채팅방 PK
	 * @param userid : 사용자 userid
	 * @param curAction : 생성진입/일반진입/이탈 여부
	 * @param curRegdate : 서버기준 발생 시간
	 * @return INSERT로 영향받은 행 개수
	 * */
	Integer insertChatUserRegdate(long chatRoomIdx, String userid, String curAction, LocalDateTime curRegdate);
	
	/**
	 * <p><strong>INSERT</strong> : 동적으로 n행 기입
	 * <p>모든 리스트 요소들의 chatRoomIdx, userid, curAction, curRegdate 필드들은 무조건 초기화돼있어야 함
	 * <p><strong>List 인덱스의 순서대로 DB에 삽입됨 - 순서가 중요한 경우 잘 체크해야함</strong>
	 * @param curList : 삽입할 정보들이 담긴 리스트
	 * @return INSERT로 영향받은 행 개수
	 */
	Integer insertChatUserRegdates(List<ChatUserRegdateDTO> curList);
	
// R
	/**
	 * <p><strong>SELECT</strong> : 특정 채팅방의 모든 진입/이탈 기록 가져오기
	 * @param chatRoomIdx : 조회할 채팅방
	 * @return 진입/이탈 기록 리스트
	 */
	List<ChatUserRegdateDTO> selectSpecificChatRoomRegdates(long chatRoomIdx);
	
	/**
	 * <p><strong>SELECT</strong> : 특정 유저의 특정 채팅방에서의 진입/이탈 기록 가져오기
	 * @param chatRoomIdx : 조회할 채팅방
	 * @param userid : 조회할 유저 userid
	 * @return 진입/이탈 기록 리스트
	 */
	List<ChatUserRegdateDTO> selectSpecificUserRegdates(long chatRoomIdx, String userid);
	
	/**
	 * <p><strong>SELECT</strong> : 특정 채팅방의 특정 시점 사이의 모든 진입/이탈 기록 가져오기
	 * <p><strong>반환값은 cur_idx 최신순 내림차순으로 정렬됨</strong>
	 * <blockquote><pre>
	 *     List IDX : cur_idx : cur_regdate
	 *     0        : 372     : "2024-06-19 17:16"
	 *     1        : 344     : "2024-06-19 14:32"
	 *     2        : 259     : "2024-06-18 23:59"
	 *     ...      : ...     : ...
	 * </pre></blockquote>
	 * <p>cur_idx는 cd.chat_detail_idx와 따로 놀기에 조회 범위는 LocalDatetime - DATETIME 을 기준으로 잡음
	 * @param chatRoomIdx : 조회할 채팅방
	 * @param startDate : 조회할 제일 마지막(오래된) 특정 시점
	 * @param endDate : 조회할 제일 최근 특정 시점
	 * @return 진입/이탈 기록 리스트
	 */
	List<ChatUserRegdateDTO> selectSpecificRangeOfUserRegdates(long chatRoomIdx, LocalDateTime startDate, LocalDateTime endDate);
	
// U
	//UPDATE도 쓸 일은 없을듯
// D
	//DELETE는 안 쓴다
}
