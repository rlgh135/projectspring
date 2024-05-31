package com.t1.tripfy.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.t1.tripfy.util.ChatMessageDTODeserializer;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>서버 <-> SharedWorker 간의 JSON 통신을 위한 클래스
 * <br>JSON 직렬/역직렬화를 위해 만듬
 * */
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
	/**
	 * <p>일단 지금은 일대일 채팅만이 목적이니 수신자 userid 필드도 하나지만
	 * <br>차후에는 컬렉션을 쓰거나 해야 함
	 * <br>아니면 수신자 머릿수 * MessageDTO를 하거나
	 * */
	@JsonIgnore
	private String receiverId;
}
