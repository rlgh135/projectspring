package com.t1.tripfy.domain.dto.chat;

import java.util.List;

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
	 * <p><strong>List&lt;String&gt; receiverId</strong>
	 * <br>수신자 userid 리스트
	 * */
	@JsonIgnore
	private List<String> receiverId;
}
