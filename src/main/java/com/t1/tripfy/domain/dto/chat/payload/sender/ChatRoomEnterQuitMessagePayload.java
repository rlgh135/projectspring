package com.t1.tripfy.domain.dto.chat.payload.sender;

import com.t1.tripfy.domain.dto.chat.MessagePayload;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>SharedWorker <- 서버 송신용
 * <br>ChatMessageDTODeserializer에 <strong>등록되지 않음</strong>
 */
@Data
@Accessors(chain=true)
public class ChatRoomEnterQuitMessagePayload implements MessagePayload {
	private Long roomidx;
	private String userid;
}
