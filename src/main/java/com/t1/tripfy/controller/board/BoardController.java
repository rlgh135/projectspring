package com.t1.tripfy.controller.board;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.PageDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.service.board.BoardService;


@Controller
@RequestMapping("/board/*")
public class BoardController {
	
	@Autowired
	private BoardService service;

	@GetMapping("list")
	public void boardlist(@RequestParam(value = "sort", required = false) String sort, Criteria cri, Model model) {  // 정렬 방법
		System.out.println(cri);
		
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		
		if(sort != null && !sort.isEmpty()) {  // sort 파라미터 있는 경우(/board/list?sort="")
			
			switch (sort) {
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
		
		model.addAttribute("list", list);
		model.addAttribute("pageMaker", new PageDTO(service.getTotal(cri), cri));
	}
	
	@GetMapping("get")
	public void boardget(long boardnum, Model model) {  // boardnum, model
		System.out.println(boardnum);
		BoardDTO board = service.getDetail(boardnum);
		model.addAttribute("board", board);
	}
	
	@GetMapping("sort") 
	public String sortlist(@RequestParam("sort") String sort) {
		
		return "/board/list";
	}
}
