package com.t1.tripfy.service.pack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.pack.PackageLikeDTO;
import com.t1.tripfy.domain.dto.TimelineDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardLikeDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.mapper.pack.PackageFileMapper;
import com.t1.tripfy.mapper.pack.PackageMapper;
import com.t1.tripfy.mapper.pack.ReservationMapper;
import com.t1.tripfy.mapper.pack.TimelineMapper;
import com.t1.tripfy.mapper.user.UserMapper;

@Service
public class PackageServiceImpl implements PackageService{
	
	@Value("${file.dir}")
	private String saveFolder;
	@Value("${tempFile.dir}")
	private String tempFileFolder;
	
	@Autowired
	private PackageMapper pmapper;
	@Autowired
	private UserMapper umapper;
	@Autowired
	private PackageFileMapper pfmapper;
	@Autowired
	private TimelineMapper tmapper;
	@Autowired
	private ReservationMapper rmapper;
	
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
        
        if(file == null || file.getSize() == 0) {
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
	public List<PackageDTO> getAbroadCheapList(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.getAbroadCheapList(cri);
	}
	@Override
	public List<PackageDTO> getAbroadPopList(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.getAbroadPopList(cri);
	}
	@Override
	public List<PackageDTO> getAbroadRecentList(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.getAbroadRecentList(cri);
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
		return pmapper.getTotal(cri);
	}
	@Override
	public boolean modify(PackageDTO pack, MultipartFile[] files) throws Exception {
	   
	    if (pmapper.updatePack(pack) != 1) {
	        return false; // 업데이트에 실패하면 false를 반환합니다.
	    }

	    // 기존 파일 목록을 가져옵니다.
	    List<PackageFileDTO> orgFileList = pfmapper.getFiles(pack.getPackagenum());
	    System.out.println("이건뭐지" + orgFileList);

	    // 새로운 업로드 파일의 시스템 이름 목록을 저장할 리스트를 초기화합니다.
	    ArrayList<String> newSysnames = new ArrayList<>(); 

	    // 새로운 파일 업로드 로직
	    boolean flag = false; 
	    if (files != null && files.length > 0) { 
	        for (int i = 0; i < files.length; i++) {
	            MultipartFile file = files[i];
	            String orgname = file.getOriginalFilename();

	            if (orgname == null || orgname.equals("")) {
	                continue;
	            }

	            int lastIdx = orgname.lastIndexOf(".");
	            String extension = orgname.substring(lastIdx);
	            LocalDateTime now = LocalDateTime.now();
	            String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	            String systemname = time + UUID.randomUUID().toString() + extension;

	            String path = saveFolder + systemname;

	            PackageFileDTO pfdto = new PackageFileDTO();
	            pfdto.setPackagenum(pack.getPackagenum());
	            pfdto.setPfOrgname(orgname);
	            pfdto.setPfSysname(systemname);

	            flag = pfmapper.insertFile(pfdto) == 1;

	            file.transferTo(new File(path));
	            newSysnames.add(systemname);

	            if (!flag) {
	                break;
	            }
	        }
	    }

	    // 기존 파일 삭제 로직
	    if (flag) { // 파일 업로드가 성공했을 경우에만 기존 파일 삭제를 진행합니다.
	        for (PackageFileDTO orgFile : orgFileList) {
	            String systemname = orgFile.getPfSysname();
	            // 새로운 업로드 파일의 시스템 이름 목록에 포함되지 않는 파일만 삭제합니다.
	            if (!newSysnames.contains(systemname)) {
	                File file = new File(saveFolder, systemname);
	                if (file.exists()) {
	                    file.delete(); // 디스크에서 기존 파일을 삭제합니다.
	                }
	                pfmapper.deleteFileBySystemname(systemname); 
	            }
	        }   
	    } else {
	        // 업로드 실패 시 새로 업로드된 파일 삭제
	        for (String systemname : newSysnames) {
	            File file = new File(saveFolder, systemname);
	            if (file.exists()) {
	                file.delete(); 
	            }
	            pfmapper.deleteFileBySystemname(systemname); 
	        }
	    }

	    return flag; // 업로드 성공 여부를 반환합니다.
	}

	@Override
	public boolean remove(long packagenum) {
		
		tmapper.deleteTimeline(packagenum);
		if(pmapper.deletePack(packagenum) == 1) {
			List<PackageFileDTO> files = pfmapper.getFiles(packagenum);
			for(PackageFileDTO pfdto : files) {
				File file = new File(saveFolder,pfdto.getPfSysname());
				if(file.exists()) {
					file.delete();
					pfmapper.deleteFileBySystemname(pfdto.getPfSysname());
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean increaseReadCount(long packagenum) {
		PackageDTO pack = pmapper.getPackageByPackageNum(packagenum);
		return pmapper.updateReadCount(packagenum,pack.getViewcnt()+1) == 1;
	}
	
	@Override
	public long getLastNum(long guidenum) {
		return pmapper.getLastNum(guidenum);
	}
	@Override
	public void saveReservationForNonMember(ReservationDTO reservationDTO) {
	    pmapper.saveReservationForNonMember(reservationDTO);
	}

	@Override
	public void saveReservationForMember(ReservationDTO reservationDTO) {
	    pmapper.saveReservationForMember(reservationDTO);
	}
	//새로 추가
		@Override
		public List<PackageFileDTO> getFiles(long packagenum) {
			return pfmapper.getFiles(packagenum);
		}
		
		@Override
		public ResponseEntity<Resource> getThumbnailResource(String systemname) throws Exception {
			//경로에 관련된 객체(자원으로 가지고 와야 하는 파일에 대한 경로)
			Path path = Paths.get(saveFolder+systemname);
			System.out.println(path);
			//경로에 있는 파일의 MIME 타입을 조사해서 그대로 담기
			String contentType = Files.probeContentType(path);
			//응답 헤더 생성
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, contentType);
			
			//해당 경로(path)에 있는 파일로부터 뻗어나오는 InputStream*Files.newInputStream(path)을
			//통해 자원화*new InputStreamResource()
			Resource resource = new InputStreamResource(Files.newInputStream(path));
			return new ResponseEntity<>(resource,headers,HttpStatus.OK);
		}
	@Override
	public String[] getDayMMdd(String startdate, String enddate) {
        List<String> dayList = new ArrayList<>();

        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("MM.dd");

            Calendar start = Calendar.getInstance();
            start.setTime(sdfInput.parse(startdate));

            Calendar end = Calendar.getInstance();
            end.setTime(sdfInput.parse(enddate));

            while (!start.after(end)) {
                dayList.add(sdfOutput.format(start.getTime()));
                start.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리 필요
        }

        return dayList.toArray(new String[0]);
    }
	@Override
	public boolean tlregist(TimelineDTO tl) {
		return tmapper.insertTimeline(tl) == 1;
	}
	@Override
	public ArrayList<TimelineDTO> tlDayList(long packagenum, int day) {
		return tmapper.getTlDayList(packagenum, day);
	}
	@Override
	public boolean deleteTimeline(TimelineDTO tl) {
		if((tmapper.getLastDetailNum(tl)).getDetailNum() == tl.getDetailNum()) {
			return tmapper.deleteTimelineByDayAndDetailNum(tl) == 1;
		}
		else {
			if(tmapper.deleteTimelineByDayAndDetailNum(tl) == 1) {				
				return tmapper.updateTimelineDetailNum(tl);	
			}else {
				return false;
			}
		}
	}
	@Override
	public TimelineDTO getTimelineContent(TimelineDTO tl) {
		return tmapper.getTimeLineContent(tl);
	}
	@Override
	public boolean tlUpdateContents(TimelineDTO tl) {
		return tmapper.tlUpdateContents(tl) == 1;
	}
	@Override
	public String SummerNoteImageFile(MultipartFile file) throws Exception{		
	    if (file.isEmpty()) {
	    	String error = "파일이 없습니다";
	        return error;
	    }
	    System.out.println("컨트롤러에서 찍는 파일"+file);
	    
		String originalFileName = file.getOriginalFilename();
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		LocalDateTime now = LocalDateTime.now();
		String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		
		String systemFileName = time + UUID.randomUUID()+extension;
		
		String path = tempFileFolder + systemFileName;
		file.transferTo(new File(path));
		String thumnailpath = "/tilelineThumnail/"+systemFileName;
		System.out.println(thumnailpath);
		//경로에 있는 파일의 MIME 타입을 조사해서 그대로 담기
		return thumnailpath;
	}
	
	@Override
	public boolean deleteSummernoteImageFile(String fileUrl) {
		boolean flag = false;
		String systemFileName = fileUrl.replace("/tilelineThumnail/", "");
		File file = new File(tempFileFolder,systemFileName);
		if(file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
	@Override
	public boolean packageVisibility(long packagenum) {
		if(pmapper.insertPackContent(packagenum) == 1) {
			return true;
		}else {
			return false;			
		}
	}
	@Override
	public List<ReviewDTO> getReviewByGuidenum(long guidenum) {
		return pmapper.getReviewByGuidenum(guidenum);
	}
	@Override
	public List<ReservationDTO> getReservationCntByPackagenum(long packagenum) {
		
		return rmapper.getReservationCntByPackagenum(packagenum);
	}
	///추가입니다
	@Override
	public List<PackageDTO> SortListByPop(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.SortListByPop(cri);
	}

	@Override
	public List<PackageDTO> SortListByPrice(Criteria cri) {
		// TODO Auto-generated method stub
		return pmapper.SortListByPrice(cri);
	}
	// 해당 userid가 해당 board에 좋아요 눌렀는지 찾음
	@Override
	public PackageLikeDTO getPackageLike(String userid, long packagenum) {
		return pmapper.getPackageLike(userid, packagenum);
	}
	
	// 좋아요 클릭
		@Override
		public boolean likeClick(String userid, long packagenum) {
			PackageDTO pack = pmapper.getPackageByPackageNum(packagenum);
			
			if(pmapper.getPackageLike(userid, packagenum) == null) {
				// 좋아요 등록
				if(pmapper.likeRegist(userid, packagenum) == 1) {
					System.out.println("좋아요 등록 성공");
					return true;
				}
				
				else {
					System.out.println("좋아요 등록 실패");
				}
			}
			
			else {
				// 좋아요 취소
				if(pmapper.likeDelete(userid, packagenum) == 1) {
					
					System.out.println("좋아요 취소 성공");
					return true;
				}
				
				else {
					System.out.println("좋아요 취소 실패");
				}
			}
			return false;
		}
	
	
}
