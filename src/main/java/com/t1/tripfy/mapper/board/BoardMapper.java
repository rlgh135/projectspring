package com.t1.tripfy.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.board.BoardDTO;

@Mapper
public interface BoardMapper {
	// boardnum으로 게시글 데이터 긁어오기
	BoardDTO getBoardByBoardNum(long boardnum);
	
	// 전체 게시글 긁어오기
	List<BoardDTO> getList(Criteria cri);

	// 
	long getTotal(Criteria cri);

	// 좋아요 순 게시글 긁어오기
	List<BoardDTO> getlikeList(Criteria cri);
	
	// 인기 순 게시글 긁어오기
	List<BoardDTO> getpopularList(Criteria cri);

}
