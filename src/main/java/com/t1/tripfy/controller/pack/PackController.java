package com.t1.tripfy.controller.pack;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.PageDTO;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.TimelineDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.service.pack.PackageService;
import com.t1.tripfy.service.user.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/package/*")
public class PackController {
	@Autowired
	private PackageService service;
	@Autowired
	private UserService uservice;
	
	@GetMapping("pmain")
	public void main(Criteria cri, Model model, HttpServletRequest req) {
	    List<PackageFileDTO> allRfiles = new ArrayList<>();
	    List<PackageFileDTO> allCfiles = new ArrayList<>();
	    List<PackageFileDTO> allPfiles = new ArrayList<>();
	    List<ReservationDTO> allRreserves = new ArrayList<>();
	    List<ReservationDTO> allCreserves = new ArrayList<>();
	    List<ReservationDTO> allPreserves = new ArrayList<>();
	    
	    List<PackageDTO> recent = service.getRecentList(cri);
	   
	    for (PackageDTO pack : recent) {
	        long rpackagenum = pack.getPackagenum();
	        List<PackageFileDTO> rfile = service.getFiles(rpackagenum);
	        List<ReservationDTO> reserve = service.getReservationCntByPackagenum(rpackagenum); 
	        allRfiles.addAll(rfile);
	        allRreserves.addAll(reserve);
	    }

	    List<PackageDTO> cheap = service.getCheapList(cri);
	    for (PackageDTO pack : cheap) {
	        long cpackagenum = pack.getPackagenum();
	        List<PackageFileDTO> cfile = service.getFiles(cpackagenum);
	        
	        List<ReservationDTO> reserve = service.getReservationCntByPackagenum(cpackagenum); 
	        allCfiles.addAll(cfile);
	        allCreserves.addAll(reserve);
	    }
	    
	    List<PackageDTO> pop = service.getPopList(cri);
	    for (PackageDTO pack : pop) {
	        long ppackagenum = pack.getPackagenum();
	        List<PackageFileDTO> pfile = service.getFiles(ppackagenum);
	        List<ReservationDTO> reserve = service.getReservationCntByPackagenum(ppackagenum);
	        allPfiles.addAll(pfile);
	        allPreserves.addAll(reserve);
	    }

	    model.addAttribute("recent", recent);
	    model.addAttribute("cheap", cheap);
	    model.addAttribute("pop", pop);
	    model.addAttribute("rfile", allRfiles);
	    model.addAttribute("cfile", allCfiles);
	    model.addAttribute("pfile", allPfiles);
	    model.addAttribute("rreserve", allRreserves);
	    model.addAttribute("creserve", allCreserves);
	    model.addAttribute("preserve", allPreserves);
	}


	@GetMapping("abroadmain")
	public void abroadmain(Criteria cri,Model model) {
		
		List<PackageFileDTO> allRfiles = new ArrayList<>();
		List<PackageFileDTO> allCfiles = new ArrayList<>();
		List<PackageFileDTO> allPfiles = new ArrayList<>();
		List<ReservationDTO> allRreserves = new ArrayList<>();
		List<ReservationDTO> allCreserves = new ArrayList<>();
		List<ReservationDTO> allPreserves = new ArrayList<>();
		
		List<PackageDTO> recent = service.getAbroadRecentList(cri);
		
		
		  for (PackageDTO pack : recent) {
			  long rpackagenum = pack.getPackagenum();
		        List<PackageFileDTO> rfile = service.getFiles(rpackagenum);
		        List<ReservationDTO> reserve = service.getReservationCntByPackagenum(rpackagenum); 
		        allRfiles.addAll(rfile);
		        allRreserves.addAll(reserve);
		    }
		List<PackageDTO> cheap = service.getAbroadCheapList(cri);
	
		for (PackageDTO pack : cheap) {
			long cpackagenum = pack.getPackagenum();
	        List<PackageFileDTO> cfile = service.getFiles(cpackagenum);
	        List<ReservationDTO> reserve = service.getReservationCntByPackagenum(cpackagenum); 
	        allCfiles.addAll(cfile);
	        allCreserves.addAll(reserve);
	    }
		
		List<PackageDTO> pop = service.getAbroadPopList(cri);
		System.out.println(pop);
		
		for (PackageDTO pack : pop) {
			 long ppackagenum = pack.getPackagenum();
		        List<PackageFileDTO> pfile = service.getFiles(ppackagenum);
		        List<ReservationDTO> reserve = service.getReservationCntByPackagenum(ppackagenum);
		        allPfiles.addAll(pfile);
		        allPreserves.addAll(reserve);
		}
	

	    model.addAttribute("cheap", cheap);
	    model.addAttribute("pop", pop);
	    model.addAttribute("rfile", allRfiles);
	    model.addAttribute("cfile", allCfiles);
	    model.addAttribute("pfile", allPfiles);
	    model.addAttribute("rreserve", allRreserves);
	    model.addAttribute("creserve", allCreserves);
	    model.addAttribute("preserve", allPreserves);
	}
	
	
	
	@GetMapping(value={"plist","abroadlist"})
	public void list(Criteria cri, Model model) {
	    System.out.println(cri);
	    List<PackageFileDTO> allfiles = new ArrayList<>();
	    List<PackageDTO> list = service.getDetailRegionList(cri);
	    List<ReservationDTO> allReserves = new ArrayList<>();
	    for (PackageDTO pack : list) {
		    long ppackagenum = pack.getPackagenum();
		    List<ReservationDTO> reserve = service.getReservationCntByPackagenum(ppackagenum); 
		    List<PackageFileDTO> lfile = service.getFiles(ppackagenum);
		    System.out.println(lfile);
		    allfiles.addAll(lfile);
		    allReserves.addAll(reserve);
		}
	    model.addAttribute("file", allfiles);
	    model.addAttribute("list", list);
	    model.addAttribute("reserve", allReserves);
	    model.addAttribute("pageMaker", new PageDTO(service.getTotal(cri), cri));
	}
	
	@GetMapping(value={"pget", "pmodify"})
	public String get(Criteria cri, long packagenum, HttpServletRequest req, HttpServletResponse resp, Model model) {
	    System.out.println("packagenum: " + packagenum);
	    String requestURI = req.getRequestURI();
	    model.addAttribute("cri", cri);
	    HttpSession session = req.getSession();
	    PackageDTO pack = service.getDetail(packagenum);
	    System.out.println(pack + "안뜨나?");
	    model.addAttribute("package", pack);
	    model.addAttribute("files", service.getFiles(packagenum));
	    String loginUser = (String) session.getAttribute("loginUser");
	    List<ReservationDTO> reserve = service.getReservationCntByPackagenum(packagenum); 
	    System.out.println(reserve+"대체 왜 없는거야 슈우발 ");
	    List<ReviewDTO> reviewlist = new ArrayList<>();
	    long gunum = pack.getGuidenum();
	    List<ReviewDTO> review = service.getReviewByGuidenum(gunum);
	    List<UserImgDTO> thumbnaillist = uservice.getAllUserImg();

	    for (ReviewDTO rdto : review) {
	        // 각 ReviewDTO에 thumbnailList를 설정
	        rdto.setUserImages(thumbnaillist);
	        reviewlist.add(rdto);
	    }

	    // model에 reviewlist 추가
	    model.addAttribute("review", reviewlist);
	    model.addAttribute("reserve",reserve);	    
	    

	    //일단 보류

	    if (requestURI.contains("pmodify")) {
	        return "package/pmodify";
	    }
	    // 세션에서 guidenum 가져오기
	    Object guidenumObj = session.getAttribute("guideNum");
	    Long guidenum = null;
	    if (guidenumObj instanceof Long) {
	        guidenum = (Long) guidenumObj;
	    } else if (guidenumObj instanceof Integer) {
	        guidenum = ((Integer) guidenumObj).longValue();
	    }
	    

	    
	    
	    // 쿠키를 사용하여 각 packagenum에 대한 조회수 증가
	    if (guidenum == null || !guidenum.equals(pack.getGuidenum())) {
	        Cookie[] cookies = req.getCookies();
	        Cookie read_pack = null;
	        if (cookies != null) {
	            for (Cookie cookie : cookies) {
	                if (cookie.getName().equals("read_pack" + packagenum)) {
	                    read_pack = cookie;
	                    break;
	                }
	            }
	        }
	        if (read_pack == null) {
	            service.increaseReadCount(packagenum);
	            System.out.println("조회수 증가");
	            Cookie cookie = new Cookie("read_pack" + packagenum, "r");
	            cookie.setMaxAge(1800); // 30분 동안 유지
	            resp.addCookie(cookie);
	        }
	    }

	    return "package/pget";
	}
	
	@GetMapping("pay")
	public String pay(long packagenum, HttpServletRequest req, HttpServletResponse resp, Model model) {
	    System.out.println("packagenum: " + packagenum);
	    String requestURI = req.getRequestURI();
	    HttpSession session = req.getSession();
	    PackageDTO pack = service.getDetail(packagenum);
	    model.addAttribute("package", pack);

	    String loginUser = (String) session.getAttribute("loginUser");
	    model.addAttribute("loginUser", loginUser);

	    if (loginUser != null) {
	        UserDTO user = service.getUser(loginUser);
	        model.addAttribute("user", user);
	    } else {
	        // 로그인하지 않은 사용자 처리: 빈 사용자 정보 추가
	        UserDTO emptyUser = new UserDTO();
	        model.addAttribute("user", emptyUser);
	    }

	    return "package/pay";
	}

	
	@PostMapping("write")
	public String write(PackageDTO pack, MultipartFile packageFile,HttpServletRequest req) throws Exception {
		//가이드num 세션있다고 가정
		long guidenum = 1;
		pack.setGuidenum(guidenum);
		System.out.println(packageFile);
		System.out.println(pack.getCountrycode());
		System.out.println(pack.getRegionname());
		if(service.regist(pack, packageFile)) {	
				long packagenum = service.getLastNum(pack.getGuidenum());
				return "redirect:/package/tlwrite?packagenum="+packagenum;
		}
		//실패처리
		else {
			return "redirect:/index";
		}
	}
	
	@GetMapping("tlwrite")
	public String tlwrite(@RequestParam long packagenum, Model model) {
		//가이드num 세션에 있다고 가정
		long guidenum = 1;
		model.addAttribute("packagenum", packagenum);
		PackageDTO pac =  service.getDetail(packagenum);
		//유효성검사 해야함
		if(guidenum != pac.getGuidenum()) {
			//옳은 경로로 가라고 경고페이지 띄워줘야함
			return "redirect:/index";
		}
		model.addAttribute("pac",pac);
		String[] dayMMdd = service.getDayMMdd(pac.getStartdate(),pac.getEnddate());
		model.addAttribute("dayMMdd",dayMMdd);
		return "/package/timelineWrite";
	}
	
	@GetMapping("tlget")
	public String tlget(@RequestParam long packagenum, Model model) {
		//가이드num 세션에 있다고 가정
		long guidenum = 1;
		PackageDTO pac =  service.getDetail(packagenum);
		//유효성검사 해야함
		model.addAttribute("pac",pac);
		String[] dayMMdd = service.getDayMMdd(pac.getStartdate(),pac.getEnddate());
		model.addAttribute("dayMMdd",dayMMdd);
		return "/package/timelineGet";
	}
	
	@PostMapping(value="timelineRegist", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<TimelineDTO> tlRegist(@RequestBody TimelineDTO tl){
		boolean result = service.tlregist(tl);
		if(result) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value="timelineList", produces = "application/json;charset=utf-8")
	public ResponseEntity<ArrayList<TimelineDTO>> timeLineDayList(@RequestParam long packagenum, @RequestParam int day){
	    ArrayList<TimelineDTO> result = service.tlDayList(packagenum, day);
	    if(result != null) {
	        return new ResponseEntity<ArrayList<TimelineDTO>>(result, HttpStatus.OK);
	    }
	    else {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value="getTimelineContent", produces = "application/json;charset=utf-8")
	public ResponseEntity<TimelineDTO> getTimelineContent(@RequestParam long packagenum, @RequestParam int day, @RequestParam int detailNum) {
		TimelineDTO tl = new TimelineDTO();
		tl.setPackagenum(packagenum);
		tl.setDay(day);
		tl.setDetailNum(detailNum);
	    TimelineDTO result = service.getTimelineContent(tl);
	    if(result != null) {
	        return new ResponseEntity<TimelineDTO>(result, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	@PostMapping(value="timelineDelete", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<TimelineDTO> deleteTimeline(@RequestBody TimelineDTO tl) {
		System.out.println(tl);
	    boolean deleted = service.deleteTimeline(tl);
	    if (deleted) {
	        return new ResponseEntity<>(HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@PutMapping(value="timelineContentsUpdate", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<Void> tlUpdateContents(@RequestBody TimelineDTO tl) {
	    boolean result = service.tlUpdateContents(tl);
	    if (result) {
	        return ResponseEntity.ok().build(); // 200 OK 상태 코드 반환
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 내부 서버 오류 상태 코드 반환
	    }
	}

	@GetMapping("thumbnail")
	public ResponseEntity<Resource> thumbnail(String systemname) throws Exception {
		System.out.println(systemname);
		return service.getThumbnailResource(systemname);
	}
	
	@PostMapping("pmodify")
	public String modify(PackageDTO pack, MultipartFile[] files,Criteria cri, Model model) throws Exception {

		if(service.modify(pack, files)) {
			return "redirect:/package/pget"+cri.getListLink()+"&packagenum="+pack.getPackagenum();
		}
		else {
			return "redirect:/package/pget"+cri.getListLink()+"&&packagenum="+pack.getPackagenum();
		}
	}
	@GetMapping("remove")
	public String remove(Criteria cri, long packagenum,HttpServletRequest req) {
		String loginUser = (String)req.getSession().getAttribute("loginUser");
		PackageDTO pack = service.getDetail(packagenum);
		
			if(service.remove(packagenum)) {
				return "redirect:/package/pmain"+cri.getListLink();
			}
		
		return "redirect:/package/pmain"+cri.getListLink()+"&packagenum="+packagenum;
		
	}

	
	@PostMapping("SummerNoteImageFile")
	public @ResponseBody String SummerNoteImageFile(@RequestParam("file") MultipartFile file) throws Exception {
	    String path = service.SummerNoteImageFile(file);
	    return path;
	}
	
	@PostMapping("deleteSummernoteImageFile")
	public @ResponseBody String deleteSummernoteImageFile(@RequestParam("file") String fileUrl) {
		String result = "";
	    if(service.deleteSummernoteImageFile(fileUrl)) {
	    	result = "성공";
	    }else {
	    	result = "실패";
	    }
	    return result;
	}
	
	@GetMapping("packageVisibility")
	public String packageVisibility(@RequestParam("packagenum") long packagenum) {
		if(service.packageVisibility(packagenum)) {
			return "redirect:/package/pget?packagenum="+packagenum;
		}else {
			return "redirect:/package/tlwrite?packagenum="+packagenum;
		}
	}

}
