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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.PageDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.TimelineDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.service.pack.PackageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/package/*")
public class PackController {
	@Autowired
	private PackageService service;
	
	@GetMapping("pmain")
	public void main(Criteria cri, Model model,HttpServletRequest req) {
		List<PackageFileDTO> allRfiles = new ArrayList<>();
		List<PackageFileDTO> allCfiles = new ArrayList<>();
		List<PackageFileDTO> allPfiles = new ArrayList<>();
		
	    List<PackageDTO> recent = service.getRecentList(cri);

	    for (PackageDTO pack : recent) {
	        long rpackagenum = pack.getPackagenum();
	        System.out.println("최신 :" + rpackagenum);
	        List<PackageFileDTO> rfile = service.getFiles(rpackagenum);
	        System.out.println(rfile);
	        allRfiles.addAll(rfile);
	    }

	    List<PackageDTO> cheap = service.getCheapList(cri);
	    for (PackageDTO pack : cheap) {
	        long cpackagenum = pack.getPackagenum();
	        System.out.println("최신 :" + cpackagenum);
	        List<PackageFileDTO> cfile = service.getFiles(cpackagenum);
	        System.out.println(cfile);
	        allCfiles.addAll(cfile);
	    }
	    List<PackageDTO> pop = service.getPopList(cri);
	    for (PackageDTO pack : pop) {
	        long ppackagenum = pack.getPackagenum();
	        System.out.println("최신 :" + ppackagenum);
	        List<PackageFileDTO> pfile = service.getFiles(ppackagenum);
	        System.out.println(pfile);
	        allPfiles.addAll(pfile);
	    }

	    model.addAttribute("recent", recent);
	    model.addAttribute("cheap", cheap);
	    model.addAttribute("pop", pop);
	    model.addAttribute("rfile", allRfiles);
	    model.addAttribute("cfile", allCfiles);
	    model.addAttribute("pfile", allPfiles);
	}

	@GetMapping("abroadmain")
	public void abroadmain(Criteria cri,Model model) {
		
		List<PackageFileDTO> allRfiles = new ArrayList<>();
		List<PackageFileDTO> allCfiles = new ArrayList<>();
		List<PackageFileDTO> allPfiles = new ArrayList<>();
		
		List<PackageDTO> recent = service.getAbroadRecentList(cri);
		System.out.println(recent);
		  for (PackageDTO pack : recent) {
		        long rpackagenum = pack.getPackagenum();
		        System.out.println("최신 :" + rpackagenum);
		        List<PackageFileDTO> rfile = service.getFiles(rpackagenum);
		        System.out.println(rfile);
		        allRfiles.addAll(rfile);
		    }
		List<PackageDTO> cheap = service.getAbroadCheapList(cri);
		System.out.println(cheap);
		for (PackageDTO pack : cheap) {
	        long cpackagenum = pack.getPackagenum();
	        System.out.println("최신 :" + cpackagenum);
	        List<PackageFileDTO> cfile = service.getFiles(cpackagenum);
	        System.out.println(cfile);
	        allCfiles.addAll(cfile);
	    }
		
		List<PackageDTO> pop = service.getAbroadPopList(cri);
		System.out.println(pop);
		
		for (PackageDTO pack : pop) {
		    long ppackagenum = pack.getPackagenum();
		    System.out.println("최신 :" + ppackagenum);
		    List<PackageFileDTO> pfile = service.getFiles(ppackagenum);
		    System.out.println(pfile);
		    allPfiles.addAll(pfile);
		}
	
//		List<PackageDTO> popguide = service.getPopularGuideList(cri);
		model.addAttribute("recent", recent);
		model.addAttribute("cheap", cheap);
		model.addAttribute("pop",pop);
//		model.addAttribute("popguide",popguide);
	    model.addAttribute("rfile", allRfiles);
	    model.addAttribute("cfile", allCfiles);
	    model.addAttribute("pfile", allPfiles);
	}
	
	
	@GetMapping(value={"plist","abroadlist"})
	public void list(Criteria cri, Model model) {
	    System.out.println(cri);
	    List<PackageFileDTO> allfiles = new ArrayList<>();
	    List<PackageDTO> list = service.getDetailRegionList(cri);
	    for (PackageDTO pack : list) {
		    long ppackagenum = pack.getPackagenum();
		    System.out.println("최신 :" + ppackagenum);
		    List<PackageFileDTO> lfile = service.getFiles(ppackagenum);
		    System.out.println(lfile);
		    allfiles.addAll(lfile);
		}
	    model.addAttribute("file", allfiles);
	    model.addAttribute("list", list);
	    model.addAttribute("pageMaker", new PageDTO(service.getTotal(cri), cri));
	}
	
	@GetMapping("pget")
	public String get(Criteria cri, long packagenum, HttpServletRequest req, HttpServletResponse resp, Model model) {
		System.out.println("packagenum: " + packagenum);
		String requestURI = req.getRequestURI();
		model.addAttribute("cri",cri);
		HttpSession session = req.getSession();
		PackageDTO pack = service.getDetail(packagenum);
		System.out.println(pack+"안뜨나?");
		model.addAttribute("package",pack);
		
		
		model.addAttribute("files",service.getFiles(packagenum));
		String loginUser = (String)session.getAttribute("loginUser");
		
		if(requestURI.contains("modify")) {
			return "board/modify";
		}
		return "package/pget";
	}
	
	
	@GetMapping("pay")
	public String pay(long packagenum, HttpServletRequest req, HttpServletResponse resp, Model model) {
		System.out.println("packagenum: " + packagenum);
		String requestURI = req.getRequestURI();
		HttpSession session = req.getSession();
		PackageDTO pack = service.getDetail(packagenum);
		model.addAttribute("package",pack);
		String loginUser = (String)session.getAttribute("loginUser");
		model.addAttribute("loginUser",loginUser);
		UserDTO user = service.getUser(loginUser);
		model.addAttribute("user",user);
		
		
		return "package/pay";
	}
	//추가
		@GetMapping("thumbnail")
		public ResponseEntity<Resource> thumbnail(String systemname) throws Exception {
			System.out.println(systemname);
			return service.getThumbnailResource(systemname);
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
	public String getMethodName(@RequestParam long packagenum, Model model) {
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
	
	@PostMapping(value="timelineRegist", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<TimelineDTO> tlregist(@RequestBody TimelineDTO tl){
		boolean result = service.tlregist(tl);
		System.out.println(result);
		if(result) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping(value="timelineList", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<TimelineDTO> regist(@RequestBody TimelineDTO tl){
		boolean result = service.tlregist(tl);
		System.out.println(result);
		if(result) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
