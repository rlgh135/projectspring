package com.t1.tripfy.domain.dto.chat.cachelike;

import java.util.Objects;

import lombok.Getter;

/**
 * <p>일단 wip -240531
 * */
@Getter
public class CacheKey {
	private final Long chatRoomIdx;
	private final String generalUserId;
	private final String sellerUserId;
	
	public CacheKey(Long chatRoomIdx, String generalUserId, String sellerUserId) {
		this.chatRoomIdx = chatRoomIdx;
		this.generalUserId = generalUserId;
		this.sellerUserId = sellerUserId;
	}

	@Override
	public int hashCode() {
		// userid중 하나를 미기입한 상황을 고려해 chatRoomIdx만으로 해시코드를 뽑게 함
		return Objects.hash(chatRoomIdx);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || this.getClass() != obj.getClass())
			return false;
		if(this == obj) return true;
		
		CacheKey other = (CacheKey) obj;
		
		// null이 아니고 타입과 chatRoomIdx이 같으면 
		// 두 userid 중 하나만 동일해도(나머지 하나가 null이여도) true 반환
		return chatRoomIdx.equals(other.getChatRoomIdx()) && 
				(
						generalUserId.equals(other.getGeneralUserId()) || 
						sellerUserId.equals(other.getSellerUserId())
				);
	}
	
	
}