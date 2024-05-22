package com.t1.tripfy.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.service.user.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/user/*")
public class UserController {
	@Autowired
	private UserService service;

	// GET
//	user/join 
//	user/checkId 
//	user/logout
//	user/noAccount
//	user/myInfo
	
	// POST
//	user/login
//	user/join
	
	//get
	@GetMapping("join")
	public void replace() {}
	
	@GetMapping("checkId")
	@ResponseBody
	public String checkId(String userid) {
		if(service.checkId(userid)) {
			return "O";
		} else {
			return "X";
		}
	}
	
	@GetMapping("logout")
	public String logout(HttpServletRequest req) {
		req.getSession().invalidate();
		System.out.println("로그아웃");
		return "redirect:/";
	}
	
	@GetMapping("noAccount")
	public String noAccountLogin() {
		return "redirect:/user/myinfo";
	}
	
	@GetMapping("myinfo")
	public String myInfo(HttpServletRequest req, Model model) {
		HttpSession session = req.getSession();
		//썸네일 가져오기
		String loginUser = (String)session.getAttribute("loginUser");		
		String thumbnail = service.getProfileImgName(loginUser);
		System.out.println(thumbnail);
		
		model.addAttribute("thumbnail", thumbnail);
		
		UserDTO user = service.getUser(loginUser);
		
		model.addAttribute("user", user);
		
		//내가 쓴 게시글 추가하기
		
		//내가 쓴 패키지 추가하기
		
		//내가 받은 가이드 댓글 추가하기
		
		return "/user/myinfo";
	}
	
	@GetMapping("thumbnail")
	public ResponseEntity<Resource> thumbnail(String sysname) throws Exception {
		return service.getThumbnailResource(sysname);
	}
	
	//post
	@PostMapping("join")
	public String join(UserDTO user, HttpServletResponse resp) {
		if(service.join(user)) {
			Cookie cookie = new Cookie("joinid", user.getUserid());
			cookie.setPath("/user/myinfo");
			cookie.setMaxAge(60);
			resp.addCookie(cookie);
		} else {
			
		}
		return "redirect:/";
	}
	
	@PostMapping("login")
	public String login(String userid, String userpw, HttpServletRequest req) {
		HttpSession session = req.getSession();
		if(service.login(userid, userpw)) {
			session.setAttribute("loginUser", userid);
			session.setAttribute("userpfimg", service.getProfileImgName(userid));
			//가이드인지 확인하고 세션에 세팅
			GuideUserDTO guide = service.getGuideNum(userid);
			if(guide!=null) {
				session.setAttribute("guideNum", guide.getGuidenum());				
			} else {
				session.setAttribute("guideNum", 0);				
			}
			return "/user/myinfo";
		}
		else {
			//
			System.out.println("로그인 실패");
		}
		return "redirect:/";
	}
	
	@PostMapping("/user/changeimg")
	public String changeimg(MultipartFile thumbnail, HttpServletRequest req) {
		if(thumbnail.isEmpty()) {
			System.out.println("사진이 전송되지 않았습니다.");
			return "redirect:/";
		}
		
		HttpSession session = req.getSession();
		String userid = (String)session.getAttribute("loginUser");
		
		if(service.updateProfileimg(thumbnail, userid)) {
			session.setAttribute("userpfimg", service.getProfileImgName(userid));
			return "redirect:/user/myinfo";
		}
		
		return "redirect:/";
	}

}
