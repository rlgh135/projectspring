package com.t1.tripfy.domain.dto.board;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardReplyPageDTO {
	private long replyCnt;
	private List<BoardReplyDTO> list;
}
