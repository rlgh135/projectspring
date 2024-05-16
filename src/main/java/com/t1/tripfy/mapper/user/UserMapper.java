package com.t1.tripfy.mapper.user;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;

@Mapper
public interface UserMapper {
	//C
	int insertUser(UserDTO user);
	int makeDefaultBadge(String userid);
	
	//R
	UserDTO getUserById(String userid);
	String getUserProfileName(String userid);
	
	//U
	int updateUser(UserDTO user);
	int updateProfileimg(UserImgDTO userimg);
	
	//D
	int deleteUser(String userid);
	int deleteProfileimg(String userid);
}
