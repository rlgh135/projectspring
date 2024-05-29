package com.t1.tripfy.service.board;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.mapper.board.BoardMapper;
import com.t1.tripfy.mapper.board.BoardReplyMapper;

@Service
public class BoardServiceImpl implements BoardService {
	// 파일을 업로드 하기 위한 경로
	@Value("${board.dir}")
	private String saveFolder;
	
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

	// 게시글 등록
	@Override
	public boolean insertBoard(BoardDTO board, MultipartFile[] files) throws Exception {
		if(bmapper.insertBoard(board) != 1) {  // 게시글 DB 삽입 실패			
			return false;
		}
		
		// 게시글 업로드 했지만 파일은 없음(파일 첨부 안함)
		if(files == null || files.length == 0) {
			return true;
		}
		
		// 게시글 업로드 O, 파일 업로드 해야함
		else {
			// 방금 등록한 게시글 번호
			long boardnum = bmapper.getLastNum(board.getUserid());
			
			boolean flag = false;
			for(int i = 0; i < files.length - 1; i++) {
				MultipartFile file = files[i];  // 업로드 된 파일 하나씩 꺼냄(실제 디렉토리에 업로드 되어 있지는 않고 데이터를 꺼낸 것)
				
				// apple.png
				String orgname = file.getOriginalFilename();
				// 5
				int lastIdx = orgname.lastIndexOf(".");  // 같은 파일명일 경우 파일명을 바꿔주기 위해
				// .png
				String extension = orgname.substring(lastIdx);  // 확장자
				
				LocalDateTime now = LocalDateTime.now();
				String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
				
				// 20240502162130141랜덤문자열.png
				String systemname = time + UUID.randomUUID().toString() + extension;
				
				// 실제 생성될 파일의 경로
				String path = saveFolder + systemname;
				
				// File DB 저장
				BoardFileDTO bfdto = new BoardFileDTO();
				bfdto.setBoardnum(boardnum);
				bfdto.setOrgname(orgname);
				bfdto.setSysname(systemname);
				flag = bmapper.insertFile(bfdto) == 1;
				
				// 실제 파일 업로드
				file.transferTo(new File(path));
				
				if(!flag) {
					// 업로드 했던 파일 삭제, 게시글 데이터 삭제, 파일 data 삭제, ...
					return false;
				}
			}
			return true;
		}
	}

	// 특정 userid로 작성된 게시글 번호 중 마지막 번호
	@Override
	public long getLastNum(String userid) {
		return bmapper.getLastNum(userid);
	}
}
