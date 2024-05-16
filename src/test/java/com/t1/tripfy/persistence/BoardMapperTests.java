package com.t1.tripfy.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.board.BoardDTO;
import com.t1.tripfy.mapper.board.BoardMapper;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BoardMapperTests {
	@Autowired
	private BoardMapper mapper;
	
	@Test
	// 주입 확인
	public void testExist() {
		assertNotNull(mapper);
	}
	
	@Test
	public void getBoardByNumTest() {
		BoardDTO board = mapper.getBoardByBoardNum(1);
		System.out.println("result: " + board);
	}
}
