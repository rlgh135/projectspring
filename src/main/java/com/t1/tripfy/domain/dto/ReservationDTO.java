package com.t1.tripfy.domain.dto;

import lombok.Data;

@Data
public class ReservationDTO {
	private long reservationnum;
	private long packagenum;
	private int adultCnt;
	private int childCnt;
	private String userid;
	private String keycode;
	private String price;
	private String payMethod;
	private int isDelete;
}
