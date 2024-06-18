package com.t1.tripfy.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
	@RequestMapping(value="/")
	public String home(HttpServletRequest req, Model model) {
		if(req.getSession().getAttribute("loginUser")!=null) {
			return "redirect:/user/myinfo";
		}
		return "index";
	}
}
