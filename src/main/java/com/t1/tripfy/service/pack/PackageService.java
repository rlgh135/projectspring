package com.t1.tripfy.service.pack;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;


import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.pack.PackageDTO;

public interface PackageService {
	boolean regist(PackageDTO pack, MultipartFile[] files) throws Exception;
	
	PackageDTO getDetail(long packagenum);
	List<PackageDTO> getRecentList(Criteria cri);
	List<PackageDTO> getPopList(Criteria cri);
	List<PackageDTO> getCheapList(Criteria cri);
	List<PackageDTO> getPopularGuideList(Criteria cri);
	List<PackageDTO> getDetailRegionList(Criteria cri);
	long getTotal(Criteria cri);
	long getLastNum(String userid);
	
	boolean modify(PackageDTO pack, MultipartFile[] files) throws Exception;
	boolean remove(long packagenum);

	
}
