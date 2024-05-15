package com.t1.tripfy.domain.dto.chat;

import lombok.Data;

@Data
public class ChatDetailDTO {
	private Long messagenum;
	private Long chatid;
	private String senderid;
	private String message;
	private String regdate;
}
