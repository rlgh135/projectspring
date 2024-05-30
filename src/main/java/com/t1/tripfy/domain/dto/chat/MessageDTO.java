package com.t1.tripfy.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.t1.tripfy.util.ChatMessageDTODeserializer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@JsonDeserialize(using=ChatMessageDTODeserializer.class)
public class MessageDTO<T extends MessagePayload> {
	@JsonProperty("act")
	private String act;
	
	@JsonProperty("payload")
	private T payload;
	
	@JsonIgnore
	private String senderId;
	@JsonIgnore
	private String receiverId;
}
