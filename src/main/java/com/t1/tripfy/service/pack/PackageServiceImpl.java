package com.t1.tripfy.service.pack;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.mapper.pack.PackageMapper;

@Service
public class PackageServiceImpl implements PackageService{
	
	@Value("${file.dir}")
	private String saveFolder;
	
	@Autowired
	private PackageMapper pmapper;
//	@Autowired
//	private FileMapper fmapper;
	
	@Override
	public boolean regist(PackageDTO pack, MultipartFile[] files) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public List<PackageDTO> getListByCountryCode(Criteria cri) {
	
		return pmapper.getListByCountryCode(cri);
	}
	@Override
	public List<PackageDTO> getRecentList(Criteria cri) {
		
		return pmapper.getRecentList(cri);
	}
	@Override
	public List<PackageDTO> getCheapList(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.getCheapList(cri);
	}
	@Override
	public List<PackageDTO> getPopList(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.getPopList(cri);
	}
	@Override
	public List<PackageDTO> getPopularGuideList(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.getPopularGuideList(cri);
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
