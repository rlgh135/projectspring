package com.t1.tripfy.service.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t1.tripfy.domain.dto.chat.ChatDetailDTO;
import com.t1.tripfy.domain.dto.chat.ChatListPayloadDTO;
import com.t1.tripfy.domain.dto.chat.ChatUserDTO;
import com.t1.tripfy.mapper.chat.ChatDetailMapper;
import com.t1.tripfy.mapper.chat.ChatInvadingMapper;
import com.t1.tripfy.mapper.chat.ChatRoomMapper;
import com.t1.tripfy.mapper.chat.ChatUserMapper;

import lombok.RequiredArgsConstructor;

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
	
	@Override
	public Long createChat(String userid, Long packagenum) {
		return null;
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
		 * */
		List<ChatListPayloadDTO> resultPayload = new ArrayList<>();
		List<ChatUserDTO> chatUserDTOList;
		
		if(null == (chatUserDTOList = chatUserMapper.selectSpecificChatUserByUserid(userid))) {
			//chat_user 조회 실패시 처리
			return null;
		}
		if(chatUserDTOList.isEmpty()) {
			//조회된 행이 없는 경우 - 해당 사용자가 가입한 채팅방이 없는 경우 처리
			//return 뭐 적절한거 리턴
		}
		for(ChatUserDTO cuDTO : chatUserDTOList) {
			ChatListPayloadDTO clplDTO = new ChatListPayloadDTO();
			//채팅방의 packagenum, package_title 가져오기
			Map<String, Object> pkgInfo = chatInvadingMapper.selectPackageInfoByChatRoomIdx(cuDTO.getChatRoomIdx());
			clplDTO.setRoomidx(cuDTO.getChatRoomIdx())
					.setPkgnum((Long)pkgInfo.get("packagenum"))
					.setPkgname((String)pkgInfo.get("package_title"));
			//채팅방의 상대 유저 userid를 가져온다
			clplDTO.setUserid(chatUserMapper.selectOpponentUserid(cuDTO.getChatRoomIdx(), userid));
			//채팅방의 마지막 메시지 정보(chat_detail_content, regdate) 가져오기
			/*채팅방에 채팅이 없는 경우도 생각해봐야됨 -240523*/
			ChatDetailDTO lastChat = chatDetailMapper.selectLastRowByChatRoomIdx(cuDTO.getChatRoomIdx());
			clplDTO.setRecentchatbody(lastChat.getChatDetailContent())
					.setRegdate(lastChat.getRegdate());
			//해당 채팅방의 미확인 채팅 개수 가져오기
			/*이거같은경우 모든 메시지가 확인 상태면 0이 나와야됨*/
			clplDTO.setUncheckedmsg(chatDetailMapper.selectSpecificCountOfChatDetail(cuDTO.getChatRoomIdx(), cuDTO.getChatDetailIdx()));
			
			//수정바람 -240523
			resultPayload.add(clplDTO);
		}
		
		return resultPayload;
	}
	
	//안 읽은 채팅 개수 가져오기
	@Override
	public Integer selectCountOfUnreadChatByUserid(String userid) {
		/* 대충 로직 구상 - 240516
		 * 
		 * + 트랜잭션 넣을까 말까 넣을까 말까 넣을까 말까 넣을까 말까 넣을까 말까 넣ㅁㄴㅇㄹ
		 * 
		 * + 반환값 경우의 수
		 *  - null : 쿼리문 오류 발생시
		 *  - -1 : userid가 가입한 채팅방이 없는 경우
		 *  - 0 : userid가 채팅방에는 들어가 있지만 안 읽은 채팅이 없는 경우
		 *  - 1~99 : 안 읽은 채팅 개수가 1~99개
		 *  - 100 : 안 읽은 채팅 개수가 100개 이상
		 * 
		 * -------------------------------------------------------------------------------
		 * + 개편?
		 *  프론트 레이아웃에 따라 개편이 필요할 수도 있음
		 *  채팅 버튼을 열면 바로 채팅방 리스트가 뜨는 경우
		 *  지금처럼 단순히 채팅 개수를 출력하는게 아니라
		 *  각 채팅방과 각각의 안 읽은 개수를 List등으로 싸서 보내줘야 함
		 * -------------------------------------------------------------------------------
		 * 
		 * 1 chat_user에서 userid가 있는지 확인
		 *  - userid의 유저가 들어가있는 채팅방이 존재하는지 확인
		 *  
		 *  1-1 없는 경우
		 *   - -1 반환
		 *  
		 *  1-2 있는 경우
		 *   - ArrayList<ChatUserDTO>로 묶어서 가져오기
		 * 
		 * 2 가져온 ArrayList<ChatUserDTO>를 까보기
		 *  - 순회하면서 각 채팅방의 마지막 조회 메시지를 파악, 이를 기준으로 안 읽은 개수를 긁어온다
		 * 
		 *  2-1 chat_detail_idx가 null인 경우
		 *   - 해당 채팅방에 메시지가 없거나 임마가 메시지를 하나도 쳐 안 읽은거다
		 *   - 둘다 결국 chat_detail을 까봐야 함
		 *   
		 *   2-1-1 chat_detail 까보기
		 *    - 행의 개수를 세본다
		 *      행의 개수가 바로 임마가 안 읽은 메시지 개수다
		 *      0인 경우 메시지가 없는 채팅방임
		 *    - 결과값을 += 한다
		 *  
		 *  2-2 chat_detail_idx가 null이 아닌 경우
		 *   - 행의 개수를 세는데 조건을 아래와 같이 준다
		 *     chat_room_idx = #{chatRoowIdx} AND chat_detail_idx > #{chatDetailIdx} AND userid != #{userid}
		 *    - 대충 해당 채팅방 메시지 중 마지막 조회 메시지보다 높은(이후 시점의) 메시지, 그 중에서도 사용자가 작성하지 않은
		 *   - 수정
		 *     chat_room_idx = #{chatRoowIdx} AND chat_detail_idx > #{chatDetailIdx}
		 *    - chat_user.chat_detail_idx를 자기 자신의 메시지도 넣게 해두면 채팅개수 select시 userid 검증이 필요없음
		 *      D:\240430_tp3\memo 참조
		 *   - 하여간 뽑은 결과값을 += 한다
		 *   
		 * 3 합산한 결과값을 반환한다
		 *  - ㅇㅇ
		 *  3-1 중간에 오류가 생긴 경우
		 *   - 생길 수가 있나..?
		 *     일단 생기면 null 반환
		 * */
		
		//userid가 들어가 있는 채팅방 리스트 확인
		List<ChatUserDTO> cudList = chatUserMapper.selectSpecificChatUserByUserid(userid);
		//오류 체크
		if(cudList == null) {
			return null;
		}
		//기입된 채팅방이 없으면 -1 반환
		if(cudList.isEmpty()) {
			return -1;
		}
		
		int sumOfUnreadChat = 0;
		
		//각 채팅방을 순회하면서 안 읽은 채팅 개수 확보
		for(ChatUserDTO cud : cudList) {
			//임마가 읽은 채팅이 없는 경우
			if(cud.getChatDetailIdx() == null) {
				Integer res = chatDetailMapper.selectAllCountOfChatDetail(cud.getChatRoomIdx());
				//오류가 발생한 경우 그냥 넘긴다
				if(res != null) {
					sumOfUnreadChat += res;
				}
			} 
			//읽은 채팅이 있는 경우
			else {
				Integer res = chatDetailMapper.selectSpecificCountOfChatDetail(cud.getChatRoomIdx(), cud.getChatDetailIdx());
				//여기서도 오류 발생시 그냥 넘기기
				if(res != null) {
					sumOfUnreadChat += res;
				}
			}
		}
		
		//안 읽은 채팅 개수 반환
		// 100개 이상인 경우 100으로 보낸다(출력을 "99.." 형태로 할 것)
		if(sumOfUnreadChat < 100)
			return sumOfUnreadChat;
		else
			return 100;
	}

}
