package com.t1.tripfy.service.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.t1.tripfy.domain.dto.chat.ChatDetailDTO;
import com.t1.tripfy.domain.dto.chat.ChatListPayloadDTO;
import com.t1.tripfy.domain.dto.chat.ChatRoomDTO;
import com.t1.tripfy.domain.dto.chat.ChatUserDTO;
import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatContentMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatLoadMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatRoomEnterMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatContentDetailMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatDetailBulkMessagePayload;
import com.t1.tripfy.domain.dto.user.UserImgDTO;
import com.t1.tripfy.mapper.chat.ChatDetailMapper;
import com.t1.tripfy.mapper.chat.ChatInvadingMapper;
import com.t1.tripfy.mapper.chat.ChatRoomMapper;
import com.t1.tripfy.mapper.chat.ChatUserMapper;
import com.t1.tripfy.service.user.UserService;

@Service
public class ChatServiceImpl implements ChatService {
	@Autowired
	private ChatRoomMapper chatRoomMapper;
	@Autowired
	private ChatUserMapper chatUserMapper;
	@Autowired
	private ChatDetailMapper chatDetailMapper;
	@Autowired
	private ChatInvadingMapper chatInvadingMapper;
	
	@Autowired
	private UserService userServiceImpl;
	
	/*
	 	중요!!
	 	기능을 기준으로 메서드 나눌것
	*/
	
	@Override
	@Transactional
	public ChatListPayloadDTO createChat(String userid, Long packagenum) {
		//chat_room insert, PK(chat_room_idx) 가져오기
		LocalDateTime current = LocalDateTime.now();
		ChatRoomDTO crDTO = new ChatRoomDTO()
				.setChatRoomTitle(null)
				.setPackagenum(packagenum)
				.setRegdate(current);
		
		if(1 != chatRoomMapper.createRoom(crDTO)) {
			System.out.println("cc-1");
			return null;
		}
		Long crPK = crDTO.getChatRoomIdx();
		
		//패키지 판매자 userid 가져오기
		String guideUserid;
		if(null == (guideUserid = chatInvadingMapper.selectGuideUseridByPackagenum(packagenum))) {
			System.out.println("cc-2");
			return null;
		}
		
		//chat_user 삽입하기
		List<ChatUserDTO> userList = new ArrayList<>();
		userList.add(new ChatUserDTO()
				.setChatRoomIdx(crPK)
				.setUserid(guideUserid)
				.setChatUserIsCreator(true)
				.setChatUserIsQuit(false)
				.setChatDetailIdx(null));
		userList.add(new ChatUserDTO()
				.setChatRoomIdx(crPK)
				.setUserid(userid)
				.setChatUserIsCreator(false)
				.setChatUserIsQuit(false)
				.setChatDetailIdx(null));
		
		if(2 != chatUserMapper.insertRow(userList)) {
			System.out.println("cc-3");
			return null;
		}
		
		//전송값 꾸리기
		String pkgTitle;
		if(null == (pkgTitle = chatInvadingMapper.selectPackageNameByChatRoomIdx(crPK))) {
			System.out.println("cc-4");
			return null;
		}
		
		//userList에서 요청자(일반유저)를 뺀다
		userList.remove(1);
		
		return new ChatListPayloadDTO()
				.setRoomidx(crPK)
				.setPkgnum(packagenum)
				.setTitle(pkgTitle)
				.setChatRegdate(current)
				.setIsCreator(false)
				.setUserList(userList)
				.setChatContent(null)
				.setChatContentRegdate(null)
				.setUncheckedmsg(0);
	}
	
	@Override
	@Transactional
	public ChatListPayloadDTO createChat(String userid, String title, List<String> invitee) {
		
	//유효성 검사
		//invitee의 모든 요소(userid)가 존재하는지 확인
		//이거 일단은 순회로 처리함
		for(String iv : invitee) {
			if(null == userServiceImpl.getUser(iv)) {
				//존재하지 않는 userid
				return null;
			}
		}
		
	//chat_room
		//현재시간 저장
		LocalDateTime current = LocalDateTime.now();
		
		//삽입할 DTO 구성
		/*
		 * title null 체크는 안함 - null 기입 가능함
		 * 또한 auto_increment 된 PK 를 crDTO 에 담아온다
		 * */
		ChatRoomDTO crDTO = new ChatRoomDTO()
				.setChatRoomType(0)
				.setChatRoomTitle(title)
				.setPackagenum(null)
				.setRegdate(current);
		
		//삽입
		if(1 != chatRoomMapper.createRoom(crDTO)) {
			//chat_room insert 실패
			return null;
		}
		
		Long crPK = crDTO.getChatRoomIdx();
		
	//chat_user
		//invitee를 순회하면서 삽입용 DTO List 초기화
		List<ChatUserDTO> cuDTOList = new ArrayList<>();
		for(String iv : invitee) {
			cuDTOList.add(new ChatUserDTO()
					.setChatRoomIdx(crPK)
					.setUserid(iv)
					.setChatUserIsCreator(false)
					.setChatUserIsQuit(false)
					.setChatDetailIdx(null));
		}
		//요청자 삽입
		cuDTOList.add(new ChatUserDTO()
				.setChatRoomIdx(crPK)
				.setUserid(userid)
				.setChatUserIsCreator(true)
				.setChatUserIsQuit(false)
				.setChatDetailIdx(null));
		
		//삽입
		if((invitee.size() + 1) != chatUserMapper.insertRow(cuDTOList)) {
			//chat_user insert 실패
			return null;
		}
	
	//전송값 꾸리고 보내기
		//cuDTOList에서 요청자 빼기
		cuDTOList.remove(cuDTOList.size() - 1);
		
		return new ChatListPayloadDTO()
				.setRoomidx(crPK)
				.setPkgnum(null)
				.setTitle(title)
				.setChatRegdate(current)
				.setIsCreator(true) // 요청자가 채팅 생성자임
				.setUserList(cuDTOList)
				.setChatContent(null)
				.setChatContentRegdate(null)
				.setUncheckedmsg(0);
	}
	
	//채팅방 진입
	// !!채팅방 진입시 chat_user.chat_detail_idx 수정해야함
	@Override
	@Transactional
	public MessageDTO<ChatDetailBulkMessagePayload> chatRoomEnterHandling(MessageDTO<? extends MessagePayload> receiveMsg, boolean doesNeedToLoadUser) {
		//payload 초기화
		ChatDetailBulkMessagePayload bulk = new ChatDetailBulkMessagePayload();
		
		//최초 채팅방 진입시 가입자들의 userid를 가져온다
		if(doesNeedToLoadUser) {
			//대충 receiverMsg.receiverId에 DB서 긁어온 값을 대입, 그 후 receiverId 값이 null인지 체크
			if(null == (receiveMsg.setReceiverId( chatUserMapper.selectOpponentUserid( ((ChatRoomEnterMessagePayload)receiveMsg.getPayload()).getRoomidx(), receiveMsg.getSenderId() ) ).getReceiverId())) {
				//조회 실패
				return null;
			}
		}
		
		//chat_user.chat_detail_idx 최신화
		if(1 != chatUserMapper.updateChatDetailIdxToEnd(
				((ChatRoomEnterMessagePayload)receiveMsg.getPayload()).getRoomidx(),
				receiveMsg.getSenderId()
		)) {
			//업데이트 실패시 처리
			return null;
		}
		
		//채팅방의 제일 최근부터 메시지 30개를 가져온다
		if(null == (bulk.setChatDetails(chatDetailMapper.selectBulkMessagesByChatRoomIdx(
				((ChatRoomEnterMessagePayload)receiveMsg.getPayload()).getRoomidx(), 
				0L, 
				30L))).getChatDetails()) {
			//조회 실패시 처리
			return null;
		}

		//나머지 속성 부여
		//메시지 수가 30개가 아닌경우 제일 첫 메시지를 가져온 것임으로 체크
		bulk
			.setRoomidx(
					((ChatRoomEnterMessagePayload)receiveMsg.getPayload()).getRoomidx())
			.setStartChatDetailIdx(
					bulk.getChatDetails().isEmpty()
							? null
							: bulk.getChatDetails().get(bulk.getChatDetails().size() - 1).getChatDetailIdx())
			.setEndChatDetailIdx(
					bulk.getChatDetails().isEmpty()
					? null
					: bulk.getChatDetails().get(0).getChatDetailIdx())
			.setIsFirst(bulk.getChatDetails().size() < 30 ? true : false)
			.setRequestUserid(receiveMsg.getSenderId());
		
		//MessageDTO 구성후 반환
		MessageDTO<ChatDetailBulkMessagePayload> msg = new MessageDTO<>();
		return msg
			.setAct(receiveMsg.getAct())
			.setPayload(bulk)
			.setSenderId(receiveMsg.getSenderId())
			//!!매개변수 doesNeedToLoadUser가 false인 경우 null이 대입됨
			.setReceiverId(receiveMsg.getReceiverId());
	}
	
	//채팅 수신 처리
	@Override
	@Transactional
	public MessageDTO<ChatContentDetailMessagePayload> chatReceiveHandling(MessageDTO<? extends MessagePayload> receivedMsg) {
		//chatRoomIdx
		Long chatRoomIdx = ((ChatContentMessagePayload)receivedMsg.getPayload()).getRoomidx();
		//현재시간
		LocalDateTime current = LocalDateTime.now();
		
		//일단 삽입
		if(1 != chatDetailMapper.insertChat(
				chatRoomIdx,
				receivedMsg.getSenderId(),
				((ChatContentMessagePayload)receivedMsg.getPayload()).getChatContent(),
				current
		)) {
			//삽입 실패 처리
			return null;
		}
		
		//채팅방 최신 메시지(방금 넣은 메시지)의 chat_detail_idx 가져오기
		ChatDetailDTO temp;
		if(null == (temp = chatDetailMapper.selectLastRowByChatRoomIdx(chatRoomIdx))) {
			//조회 실패 처리
			return null;
		}
		
		//title 처리
		//chat_room 가져오기
		ChatRoomDTO crdto;
		String title;
		if(null == (crdto = chatRoomMapper.selectRowByChatRoomIdx(chatRoomIdx))) {
			//조회 실패 처리
			return null;
		}
		
		if(null == crdto.getPackagenum()) {
			//일반 채팅방
			title = crdto.getChatRoomTitle();
		} else {
			//패키지 채팅방
			//채팅방의 패키지명 가져오기
			if(null == (title = chatInvadingMapper.selectPackageNameByChatRoomIdx(chatRoomIdx))) {
				//조회 실패 처리
				return null;
			}
		}
		
		//메시지 송신자 chat_user.chat_detail_idx 최신화
		if(1 != chatUserMapper.updateChatDetailIdx(chatRoomIdx, receivedMsg.getSenderId(), temp.getChatDetailIdx())) {
			//수정 실패 처리
			return null;
		}
		
		//payload 초기화
		ChatContentDetailMessagePayload sendPayload = new ChatContentDetailMessagePayload();
		sendPayload
			.setRoomidx(chatRoomIdx)
			.setChatDetailIdx(temp.getChatDetailIdx())
			.setTitle(title)
			.setUserid(receivedMsg.getSenderId())
			.setChatContent(temp.getChatDetailContent())
			.setRegdate(current);
		
		//MessageDTO 구성 후 반환
		MessageDTO<ChatContentDetailMessagePayload> msg = new MessageDTO<>();
		return msg
			.setAct(receivedMsg.getAct())
			.setPayload(sendPayload)
			.setSenderId(receivedMsg.getSenderId());
	}
	
	//채팅 로드 처리
	@Override
	public MessageDTO<ChatDetailBulkMessagePayload> chatLoadHandling(MessageDTO<? extends MessagePayload> receivedMsg) {
		//chatRoomIdx
		Long chatRoomIdx = ((ChatLoadMessagePayload)receivedMsg.getPayload()).getRoomidx();
		//startChatDetailIdx
		Long startChatDetailIdx = ((ChatLoadMessagePayload)receivedMsg.getPayload()).getStartChatDetailIdx();
		
		//가져오기
		List<ChatDetailDTO> list;
		if(null == (list = chatDetailMapper.selectBulkMessagesByChatDetailIdx(chatRoomIdx, startChatDetailIdx, 30L))) {
			//조회 실패 처리
			return null;
		}
		
		//payload 초기화
		ChatDetailBulkMessagePayload sendPayload = new ChatDetailBulkMessagePayload();
		sendPayload
			.setRoomidx(chatRoomIdx)
			.setStartChatDetailIdx(list.isEmpty() ? null : list.get(list.size() - 1).getChatDetailIdx())
			.setEndChatDetailIdx(list.isEmpty() ? null : list.get(0).getChatDetailIdx())
			.setIsFirst(list.size() < 30 ? true : false)
			.setChatDetails(list);
		//MessageDTO 구성 후 반환
		return new MessageDTO<ChatDetailBulkMessagePayload>()
				.setAct("loadChat")
				.setPayload(sendPayload);
	}

	//채팅방 리스트 가져오기
	@Override
	public List<ChatListPayloadDTO> selectChatList(String userid) {
		//유효성
		
		/*
		 * 240603
		 * 이제 한번에 모든 채팅방을 가져온다
		 * */
		
		Integer chatRoomCnt;
		if(null == (chatRoomCnt = chatUserMapper.selectCountOfSpecificChatUserByUserid(userid))) {
			//채팅방 개수 조회 실패
			return null;
		}
		if(chatRoomCnt == 0) {
			//userid가 가입한 채팅방이 없는 경우(조회된 행이 0개인 경우)
			return new ArrayList<ChatListPayloadDTO>();
		}
		
		//반환할 List 객체
		List<ChatListPayloadDTO> resultPayload = new ArrayList<>();
		
		//userid가 가입한 채팅방 중 닫히지 않은
		//    (chat_user.chat_user_is_quit = false, chat_room.chat_room_is_terminated = false)
		//채팅방의
		//    chat_room_idx, chat_user_is_creator, regdate(채팅방 개설 시간), last_msg_date(해당 채팅방의 마지막 메시지 regdate)
		//를 가져온다
		/*!!주의 last_msg_date는 null일 수 있음 <- 메시지가 없는 채팅방*/
		List<Map<String, Object>> targetList;
		if(null == (targetList = chatUserMapper.selectAllByUserid(userid))) {
			//타겟 조회 실패
			return null;
		}
		
		//채팅방 정보 가져오기
		/*
		 * 일단 null체크는 안함
		 * */
		for(Map<String, Object> tgt : targetList) {
			Long crIdx = (Long) tgt.get("chat_room_idx");
			ChatListPayloadDTO clplDTO = new ChatListPayloadDTO();
			
			//roomidx 할당
			clplDTO.setRoomidx(crIdx);
			
			//packagenum, package_title/chat_room_title 삽입
			if(tgt.get("packagenum") == null) {
				//일반<->일반 채팅
				clplDTO.setPkgnum(null)
						.setTitle(null == tgt.get("chat_room_title") ? null : (String)tgt.get("chat_room_title"));
			} else {
				//판매자<->일반 채팅
				/*
				 * 이거 chatInvadingMapper 나중에라도 수정해라
				 * 이제 Map<> 써서 가져올 필요가 없음 그냥 cr.packagenum으로 package.package_title만 가져오면 됨
				 * */
				Map<String, Object> pkgInfo = chatInvadingMapper.selectPackageInfoByChatRoomIdx(crIdx);
				clplDTO.setPkgnum((Long)pkgInfo.get("packagenum"))
						.setTitle((String)pkgInfo.get("package_title"));
			}
			
			//채팅방의 상대 유저 userid 가져오기
			/*상대 유저 "들" 가져오기로 변경 - 240603*/
			clplDTO.setUserList(chatUserMapper.selectOpponentUserInfo(crIdx, userid));
			
			//상대 유저들 이미지 가져오기
			List<UserImgDTO> imgList = new ArrayList<>();
			for(ChatUserDTO dto : clplDTO.getUserList()) {
				imgList.add(userServiceImpl.getProfileImgDTO(dto.getUserid()));
			}
			clplDTO.setUserImage(imgList);
			
			//채팅방의 마지막 메시지 정보(chat_detail_content, regdate 가져오기)
			/*채팅방에 메시지가 없는 경우도 고려해야함*/
			if(tgt.get("last_msg_date") != null) {
				ChatDetailDTO lastChat = chatDetailMapper.selectLastRowByChatRoomIdx(crIdx);
				clplDTO.setChatContent(lastChat.getChatDetailContent())
						.setChatContentRegdate((LocalDateTime)tgt.get("last_msg_date"));
			} else {
				clplDTO.setChatContent(null)
						.setChatContentRegdate(null);
			}
			clplDTO.setChatRegdate((LocalDateTime)tgt.get("regdate"));
			
			//채팅방의 미확인 채팅 개수 가져오기
			/*모든 메시지를 확인했으면 0임*/
			clplDTO.setUncheckedmsg(chatDetailMapper.selectSpecificCountOfChatDetail(crIdx, userid));
			
			//isCreator 설정
			clplDTO.setIsCreator((Boolean) tgt.get("chat_user_is_creator"));
			
			//삽입
			resultPayload.add(clplDTO);
		}
		return resultPayload;
	}
	
	//안 읽은 채팅 개수 가져오기
	//모든 채팅 개수를 가져온다
	@Override
	public Integer selectCountOfUnreadChatByUserid(String userid) {
		int count = 0;
		
		//userid가 가입한 모든 채팅방 정보를 가져온다
		List<ChatUserDTO> chatlist = chatUserMapper.selectSpecificChatUserByUserid(userid);
		
		for(ChatUserDTO tgt : chatlist) {
			Integer cnt;
			cnt = chatDetailMapper.selectSpecificCountOfChatDetail(tgt.getChatRoomIdx(), tgt.getUserid());
			count += cnt;
		}
		return count;
	}

}
