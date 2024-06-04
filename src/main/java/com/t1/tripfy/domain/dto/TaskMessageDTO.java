package com.t1.tripfy.domain.dto;

import lombok.Data;

@Data
public class TaskMessageDTO {
	private long messagenum;
	private String taskType;
	private String detailNum;
	private String sendid;
	private String contents;
	private String answer;
	private String taskStatus;
	private String regdate;
}
