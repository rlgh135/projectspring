package com.t1.tripfy.controller.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.t1.tripfy.domain.dto.chat.ChatListPayloadDTO;
import com.t1.tripfy.service.chat.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
public class ChatRestController {
	@Autowired
	private ChatService chatSV;
	
	//채팅 생성
	@PostMapping
	public ResponseEntity<Object> createChat(
			@SessionAttribute(name="loginUser", required=false) String loginUserId,
			@RequestParam Long packagenum
			) {
		
		if(loginUserId == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		if(packagenum == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		chatSV.createChat(loginUserId, packagenum);
		
		return null;
	}
	
	//채팅방 리스트 요청
	@GetMapping
	public ResponseEntity<List<ChatListPayloadDTO>> getChatList(
			@SessionAttribute(name="loginUser", required=false) String loginUserId
			) {
		
		System.out.println("ChatRestController.getChatList 진입");
		
	//유효성
		if(loginUserId == null) {
			System.out.println("로그인 체크 통과 실패");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			//postman 테스트용
			//일반
//			loginUserId = "testUserId01";
			//메시지 없는 채팅방
//			loginUserId = "testNoMessageInChatUser06";
			//가입한 채팅방이 없는 유저
//			loginUserId = "testNoChattingUser05";
		}
		
	//서비스
		//일단 받기
		List<ChatListPayloadDTO> chatList = chatSV.selectChatList(loginUserId);
	
	//결과 체크
		//DB접근 실패 체크
		/*모든 Service단에서의 실패는 500으로 전송*/
		if(chatList == null) {
			//실패 처리
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		//chatList가 비어있는지(userid가 가입한 채팅방이 없는 경우)는 체크하지 않음
		//아마 프론트에서 처리할 수 있을거임
		
		System.out.println(chatList);
		
		return new ResponseEntity<List<ChatListPayloadDTO>>(chatList, HttpStatus.OK);
	}
}
