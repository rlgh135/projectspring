package com.t1.tripfy.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.t1.tripfy.domain.dto.user.UserDTO;
import com.t1.tripfy.mapper.user.UserMapper;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserMapperTests {
	@Autowired
	private UserMapper umapper;
	
//	@Test
	public void testExist() {
		assertNotNull(umapper);
	}
	
//	@Test
	public void insertUserTest() {
		UserDTO user = new UserDTO();
		user.setUserid("a");
		user.setUserpw("b");
		user.setPhone("c");
		user.setEmail("d");
		user.setGender("e");
		user.setBirth("f");
		user.setAddr("g");
		user.setPlaceid("h");
		
		boolean result = umapper.insertUser(user)==1;
		System.out.println("insertUserTest 결과: "+result);
	}
	
	@Test
	public void getUserByIdTest() {
		UserDTO user = umapper.getUserById("testid");
		System.out.println("getUserByIdTest 결과: "+user);
	}
}
