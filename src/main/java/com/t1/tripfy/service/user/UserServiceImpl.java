package com.t1.tripfy.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.mapper.user.UserMapper;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserMapper umapper;
	
	@Override
	public boolean join(UserDTO user) {
		if(umapper.insertUser(user)==1) {
			if(umapper.makeDefaultBadge(user.getUserid())==1) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean login(String userid, String userpw) {
		UserDTO user = umapper.getUserById(userid);
		if(user!=null) {
			if(userpw.equals(user.getUserpw())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean updateUser(UserDTO user) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean updateProfileimg(UserImgDTO userimg) {
		return umapper.updateProfileimg(userimg)==1;
	}
	
	@Override
	public boolean checkId(String userid) {
		return umapper.getUserById(userid) == null;
	}
	
	@Override
	public String getProfileImgName(String userid) {
		return umapper.getUserProfileName(userid);
	}
}
