package com.t1.tripfy.domain.dto.chat.payload;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.t1.tripfy.domain.dto.chat.MessagePayload;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@JsonTypeName("chatRoomEnter")
public class ChatRoomEnterMessagePayload implements MessagePayload {
	private Long roomidx;
}
