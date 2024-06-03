package com.t1.tripfy.domain.dto.chat.payload.sender;

import java.util.List;

import com.t1.tripfy.domain.dto.chat.ChatDetailDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>SharedWorker <- 서버 송신용
 * <br>ChatMessageDTODeserializer에 <strong>등록되지 않음</strong>
 * <p><strong>chatDetails는 최신순임</strong>
 * <br><strong>List idx : chat_detail_idx</strong> -> 0:30, 1:29, 2:28, ... , 29:1 순
 * */
@Data
@Accessors(chain=true)
public class ChatDetailBulkMessagePayload implements MessagePayload {
	private Long roomidx;
	private Long startChatDetailIdx;
	private Long endChatDetailIdx;
	private Boolean isFirst;
	private List<ChatDetailDTO> chatDetails;
}
