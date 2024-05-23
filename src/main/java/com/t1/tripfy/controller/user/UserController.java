package com.t1.tripfy.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.PageDTO;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.service.user.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	public String myInfo(HttpServletRequest req, Model model, Criteria cri) {
		HttpSession session = req.getSession();
		//썸네일 가져오기
		String loginUser = (String)session.getAttribute("loginUser");
		String thumbnail = service.getProfileImgName(loginUser);
		System.out.println(thumbnail);
		
		model.addAttribute("thumbnail", thumbnail);
		
		UserDTO user = service.getUser(loginUser);
		
		model.addAttribute("user", user);
		
		//내가 쓴 게시글 추가하기
		cri = new Criteria(1, 6);
		List<BoardDTO> mylist = service.getMyBoardList(cri, loginUser);
		System.out.println("마이인포: "+mylist.size());
		model.addAttribute("list",mylist);
		model.addAttribute("pageMaker",new PageDTO(service.getMyTotal(cri), cri));
		//내가 쓴 패키지 추가하기
		
		//내가 받은 가이드 댓글 추가하기
		
		return "/user/myinfo";
	}
	
	@GetMapping("thumbnail")
	public ResponseEntity<Resource> thumbnail(String sysname) throws Exception {
		return service.getThumbnailResource(sysname);
	}
	
	@GetMapping("myboard")
	@ResponseBody
	public List<BoardDTO> getMyBoard(Criteria cri,HttpServletRequest req){
		cri = new Criteria(1, 6);
		String loginuser = (String)req.getSession().getAttribute("loginUser");
		System.out.println("마이보드: "+service.getMyBoardList(cri, loginuser).size());
		return service.getMyBoardList(cri, loginuser);
	}
	
	@GetMapping("mypackage")
	@ResponseBody
	public Map<String, Object> getMyPackage(Criteria cri, HttpServletRequest req){
		cri = new Criteria(1, 6);
		String userid = (String)req.getAttribute("loginUser");

		List<ReservationDTO> myreservation = service.getMyReservation(cri, userid);
		
		List<PackageDTO> joinpackage = null;
		Map<String, Object> datas = new HashMap<>();

		if(myreservation.size()>0) {
			for (ReservationDTO reservation : myreservation) {
				joinpackage.add(service.getJoinPackage(reservation.getPackagenum()));
			}			
			datas.put("myreservation", myreservation);
			datas.put("joinpackage", joinpackage);
		} else {
			datas.put("joinpackage", joinpackage);
			datas.put("nodata", "참여한 패키지가 없어요");
		}
		

		return datas;

	}
	
	@GetMapping("/user/sogae")
	public String changeSogae(HttpServletRequest req, String introduce) {
		String userid = (String)req.getSession().getAttribute("loginUser");
		String backuri = req.getHeader("Referer");
		System.out.println("돌아가는 경로:"+backuri);
		if(service.changeSogae(userid, introduce)) {
			return "redirect:"+backuri;
		} else {
			return "redirect:/";
		}
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
		String backuri = req.getHeader("Referer");
		if(backuri.equals("http://localhost:8080/")) {
			backuri = "/user/myinfo";
		}
		
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
			
			System.out.println("login 가이드검사 : " +session.getAttribute("guideNum"));
			return "redirect:"+backuri;
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
