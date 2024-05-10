package com.t1.tripfy.domain.dto.pack;

import lombok.Data;

@Data
public class PackageDTO {
	private long packagenum;
	private long guidenum;
	private String packageTitle;
	private String packageContent;
	private int maxcnt;
	private int adultPrice;
	private int childPrice;
	private String startdate;
	private String enddate;
	private int tourdays;
	private long viewcnt;
	private String deadline;
	private int isDelete;
}
