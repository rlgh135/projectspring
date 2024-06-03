package com.t1.tripfy.domain.dto.board;

import lombok.Data;

@Data
public class BoardReplyDTO {
	private long replynum;
	private long boardnum;
	private String userid;
	private int updatecheck;
	private String emSysname;
	private String contents;
	private String regdate;
	private String sysname;
}
