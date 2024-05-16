package com.t1.tripfy.service.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.mapper.board.BoardMapper;

@Service
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardMapper bmapper;
	
	@Override
	public BoardDTO getDetail(long boardnum) {
		return bmapper.getBoardByBoardNum(boardnum);
	}

	// 전체 게시글 긁어오기(최근)
	@Override
	public List<BoardDTO> getList(Criteria cri) {
		return bmapper.getList(cri);
	}

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
}
