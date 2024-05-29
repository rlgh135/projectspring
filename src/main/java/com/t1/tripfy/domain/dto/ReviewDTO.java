package com.t1.tripfy.domain.dto;

import lombok.Data;

@Data
public class ReviewDTO {
	private long guidenum;
	private String userid;
	private String contents;
	private long packagenum;
	private String emSysname;
}
