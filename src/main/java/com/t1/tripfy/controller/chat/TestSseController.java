package com.t1.tripfy.controller.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ssetest/*")
public class TestSseController {
	@GetMapping("sse_test")
	public String sse_test() {
		return "/test/sse_test.html";
	}
}
