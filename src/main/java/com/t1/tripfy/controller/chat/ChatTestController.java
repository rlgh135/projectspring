package com.t1.tripfy.controller.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.t1.tripfy.domain.dto.user.GuideDTO;
import com.t1.tripfy.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chattest/*")
public class ChatTestController {
	@Autowired
	private UserService service;
	
	@GetMapping("sse_test")
	public String sse_test() {
		return "/test/sse_test.html";
	}
	@GetMapping("chat_test")
	public String chat_test(@SessionAttribute(name="loginUser", required=false) String loginUserId,
			Model model) {
		
		System.out.println("loginUser = " + loginUserId);
		
		if(loginUserId == null) {
			return "redirect:/";
		}
		
		model.addAttribute("loginUser", loginUserId);
		
		return "test/chat_test.html";
	}
	@GetMapping("chat_test_02")
	public String chat_test_02() {
		return "/test/chat_test02.html";
	}
	@GetMapping("chat_test_03")
	public String chat_test_03() {
		return "/test/chat_test03.html";
	}
	
	@GetMapping("test_login")
	public ResponseEntity<Object> test_login(
			@RequestParam String userid,
			@RequestParam String userpw,
			HttpServletRequest req
	) {
		HttpSession session = req.getSession();
		
		if(service.login(userid, userpw)) {
			session.setAttribute("loginUser", userid);
			session.setAttribute("userpfimg", service.getProfileImgName(userid));
			//가이드인지 확인하고 세션에 세팅
			GuideDTO guide = service.getGuideNum(userid);
			if(guide!=null) {
				session.setAttribute("guideNum", guide.getGuidenum());				
			} else {
				session.setAttribute("guideNum", 0);				
			}
			
			System.out.println("login 가이드검사 : " +session.getAttribute("guideNum"));
			return ResponseEntity.status(HttpStatus.ACCEPTED).build();
		}
		System.out.println("로그인 실패");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}
}
