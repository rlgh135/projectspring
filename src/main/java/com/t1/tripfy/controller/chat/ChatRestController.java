package com.t1.tripfy.controller.chat;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.t1.tripfy.domain.dto.chat.ChatListPayloadDTO;
import com.t1.tripfy.domain.dto.chat.ChatUserDTO;
import com.t1.tripfy.service.chat.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
public class ChatRestController {
	@Autowired
	private ChatService chatSV;
	
	//채팅 생성
	@PostMapping
	public ResponseEntity<ChatListPayloadDTO> createChat(
			@SessionAttribute(name="loginUser", required=false) String loginUserId,/*
			@RequestBody(required=false) Long packagenum,
			@RequestBody(required=false) String title,
			@RequestBody(required=false) List<String> invitee*/
			@RequestBody Map<String, Object> data
			) {
		
		//로그인 검증
		if(loginUserId == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		//반환값 생성
		ChatListPayloadDTO respondDTO;
		
		/*임시*/
		//데이터 변환
		Integer chatRoomType = null != data.get("chatRoomType") ? (Integer)data.get("chatRoomType") : null;
		Long packagenum = null != data.get("packagenum") ? Long.valueOf((Integer)data.get("packagenum")) : null;
		String title = null != data.get("title") ? (String)data.get("title") : null;
		List<String> invitee = null != data.get("invitee") ? (List<String>)data.get("invitee") : null;
		
		System.out.println("========================================\n" + packagenum + "\n========================================");
		
		//다대다챗
		if(chatRoomType == 2) {
			if(packagenum == null || invitee == null || invitee.isEmpty()) {
				//400
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			} else {
				//sv
//				if(null == (respondDTO = ))
			}
		
		} 
		//패키지(문의)챗
		else if(chatRoomType == 1) {
			if(packagenum == null) {
				//400
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			} else {
				//sv
				if(null == (respondDTO = chatSV.createChat(loginUserId, packagenum))) {
					//500
					System.out.println("crccc-1");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				} else {
					//생성 성공
					return ResponseEntity.ok(respondDTO);
				}
			}
		} 
		//일반챗
		else if(chatRoomType == 0){
			/* 
			 * 일반/일반 챗의 경우 초대자를 명시해야 함
			 * List에 적재된 userid들의 유효성은 서비스에서 처리
			 * */
			if(invitee == null || invitee.isEmpty()) {
				//400
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			} else {
				//sv
				if(null == (respondDTO = chatSV.createChat(loginUserId, title, invitee))) {
					//500
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				} else {
					//생성 성공
					return ResponseEntity.ok(respondDTO);
				}
			}
		}
		
		/*chatRoomType 값이 잘못된 경우*/
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
	
	//유저 검색
	@GetMapping("/search")
	public ResponseEntity<List<Map<String, Object>>> getSearchUserResult(
			@SessionAttribute(name="loginUser", required=false) String loginUserId,
			@RequestParam String input
	) {
		//유효성
		if(loginUserId == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		//서비스
		
		
		return null;
	}
}
