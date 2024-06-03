package com.t1.tripfy.mapper.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.chat.ChatDetailDTO;

@Mapper
public interface ChatDetailMapper {
	//특정 채팅방에서 userid 가 읽지 않은 채팅 개수 세기
	Integer selectSpecificCountOfChatDetail(Long chatRoomIdx, String userid);
	//특정 채팅방의 모든 메시지 개수 세기
	Integer selectAllCountOfChatDetail(Long chatRoomIdx);
	//특정 채팅방의 마지막 메시지 가져오기
	ChatDetailDTO selectLastRowByChatRoomIdx(Long chatRoomIdx);
	/**
	 * <p><strong>SELECT</strong> : 특정 채팅방의 메시지 최근순 m번째부터 n개 가져오기
	 * <p><strong>m은 순서 - 1 임(최신 ~ 30개의 경우 m=0, n=30)</strong>
	 * */
	List<ChatDetailDTO> selectBulkMessagesByChatRoomIdx(Long chatRoomIdx, Long m, Long n);
	/**
	 * <p><strong>SELECT</strong> : 특정 채팅방의 메시지 k번째(chat_detail_idx) 이전 n개 가져오기
	 * <p><strong>m은 순서 - 1 임(최신 ~ 30개의 경우 m=0, n=30)</strong>
	 * */
	List<ChatDetailDTO> selectBulkMessagesByChatDetailIdx(Long chatRoomIdx, Long k, Long n);
	
	/**
	 * <p><strong>INSERT</strong> : 채팅 삽입
	 * */
	Integer insertChat(Long chatRoomIdx, String userid, String chatContent, LocalDateTime time);
}
