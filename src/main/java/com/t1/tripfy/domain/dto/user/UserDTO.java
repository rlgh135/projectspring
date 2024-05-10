package com.t1.tripfy.domain.dto.user;

import lombok.Data;

@Data
public class UserDTO {
	private String userid;
	private String userpw;
	private String phone;
	private String email;
	private String gender;
	private String birth;
	private String addr;
	private String addrdetail;
	private String addretc;
	private String regdate;
	private long userWarncnt;
	private int isDelete;
}
