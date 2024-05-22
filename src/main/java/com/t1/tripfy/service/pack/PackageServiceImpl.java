package com.t1.tripfy.service.pack;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.mapper.pack.PackageFileMapper;
import com.t1.tripfy.mapper.pack.PackageMapper;
import com.t1.tripfy.mapper.user.UserMapper;

@Service
public class PackageServiceImpl implements PackageService{
	
	@Value("${file.dir}")
	private String saveFolder;
	
	@Autowired
	private PackageMapper pmapper;
	@Autowired
	private UserMapper umapper;
	@Autowired
	private PackageFileMapper pfmapper;
	
	@Override
	public boolean regist(PackageDTO pack, MultipartFile file) throws Exception {
		String startdate = pack.getStartdate();
		String enddate = pack.getEnddate();
		
		// DateTimeFormatter를 사용하여 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 문자열을 LocalDate로 변환
        LocalDate fmStartdate = LocalDate.parse(startdate, formatter);
        LocalDate fmEnddate = LocalDate.parse(enddate, formatter);
        
        // 두 날짜 사이의 차이를 계산
        int tourdays = Math.toIntExact(ChronoUnit.DAYS.between(fmStartdate, fmEnddate))+1;
        // 오늘 날짜를 얻음
        LocalDate today = LocalDate.now();
        
        // 8일 후의 날짜를 계산
        LocalDate dateDeadline = today.plusDays(8);
        
        // 날짜를 원하는 형식의 문자열로 변환
        String deadline = dateDeadline.format(formatter);
        pack.setTourdays(tourdays);
        pack.setDeadline(deadline);
        if(pmapper.insertPack(pack) != 1) {
			return false;
		}
        if(file == null) {
        	System.out.println("파일 널 체크 안됨요");
			return true;
		}else {
			boolean flag = false;
			long packagenum = pmapper.getLastNum(pack.getGuidenum());
			String pfOrgname = file.getOriginalFilename();
			int lastIdx = pfOrgname.lastIndexOf(".");
			String extension = pfOrgname.substring(lastIdx);
			
			LocalDateTime now = LocalDateTime.now();
			String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
			
			String pfSysname = time+UUID.randomUUID().toString()+extension;
			
			String path = saveFolder+pfSysname;
			
			PackageFileDTO pfdto = new PackageFileDTO();
			pfdto.setPackagenum(packagenum);
			pfdto.setPfSysname(pfSysname);
			pfdto.setPfOrgname(pfOrgname);
			
			flag = pfmapper.insertFile(pfdto) == 1;
			
			//실제 파일 업로드
			file.transferTo(new File(path));
			
			if(!flag) {
				//업로드했던 파일 삭제, 게시글 데이터 삭제, 파일 data 삭제, ...
				return false;
			}
		}
		return true;
	}
	@Override
	public List<PackageDTO> getDetailRegionList(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.getDetailRegionList(cri);
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
		return pmapper.getPackageByPackageNum(packagenum);
	}
	@Override
	public UserDTO getUser(String userid) {
		return umapper.getUserById(userid);
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
	@Override
	public long getLastNum(long guidenum) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void saveReservation(ReservationDTO reservationDTO) {
        pmapper.saveReservation(reservationDTO);
    }
	
	
}
