package com.t1.tripfy.controller.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.t1.tripfy.domain.dto.chat.ChatListPayloadDTO;
import com.t1.tripfy.service.chat.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
public class ChatRestController {
	@Autowired
	private ChatService chatSV;
	
	//채팅방 리스트 요청
	@GetMapping
	public ResponseEntity<List<ChatListPayloadDTO>> getChatList(@RequestParam(required=true) String userid) {
	//유효성
		/*나중에 나중에 진짜 나중에 - 240523*/
		//대충 파라미터로 온 userid == session.getAttribute(userid) 체크
		
	//서비스
		//일단 받기
		List<ChatListPayloadDTO> chatList = chatSV.selectChatList(userid);
	
	//결과 체크
		//DB접근 실패 체크
		if(chatList == null) {
			//실패 처리
		}
		//chatList가 비어있는지(userid가 가입한 채팅방이 없는 경우)는 체크하지 않음
		//아마 프론트에서 처리할 수 있을거임
		
		return new ResponseEntity<List<ChatListPayloadDTO>>(chatList, HttpStatus.OK);
	}
}
