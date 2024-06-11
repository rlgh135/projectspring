package com.t1.tripfy.service.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.domain.dto.ReviewDTO;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.domain.dto.board.BoardFileDTO;
import com.t1.tripfy.domain.dto.board.BoardLikeDTO;
import com.t1.tripfy.domain.dto.board.BoardReplyDTO;
import com.t1.tripfy.domain.dto.pack.PackageDTO;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;
import com.t1.tripfy.domain.dto.user.GuideDTO;
import com.t1.tripfy.domain.dto.user.GuideUserDTO;
import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.mapper.board.BoardMapper;
import com.t1.tripfy.mapper.board.BoardReplyMapper;
import com.t1.tripfy.mapper.pack.PackageFileMapper;
import com.t1.tripfy.mapper.pack.PackageMapper;
import com.t1.tripfy.mapper.pack.ReservationMapper;
import com.t1.tripfy.mapper.user.UserMapper;
import com.t1.tripfy.util.PathUtil;

@Service
public class UserServiceImpl implements UserService{
	@Value("${userthumbnail.dir}")
	private String saveFolder;
	
	@Autowired
	private UserMapper umapper;
	
	@Autowired
	private BoardMapper bmapper;
	
	@Autowired
	private ReservationMapper resmapper;
	
	@Autowired
	private PackageMapper pmapper;
	
	@Autowired
	private PackageFileMapper pfilemapper;
	
	@Autowired
	private BoardReplyMapper brmapper;
	
	@Override
	public boolean join(UserDTO user) {
		if(umapper.insertUser(user)==1) {
			if(umapper.makeDefaultBadge(user.getUserid())==1) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean login(String userid, String userpw) {
		UserDTO user = umapper.getUserById(userid);
		if(user!=null) {
			if(userpw.equals(user.getUserpw())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean updateUser(UserDTO user) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	@Transactional
	public boolean updateProfileimg(MultipartFile thumbnail, String userid) {
		// 사진을 /static/image에 UUID로 변경해서 저장
		String orgname = thumbnail.getOriginalFilename();
        String sysname = PathUtil.writeImageFile(thumbnail);
        System.out.println("orgname: "+orgname);
        System.out.println("sysname: "+sysname);
        
        // 저장된 파일의 경로를 DB에 저장
        UserImgDTO userimg = new UserImgDTO();
        userimg.setOrgname(orgname);
        userimg.setSysname(sysname);
        userimg.setUserid(userid);
        
        String path = saveFolder+sysname;
        
        if(umapper.updateProfileimg(userimg)==1) {
        	//실제 파일 업로드
        	try {
        		thumbnail.transferTo(new File(path));
        	} catch (IllegalStateException | IOException e) {
        		System.out.println("파일 실제 저장 에러: "+e);
        		return false;
        	}
        }
        
        return true;
	}
	
	@Override
	public boolean checkId(String userid) {
		return umapper.getUserById(userid) == null;
	}
	
	@Override
	public String getProfileImgName(String userid) {
		return umapper.getUserProfileName(userid);
	}
	
	@Override
	public GuideDTO getGuideNum(String userid) {
		return umapper.getGuideNum(userid);
	}
	
	@Override
	public UserDTO getUser(String userid) {
		return umapper.getUserById(userid);
	}
	
	@Override
	public ResponseEntity<Resource> getThumbnailResource(String sysname) throws Exception{
		Path path = Paths.get(saveFolder + sysname);
		String contentType = Files.probeContentType(path);
		HttpHeaders headers = new HttpHeaders();
		
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);

		Resource resource = new InputStreamResource(Files.newInputStream(path));
		
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	@Override
	public List<BoardDTO> getMyBoardList(Criteria cri, String userid) {
		return bmapper.getMyList(cri, userid);
	}
	
	@Override
	public long getMyTotal(Criteria cri) {
		return bmapper.getTotal(cri);
	}
	
	@Override
	public List<ReservationDTO> getMyReservation(Criteria cri, String userid) {
		return resmapper.getMyReservation(cri, userid);
	}
	
	@Override
	public PackageDTO getJoinPackage(long packagenum) {
		return pmapper.getPackageByPackageNum(packagenum);
	}
	
	@Override
	public boolean changeSogae(String userid, String introduce) {
		return umapper.updateSogae(userid, introduce)==1;
	}
	
	@Override
	public List<PackageDTO> getMyPackages(long guidenum, Criteria cri) {
		return pmapper.getMyPackages(guidenum, cri);
	}
	
	@Override
	public List<PackageDTO> getMyIngPackages(long guidenum, Criteria cri) {
		return pmapper.getMyIngPackages(guidenum, cri);
	}
	
	@Override
	public int getTotalPackageCnt(long guidenum) {
		return pmapper.getMyPackageCnt(guidenum);
	}
	
	@Override
	public int getTotalReview(long guidenum) {
		return umapper.getTotalReviewCnt(guidenum);
	}
	
	@Override
	public List<ReviewDTO> getReviewByPackagenum(long packagenum) {
		return umapper.getReviews(packagenum);
	}
	
	@Override
	public List<ReservationDTO> getApplyByPackagenum(long packagenum) {
		return resmapper.getApply(packagenum);
	}
	
	@Override
	public boolean changeApplyCansle(long reservationnum, int isdelete) {
		return resmapper.changeIsdelete(reservationnum, isdelete)==1;
	}
	@Override
	public PackageDTO getMyPackageTwoWeek(long packagenum) {
		return resmapper.getMyPackageTwoWeek(packagenum);
	}
	
	@Override
	public ReviewDTO getMyReviewByPackagenum(long packagenum, String userid) {
		return umapper.getMyReview(packagenum, userid);
	}
	
	@Override
	public List<GuideUserDTO> getLikeGuides(String userid) {
		return umapper.getLikeGuides(userid);
	}
	@Override
	public UserImgDTO getGuideAndImg(long packagenum) {
		return umapper.getGuideAndImg(packagenum);
	}
	
	@Override
	public GuideUserDTO getLikeThisGuide(long guidenum, String userid) {
		return umapper.getLikeThisGuide(guidenum, userid);
	}
	
	@Override
	public boolean presslike(String userid, long guidenum) {
		if(umapper.getLikeThisGuide(guidenum, userid)!=null) {
			if(umapper.deleteLike(guidenum, userid)==1) {
				return true;
			}
			System.out.println("deletelike미스");
		} else {
			if(umapper.addLike(guidenum, userid)==1) {
				return true;
			}
			System.out.println("addlike미스");
		}
		System.out.println("뭔가조짐");
		return false;
	}
	
	@Override
	public int uploadHugi(ReviewDTO review) {
		umapper.deleteHugi(review);
		return umapper.addHugi(review);
	}
	
	@Override
	public List<ReservationDTO> getForeignerReservations(String fname, String phone, Criteria cri) {
		return resmapper.getForignerReservations(fname, phone, cri);
	}
	
	@Override
	public ReservationDTO getForeignerReservation(String keycode) {
		return resmapper.getForignerReservation(keycode);
	}
	
	@Override
	public PackageFileDTO getPackThumbnail(long packagenum) {
		return umapper.getMyPackThumb(packagenum);
	}
	@Override
	public List<UserImgDTO> getAllUserImg() {
		return umapper.getAllUserImg();
	}
	@Override
	public int insertGuide(GuideDTO guide) {
		return umapper.insertGuide(guide);
	}
	@Override
	public int applyCansle(long reservationnum, int cansleStatus) {
		return umapper.updateCansle(reservationnum, cansleStatus);
	}
	@Override
	public ReservationDTO getResevationByIdPackagenum(String userid, long packagenum) {
		return resmapper.getMyRservationWithPackagenum(userid, packagenum);
	}
	@Override
	public BoardFileDTO getMyBoardThumbnail(long boardnum) {
		return bmapper.getThumbnail(boardnum);
	}
	@Override
	public BoardLikeDTO getMyBoardLike(String userid, long boardnum) {
		return bmapper.getBoardLike(userid, boardnum);
	}
	
	@Override
	public List<BoardDTO> getLikeBoardList(Criteria cri, String userid) {
		return umapper.getLikeBoards(cri, userid);
	}
	@Override
	public List<PackageDTO> getReadyPack(Criteria cri, String userid) {
		return umapper.getReadyPack(cri, userid);
	}
	@Override
	public List<PackageDTO> getDonePack(Criteria cri, String userid) {
		return umapper.getDonePack(cri, userid);
	}
	@Override
	public List<PackageDTO> getGuideDonePack(Criteria cri, String guideid) {
		return umapper.getReadyGPack(cri, guideid);
	}
	@Override
	public List<PackageDTO> getGuideReadyPack(Criteria cri, String guideid) {
		return umapper.getDoneGPack(cri, guideid);
	}
	@Override
	public Integer getTotalResCnt(long packagenum) {
		return resmapper.getTotalResCnt(packagenum);
	}
	@Override
	public List<ReviewDTO> getMyReviews(Criteria cri, String userid) {
		return umapper.getMyReviews(cri, userid);
	}
	@Override
	public List<ReviewDTO> getMineReviews(Criteria cri, String guideid) {
		return umapper.getMineReviews(cri, guideid);
	}
	@Override
	public Integer getTotalBoardCnt(String userid) {
		return umapper.getTotalBoardCnt(userid);
	}
	@Override
	public Integer getTotalReplyCnt(String userid) {
		return umapper.getTotalReplyCnt(userid);
	}
	
	@Override
	public boolean insertDummy() {
		Random random = new Random();
		String[] userid = {"minsoo", "sanghyeon", "yeonghwan", "seonghwi", "kiho"};
		
		//회원
		for (String id : userid) {
			UserDTO user = new UserDTO();
			user.setUserid(id);
			user.setUserpw("Abcd!234");
			user.setPhone("01012345678");
			user.setEmail(id+"1212@gmail.com");
			user.setGender("M");
			user.setBirth("010203");
			user.setAddr("서울");
			user.setPlaceid("C230DVDE230DFES");
			user.setIntroduce("자기소개가 없어요.");
			
			if(umapper.insertUser(user)==1) {
				umapper.makeDefaultBadge(id);
			}
		}
		
		String[] countrycodes = {"kr$mol", "ea$동남아시아", "gu$괌/사이판/호주/뉴질랜드", "jp$일본", "cn$중국", "eu$유럽", "us$미국"};
		String[] krregions = {"서울", "제주도", "경기도", "강원도", "충청도", "경상도", "전라도", "인천광역시"};
		//게시판
		for(int i=0; i<3000; i++) {
			BoardDTO board = new BoardDTO();
			board.setUserid(userid[random.nextInt(5)]);
			board.setTitle((i+1)+"번째 게시글!");
			board.setContent((i+1)+"번째 게시글의 내용!");
			int idx = random.nextInt(7);
			String[] location = countrycodes[idx].split("\\$");
			board.setCountrycode(location[0]);
			if(idx==0) {
				board.setRegionname(krregions[random.nextInt(8)]);
			} else {
				board.setRegionname(location[1]);
			}
			
			if(bmapper.insertBoard(board)==1) {
				int coin = random.nextInt(3);
				if(coin==0) {
					continue;
				} else {
					BoardFileDTO bfile = new BoardFileDTO();
					bfile.setBoardnum(i+1);
					bfile.setSysname("BoardThumnail"+(random.nextInt(4)+1)+".png");
					bfile.setOrgname("image"+i+".png");
					bmapper.insertFile(bfile);
				}
			}
		}
		
		//패키지
		//삽입 후에 update package set visibility='O' where packagenum>0; 수행
		for(int i=0; i<4000; i++) {
			PackageDTO pack = new PackageDTO();
			int idx = random.nextInt(7);
			String[] location = countrycodes[idx].split("\\$");
			pack.setCountrycode(location[0]);
			if(idx==0) {
				pack.setRegionname(krregions[random.nextInt(8)]);
			} else {
				pack.setRegionname(location[1]);
			}
			pack.setGuidenum((random.nextInt(2)+1));
			pack.setPackageTitle((i+1)+"번째 패키지!");
			pack.setPackageContent(location[1]+"으로 떠나는 \n"+(i+1)+"번째 패키지 내용입니다. ");
			pack.setMaxcnt(random.nextInt(9)+20);
			pack.setAdultPrice(100*(random.nextInt(4000)+1));
			pack.setChildPrice(100*(random.nextInt(4000)+1));
			int month = random.nextInt(12)+1;
			String monthstring = "";
			if(month<10) {
				monthstring="0"+month;
			} else {
				monthstring=""+month;
			}
			int day = random.nextInt(28)+1;
			String daystring = "";
			if(day<10) {
				daystring = "0"+day;
			} else {
				daystring = ""+day;
			}
			String startdate = "2024-"+monthstring+"-"+daystring;
			pack.setStartdate(startdate);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.parse(startdate, formatter);
			System.out.println(date);
			int tourdate = random.nextInt(20)+5;
			LocalDate localenddate = date.plus(tourdate, ChronoUnit.DAYS);
			String enddate = localenddate.format(formatter);
			LocalDate loocaldeadline = date.minus(7, ChronoUnit.DAYS);
			String deadline = loocaldeadline.format(formatter);
			pack.setEnddate(enddate);
			pack.setDeadline(deadline);
			pack.setVisibility("O");
			
			if(pmapper.insertPack(pack)==1) {
				int coin = random.nextInt(3);
				if(coin==0) {
					PackageFileDTO pfile = new PackageFileDTO();
					pfile.setPackagenum(i+1);
					pfile.setPfSysname("defaultimg.jpg");
					pfile.setPfOrgname("defaultimg.jpg");
					pfilemapper.insertFile(pfile);
				} else {
					PackageFileDTO pfile = new PackageFileDTO();
					pfile.setPackagenum(i+1);
					pfile.setPfSysname("pakcagededummy"+coin+".jpg");
					pfile.setPfOrgname("pakcagededummy"+coin+".jpg");
					pfilemapper.insertFile(pfile);
				}
			}
		}
		
		//reservation
		//pcount = 데이터베이스의 총 패키지 갯수
		int pcount = 4000;
		for (int i = 1; i<=4000; i++) {
			for (String id : userid) {
				int coin = random.nextInt(2);
				if(coin==0) {
					System.out.println("no reservation");
				} else {
					System.out.println(id+": apply package");
					ReservationDTO reservation = new ReservationDTO();
					reservation.setPackagenum(i);
					reservation.setUserid(id);
					reservation.setAdultCnt(random.nextInt(2)+1);
					reservation.setChildCnt(random.nextInt(3));
					reservation.setPhone("01012345678");
					reservation.setEmail(id+"1212@gmail.com");
					reservation.setKeycode("dummyKeycode");
					reservation.setIsDelete(0);
					reservation.setPrice("dummyPrice");
					reservation.setPayMethod("카드");
					reservation.setName("회원");
					
					pmapper.saveReservationForMember(reservation);
				}
			}			
		}
		
		//review
		for (String id : userid) {
			List<Long> pnumlist = umapper.getAllPackagenumInReservation(id, pcount); 
			for (Long pnum : pnumlist) {
				int coin = random.nextInt(2);
				
				if(coin==0) {
					System.out.println(id+": no review");
					continue;
				} else {
					System.out.println(id+": add review");
					PackageDTO pack = pmapper.getPackageByPackageNum(pnum);
					
					ReviewDTO review = new ReviewDTO();
					review.setPackagenum(pack.getPackagenum());
					review.setUserid(id);
					review.setGuidenum(pack.getGuidenum());
					review.setContents(id+"의 "+pack.getPackagenum()+"번 패키지 리뷰");
					
					int emcoin = random.nextInt(3);
					if(emcoin==0) {
						review.setEmSysname("");
					} else {
						review.setEmSysname((random.nextInt(20)+1)+".png");
					}
					
					umapper.addHugi(review);
				}
				
			}
		}
		
		//미래의 리뷰 없애기
		umapper.deleteFutureReview();
		
		//reply
		//bcount = 보드의 갯수
		int bcount = 3000;
		for (int i=1; i<=bcount; i++) {
			for (String id : userid) {
				int coin = random.nextInt(2);
				
				if(coin==0) {
					System.out.println(id+"는 "+i+"번 게시글에 댓글을 달지 않았습니다");
					continue;
				} else {
					System.out.println(id+"가 "+i+"번 게시글에 댓글을 달았습니다");
					BoardReplyDTO reply = new BoardReplyDTO();
					
					reply.setBoardnum(i);
					reply.setContents(id+"의 "+i+"번 게시글에 단 댓글");
					reply.setUserid(id);
					
					int emcoin = random.nextInt(3);
					if(emcoin==0) {
						reply.setEmSysname("");
					} else {
						reply.setEmSysname((random.nextInt(20)+1)+".png");
					} 
					
					brmapper.insertReply(reply);
					bmapper.addReplyCnt(i);
				}
			}
		}
		
		return true;
	}
}
