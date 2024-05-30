package com.t1.tripfy.service.board;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.domain.dto.board.BoardaddrDTO;

public interface BoardService {
	
	// boardnum으로 게시글 데이터 긁어오기
	BoardDTO getDetail(long boardnum);
	
	// 전체 게시글 긁어오기(최근)
	List<BoardDTO> getList(Criteria cri);
	
	// 좋아요 순 게시글 긁어오기
	List<BoardDTO> getlikeList(Criteria cri);
	
	// 인기 순 게시글 긁어오기
	List<BoardDTO> getpopularList(Criteria cri);
	
	// 총 게시글 개수
	long getTotal(Criteria cri);

	// boardnum으로 댓글 개수
	int getReplyCnt(long boardnum);

	// 게시글 등록
	boolean insertBoard(BoardDTO board, BoardaddrDTO boardaddr, MultipartFile[] files) throws Exception;

	// 특정 userid로 작성된 게시글 번호 중 마지막 번호
	long getLastNum(String userid);

	BoardaddrDTO getBoardAddr(long boardnum);

	int getDays(String startdate, String enddate);


	// boardnum으로 파일 가져오기
	List<BoardFileDTO> getFiles(long boardnum);
	// boardnum으로 그 보드에 썸네일만 가져오기
	List<BoardFileDTO> getBoardThumnailList(long boardnum);
	//
//	BoardFileDTO getBoardThumnail(long boardnum);

	// 파일 다운로드
	ResponseEntity<Resource> downloadFile(String systemname, String orgname) throws Exception;

	// 게시글 삭제
	boolean remove(long boardnum);

	String SummerNoteImageFile(MultipartFile file) throws Exception;

	boolean deleteSummernoteImageFile(String fileUrl);
	
	

	// modify 이미지 썸네일
	ResponseEntity<Resource> getThumbnailResource(String sysname) throws Exception;

	// boardnum으로 썸네일 가져오기
	BoardFileDTO getThumbnail(long boardnum);

}
