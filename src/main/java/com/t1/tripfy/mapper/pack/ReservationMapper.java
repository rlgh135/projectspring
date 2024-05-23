package com.t1.tripfy.mapper.pack;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.t1.tripfy.domain.dto.Criteria;
import com.t1.tripfy.domain.dto.ReservationDTO;

@Mapper
public interface ReservationMapper {
	//C
	//R
	//회원 예약리스트
	List<ReservationDTO> getMyReservation(Criteria cri, String userid);
	//U
	//D
}
