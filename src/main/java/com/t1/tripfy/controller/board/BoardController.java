package com.t1.tripfy.controller.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.t1.tripfy.domain.dto.board.BoardaddrDTO;
import com.t1.tripfy.service.board.BoardService;
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
		System.out.println(cri);
		System.out.println("sort: " + method);
		
		model.addAttribute("sort", method);
		model.addAttribute("list", list);
		model.addAttribute("pageMaker", new PageDTO(service.getTotal(cri), cri));
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
		
		Map<String, Object> data = new HashMap<>();
		data.put("sortlist", sortlist);
		data.put("pageMaker", new PageDTO(service.getTotal(cri), cri));
		data.put("sortValue", method);
		
		System.out.println(data);
		
		return data;
	}
	
	
	@GetMapping("get")
	public void boardget(long boardnum, Model model) {
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
	    
	    model.addAttribute("board", board);
	    model.addAttribute("replyCnt", replyCnt);
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
}
