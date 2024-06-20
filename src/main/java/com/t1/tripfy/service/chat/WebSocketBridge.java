package com.t1.tripfy.service.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.t1.tripfy.config.handler.WebSocketHandler;
import com.t1.tripfy.domain.dto.chat.MessagePayload;

@Service
public class WebSocketBridge {
	@Autowired @Lazy
	private WebSocketHandler wsh;
	
	public void broadcastBridge(String action, MessagePayload payload, long targetRoomIdx, String userThatExcept) {
		try {
			wsh.broadcastRequestHandler(action, payload, targetRoomIdx, userThatExcept);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
