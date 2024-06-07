package com.t1.tripfy.domain.dto.chat.payload.sender;

import java.time.LocalDateTime;

import com.t1.tripfy.domain.dto.chat.MessagePayload;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatContentDetailMessagePayload implements MessagePayload {
	private Long roomidx;
	private Long chatDetailIdx;
	private String title;
	private String userid;
	private String chatContent;
	private LocalDateTime regdate;
//	private Integer uncheckedmsg; //수신자 기준 미확인 메시지
}
