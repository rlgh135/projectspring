package com.t1.tripfy.mapper.pack;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;

@Mapper
public interface PackageMapper {
	int insertPack(PackageDTO pack);
	int insertPackContent(PackageDTO pack);
	
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
	
	void saveReservation(ReservationDTO reservation);
	
	//해외
	List<PackageDTO> getAbroadCheapList(Criteria cri);
	List<PackageDTO> getAbroadPopList(Criteria cri);
	List<PackageDTO> getAbroadRecentList(Criteria cri);
}