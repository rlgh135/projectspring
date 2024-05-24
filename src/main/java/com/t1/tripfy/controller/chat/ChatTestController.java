package com.t1.tripfy.controller.chat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/chattest/*")
public class ChatTestController {
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
		
		return "/test/chat_test.html";
	}
}
