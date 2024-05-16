package com.t1.tripfy.domain.dto.chat;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatRoomDTO {
	private Long chatRoomIdx;
	private Long packagenum;
}
