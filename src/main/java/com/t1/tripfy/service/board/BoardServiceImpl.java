package com.t1.tripfy.service.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.mapper.board.BoardMapper;
import com.t1.tripfy.mapper.board.BoardReplyMapper;

@Service
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardMapper bmapper;
	
	@Autowired
	private BoardReplyMapper brmapper;
	
	
	@Override
	public BoardDTO getDetail(long boardnum) {
		return bmapper.getBoardByBoardNum(boardnum);
	}

	// 전체 게시글 긁어오기(최근)
	@Override
	public List<BoardDTO> getList(Criteria cri) {
		return bmapper.getList(cri);
	}

	// 총 게시글 개수
	@Override
	public long getTotal(Criteria cri) {
		return bmapper.getTotal(cri);
	}

	// 좋아요 순 게시글 긁어오기
	@Override
	public List<BoardDTO> getlikeList(Criteria cri) {
		return bmapper.getlikeList(cri);
	}

	// 인기 순 게시글 긁어오기
	@Override
	public List<BoardDTO> getpopularList(Criteria cri) {
		return bmapper.getpopularList(cri);
	}

	// boardnum으로 댓글 개수
	@Override
	public int getReplyCnt(long boardnum) {
		return brmapper.getReplyCnt(boardnum);
	}
}
