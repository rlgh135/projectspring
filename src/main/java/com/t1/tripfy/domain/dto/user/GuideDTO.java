package com.t1.tripfy.domain.dto.user;

import lombok.Data;

@Data
public class GuideDTO {
	private long guidenum;
	private String userid;
	private long guideLikecnt;
	private long guideWarncnt;
	private String introduce;
}
