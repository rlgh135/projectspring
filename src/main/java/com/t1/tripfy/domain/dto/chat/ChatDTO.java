package com.t1.tripfy.domain.dto.chat;

import lombok.Data;

@Data
public class ChatDTO {
	private Long chatid;
	private String useridA;
	private String useridB;
	private Long packagenum;
}
