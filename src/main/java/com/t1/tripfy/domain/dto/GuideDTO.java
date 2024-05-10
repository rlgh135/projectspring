package com.t1.tripfy.domain.dto;

import lombok.Data;

@Data
public class GuideDTO {
	private long guidenum;
	private String userid;
	private long guideLikecnt;
	private long guideWarncnt;
}
