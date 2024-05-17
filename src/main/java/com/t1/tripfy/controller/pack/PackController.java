package com.t1.tripfy.controller.pack;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.PageDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
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
		List<PackageDTO> cheap = service.getCheapList(cri);
		List<PackageDTO> pop = service.getPopList(cri);
//		List<PackageDTO> popguide = service.getPopularGuideList(cri);
		model.addAttribute("recent", recent);
		model.addAttribute("cheap", cheap);
		model.addAttribute("pop",pop);
//		model.addAttribute("popguide",popguide);
	}
	
	@GetMapping("plist")
	public void list(Criteria cri, Model model) {
		System.out.println(cri);
		List<PackageDTO> list = service.getListByCountryCode(cri);
		model.addAttribute("list",list);
		model.addAttribute("pageMaker",new PageDTO(service.getTotal(cri),cri));
	}
	
	@GetMapping("pget")
	public String get(Criteria cri, long packagenum, HttpServletRequest req, HttpServletResponse resp, Model model) {
		String reuqestURI = req.getRequestURI();
		model.addAttribute("cri",cri);
		HttpSession session = req.getSession();
		PackageDTO pack = service.getDetail(packagenum);
		model.addAttribute("package",pack);
		//model.addAttribute("files",service.getFiles(boardnum));
		String loginUser = (String)session.getAttribute("loginUser");
		return "package/pget";
	}
	

}
