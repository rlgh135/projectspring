package com.t1.tripfy.service.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.mapper.user.UserMapper;
import com.t1.tripfy.util.PathUtil;

@Service
public class UserServiceImpl implements UserService{
	@Value("${userthumbnail.dir}")
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
	@Transactional
	public boolean updateProfileimg(MultipartFile thumbnail, String userid) {
		// 사진을 /static/image에 UUID로 변경해서 저장
		String orgname = thumbnail.getOriginalFilename();
        String sysname = PathUtil.writeImageFile(thumbnail);
        
        // 저장된 파일의 경로를 DB에 저장
        UserImgDTO userimg = new UserImgDTO();
        userimg.setOrgname(orgname);
        userimg.setSysname(sysname);
        userimg.setUserid(userid);
        
        String path = saveFolder+sysname;
        
        if(umapper.updateProfileimg(userimg)==1) {
        	//실제 파일 업로드
        	try {
        		thumbnail.transferTo(new File(path));
        	} catch (IllegalStateException | IOException e) {
        		System.out.println("파일 실제 저장 에러: "+e);
        		return false;
        	}
        }
        
        return true;
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
	
	@Override
	public ResponseEntity<Resource> getThumbnailResource(String sysname) throws Exception{
		// 경로에 관련된 객체(자원으로 가지고 와야 하는 파일에 대한 경로)
		Path path = Paths.get(saveFolder + sysname);
		// 경로에 있는 파일의 MIME 타입을 조사해서 그대로 담기
		String contentType = Files.probeContentType(path);
		// 응답 헤더 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);

		// 해당 경로(path)에 있는 파일로부터 뻗어나오는 InputStream*Files.newInputStream(path)을
		// 통해 자원화*new InputStreamResource()
		Resource resource = new InputStreamResource(Files.newInputStream(path));
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
}
