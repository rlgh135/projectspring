package com.t1.tripfy.controller.user;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Controller
@RequestMapping("/sendsms/*")
public class SMSController {

	@PostMapping("checker")
	@ResponseBody
	public String sendCheckString(HttpServletRequest req) {
		String reciever = req.getParameter("phonenumber");
		System.out.println(reciever);
		
		//6자리 노중복 대소숫 문자열
		String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		
		Set<Character> usedChar = new HashSet<>();
		
		StringBuilder builder = new StringBuilder();
		String targetString = "";
		
		while(usedChar.size()<6) {
			int idx =random.nextInt(charSet.length());
			char randomchar = charSet.charAt(idx);
			
			if(!usedChar.contains(randomchar)) {
				usedChar.add(randomchar);
				builder.append(randomchar);
			}
		}
		
		targetString = builder.toString();
		System.out.println("target String: " + targetString);
//		String api_key="YOUR_API_KEY";
		String api_key="NCSXBY2OUL7CFHH9";
//		String api_secret="YOUR_API_SECREAT_KEY";
		String api_secret="QG2XTXL6YOVJ3OJYS1CN7ORCSLRLV4EU";
		
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(api_key, api_secret, "https://api.coolsms.co.kr");
		Message message = new Message();
//		message.setFrom("YOUR_PHONE_NUM");
		message.setFrom("01028946094");
		message.setTo(reciever);
//		message.setTo("01028946094");
		message.setText("인증번호는 ["+targetString+"] 입니다.");
		
		try {
			messageService.send(message);
			System.out.println("성공");
			return targetString;
		} catch (NurigoMessageNotReceivedException e) {
			System.out.println(e.getFailedMessageList());
			System.out.println(e.getMessage());
			return "fail";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "fail";
		}
		
	}
	
}
