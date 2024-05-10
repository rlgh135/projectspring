package com.t1.tripfy.domain.dto.chat;

import lombok.Data;

@Data
public class ChatDTO {
	private long chatid;
	private String useridA;
	private String useridB;
	private long packagenum;
}
