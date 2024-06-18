package com.t1.tripfy.controller.board;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.PageDTO;
import com.t1.tripfy.domain.dto.TimelineDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.domain.dto.board.BoardLikeDTO;
import com.t1.tripfy.domain.dto.board.BoardReplyDTO;
import com.t1.tripfy.domain.dto.board.BoardReplyPageDTO;
import com.t1.tripfy.domain.dto.board.BoardaddrDTO;
import com.t1.tripfy.domain.dto.user.GuideDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.service.board.BoardService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board/*")
public class BoardController {
	
	@Autowired
	private BoardService service;

	@GetMapping("list")
	public String boardlist(@RequestParam(value = "sort", required = false) String method, Criteria cri, Model model) {
		
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
		List<GuideDTO> guides = new ArrayList<GuideDTO>(); 
		
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
			
			// 서머노트로 작성한 content 이미지 태그 제거
			String content = list.get(i).getContent();
			String newContent = service.exceptImgTag(content);
			list.get(i).setContent(newContent);
			
			BoardFileDTO thumbnail = service.getThumbnail(boardnum);
			UserImgDTO profile = service.getUserProfile(userid);
			GuideDTO guide = service.getGuide(userid);
			
			guides.add(guide);
			
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
		model.addAttribute("guides", guides);
		
		System.out.println("thumbnails: " + thumbnails);
		System.out.println("profiles: " + profiles);
		System.out.println("pageMaker:" + new PageDTO(service.getTotal(cri), cri));
		System.out.println("guides: " + guides);
		
		return "board/list";
	}
	
	@GetMapping("sort")
	@ResponseBody
	public Map<String, Object> boardSort(@RequestParam(value = "sort", required = false) String method, Criteria cri, HttpServletRequest req, HttpServletResponse resp) {  // 정렬 방법
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
		List<GuideDTO> sortguides = new ArrayList<GuideDTO>(); 
		
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
			
			// 서머노트로 작성한 content 이미지 태그 제거
			String sortcontent = sortlist.get(i).getContent();
			String newsortContent = service.exceptImgTag(sortcontent);
			sortlist.get(i).setContent(newsortContent);
			
			BoardFileDTO sortthumbnail = service.getThumbnail(boardnum);
			UserImgDTO sortprofile = service.getUserProfile(userid);
			GuideDTO sortguide = service.getGuide(userid);
			
			sortguides.add(sortguide);
			
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

		
		// 최근 검색어를 위한 쿠키 생성
		if(cri.getKeyword() != null && !cri.getKeyword().isEmpty()) {
			String existingkeywords = "";
			Cookie[] cookies = req.getCookies();
			
			if(cookies != null) {
				for(Cookie cookie : cookies) {
					if(cookie.getName().equals("keywords")) {
						existingkeywords = cookie.getValue();
						break;
					}
				}
			}
			
			if(!existingkeywords.contains(cri.getKeyword())) {
				// 띄어쓰기 있는 경우 다른 문자열(--_--)로 대체
				existingkeywords += (existingkeywords.isEmpty() ? "" : "_-_") + cri.getKeyword().replace(" ", "--_--");
				 
				// 검색어 목록을 쿠키에 저장
		        Cookie keywordCookie = new Cookie("keywords", existingkeywords);
		        keywordCookie.setMaxAge(24 * 60 * 60); // 유효기간 1일
		        resp.addCookie(keywordCookie);
			}
			
		}
		
		Map<String, Object> data = new HashMap<>();
		data.put("sortlist", sortlist);
		data.put("pageMaker", new PageDTO(service.getTotal(cri), cri));
		data.put("sortValue", method);
		data.put("sortthumbnails", sortthumbnails);
		data.put("sortprofiles", sortprofiles);
		data.put("sortguides", sortguides);
		
		System.out.println(data);
		
		return data;
	}
	
	
	@GetMapping(value={"get", "modify"})
	public String boardget(long boardnum, HttpServletRequest req, HttpServletResponse resp, Model model) {
		String requestURI = req.getRequestURI();
		HttpSession session = req.getSession();
		String loginUser = (String) session.getAttribute("loginUser");

	    BoardDTO board = service.getDetail(boardnum);
	    String userid = board.getUserid();
	    BoardLikeDTO boardLike = service.getBoardLike(loginUser, boardnum);
	    GuideDTO guide = service.getGuide(userid);
	    
	    UserImgDTO userprofile = service.getUserProfile(userid);
	    
	    if(userprofile == null) {
	    	userprofile = new UserImgDTO();
	    	userprofile.setUserid(userid);
	    	userprofile.setSysname("profile.png");
	    }
	    
	    // int replyCnt = service.getReplyCnt(boardnum);

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
	        //model.addAttribute("replyCnt", replyCnt);
	        model.addAttribute("files", files);
	        model.addAttribute("thumbnailImg", thumbnailImg);
			return "board/modify";
		}
	    
	    model.addAttribute("board", board);
	    //model.addAttribute("replyCnt", replyCnt);
	    model.addAttribute("files", files);
	    model.addAttribute("thumbnailImg", thumbnailImg);
	    model.addAttribute("userprofile", userprofile);
	    model.addAttribute("boardLike", boardLike);
	    model.addAttribute("guide", guide);
	    System.out.println("userprofile: " + userprofile);
	    System.out.println("boardLike: " + boardnum + ", " + boardLike);
	    
	    
	    return "board/get";
	}
	
	@PostMapping("modify")
	public String modify(BoardDTO board, BoardaddrDTO boardaddr, MultipartFile[] files, String updateCnt) throws Exception {
		if(service.modifyBoard(board, boardaddr, files, updateCnt)) {
			return "redirect:/board/get?boardnum=" + board.getBoardnum();
		}
		else {  			
			return "redirect:/board/get?boardnum" + board.getBoardnum();
		}
	}
	
	
	@GetMapping("write")
	public String boardwrite() {return "/board/write";}
	
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
	

	@PutMapping("like")
	@ResponseBody
	public Map<String, Object> boardlike(HttpServletRequest req, long boardnum) {
		String userid = (String)req.getSession().getAttribute("loginUser");
		System.out.println("boardnum: " + boardnum);
		
		BoardLikeDTO boardlike = null;
		BoardDTO board = null;
		
		// 좋아요 클릭 혹은 취소 관련
		if(service.likeClick(userid, boardnum)) {  // 성공
			// 해당 게시글에 해당 유저가 좋아요 유무 -> 객체 O 또는 X
			boardlike = service.getBoardLike(userid, boardnum);
			board = service.getDetail(boardnum);
			
		}
		
		Map<String, Object> data = new HashMap<>();
		data.put("boardlike", boardlike);
		data.put("board", board);
		
		return data;
	}

	@PostMapping(value="replyRegist", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<BoardReplyDTO> replyRegist(@RequestBody BoardReplyDTO reply){
		
		BoardReplyDTO result = service.replyRegist(reply);
		if(result == null) {
			return new ResponseEntity<BoardReplyDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			return new ResponseEntity<BoardReplyDTO>(result,HttpStatus.OK);
		}
	}
	
	@GetMapping(value="replyList", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<BoardReplyPageDTO> timeLineDayList(@RequestParam long boardnum, @RequestParam int pagenum){
	    BoardReplyPageDTO result = service.getReplyList(boardnum, pagenum);
        return new ResponseEntity<BoardReplyPageDTO>(result, HttpStatus.OK);
	}
	
	@PostMapping(value="replyModify", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<BoardReplyDTO> replyModify(@RequestBody BoardReplyDTO reply, HttpServletRequest req){
		HttpSession session = req.getSession();
		String loginUser = (String) session.getAttribute("loginUser");
		long replynum = reply.getReplynum();
		if(!service.getReplyByReplyNum(replynum).getUserid().equals(loginUser)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}else {
			if (service.replyModify(reply)) {
				 return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}			
		}
	}
	
	@PostMapping(value="replyDelete", consumes = "application/json", produces = "application/json;charset=utf-8")
	public ResponseEntity<BoardReplyDTO> deleteReply(@RequestBody BoardReplyDTO reply, HttpServletRequest req) {
		HttpSession session = req.getSession();
		String loginUser = (String) session.getAttribute("loginUser");
		long replynum = reply.getReplynum();
		if(!service.getReplyByReplyNum(replynum).getUserid().equals(loginUser)) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}else {
		    if (service.deleteReply(reply)) {
		        return new ResponseEntity<>(HttpStatus.OK);
		    } else {
		        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		    }
		}
	}
	
	@PutMapping("getReplyCnt")
	public ResponseEntity<Integer> getReplyCnt(@RequestParam long boardnum) {
		int replycnt = service.getReplyCnt(boardnum);
		return ResponseEntity.ok(replycnt);
	}
	
}
