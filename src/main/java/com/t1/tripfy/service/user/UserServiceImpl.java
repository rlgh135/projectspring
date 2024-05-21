package com.t1.tripfy.service.user;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.mapper.user.UserMapper;

@Service
public class UserServiceImpl implements UserService{
	@Value("${user.dir}")
	private String saveFolder;
	
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
	public boolean updateProfileimg(MultipartFile thumbnail, String userid) {
		System.out.println("service : "+thumbnail.getOriginalFilename());
		//UUID uuid = UUID.randomUUID();
		MultipartFile file = thumbnail;
		System.out.println(file.getOriginalFilename());
		
		//apple.png
		String orgname = file.getOriginalFilename();
		//5
		int lastIdx = orgname.lastIndexOf(".");
		//.png
		String extension = orgname.substring(lastIdx);
		
		LocalDateTime now = LocalDateTime.now();
		String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

		//20240502162130141랜덤문자열.png
		String systemname = time+UUID.randomUUID().toString()+extension;
		
		//실제 생성될 파일의 경로
		//D:/0900_GB_JDS/7_spring/file/20240502162130141랜덤문자열.png
		String path = saveFolder+systemname;
		
		//File DB 저장
		UserImgDTO newimg = new UserImgDTO();
		newimg.setUserid(userid);
		newimg.setOrgname(orgname);
		newimg.setSysname(systemname);
		
		try {
			file.transferTo(new File(path));
		} catch (IllegalStateException e) {
			System.out.println("IllegalStateException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		return umapper.updateProfileimg(newimg)==1;
	}
	
	@Override
	public boolean checkId(String userid) {
		return umapper.getUserById(userid) == null;
	}
	
	@Override
	public String getProfileImgName(String userid) {
		return umapper.getUserProfileName(userid);
	}
	
	@Override
	public GuideUserDTO getGuideNum(String userid) {
		return umapper.getGuideNum(userid);
	}
	
	@Override
	public UserDTO getUser(String userid) {
		return umapper.getUserById(userid);
	}
}
