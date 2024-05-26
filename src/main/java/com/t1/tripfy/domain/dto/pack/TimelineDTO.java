package com.t1.tripfy.domain.dto.pack;

import lombok.Data;

@Data
public class TimelineDTO {
	long timelinenum;
	long packagenum;
	int day;
	int detailNum;
	String title;
	String contents;
}
