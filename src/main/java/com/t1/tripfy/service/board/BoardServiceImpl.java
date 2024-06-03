package com.t1.tripfy.service.board;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.domain.dto.board.BoardLikeDTO;
import com.t1.tripfy.domain.dto.board.BoardReplyDTO;
import com.t1.tripfy.domain.dto.board.BoardReplyPageDTO;
import com.t1.tripfy.domain.dto.board.BoardaddrDTO;
import com.t1.tripfy.domain.dto.user.GuideDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.mapper.board.BoardMapper;
import com.t1.tripfy.mapper.board.BoardReplyMapper;
import com.t1.tripfy.mapper.user.UserMapper;

@Service
public class BoardServiceImpl implements BoardService {
	// 파일을 업로드 하기 위한 경로
	@Value("${board.dir}")
	private String saveFolder;
	
	@Value("${boardSummerNote.dir}")
	private String BoardSummernotesaveFolder;
	
	@Autowired
	private BoardMapper bmapper;
	
	@Autowired
	private BoardReplyMapper brmapper;
	
	@Autowired
	private UserMapper umapper;
	
	
	//사진검사
	public static boolean isImageFile(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        // 확장자를 소문자로 변환하여 비교
        extension = extension.toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") ||
               extension.equals("gif") || extension.equals("bmp") || extension.equals("tiff") ||
               extension.equals("tif") || extension.equals("cr2") || extension.equals("crw") ||
               extension.equals("nef") || extension.equals("nrw") || extension.equals("arw") ||
               extension.equals("srf") || extension.equals("sr2") || extension.equals("orf") ||
               extension.equals("pef") || extension.equals("rw2") || extension.equals("raf") ||
               extension.equals("srw") || extension.equals("dng") || extension.equals("rwl") ||
               extension.equals("heif") || extension.equals("heic") || extension.equals("webp") ||
               extension.equals("psd");
    }
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
	public boolean insertBoard(BoardDTO board, BoardaddrDTO boardaddr, MultipartFile[] files) throws Exception {
		if(bmapper.insertBoard(board) != 1) {  // 게시글 DB 삽입 실패			
			return false;
		}
		
		long boardnum = bmapper.getLastNum(board.getUserid());
		System.out.println("보드 : "+board);
		System.out.println("보드에이디디알 : "+boardaddr);
		if (!boardaddr.getPlacename().equals("") && !boardaddr.getPlacename().isEmpty()
		    && !boardaddr.getStartdate().equals("") && !boardaddr.getStartdate().isEmpty()
		    && !boardaddr.getEnddate().equals("") && !boardaddr.getEnddate().isEmpty()) {

		    boardaddr.setBoardnum(boardnum);
		    System.out.println("Boardnum set to boardaddr: " + boardaddr.getBoardnum()); 
		    if (bmapper.insertBoardAddr(boardaddr) != 1) {
		        // 게시글은 작성되었으므로 삭제
		    	bmapper.deleteBoard(boardnum);
		    	
		    	// 파일이 있다면 삭제
		    	List<BoardFileDTO> boardfiles = bmapper.getFiles(boardnum);
				System.out.println("files.size(): " + boardfiles.size());
				
				for(BoardFileDTO bfdto : boardfiles) {
					File file = new File(saveFolder, bfdto.getSysname());
					if(file.exists()) {
						file.delete();
						bmapper.deleteFilesBySystemname(bfdto.getSysname());
					}
				}
				
		        return false;
		    }
		}

		
		// 게시글 업로드 했지만 파일은 없음(파일 첨부 안함)
		if(files == null || files.length == 0) {
			System.out.println("널입니다");
			return true;
		}
		
		// 게시글 업로드 O, 파일 업로드 해야함
		else {
			System.out.println("널아닙니다");
			// 방금 등록한 게시글 번호
			// long boardnum = bmapper.getLastNum(board.getUserid());
			boolean thumnailFlag = false;
			boolean flag = false;
			for(int i = 0; i < files.length - 1; i++) {
				MultipartFile file = files[i];  // 업로드 된 파일 하나씩 꺼냄(실제 디렉토리에 업로드 되어 있지는 않고 데이터를 꺼낸 것)
				if(file.getSize() == 0) {
					continue;
				}
				String systemname = "";
				// apple.png
				String orgname = file.getOriginalFilename();
				// 5
				int lastIdx = orgname.lastIndexOf(".");  // 같은 파일명일 경우 파일명을 바꿔주기 위해
				// .png
				String extension = orgname.substring(lastIdx);  // 확장자
				LocalDateTime now = LocalDateTime.now();
				String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
				
				if(!thumnailFlag && isImageFile(extension.replace(".", ""))) {
					thumnailFlag = true;
					systemname = "BoardThumnail"+boardnum+extension;
				}else {
					systemname = time + UUID.randomUUID().toString() + extension;					
				}
				// 20240502162130141랜덤문자열.png
				
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

	@Override
	public BoardaddrDTO getBoardAddr(long boardnum) {
		BoardaddrDTO boardaddr =  bmapper.getBoardaddrByBoardnum(boardnum);
		if(boardaddr != null) {
			String startdate = (boardaddr.getStartdate()).replace("-", ".");
			String enddate = (boardaddr.getEnddate()).replace("-", ".");
			boardaddr.setStartdate(startdate);
			boardaddr.setEnddate(enddate);
		}
		return boardaddr;
	}

	@Override
	public int getDays(String startdate, String enddate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate startDate = LocalDate.parse(startdate, formatter);
        LocalDate endDate = LocalDate.parse(enddate, formatter);

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return (int) daysBetween+1;
    }
	
	// boardnum으로 파일 가져오기
	@Override
	public List<BoardFileDTO> getFiles(long boardnum) {
		return bmapper.getFiles(boardnum);
	}
	
	
	// 파일 다운로드
	@Override
	public ResponseEntity<Resource> downloadFile(String sysname, String orgname) throws Exception {
		Path path = Paths.get(saveFolder + sysname);
		Resource resource = new InputStreamResource(Files.newInputStream(path));
		
		File file = new File(saveFolder, sysname);
		
		HttpHeaders headers = new HttpHeaders();
		String dwName = "";
		
		try {
			dwName = URLEncoder.encode(orgname,"UTF-8").replaceAll("\\+", "%20");
		} catch(UnsupportedEncodingException e) {
			dwName = URLEncoder.encode(file.getName(),"UTF-8").replaceAll("\\+","%20");
		}
		
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename(dwName).build());
		return new ResponseEntity<>(resource,headers,HttpStatus.OK);
		
	}
	
	// 게시글 삭제
	@Override
	public boolean remove(long boardnum) {
		System.out.println("bmapper.getBoardaddrByBoardnum(boardnum): " + bmapper.getBoardaddrByBoardnum(boardnum));
		
		// boardaddr 있다면 삭제
		if(bmapper.getBoardaddrByBoardnum(boardnum) != null) {
			System.out.println("bmapper.deleteBoardaddr(boardnum): " + bmapper.deleteBoardaddr(boardnum));
			if(bmapper.deleteBoardaddr(boardnum) == 1) {
				System.out.println("boardaddr 삭제 완료");
			}
		}
		
		// 파일 있다면 파일 삭제
		List<BoardFileDTO> files = bmapper.getFiles(boardnum);
		System.out.println("files.size(): " + files.size());
		
		for(BoardFileDTO bfdto : files) {
			File file = new File(saveFolder, bfdto.getSysname());
			if(file.exists()) {
				file.delete();
				bmapper.deleteFilesBySystemname(bfdto.getSysname());
			}
		}
		
		if(bmapper.deleteBoard(boardnum) == 1) {
			// 댓글 삭제
			if(bmapper.getTotalBoardReply(boardnum) != null) {
				if(bmapper.deleteTotalBoardReply(boardnum) > 0) {
					System.out.println("board의 댓글 삭제 완료");
				}
			}
			
			// board의 좋아요 삭제
			if(bmapper.getTotalBoardLike(boardnum) != null) {
				  if(bmapper.deleteTotalBoardLike(boardnum) > 0) {
					  System.out.println("board의 좋아요 삭제 완료");
				  }
			}
			System.out.println("게시글 삭제 완료");
		}
		
		else {
			System.out.println("게시글 삭제 실패");
			return false;
		}
		
		return true;
	}

	@Override
	public String SummerNoteImageFile(MultipartFile file) throws Exception{		
	    if (file.isEmpty()) {
	    	String error = "파일이 없습니다";
	        return error;
	    }
	    System.out.println("컨트롤러에서 찍는 파일"+file);
	    
		String originalFileName = file.getOriginalFilename();
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		LocalDateTime now = LocalDateTime.now();
		String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		
		String systemFileName = time + UUID.randomUUID()+extension;
		
		String path = BoardSummernotesaveFolder + systemFileName;
		file.transferTo(new File(path));
		String thumnailpath = "/BoardSummerNoteThumnail/"+systemFileName;
		return thumnailpath;
	}
	
	@Override
	public boolean deleteSummernoteImageFile(String fileUrl) {
		boolean flag = false;
		String systemFileName = fileUrl.replace("/BoardSummerNoteThumnail/", "");
		File file = new File(BoardSummernotesaveFolder,systemFileName);
		if(file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
//	썸네일 여러개 긁을때쓰세요
	@Override
	public List<BoardFileDTO> getBoardThumnailList(long boardnum) {
		
		return null;
	}

// 썸네일 하나 긁을때 쓰세요
//	@Override
//	public BoardFileDTO getBoardThumnail(long boardnum) {
//		return bmapper.getBoardThumnail(boardnum);
//	}
	
	// modify 이미지 썸네일
	@Override
	public ResponseEntity<Resource> getThumbnailResource(String sysname) throws Exception {
		//경로에 관련된 객체(자원으로 가지고 와야 하는 파일에 대한 경로)
		Path path = Paths.get(saveFolder+sysname);
		//경로에 있는 파일의 MIME 타입을 조사해서 그대로 담기
		String contentType = Files.probeContentType(path);
		//응답 헤더 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);
		
		//해당 경로(path)에 있는 파일로부터 뻗어나오는 InputStream*Files.newInputStream(path)을
		//통해 자원화*new InputStreamResource()
		Resource resource = new InputStreamResource(Files.newInputStream(path));
		return new ResponseEntity<>(resource,headers,HttpStatus.OK);
	}
	
	// boardnum으로 썸네일 가져오기
	@Override
	public BoardFileDTO getThumbnail(long boardnum) {
		return bmapper.getThumbnail(boardnum);
	}
	@Override
	public boolean modifyBoard(BoardDTO board, BoardaddrDTO boardaddr, MultipartFile[] files, String updateCnt) throws Exception {
		long boardnum = board.getBoardnum();
		if(bmapper.updateBoard(board) != 1) {
			return false;
		}
		if (!boardaddr.getPlacename().equals("") && !boardaddr.getPlacename().isEmpty()
		    && !boardaddr.getStartdate().equals("") && !boardaddr.getStartdate().isEmpty()
		    && !boardaddr.getEnddate().equals("") && !boardaddr.getEnddate().isEmpty()) {
		    boardaddr.setBoardnum(boardnum);
		    if (bmapper.getBoardaddrByBoardnum(boardnum) != null) {
		    	if(bmapper.deleteBoardaddr(boardnum) != 1) {
		    		return false;		    		
		    	}
		    }
		}
		ArrayList<String> deleteNames = null;
		boolean deleteThumnailflag = true;
		
		 if (updateCnt != null && !updateCnt.isEmpty()) {
            deleteNames = new ArrayList<>(Arrays.asList(updateCnt.split("\\\\")));

            Iterator<String> iterator = deleteNames.iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                if (name.startsWith("BoardThumnail")) {
                    File file = new File("saveFolder", name);
                    if (file.exists()) {
                        file.delete();
                    }
                    bmapper.deleteFilesBySystemname(name);
                    deleteThumnailflag = false;
                    iterator.remove(); // 요소 제거
                }
            }
        }
		 
		List<BoardFileDTO> orgFileList = bmapper.getFiles(board.getBoardnum());
		if(orgFileList.size() == 0 && (files == null || files.length == 0 || files[0].getSize() == 0)) {
			return true;
		}else {
			System.out.println("service : "+files.length);
			if(files != null && files.length != 0) {
				boolean flag = false;
				boolean thumnailflag = false;
				ArrayList<String> sysnames = new ArrayList<>();
				for(int i=0;i<files.length-1;i++) {
					MultipartFile file = files[i];
					if(file.getSize() == 0) {
						continue;
					}
					String orgname = file.getOriginalFilename();
					if(orgname == null || orgname.equals("")) {
						continue;
					}
	
					int lastIdx = orgname.lastIndexOf(".");
					String extension = orgname.substring(lastIdx);
					LocalDateTime now = LocalDateTime.now();
					String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
					String systemname = "";
					if(!deleteThumnailflag && !thumnailflag && isImageFile(extension.replace(".", ""))) {
						systemname = "BoardThumnail"+boardnum+extension;
						deleteThumnailflag = true;
						thumnailflag = true;
					}else {
						systemname = time+UUID.randomUUID().toString()+extension;
					}
										
					String path = saveFolder+systemname;
								
					BoardFileDTO fdto = new BoardFileDTO();
					fdto.setBoardnum(board.getBoardnum());
					fdto.setOrgname(orgname);
					fdto.setSysname(systemname);
					flag = bmapper.insertFile(fdto) == 1;
	
					file.transferTo(new File(path));
					sysnames.add(systemname);
					
					if(!flag) {
						break;
					}
				}
				//강제탈출
				if(!flag){
					for(String systemname : sysnames){
						File file = new File(saveFolder,systemname);
						if(file.exists()) {
							file.delete();
						}
						bmapper.deleteFilesBySystemname(systemname);
					}
				}
			}
			
			if (deleteNames != null) {
			    for(String systemname : deleteNames) {
			        File file = new File(saveFolder, systemname);
			        if(file.exists()) {
			            file.delete();
			        }
			        bmapper.deleteFilesBySystemname(systemname);
			    }
			}
			return true;
		}
	}
	
	// 조회수 증가
	@Override
	public boolean increaseViewCount(long boardnum) {
		BoardDTO board = bmapper.getBoardByBoardNum(boardnum);
		return bmapper.updateViewCnt(boardnum, board.getViewcnt() + 1) == 1;
	}
	
	// 유저 프로필
	@Override
	public UserImgDTO getUserProfile(String userid) {
		
		return umapper.getUserProfile(userid);
	}
	@Override
	public BoardReplyDTO replyRegist(BoardReplyDTO reply) {
		if(bmapper.addReplyCnt(reply.getBoardnum()) == 1) {
			if(brmapper.insertReply(reply) == 1) {
				return brmapper.getLastReply(reply.getUserid());
			}			
		}
		return null;
	}
	@Override
	public BoardReplyPageDTO getReplyList(long boardnum, int pagenum) {
		int amount = 10;
		int startrow = (pagenum-1)*amount;
		return new BoardReplyPageDTO(brmapper.getTotal(boardnum), brmapper.getReplyList(amount,startrow,boardnum));
	}
	@Override
	public boolean replyModify(BoardReplyDTO reply) {
		return brmapper.updateReply(reply) == 1;
	}
	@Override
	public boolean deleteReply(BoardReplyDTO reply) {
		if(bmapper.reduceReplyCnt(brmapper.getReplyByReplyNum(reply.getReplynum()).getBoardnum()) == 1) {
			return brmapper.deleteReply(reply) == 1;			
		}else {
			return false;
		}
	}
	@Override
	public BoardReplyDTO getReplyByReplyNum(long replynum) {
		return brmapper.getReplyByReplyNum(replynum);
	}
	
	// 해당 userid가 해당 board에 좋아요 눌렀는지 찾음
	@Override
	public BoardLikeDTO getBoardLike(String userid, long boardnum) {
		return bmapper.getBoardLike(userid, boardnum);
	}
	
	// 좋아요 클릭
	@Override
	public boolean likeClick(String userid, long boardnum) {
		BoardDTO board = bmapper.getBoardByBoardNum(boardnum);
		
		if(bmapper.getBoardLike(userid, boardnum) == null) {
			// 좋아요 등록
			if(bmapper.likeRegist(userid, boardnum) == 1) {
				bmapper.updateLikeCnt(boardnum, board.getLikecnt() + 1);
				System.out.println("좋아요 등록 성공");
				return true;
			}
			
			else {
				System.out.println("좋아요 등록 실패");
			}
		}
		
		else {
			// 좋아요 취소
			if(bmapper.likeDelete(userid, boardnum) == 1) {
				bmapper.updateLikeCnt(boardnum, board.getLikecnt() - 1);
				System.out.println("좋아요 취소 성공");
				return true;
			}
			
			else {
				System.out.println("좋아요 취소 실패");
			}
		}
		return false;
	}
	
	// 가이드
	@Override
	public GuideDTO getGuide(String userid) {
		return umapper.getGuideNum(userid);
	}
	
	// content 이미지 태그 제외하고 추출
	@Override
	public String exceptImgTag(String content) {
		Document doc = Jsoup.parse(content);
		
		System.out.println("Before filtering:");
		
		// 모든 <img> 태그를 선택하고 제거
        Elements imgTags = doc.select("img");
        imgTags.remove();
        
        // 모든 <iframe> 태그를 선택하고 제거(동영상)
        Elements iframeTags = doc.select("iframe");
        for (Element iframeTag : iframeTags) {
            iframeTag.remove();
        }
        
        // 모든 <a> 태그를 선택하고 제거(링크)
        Elements linkTags = doc.select("a");
        for (Element linkTag : linkTags) {
            linkTag.remove();
        }
		
        // HTML 구조를 유지하면서 body의 내용을 가져옴
        String exceptContent = doc.body().html();
		
		System.out.println("After filtering:");
		System.out.println("exceptContent: " + exceptContent);
		
		return exceptContent;
	}
}
