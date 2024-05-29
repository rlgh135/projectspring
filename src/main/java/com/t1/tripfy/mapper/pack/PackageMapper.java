package com.t1.tripfy.mapper.pack;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;


@Mapper
public interface PackageMapper {
	int insertPack(PackageDTO pack);
	int insertPackContent(long packagenum);
	
	List<PackageDTO>getRecentList(Criteria cri);
	List<PackageDTO> getPopList(Criteria cri);
	List<PackageDTO> getCheapList(Criteria cri);
	List<PackageDTO> getPopularGuideList(Criteria cri);
	List<PackageDTO>getListByCountryCode(Criteria cri);
	List<PackageDTO>getDetailRegionList(Criteria cri);
	
	long getTotal(Criteria cri);
	long getLastNum(long guidenum);
	PackageDTO getPackageByPackageNum(long packagenum);
	
	int updatePack(PackageDTO pack);
	int deletePack(long packagenum);	
	int updateReadCount(@Param("packagenum")long packagenum,@Param("viewcnt") long viewcnt);
	
	List<ReviewDTO> getReviewByGuidenum(long guidenum);
	void saveReservation(ReservationDTO reservation);
	
	//해외
	List<PackageDTO> getAbroadCheapList(Criteria cri);
	List<PackageDTO> getAbroadPopList(Criteria cri);
	List<PackageDTO> getAbroadRecentList(Criteria cri);
	
	//가이드가 불러올 자신 패키지
	List<PackageDTO> getMyPackages(long guidenum, Criteria cri);
	List<PackageDTO> getMyIngPackages(long guidenum, Criteria cri);
	int getMyPackageCnt(long guidenum);
}