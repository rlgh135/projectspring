package com.t1.tripfy.domain.dto.chat.payload.sender;

import com.t1.tripfy.domain.dto.chat.MessagePayload;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatFailedMessagePayload implements MessagePayload {
	private String reason;
	
	public static class ChatFailReason {
		//서버단 오류
		public static final String SERVER_FAIL = "SERVER_FAIL";
		//권한 없음
		public static final String FORBIDDEN = "FORBIDDEN";
	}
}
