package com.t1.tripfy.domain.dto.chat.payload.receiver;

import com.t1.tripfy.domain.dto.chat.MessagePayload;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatLoadMessagePayload implements MessagePayload {
	private Long roomidx;
	private Long startChatDetailIdx;
}
