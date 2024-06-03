package com.t1.tripfy.domain.dto.chat.payload.receiver;

import com.t1.tripfy.domain.dto.chat.MessagePayload;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatContentMessagePayload implements MessagePayload {
	private Long roomidx;
	private String chatContent;
}
