package com.t1.tripfy.mapper.pack;

import org.apache.ibatis.annotations.Mapper;
import com.t1.tripfy.domain.dto.TimelineDTO;

@Mapper
public interface TimelineMapper {
	//c
	int insertTimeline(TimelineDTO tl);
}



