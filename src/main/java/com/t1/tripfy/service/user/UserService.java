package com.t1.tripfy.service.user;

import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;

public interface UserService {
	boolean join(UserDTO user);
	boolean login(String userid, String userpw);
	boolean updateUser(UserDTO user);
	boolean updateProfileimg(MultipartFile thumbnail, String userid);
	boolean checkId(String userid);
	UserDTO getUser(String userid);
	GuideUserDTO getGuideNum(String userid);
	String getProfileImgName(String userid);
}
