package com.t1.tripfy.controller.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.t1.tripfy.service.chat.SseEmitterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sse/*")
public class SseController {
	@Autowired
	private SseEmitterService sseSv;
	
	/* 서블릿의 HttpSession을 더 편하게 쓰게 해주는 스프링의 @SessionAttribute
	 * https://velog.io/@woply/spring-%EC%84%9C%EB%B8%94%EB%A6%BF%EC%9D%B4-%EC%A0%9C%EA%B3%B5%ED%95%98%EB%8A%94-HttpSession-%EC%8A%A4%ED%94%84%EB%A7%81%EC%9D%B4-%EC%A0%9C%EA%B3%B5%ED%95%98%EB%8A%94-SessionAttribute
	 * */
	
	//클라이언트 SSE subscribe 요청 처리
	@GetMapping(path="/subscribe", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribe(
			@RequestHeader(value="Last-Event-ID", required=false, defaultValue="") String lastEventID,
			@SessionAttribute(name="loginUser", required=false) String userid) {
		
		//SSE 연결 전에 우선 유효성 체크를 해야 함
		if(userid == null) {
			//비로그인 유저의 요청
			// 400 Bad Request
			return ResponseEntity.badRequest().body(null);
		}
		
		log.debug("SSE subscribe from userid={}", userid);
		log.debug("lastEventID={}", lastEventID);
		
		SseEmitter emit = sseSv.subscribe(lastEventID, userid);
		return ResponseEntity.ok(emit);
	}
	/* SSE 안 읽은 메시지 알림 관련
	 * 
	 * SSE로 보낼 메시지는 아래 두개임(연결시 더미값, 핑퐁(구현한다면) 제외)
	 * - 총 안 읽은 메시지 개수
	 * - 채팅방에 메시지 전송(웹소켓)시 각각 알람
	 *   ㄴ 임마는 팝업메시지 등으로 메시지 내용 띄우기 + 클라단 안 읽은 메시지 표시 ++하기의 두개 목적
	 * 
	 * 대충 생각해본게
	 * 매번 SSE 연결시마다(60초) DB단을 찍턴해 메시지 개수를 끌고오는건 좀 개씹에바임
	 * 그래서 생각해본게 클라단에서 안 읽은 메시지 개수를 가지고있는건 어떨까 싶음
	 * 대충 받아서 쿠키같은데 처박고는 실제 알림시 +1만 해주는거임
	 *   ㄴ 근데 그렇게 할거면 SSE로 총 채팅 개수를 보낼 필요도 없을지도 모름
	 *      로그인 이후로는 항상 SSE가 열려있다고 보면
	 *      최초 로그인시 쿠키로 총 안 읽은 메시지 개수를 넣어주고
	 *      메시지 전송-알림 발생시 +1
	 *      알림 누락시 Last-Event-ID로 다시 받아오기 정도?
	 *   쿠키는 브라우저별임 뭐... 대충 페이지별로 다 적용되긴 하겠네
	 * */
}