package com.t1.tripfy.persistence.chat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.t1.tripfy.domain.dto.chat.ChatRoomDTO;
import com.t1.tripfy.domain.dto.chat.ChatUserDTO;
import com.t1.tripfy.mapper.chat.ChatDetailMapper;
import com.t1.tripfy.mapper.chat.ChatInvadingMapper;
import com.t1.tripfy.mapper.chat.ChatRoomMapper;
import com.t1.tripfy.mapper.chat.ChatUserMapper;

@MybatisTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatMappersTests {
	@Autowired
	private ChatRoomMapper crmp;
	@Autowired
	private ChatUserMapper cump;
	@Autowired
	private ChatDetailMapper cdmp;
	@Autowired
	private ChatInvadingMapper cimp;
	
	@Test
	@Order(1)
	@DisplayName("01 객체 생성 체크")
	public void testMapperObjectsExist() {
		assertNotNull(crmp);
		assertNotNull(cump);
		assertNotNull(cdmp);
		assertNotNull(cimp);
	}

	@Test
	@Order(2)
	@DisplayName("02 chat_room insert 후 pk 받아오기")
	public void chatRoomMapper_selectSpecificChatUserByUseridTest() {
		ChatRoomDTO dto = new ChatRoomDTO().setChatRoomTitle(null).setPackagenum(30L);
		
		Integer chk = crmp.createRoom(dto);

		assertSame(Integer.valueOf(1), chk);
		assertNotNull(dto.getChatRoomIdx());
		
		System.out.println("chk=" + chk + " cri=" + dto.getChatRoomIdx());
	}
	
	@Test
	@Order(3)
	@DisplayName("03 ChatInvadingMapper에서 packagenum으로 가이드 userid 가져오기")
	public void chatInvadingMapper_selectGuideUseridByPackagenum() {
		String res = cimp.selectGuideUseridByPackagenum(5L);
		
		assertNotNull(res);
		assertTrue(res.equals("chatTestSeller01"));
	}

	@Test
	@Order(4)
	@DisplayName("04 유저 추가")
	public void chatUserMapper_insertRow() {
		List<ChatUserDTO> userList = new ArrayList<>();
		userList.add(new ChatUserDTO()
				.setChatRoomIdx(30L)
				.setUserid("testtest01")
				.setChatUserIsCreator(true));
		userList.add(new ChatUserDTO()
				.setChatRoomIdx(30L)
				.setUserid("testtest02")
				.setChatUserIsCreator(false));
		
		assertSame(Integer.valueOf(2), cump.insertRow(userList));
	}
	
//	@Test
//	@Order(3)
//	@DisplayName("03 chat_detail_idx와 chat_room_idx로 특정 채팅방의 특정 메시지 이후의 메시지 개수 세기")
//	public void chatDetailMapper_selectSpecificCountOfChatDetailTest() {
//		Integer res = null;
//		assertNotNull(res = cdmp.selectSpecificCountOfChatDetail(1L, 5L));
//		System.out.println(res);
//	}
}
