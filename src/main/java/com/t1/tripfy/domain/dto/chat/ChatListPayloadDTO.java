package com.t1.tripfy.domain.dto.chat;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatListPayloadDTO {
	private Long roomidx; //chat_room_idx
	private Long pkgnum; //packagenum
	private String pkgname; //package.package_title
	private String userid; //상대 유저 userid(chat_user.userid) !!중요 - 마지막 메시지 송신자가 아님
//	private Long recentchatidx; //해당 채팅방의 제일 최신의 chat_detail_idx <- 이거 필요없어보임
	private String chatContent; //위의 chat의 본문
	private LocalDateTime regdate; //위 chat의 전송일
	private Integer uncheckedmsg; //미확인 채팅 개수
}
