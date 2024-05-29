package com.t1.tripfy.domain.dto.board;

import lombok.Data;

@Data
public class BoardaddrDTO {
	private long boardnum;
	private String placename;
	private String startdate;
	private String enddate;
}
