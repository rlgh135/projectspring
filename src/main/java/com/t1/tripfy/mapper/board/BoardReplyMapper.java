package com.t1.tripfy.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.board.BoardReplyDTO;

@Mapper
public interface BoardReplyMapper {

	// boardnum으로 댓글 개수
	int getReplyCnt(long boardnum);

	int insertReply(BoardReplyDTO reply);

	BoardReplyDTO getLastReply(String userid);

	long getTotal(long boardnum);

	List<BoardReplyDTO> getReplyList(int amount, int startrow, long boardnum);

	int updateReply(BoardReplyDTO reply);

	int deleteReply(BoardReplyDTO reply);

	BoardReplyDTO getReplyByReplyNum(long replynum);
}
