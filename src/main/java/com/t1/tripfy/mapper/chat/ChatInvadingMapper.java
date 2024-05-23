package com.t1.tripfy.mapper.chat;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

//채팅 관련 쿼리문 중 다른 테이블에 접근하는 친구들을 모아둠
@Mapper
public interface ChatInvadingMapper {
	Map<String, Object> selectPackageInfoByChatRoomIdx(Long chatRoomIdx);
}