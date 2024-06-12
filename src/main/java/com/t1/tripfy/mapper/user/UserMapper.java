package com.t1.tripfy.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.user.GuideDTO;
import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;

@Mapper
public interface UserMapper {
	//C
	int insertUser(UserDTO user);
	int makeDefaultBadge(String userid);
	int addLike(long guidenum, String userid);
	int addHugi(ReviewDTO review);
	int insertGuide(GuideDTO guide);
	
	//R
	UserDTO getUserById(String userid);
	String getUserProfileName(String userid);
	UserImgDTO getUserProfileDTO(String userid);
	GuideDTO getGuideNum(String userid);
	int getTotalReviewCnt(long guidenum);
	List<ReviewDTO> getReviews(long packagenum);
	ReviewDTO getMyReview(long packagenum, String userid);
	List<GuideUserDTO> getLikeGuides(String userid);
	UserImgDTO getGuideAndImg(long packagenum);
	GuideUserDTO getLikeThisGuide(long guidenum, String userid);
	List<UserImgDTO> getAllUserImg();
	PackageFileDTO getMyPackThumb(long packagenum);
	List<BoardDTO> getLikeBoards(Criteria cri, String userid);
	List<PackageDTO> getReadyPack(Criteria cri, String userid);
	List<PackageDTO> getDonePack(Criteria cri, String userid);
	List<PackageDTO> getReadyGPack(Criteria cri, String guideid);
	List<PackageDTO> getDoneGPack(Criteria cri, String guideid);
	List<ReviewDTO> getMyReviews(Criteria cri, String userid);
	List<ReviewDTO> getMineReviews(Criteria cri, String guideid);
	Integer getTotalBoardCnt(String userid);
	Integer getTotalReplyCnt(String userid);
	List<Long> getAllPackagenumInReservation(String userid, int pcount);
	List<PackageDTO> getLikePackage(Criteria cri, String userid);
	
	//U
	int updateUser(UserDTO user);
	int updateProfileimg(UserImgDTO userimg);
	int updateSogae(String userid, String introduce);
	int updateCansle(long reservationnum, int cansleStatus);
	
	//D
	int deleteLike(long guidenum, String userid);
	int deleteUser(String userid);
	int deleteProfileimg(String userid);
	int deleteHugi(ReviewDTO review);
	void deleteFutureReview();
	
	// 유저 프로필
	UserImgDTO getUserProfile(String userid);
	
}
