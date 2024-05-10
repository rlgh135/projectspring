package com.t1.tripfy.domain.dto;

import lombok.Data;

@Data
public class TimelineDTO {
	private long timelinenum;
	private long packagenum;
	private int day;
	private String title;
	private String contents;
}
