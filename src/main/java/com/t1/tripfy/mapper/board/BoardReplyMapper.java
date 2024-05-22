package com.t1.tripfy.mapper.board;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardReplyMapper {

	// boardnum으로 댓글 개수
	int getReplyCnt(long boardnum);

}
