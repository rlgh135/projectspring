package com.t1.tripfy.domain.dto.chat;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ChatListPayloadDTO {
	private Long roomidx; //chat_room_idx
	private Long pkgnum; //packagenum
	private String title; //package.package_title 혹은 chat_room.chat_room_title
	private LocalDateTime chatRegdate; //채팅방 개설 시간
	private Boolean isCreator;
	private List<ChatUserDTO> userList; //상대 유저 userid(chat_user.userid) !!중요 - 마지막 메시지 송신자가 아님
	private String chatContent; //제일 최신의 챗	
	private LocalDateTime chatContentRegdate; //위 chat의 전송일
	private Integer uncheckedmsg; //미확인 채팅 개수
}

/*
 * roomidx
 * pkgnum
 * pkgname
 * chatRegdate
 * isCreator
 * userList
 * chatContent
 * chatContentRegdate
 * uncheckedmsg
 * */
 