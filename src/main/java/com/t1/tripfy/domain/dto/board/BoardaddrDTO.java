package com.t1.tripfy.domain.dto.board;

import lombok.Data;

@Data
public class BoardaddrDTO {
	private long boardnum;
	private String placename;
	private String roadaddress;
	private String address;
	private String duedate;
	private String enddate;
}
