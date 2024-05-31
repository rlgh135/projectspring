package com.t1.tripfy.controller.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.PageDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.domain.dto.board.BoardaddrDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.service.board.BoardService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMethod;



@Controller
@RequestMapping("/board/*")
public class BoardController {
	
	@Autowired
	private BoardService service;

	@GetMapping("list")
	public void boardlist(@RequestParam(value = "sort", required = false) String method, Criteria cri, Model model) {
		
		if(cri.getKeyword() == "") {
			cri.setKeyword(null);
		}
		
		if(cri.getRegionname() == "") {
			cri.setRegionname(null);
		}
		
		if(cri.getCountrycode() == "") {
			cri.setCountrycode(null);
		}
		
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		List<BoardFileDTO> thumbnails = new ArrayList<BoardFileDTO>();
		List<UserImgDTO> profiles = new ArrayList<UserImgDTO>();
		
		if(method != null && !method.isEmpty()) {  // sort 파라미터 있는 경우(/board/list?sort="")
			
			switch (method) {
			case "recent": 
				list = service.getList(cri);
				
				break;
			
			case "popular":
				list = service.getpopularList(cri);
			
				break;
				
			case "like":
				list = service.getlikeList(cri);
				
				break;
			}
		}
		
		else {  // sort 파라미터 없을 경우(/board/list 요청 시)
			list = service.getList(cri);
		}
		
		// 게시글 썸네일 및 유저 프로필 사진
		for(int i = 0; i < list.size(); i++) {
			long boardnum = list.get(i).getBoardnum();
			String userid = list.get(i).getUserid();
			
			BoardFileDTO thumbnail = service.getThumbnail(boardnum);
			UserImgDTO profile = service.getUserProfile(userid);
			
			if(thumbnail == null) {
				BoardFileDTO boardfile = new BoardFileDTO();
				boardfile.setBoardnum(boardnum);
				boardfile.setSysname("no_img.jpg");
				thumbnails.add(boardfile);
			}
			
			else {					
				thumbnails.add(thumbnail);
			}
			
			if(profile == null) {
				UserImgDTO defaultImg = new UserImgDTO();
				defaultImg.setUserid(userid);
				defaultImg.setSysname("profile.png");
				profiles.add(defaultImg);
			}
			
			else {
				profiles.add(profile);
			}
		}
		
		System.out.println(cri);
		System.out.println("sort: " + method);
		
		model.addAttribute("sort", method);
		model.addAttribute("list", list);
		model.addAttribute("pageMaker", new PageDTO(service.getTotal(cri), cri));
		model.addAttribute("thumbnails", thumbnails);
		model.addAttribute("profiles", profiles);
		
		System.out.println("thumbnails: " + thumbnails);
		System.out.println("profiles: " + profiles);
		System.out.println("pageMaker:" + new PageDTO(service.getTotal(cri), cri));
	}
	
	@GetMapping("sort")
	@ResponseBody
	public Map<String, Object> boardSort(@RequestParam(value = "sort", required = false) String method, Criteria cri) {  // 정렬 방법
		System.out.println("sort: " + method);
		System.out.println(cri.getKeyword());
		
		if(cri.getKeyword() == "") {
			cri.setKeyword(null);
		}
		
		if(cri.getRegionname() == "") {
			cri.setRegionname(null);
		}
		
		if(cri.getCountrycode() == "") {
			cri.setCountrycode(null);
		}
		
		System.out.println(cri);
		
		List<BoardDTO> sortlist = new ArrayList<BoardDTO>();
		List<BoardFileDTO> sortthumbnails = new ArrayList<BoardFileDTO>();
		List<UserImgDTO> sortprofiles = new ArrayList<UserImgDTO>();
		
		if(method != null && !method.isEmpty()) {  // sort 파라미터 있는 경우(/board/list?sort="")
			
			switch (method) {
			case "recent": 
				sortlist = service.getList(cri);
				
				break;
			
			case "popular":
				sortlist = service.getpopularList(cri);
				
				break;
				
			case "like":
				sortlist = service.getlikeList(cri);
				
				break;
			}
		}
		
		else {  // sort 파라미터 없을 경우(/board/list 요청 시)
			sortlist = service.getList(cri);
		}
		
		// 게시글 썸네일 및 유저 프로필 사진
		for(int i = 0; i < sortlist.size(); i++) {
			long boardnum = sortlist.get(i).getBoardnum();
			String userid = sortlist.get(i).getUserid();
			
			BoardFileDTO sortthumbnail = service.getThumbnail(boardnum);
			UserImgDTO sortprofile = service.getUserProfile(userid);
			
			if(sortthumbnail == null) {
				BoardFileDTO sortboardfile = new BoardFileDTO();
				sortboardfile.setBoardnum(boardnum);
				sortboardfile.setSysname("no_img.jpg");
				sortthumbnails.add(sortthumbnail);
			}
			
			else {					
				sortthumbnails.add(sortthumbnail);
			}
			
			if(sortprofile == null) {
				UserImgDTO sortdefaultImg = new UserImgDTO();
				sortdefaultImg.setUserid(userid);
				sortdefaultImg.setSysname("profile.png");
				sortprofiles.add(sortdefaultImg);
			}
			
			else {
				sortprofiles.add(sortprofile);
			}
		}
		
		Map<String, Object> data = new HashMap<>();
		data.put("sortlist", sortlist);
		data.put("pageMaker", new PageDTO(service.getTotal(cri), cri));
		data.put("sortValue", method);
		data.put("sortthumbnails", sortthumbnails);
		data.put("sortprofiles", sortprofiles);
		
		System.out.println(data);
		
		return data;
	}
	
	
	@GetMapping(value={"get", "modify"})
	public String boardget(long boardnum, HttpServletRequest req, HttpServletResponse resp, Model model) {
		String requestURI = req.getRequestURI();
		HttpSession session = req.getSession();
		String loginUser = (String) session.getAttribute("loginUser");

	    BoardDTO board = service.getDetail(boardnum);
	    int replyCnt = service.getReplyCnt(boardnum);

	    BoardaddrDTO boardaddr = service.getBoardAddr(boardnum);
	    if (boardaddr != null) {
	    	if(boardaddr.getStartdate() != null && boardaddr.getEnddate() != null) {
	    		int days = service.getDays(boardaddr.getStartdate(),boardaddr.getEnddate());
	    		model.addAttribute("days",days);
	    	}
	        model.addAttribute("boardaddr", boardaddr);
	    }
	    
	    List<BoardFileDTO> files = service.getFiles(boardnum);
	    
	    // 게시글 맨 위 이미지
	    BoardFileDTO thumbnailImg = service.getThumbnail(boardnum);
	    
	    if(thumbnailImg == null) {
	    	thumbnailImg = new BoardFileDTO();
	    	thumbnailImg.setSysname("no_img.jpg");
	    }
	    
	    // 조회수
	    if(requestURI.contains("get")) {	    	
	    	if(!board.getUserid().equals(loginUser)) {
	    		Cookie[] cookies = req.getCookies();
	    		Cookie read_board = null;
	    		
	    		if(cookies != null) {
	    			for(Cookie cookie : cookies) {
	    				//ex) 1번 게시글을 조회하고자 클릭했을 때에는 "read_board1" 쿠키를 찾음
	    				if(cookie.getName().equals("read_board"+boardnum)) {
							read_board = cookie;
							break;
						}
	    			}
	    		}
	    		
	    		//read_board가 null이라는 뜻은 위에서 쿠키를 찾았을 때 존재하지 않았다는 뜻
				//첫 조회거나 조회한지 1시간이 지난 후
				if(read_board == null) {
					service.increaseViewCount(boardnum);
					board.setViewcnt(board.getViewcnt() + 1);  // get.html에서도 조회수 업데이트
					
					//read_board1 이름의 쿠키(유효기간:1800초(30분))를 생성해서 클라이언트에 저장
					Cookie cookie = new Cookie("read_board"+boardnum, "r");
					cookie.setMaxAge(1800);
					resp.addCookie(cookie);
				}
	    	}
	    }
	    
	    if(requestURI.contains("modify")) {
	    	model.addAttribute("board", board);
	        model.addAttribute("replyCnt", replyCnt);
	        model.addAttribute("files", files);
	        model.addAttribute("thumbnailImg", thumbnailImg);
			return "board/modify";
		}
	    
	    model.addAttribute("board", board);
	    model.addAttribute("replyCnt", replyCnt);
	    model.addAttribute("files", files);
	    model.addAttribute("thumbnailImg", thumbnailImg);
	    
	    return "board/get";
	}

	
	@GetMapping("write")
	public void boardwrite() {}
	
	@PostMapping("write")
	public String insertBoard(BoardDTO board, BoardaddrDTO boardaddr, MultipartFile[] files, Criteria cri) throws Exception {
		if(service.insertBoard(board, boardaddr, files)) {
			long boardnum = service.getLastNum(board.getUserid());  // 해당 userid로 작성된 마지막 게시글의 번호
			return "redirect:/board/get?boardnum=" + boardnum;
		}
		
		else {  // 실패			
			return "redirect:/board/list";
		}
	}
	@PostMapping("SummerNoteImageFile")
	public @ResponseBody String SummerNoteImageFile(@RequestParam("file") MultipartFile file) throws Exception {
	    String path = service.SummerNoteImageFile(file);
	    return path;
	}
	

	@GetMapping("download")
	public ResponseEntity<Resource> download(String sysname, String orgname) throws Exception {
		return service.downloadFile(sysname, orgname);
	}
	
	@GetMapping("remove")
	public String remove(long boardnum, HttpServletRequest req) {
		// System.out.println("boardnum: " + boardnum);
		
		String loginUser = (String)req.getSession().getAttribute("loginUser");
		BoardDTO board = service.getDetail(boardnum);
		
		// System.out.println("board User: " + board.getUserid());
		
		if(loginUser.equals(board.getUserid())) {
			
			// System.out.println("true or false: " + service.remove(boardnum));
			
			if(service.remove(boardnum)) {
				return "redirect:/board/list";
			}
		}
		return "redirect:/board/get?boardnum="+boardnum;
	}
	

	@PostMapping("deleteSummernoteImageFile")
	public @ResponseBody String deleteSummernoteImageFile(@RequestParam("file") String fileUrl) {
		String result = "";
	    if(service.deleteSummernoteImageFile(fileUrl)) {
	    	result = "성공";
	    }else {
	    	result = "실패";
	    }
	    return result;
	}
	
	@GetMapping("thumbnail")
	public ResponseEntity<Resource> thumbnail(String sysname) throws Exception {
		System.out.println(sysname);
		return service.getThumbnailResource(sysname);
	}

}
