package com.t1.tripfy.controller.pack;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.PageDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.mapper.user.UserMapper;
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
	public void main(Criteria cri,Model model) {
		List<PackageDTO> recent = service.getRecentList(cri);
		System.out.println(recent);
		List<PackageDTO> cheap = service.getCheapList(cri);
		System.out.println(cheap);
		List<PackageDTO> pop = service.getPopList(cri);
		System.out.println(pop);
//		List<PackageDTO> popguide = service.getPopularGuideList(cri);
		model.addAttribute("recent", recent);
		model.addAttribute("cheap", cheap);
		model.addAttribute("pop",pop);
//		model.addAttribute("popguide",popguide);
	}
	@GetMapping("abroadmain")
	public void abroadmain(Criteria cri,Model model) {
		List<PackageDTO> recent = service.getAbroadRecentList(cri);
		System.out.println(recent);
		List<PackageDTO> cheap = service.getAbroadCheapList(cri);
		System.out.println(cheap);
		List<PackageDTO> pop = service.getAbroadPopList(cri);
		System.out.println(pop);
//		List<PackageDTO> popguide = service.getPopularGuideList(cri);
		model.addAttribute("recent", recent);
		model.addAttribute("cheap", cheap);
		model.addAttribute("pop",pop);
//		model.addAttribute("popguide",popguide);
	}
	
	
	@GetMapping(value={"plist","abroadlist"})
	public void list(Criteria cri, Model model) {
	    System.out.println(cri);
	    List<PackageDTO> list = service.getDetailRegionList(cri);
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
		//model.addAttribute("files",service.getFiles(boardnum));
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
		else {
			return "redirect:/index";
		}
	}
	
	@GetMapping("tlwrite")
	public String getMethodName(@RequestParam String packagenum, Model model) {
		model.addAttribute("packagenum", packagenum);
		return "/package/ptimeline";
	}
}
