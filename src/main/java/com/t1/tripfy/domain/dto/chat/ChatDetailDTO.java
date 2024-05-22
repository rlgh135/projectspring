package com.t1.tripfy.domain.dto.chat;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatDetailDTO {
	private Long chatDetailIdx;
	private Long chatRoomIdx;
	private String userid;
	private String chatDetailContent;
	private LocalDateTime regdate;
}
