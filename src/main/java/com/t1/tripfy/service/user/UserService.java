package com.t1.tripfy.service.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.domain.dto.board.BoardLikeDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.user.GuideDTO;
import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;

public interface UserService {
	boolean join(UserDTO user);
	boolean login(String userid, String userpw);
	boolean updateUser(UserDTO user);
	boolean updateProfileimg(MultipartFile thumbnail, String userid);
	boolean checkId(String userid);
	UserDTO getUser(String userid);
	GuideDTO getGuideNum(String userid);
	String getProfileImgName(String userid);
	ResponseEntity<Resource> getThumbnailResource(String systemname) throws Exception;
	long getMyTotal(Criteria cri);
	List<BoardDTO> getMyBoardList(Criteria cri, String userid);
	List<ReservationDTO> getMyReservation(Criteria cri, String userid);
	PackageDTO getJoinPackage(long packagenum);
	boolean changeSogae(String userid, String introduce);
	List<PackageDTO> getMyPackages(long guidenum, Criteria cri);
	List<PackageDTO> getMyIngPackages(long guidenum, Criteria cri);
	int getTotalPackageCnt(long guidenum);
	int getTotalReview(long guidenum);
	List<ReviewDTO> getReviewByPackagenum(long packagenum);
	List<ReservationDTO> getApplyByPackagenum(long packagenum);
	boolean changeApplyCansle(long reservationnum, int tf);
	PackageDTO getMyPackageTwoWeek(long packagenum);
	ReviewDTO getMyReviewByPackagenum(long packagenum, String userid);
	List<GuideUserDTO> getLikeGuides(String userid);
	UserImgDTO getGuideAndImg(long packagenum);
	GuideUserDTO getLikeThisGuide(long guidenum, String userid);
	boolean presslike(String userid, long guidenum);
	int uploadHugi(ReviewDTO review);
	List<ReservationDTO> getForeignerReservations(String fname, String phone, Criteria cri);
	ReservationDTO getForeignerReservation(String keycode);
	PackageFileDTO getPackThumbnail(long packagenum);
	List<UserImgDTO> getAllUserImg();
	int insertGuide(GuideDTO guide);
	int applyCansle(long reservationnum, int cansleStatus);
	ReservationDTO getResevationByIdPackagenum(String userid, long packagenum);
	BoardFileDTO getMyBoardThumbnail(long boardnum);
	BoardLikeDTO getMyBoardLike(String userid, long boardnum);
	List<BoardDTO> getLikeBoardList(Criteria cri, String userid);
	List<PackageDTO> getReadyPack(Criteria cri, String userid);
	List<PackageDTO> getDonePack(Criteria cri, String userid);
	List<PackageDTO> getGuideReadyPack(Criteria cri, String guideid);
	List<PackageDTO> getGuideDonePack(Criteria cri, String guideid);
	Integer getTotalResCnt(long packagenum);
	List<ReviewDTO> getMyReviews(Criteria cri, String userid);
	List<ReviewDTO> getMineReviews(Criteria cri, String guideid);
}
