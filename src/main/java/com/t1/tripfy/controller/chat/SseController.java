package com.t1.tripfy.controller.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.t1.tripfy.service.chat.SseEmitterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sse/*")
public class SseController {
	@Autowired
	private SseEmitterService sseSv;
	
	//클라이언트 SSE subscribe 요청 처리
	@GetMapping(path="/subscribe", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribe(
			@RequestHeader(value="Last-Event-ID", required=false, defaultValue="") String lastEventID,
			@RequestParam String clientid) {
		
		if(sseSv != null) {
			SseEmitter emit = sseSv.subscribe(lastEventID, clientid);
			return ResponseEntity.ok(emit);
		} else {
			System.out.println("ssibal");
			return ResponseEntity.internalServerError().build();
		}
		
	}
}
