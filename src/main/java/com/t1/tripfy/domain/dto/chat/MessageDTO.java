package com.t1.tripfy.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="act")
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
