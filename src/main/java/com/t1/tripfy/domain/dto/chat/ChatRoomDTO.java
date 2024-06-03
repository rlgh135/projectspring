package com.t1.tripfy.domain.dto.chat;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatRoomDTO {
	private Long chatRoomIdx;
	private String chatRoomTitle;
	private Long packagenum;
	private Boolean chatRoomIsTerminated;
	private LocalDateTime regdate;
}
