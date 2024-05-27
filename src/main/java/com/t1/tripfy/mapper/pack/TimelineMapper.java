package com.t1.tripfy.mapper.pack;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import com.t1.tripfy.domain.dto.TimelineDTO;

@Mapper
public interface TimelineMapper {
	//c
	int insertTimeline(TimelineDTO tl);
	//r
	ArrayList<TimelineDTO> getTlDayList(long packagenum, int day);
	int deleteTimelineByDayAndDetailNum(TimelineDTO tl);
	boolean updateTimelineDetailNum(TimelineDTO tl);
	TimelineDTO getLastDetailNum(TimelineDTO tl);
	String getTimeLineContent(TimelineDTO tl);
}



