package com.t1.tripfy.service.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t1.tripfy.domain.dto.chat.ChatDetailDTO;
import com.t1.tripfy.domain.dto.chat.ChatListPayloadDTO;
import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.receiver.ChatRoomEnterMessagePayload;
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
	/*해야할거
	 * 해당 채팅방의 캐시 존재여부 확인
	 *  ㄴ 없을 경우 생성, userid 등록
	 *  ㄴ 있는 경우 userid 등록
	 * 최근채팅 n개 모아서 보내주기
	 *  ㄴ 캐시 있는경우 우선 캐시 순회, 캐시의 메시지(최근 채팅방캐시 개설 후 수신 메시지) 가져오기
	 *      ㄴ 일단 현재는 캐시 구현 x - 무조건 DB 조회 -240531
	 *  ㄴ 캐시가 없거나 캐시 메시지가 n개 미만인 경우 DB 추가순회
	 * 해당 채팅방/userid의 chat_user.chat_detail_idx 갱신하기
	 * */
	@Override
	public MessageDTO<ChatDetailBulkMessagePayload> chatRoomEnterHandling(MessageDTO<? extends MessagePayload> receiveMsg) {
		ChatDetailBulkMessagePayload bulk = new ChatDetailBulkMessagePayload();
		String opperUserid;
		
		//상대 유저의 이름이 오지 않았으면 DB서 가져온다
		if(null == (opperUserid = receiveMsg.getReceiverId())) {
			if(null == (opperUserid = chatUserMapper.selectOpponentUserid(((ChatRoomEnterMessagePayload)receiveMsg.getPayload()).getRoomidx()
					, receiveMsg.getSenderId()))) {
				//조회 실패시 처리
				return null;
			}
		}
		
		//채팅방의 제일 최근부터 메시지 30개를 가져온다
		if(null == (bulk.setChatDetails(chatDetailMapper.selectBulkMessagesByChatRoomIdx(
				((ChatRoomEnterMessagePayload)receiveMsg.getPayload()).getRoomidx(), 
				0, 
				30))).getChatDetails()) {
			//조회 실패시 처리
			return null;
		}

		//나머지 속성 부여
		//메시지 수가 30개가 아닌경우 제일 첫 메시지를 가져온 것임으로 체크
		bulk
			.setRoomidx(
					((ChatRoomEnterMessagePayload)receiveMsg.getPayload()).getRoomidx())
			.setStartChatDetailIdx(
					bulk.getChatDetails().get(bulk.getChatDetails().size() - 1).getChatDetailIdx())
			.setEndChatDetailIdx(
					bulk.getChatDetails().get(0).getChatDetailIdx())
			.setIsFirst(bulk.getChatDetails().size() < 30 ? true : false);
		
		//MessageDTO 구성후 반환
		MessageDTO<ChatDetailBulkMessagePayload> msg = new MessageDTO<>();
		return msg
			.setAct(receiveMsg.getAct())
			.setPayload(bulk)
			.setSenderId(receiveMsg.getSenderId())
			.setReceiverId(opperUserid);
	}

	//채팅방 리스트 가져오기
	@Override
	public List<ChatListPayloadDTO> selectChatList(Integer start, Integer end, String userid) {
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
//		if(Integer.compare(end, chatRoomCnt) > 0) {
//			//잘못된 end 값(채팅방 개수를 넘긴 end)
//			return null;
//		}
		
		//반환할 List 객체
		List<ChatListPayloadDTO> resultPayload = new ArrayList<>();
		//조회 타겟
		List<Map<String, Object>> targetList;
		
		if(null == (targetList = chatUserMapper.selectSpecificRecentReceivedChatRoomIdx(start, end, userid))) {
			//userid의 start ~ end 번째 최근 갱신 채팅방 조회를 실패한 경우
			return null;
		}
		
		//채팅방 정보 가져오기
		/*
		 * 일단 null체크는 안함
		 * */
		for(Map<String, Object> tgt : targetList) {
			Long crIdx = (Long) tgt.get("chat_room_idx");
			ChatListPayloadDTO clplDTO = new ChatListPayloadDTO();
			
			//채팅방의 packagenum, package_title 가져오기
			Map<String, Object> pkgInfo = chatInvadingMapper.selectPackageInfoByChatRoomIdx(crIdx);
			clplDTO.setRoomidx(crIdx)
					.setPkgnum((Long)pkgInfo.get("packagenum"))
					.setPkgname((String)pkgInfo.get("package_title"));
			
			//채팅방의 상대 유저 userid 가져오기
			clplDTO.setUserid(chatUserMapper.selectOpponentUserid(crIdx, userid));
			
			//채팅방의 마지막 메시지 정보(chat_detail_content, regdate 가져오기)
			/*채팅방에 메시지가 없는 경우도 고려해야함*/
			ChatDetailDTO lastChat = chatDetailMapper.selectLastRowByChatRoomIdx(crIdx);
			if(lastChat == null) {
				//채팅방에 메시지가 없는 경우
				clplDTO.setRecentchatbody(null)
						.setRegdate(null);
			} else {
				//메시지가 있는 경우
				clplDTO.setRecentchatbody(lastChat.getChatDetailContent())
						.setRegdate(lastChat.getRegdate());
			}
			
			//채팅방의 미확인 채팅 개수 가져오기
			/*모든 메시지를 확인했으면 0임*/
			clplDTO.setUncheckedmsg(chatDetailMapper.selectSpecificCountOfChatDetail(crIdx, userid));
			
			//삽입
			resultPayload.add(clplDTO);
		}
		return resultPayload;
	}
	
	//안 읽은 채팅 개수 가져오기
	/**
	 * <p>아마... 안쓸거임 이거
	 * <p>그래도 혹시 모르니 일단 냅둠
	 * @deprecated
	 * */
	@Override
	public Integer selectCountOfUnreadChatByUserid(String userid) {
		return null;
	}

}
