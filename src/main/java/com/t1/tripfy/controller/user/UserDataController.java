package com.t1.tripfy.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.domain.dto.board.BoardLikeDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/userdata/*")
public class UserDataController {
	@Autowired
	private UserService service;
	
	@GetMapping("myboard")
	@ResponseBody
	public Map<String, Object> getMyBoard(HttpServletRequest req, Criteria cri){
		Map<String, Object> datas = new HashMap<>();
		int page = Integer.parseInt(req.getParameter("page"));
		String userid = req.getParameter("userid");
		cri = new Criteria(page, 6);
		String jjim = req.getParameter("jjim");
		
		String userthumbnail = service.getProfileImgName(userid);
		datas.put("thumb", userthumbnail);
		
		List<BoardFileDTO> bflist = new ArrayList<>();
		if(jjim.equals("no")) {
			List<BoardDTO> boardlist = service.getMyBoardList(cri, userid);
			datas.put("boardlist", boardlist);
			
			if(boardlist.size()>0) {
				for (BoardDTO board : boardlist) {
					BoardFileDTO boardthumb = service.getMyBoardThumbnail(board.getBoardnum());
					if(boardthumb == null) {
						BoardFileDTO boardfile = new BoardFileDTO();
						boardfile.setBoardnum(board.getBoardnum());
						boardfile.setSysname("no_img.jpg");
						bflist.add(boardfile);
					} else {
						bflist.add(boardthumb);
					}
				}
			}
		} else if(jjim.equals("yes")) {
			List<BoardDTO> boardlist = service.getLikeBoardList(cri ,userid);
			datas.put("boardlist", boardlist);
			
			if(boardlist.size()>0) {
				for (BoardDTO board : boardlist) {
					BoardFileDTO boardthumb = service.getMyBoardThumbnail(board.getBoardnum());
					if(boardthumb == null) {
						BoardFileDTO boardfile = new BoardFileDTO();
						boardfile.setBoardnum(board.getBoardnum());
						boardfile.setSysname("no_img.jpg");
						bflist.add(boardfile);
					} else {
						bflist.add(boardthumb);
					}
				}
			}
		} else {
			System.out.println("Wrong param");
		}
		
		datas.put("bflist", bflist);
		
		return datas;
	}
	
	@GetMapping("joinpackage")
	@ResponseBody
	public Map<String, Object> getMyJoinPack(HttpServletRequest req, Criteria cri){
		Map<String, Object> datas = new HashMap<>();
		int page = Integer.parseInt(req.getParameter("page"));
		String userid = req.getParameter("userid");
		cri = new Criteria(page, 6);
		String when = req.getParameter("when");
		
		List<PackageFileDTO> pflist = new ArrayList<>();
		List<ReservationDTO> reslist = new ArrayList<>();
		if(when.equals("before")) {
			List<PackageDTO> packagelist = service.getReadyPack(cri, userid);
			if(packagelist.size()>0) {
				for (PackageDTO pack : packagelist) {
					pflist.add(service.getPackThumbnail(pack.getPackagenum()));
					reslist.add(service.getResevationByIdPackagenum(userid, pack.getPackagenum()));
				}
			}
			
			datas.put("packagelist", packagelist);
			
		} else if(when.equals("after")) {
			List<PackageDTO> packagelist = service.getDonePack(cri, userid);
			if(packagelist.size()>0) {
				for (PackageDTO pack : packagelist) {
					pflist.add(service.getPackThumbnail(pack.getPackagenum()));
					reslist.add(service.getResevationByIdPackagenum(userid, pack.getPackagenum()));
				}
			}
			
			datas.put("packagelist", packagelist);
			
		} else {
			System.out.println("Wrong param");
		}
		
		datas.put("pflist", pflist);
		datas.put("reslist", reslist);
		
		return datas;
	}
	
	@GetMapping("mypackage")
	@ResponseBody
	public Map<String, Object> getMyPack(HttpServletRequest req, Criteria cri){
		Map<String, Object> datas = new HashMap<>();
		String guideid = req.getParameter("guideid");
		int page = Integer.parseInt(req.getParameter("page"));
		cri = new Criteria(page, 6);
		String when = req.getParameter("when");
		
		List<PackageFileDTO> pflist = new ArrayList<>();
		ArrayList<Integer> rescnt = new  ArrayList<>();
		if(when.equals("after")) {
			List<PackageDTO> packagelist = service.getGuideReadyPack(cri, guideid);
			
			if(packagelist.size()>0) {
				for (PackageDTO pack : packagelist) {
					pflist.add(service.getPackThumbnail(pack.getPackagenum()));
					rescnt.add(service.getTotalResCnt(pack.getPackagenum()));
				}
			}
			datas.put("packagelist", packagelist);
		} else if (when.equals("before")) {
			List<PackageDTO> packagelist = service.getGuideDonePack(cri, guideid);
			
			if(packagelist.size()>0) {
				for (PackageDTO pack : packagelist) {
					pflist.add(service.getPackThumbnail(pack.getPackagenum()));
					rescnt.add(service.getTotalResCnt(pack.getPackagenum()));
				}
			}
			datas.put("packagelist", packagelist);
		} else {
			System.out.println("wrong param");
		}
		
		datas.put("pflist", pflist);
		datas.put("rescnt", rescnt);
		
		return datas;
	}
	
	@GetMapping("myreview")
	@ResponseBody
	public Map<String, Object> getMyReview(HttpServletRequest req, Criteria cri){
		Map<String, Object> datas = new HashMap<>();
		String userid = req.getParameter("userid");
		int page = Integer.parseInt(req.getParameter("page"));
		cri = new Criteria(page, 6);
		String who = req.getParameter("who");
		
		List<String> thums = new ArrayList<>();
		if(who.equals("me")) {
			List<ReviewDTO> reviews = service.getMyReviews(cri, userid);
			if(reviews.size()>0) {
				for(ReviewDTO review : reviews) {
					thums.add(service.getProfileImgName(review.getUserid()));
				}
			}
			datas.put("reviews", reviews);
			datas.put("thums", thums);
			
		} else if (who.equals("others")) {
			System.out.println(userid);
			if(service.getGuideNum(userid)!=null) {
				datas.put("isg", "yes");
				
				
				List<ReviewDTO> reviews = service.getMineReviews(cri, userid);
				if(reviews.size()>0) {
					for(ReviewDTO review : reviews) {
						thums.add(service.getProfileImgName(review.getUserid()));
					}
				}
				
				datas.put("reviews", reviews);
				datas.put("thums", thums);
				
			} else {
				datas.put("isg", "no");
			}
		} else {
			System.out.println("wrong param");
		}
		return datas;
	}
	
	@GetMapping("insertdummy")
	public String insertDummy() {
		if(service.insertDummy()) {
			return "성공";
		}
		return "실패";
	}
}
