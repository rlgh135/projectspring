package com.t1.tripfy.persistence.chat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.t1.tripfy.domain.dto.chat.ChatUserDTO;
import com.t1.tripfy.mapper.chat.ChatDetailMapper;
import com.t1.tripfy.mapper.chat.ChatUserMapper;

@MybatisTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatMappersTests {
	@Autowired
	private ChatUserMapper cump;
	@Autowired
	private ChatDetailMapper cdmp;
	
	@Test
	@Order(1)
	@DisplayName("01 객체 생성 체크")
	public void testMapperObjectsExist() {
		assertNotNull(cump);
		assertNotNull(cdmp);
	}

	@Test
	@Order(2)
	@DisplayName("02 userid로 chat_user에서 특정 행들만 뽑아내기")
	public void chatUserMapper_selectSpecificChatUserByUseridTest() {
		List<ChatUserDTO> list = cump.selectSpecificChatUserByUserid("testUserId01");
		if(list != null && !list.isEmpty()) {
			for(ChatUserDTO dto : list) {
				System.out.println(dto);
				if(dto.getChatRoomIdx() == null || dto.getUserid() == null || dto.getChatUserIsSeller() == null || dto.getChatDetailIdx() == null) {
					assertNotNull(null);
				}
			}
		} else {
			assertNotNull(null);
		}
		assertNotNull(new Object());
	}

	@Test
	@Order(3)
	@DisplayName("03 chat_detail_idx와 chat_room_idx로 특정 채팅방의 특정 메시지 이후의 메시지 개수 세기")
	public void chatDetailMapper_selectSpecificCountOfChatDetailTest() {
		Integer res = null;
		assertNotNull(res = cdmp.selectSpecificCountOfChatDetail(1L, 5L));
		System.out.println(res);
	}
}
