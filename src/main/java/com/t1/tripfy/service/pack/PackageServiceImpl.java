package com.t1.tripfy.service.pack;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.pack.PackageDTO;

@Service
public class PackageServiceImpl implements PackageService{
	@Override
	public boolean regist(PackageDTO pack, MultipartFile[] files) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public List<PackageDTO> getListByCountryCode(Criteria cri) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<PackageDTO> getRecentList() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<PackageDTO> getCheapList() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<PackageDTO> getPopList() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<PackageDTO> getPopularGuideList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PackageDTO getDetail(long packagenum) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getLastNum(String userid) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long getTotal(Criteria cri) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean modify(PackageDTO pack, MultipartFile[] files) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean remove(long packagenum) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
