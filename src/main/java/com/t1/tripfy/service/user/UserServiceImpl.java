package com.t1.tripfy.service.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.user.GuideDTO;
import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.mapper.board.BoardMapper;
import com.t1.tripfy.mapper.pack.PackageMapper;
import com.t1.tripfy.mapper.pack.ReservationMapper;
import com.t1.tripfy.mapper.user.UserMapper;
import com.t1.tripfy.util.PathUtil;

@Service
public class UserServiceImpl implements UserService{
	@Value("${userthumbnail.dir}")
	private String saveFolder;
	
	@Autowired
	private UserMapper umapper;
	
	@Autowired
	private BoardMapper bmapper;
	
	@Autowired
	private ReservationMapper resmapper;
	
	@Autowired
	private PackageMapper pmapper;
	
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
	public GuideDTO getGuideNum(String userid) {
		return umapper.getGuideNum(userid);
	}
	
	@Override
	public UserDTO getUser(String userid) {
		return umapper.getUserById(userid);
	}
	
	@Override
	public ResponseEntity<Resource> getThumbnailResource(String sysname) throws Exception{
		Path path = Paths.get(saveFolder + sysname);
		String contentType = Files.probeContentType(path);
		HttpHeaders headers = new HttpHeaders();
		
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);

		Resource resource = new InputStreamResource(Files.newInputStream(path));
		
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	@Override
	public List<BoardDTO> getMyBoardList(Criteria cri, String userid) {
		return bmapper.getMyList(cri, userid);
	}
	
	@Override
	public long getMyTotal(Criteria cri) {
		return bmapper.getTotal(cri);
	}
	
	@Override
	public List<ReservationDTO> getMyReservation(Criteria cri, String userid) {
		return resmapper.getMyReservation(cri, userid);
	}
	
	@Override
	public PackageDTO getJoinPackage(long packagenum) {
		return pmapper.getPackageByPackageNum(packagenum);
	}
	
	@Override
	public boolean changeSogae(String userid, String introduce) {
		return umapper.updateSogae(userid, introduce)==1;
	}
	
	@Override
	public List<PackageDTO> getMyPackages(long guidenum, Criteria cri) {
		return pmapper.getMyPackages(guidenum, cri);
	}
	
	@Override
	public List<PackageDTO> getMyIngPackages(long guidenum, Criteria cri) {
		return pmapper.getMyIngPackages(guidenum, cri);
	}
	
	@Override
	public int getTotalPackageCnt(long guidenum) {
		return pmapper.getMyPackageCnt(guidenum);
	}
	
	@Override
	public int getTotalReview(long guidenum) {
		return umapper.getTotalReviewCnt(guidenum);
	}
	
	@Override
	public List<ReviewDTO> getReviewByPackagenum(long packagenum) {
		return umapper.getReviews(packagenum);
	}
	
	@Override
	public List<ReservationDTO> getApplyByPackagenum(long packagenum) {
		return resmapper.getApply(packagenum);
	}
	
	@Override
	public boolean changeApplyCansle(long reservationnum, int isdelete) {
		return resmapper.changeIsdelete(reservationnum, isdelete)==1;
	}
	@Override
	public PackageDTO getMyPackageTwoWeek(long packagenum) {
		return resmapper.getMyPackageTwoWeek(packagenum);
	}
	
	@Override
	public ReviewDTO getMyReviewByPackagenum(long packagenum, String userid) {
		return umapper.getMyReview(packagenum, userid);
	}
	
	@Override
	public List<GuideUserDTO> getLikeGuides(String userid) {
		return umapper.getLikeGuides(userid);
	}
	@Override
	public UserImgDTO getGuideAndImg(long packagenum) {
		return umapper.getGuideAndImg(packagenum);
	}
	
	@Override
	public GuideUserDTO getLikeThisGuide(long guidenum, String userid) {
		return umapper.getLikeThisGuide(guidenum, userid);
	}
	
	@Override
	public boolean presslike(String userid, long guidenum) {
		if(umapper.getLikeThisGuide(guidenum, userid)!=null) {
			if(umapper.deleteLike(guidenum, userid)==1) {
				return true;
			}
			System.out.println("deletelike미스");
		} else {
			if(umapper.addLike(guidenum, userid)==1) {
				return true;
			}
			System.out.println("addlike미스");
		}
		System.out.println("뭔가조짐");
		return false;
	}
}
