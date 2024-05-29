package com.t1.tripfy.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.domain.dto.board.BoardaddrDTO;

@Mapper
public interface BoardMapper {
	// boardnum으로 게시글 데이터 긁어오기
	BoardDTO getBoardByBoardNum(long boardnum);
	
	// 전체 게시글 긁어오기
	List<BoardDTO> getList(Criteria cri);
	List<BoardDTO> getMyList(Criteria cri, String userid);

	// 전체 게시글 개수
	long getTotal(Criteria cri);

	// 좋아요 순 게시글 긁어오기
	List<BoardDTO> getlikeList(Criteria cri);
	
	// 인기 순 게시글 긁어오기
	List<BoardDTO> getpopularList(Criteria cri);

	//댓글 수 업다운
	int addReplyCnt(long boardnum);
	int reduceReplyCnt(long boardnum);
	
	// 게시글 등록(insert)
	int insertBoard(BoardDTO board);

	// 특정 userid로 작성된 게시글 번호 중 마지막 번호
	long getLastNum(String userid);
	
	// ----- 파일 -----
	// C
	int insertFile(BoardFileDTO file);
	
	// R
	BoardFileDTO getFileBySystemname(String systemname);
	List<BoardFileDTO> getFiles(long boardnum);
	
	// D
	int deleteFilesBySystemname(String systemname);
	int deleteFilesByBoardnum(long boardnum);

	BoardaddrDTO getBoardaddrByBoardnum(long boardnum);
}
