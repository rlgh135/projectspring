package com.t1.tripfy.domain.dto.chat.payload.receiver;

import com.t1.tripfy.domain.dto.chat.MessagePayload;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>SharedWorker -> 서버 수신용
 * <br>ChatMessageDTODeserializer에 등록됨
 * */
@Data
@Accessors(chain=true)
public class ChatRoomEnterMessagePayload implements MessagePayload {
	private Long roomidx;
}
