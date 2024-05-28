package com.t1.tripfy.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.user.GuideDTO;
import com.t1.tripfy.domain.dto.user.GuideUserDTO;
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
	GuideDTO getGuideNum(String userid);
	int getTotalReviewCnt(long guidenum);
	List<ReviewDTO> getReviews(long packagenum);
	ReviewDTO getMyReview(long packagenum, String userid);
	List<GuideUserDTO> getLikeGuides(String userid);
	UserImgDTO getGuideAndImg(long packagenum);
	
	//U
	int updateUser(UserDTO user);
	int updateProfileimg(UserImgDTO userimg);
	int updateSogae(String userid, String introduce);
	
	//D
	int deleteUser(String userid);
	int deleteProfileimg(String userid);
}
