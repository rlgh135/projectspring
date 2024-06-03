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
import com.t1.tripfy.domain.dto.chat.ChatUserDTO;
import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatContentMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatLoadMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatRoomEnterMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatContentDetailMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.sender.ChatDetailBulkMessagePayload;
import com.t1.tripfy.mapper.chat.ChatDetailMapper;
import com.t1.tripfy.mapper.chat.ChatInvadingMapper;
import com.t1.tripfy.mapper.chat.ChatRoomMapper;
import com.t1.tripfy.mapper.chat.ChatUserMapper;

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
	
	/*
	 	중요!!
	 	기능을 기준으로 메서드 나눌것
	*/
	
	@Override
	public Long createChat(String userid, Long packagenum) {
		return null;
	}
	
	//채팅방 진입
	// !!채팅방 진입시 chat_user.chat_detail_idx 수정해야함
	@Override
	@Transactional
	public MessageDTO<ChatDetailBulkMessagePayload> chatRoomEnterHandling(MessageDTO<? extends MessagePayload> receiveMsg) {
		ChatDetailBulkMessagePayload bulk = new ChatDetailBulkMessagePayload();
		String opperUserid;
		
		//상대 유저의 이름이 오지 않았으면 DB서 가져온다
//		if(null == (opperUserid = receiveMsg.getReceiverId())) {
//			if(null == (opperUserid = chatUserMapper.selectOpponentUserid(((ChatRoomEnterMessagePayload)receiveMsg.getPayload()).getRoomidx()
//					, receiveMsg.getSenderId()))) {
//				//조회 실패시 처리
//				return null;
//			}
//		}
//		테스트를 위해 주석처리 - 240603
		
		
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
			.setIsFirst(bulk.getChatDetails().size() < 30 ? true : false);
		
		//MessageDTO 구성후 반환
		MessageDTO<ChatDetailBulkMessagePayload> msg = new MessageDTO<>();
		return msg
			.setAct(receiveMsg.getAct())
			.setPayload(bulk)
			.setSenderId(receiveMsg.getSenderId())
//			.setReceiverId(opperUserid);
			;
//		테스트를 위해 주석처리 - 240603
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
		
		//채팅방 최신 메시지의 chat_detail_idx 가져오기
		ChatDetailDTO temp;
		if(null == (temp = chatDetailMapper.selectLastRowByChatRoomIdx(chatRoomIdx))) {
			//조회 실패 처리
			return null;
		}
		
		//채팅방의 패키지명 가져오기
		String pkgname;
		if(null == (pkgname = chatInvadingMapper.selectPackageNameByChatRoomIdx(chatRoomIdx))) {
			//조회 실패 처리
			return null;
		}
		
		//수신자 기준 미확인 메시지 개수 가져오기
//		Integer unchk;
//		if(null == (unchk = chatDetailMapper.selectSpecificCountOfChatDetail(chatRoomIdx, receivedMsg.getReceiverId()))) {
//			//조회 실패 처리
//			return null;
//		}
		Integer unchk = null;
//		테스트를 위해 주석처리 - 240603
		
		System.out.println(temp.getChatDetailIdx());
		
		//메시지 송신자 chat_user.chat_detail_idx 최신화
		if(1 != chatUserMapper.updateChatDetailIdx(chatRoomIdx, receivedMsg.getSenderId(), temp.getChatDetailIdx())) {
			//수정 실패 처리
			return null;
		}
		
		//payload 초기화
		ChatContentDetailMessagePayload sendPayload = new ChatContentDetailMessagePayload();
		sendPayload
			.setRoomidx(chatRoomIdx)
			.setPkgname(pkgname)
			.setUserid(receivedMsg.getSenderId())
			.setChatContent(temp.getChatDetailContent())
			.setRegdate(current)
			.setUncheckedmsg(unchk);
		
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
		
		/* userid로 ChatUserDTO를 긁어온다
		 * ChatUserDTO의 chat_room_idx로
		 *     chat_room의 packagenum, packagenum으로 package.package_title을 가져오고
		 *     chat_user에서 가입해있는 상대방 사용자의 userid를 가져오고
		 *     chat_detail에서 해당 채팅방의 최신 메시지를 가져온다
		 *         (chat_detail_content, regdate)
		 * ChatUserDTO의 chat_detail_idx로
		 *     cu.cdi < cd.cdi ? 를 돌려서 안 읽은 채팅 개수도 가져온다
		 * 
		 * 이 메서드는 일반사용자 뿐만이 아니라 패키지 판매자도 사용하게 만들거임
		 * 따라서 chat_user.chat_user_is_seller 등의 컬럼은 가져오지 않는다
		 * 1대1 채팅만 구현하는 상황이니 상대방이 판매자인지 체크할 필요가 없음
		 *     내가 일반사용자면 상대가 판매자고 내가 판매자여도 마찬가지 - 240523
		 *     
		 * 240528 좆됐다
		 * 이거 서버단에서 정렬해서 보내줘야하는데 안했다
		 * 다시짠다
		 * 
		 * 우선 userid의 최근 갱신된 채팅방중 x ~ y번째를 불러온다
		 * 이 결과값을 List<Map<String, Object>>으로 담아온다
		 * 
		 * 해당 값을 기준으로 기존 로직을 반복하면 됨
		 * 
		 * end > chatRoomCnt ? return null 이거 지움
		 * 
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
			
			//채팅방의 packagenum, package_title 가져와서 roomidx와 같이 넣기
			Map<String, Object> pkgInfo = chatInvadingMapper.selectPackageInfoByChatRoomIdx(crIdx);
			clplDTO.setRoomidx(crIdx)
					.setPkgnum((Long)pkgInfo.get("packagenum"))
					.setPkgname((String)pkgInfo.get("package_title"));
			
			//채팅방의 상대 유저 userid 가져오기
			/*상대 유저 "들" 가져오기로 변경 - 240603*/
			clplDTO.setUserList(chatUserMapper.selectOpponentUserInfo(crIdx, userid));
			
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
