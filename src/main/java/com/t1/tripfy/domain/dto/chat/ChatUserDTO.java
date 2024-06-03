package com.t1.tripfy.domain.dto.chat;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatUserDTO {
	private Long chatRoomIdx;
	private String userid;
	private Boolean chatUserIsCreator;
	private Boolean chatUserIsQuit;
	private Long chatDetailIdx;
}
