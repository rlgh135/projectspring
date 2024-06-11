package com.t1.tripfy.domain.dto.chat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatRoomDTO {
	private Long chatRoomIdx;
	private String chatRoomTitle;
	private Long packagenum;
	private Boolean chatRoomIsTerminated;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm", timezone="Asia/Seoul")
	private LocalDateTime regdate;
}
