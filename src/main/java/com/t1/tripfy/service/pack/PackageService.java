package com.t1.tripfy.service.pack;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.TimelineDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;

public interface PackageService {
	boolean regist(PackageDTO pack, MultipartFile file) throws Exception;
	
	PackageDTO getDetail(long packagenum);
	UserDTO getUser(String userid);
	List<PackageDTO> getRecentList(Criteria cri);
	List<PackageDTO> getPopList(Criteria cri);
	List<PackageDTO> getCheapList(Criteria cri);
	List<PackageDTO> getPopularGuideList(Criteria cri);
	List<PackageDTO> getDetailRegionList(Criteria cri);
	//해
	List<PackageDTO> getAbroadRecentList(Criteria cri);
	List<PackageDTO> getAbroadPopList(Criteria cri);
	List<PackageDTO> getAbroadCheapList(Criteria cri);
	long getTotal(Criteria cri);
	long getLastNum(long guidenum);
	
	
	boolean modify(PackageDTO pack, MultipartFile[] files) throws Exception;
	boolean remove(long packagenum);

	void saveReservation(ReservationDTO reservationDTO);

	//추가
	ResponseEntity<Resource> getThumbnailResource(String systemname) throws Exception;
	List<PackageFileDTO>getFiles(long packagenum);
	
	String[] getDayMMdd(String startdate, String enddate);

	boolean tlregist(TimelineDTO tl);
	
	ArrayList<TimelineDTO> tlDayList(long packagenum, int day);
	
	boolean deleteTimeline(TimelineDTO tl);

	TimelineDTO getTimelineContent(TimelineDTO tl);

	boolean tlUpdateContents(TimelineDTO tl);
}
