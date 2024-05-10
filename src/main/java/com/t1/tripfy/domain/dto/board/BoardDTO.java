package com.t1.tripfy.domain.dto.board;

import lombok.Data;

@Data
public class BoardDTO {
	private long boardnum;
	private String userid;
	private String title;
	private String content;
	private String regdate;
	private int updatecheck;
	private String regionname;
	private String countrycode;
	private long likecnt;
	private long viewcnt;
	
}
