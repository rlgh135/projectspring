package com.t1.tripfy.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.t1.tripfy.domain.dto.chat.payload.ChatRoomEnterMessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.ChatTempTestMessagePayload;

/* jackson jsonTypeInfo, JsonSubTypes 관련 참조
 * https://kdohyeon.tistory.com/99
 * https://velog.io/@happyjamy/Jackson-%EC%9D%B4-%EA%B0%9D%EC%B2%B4%EB%A5%BC-%EB%A7%8C%EB%93%9C%EB%8A%94-%EB%B2%95-InvalidDefinitionException
 * https://velog.io/@conatuseus/RequestBody%EC%97%90-%EC%99%9C-%EA%B8%B0%EB%B3%B8-%EC%83%9D%EC%84%B1%EC%9E%90%EB%8A%94-%ED%95%84%EC%9A%94%ED%95%98%EA%B3%A0-Setter%EB%8A%94-%ED%95%84%EC%9A%94-%EC%97%86%EC%9D%84%EA%B9%8C-3-idnrafiw#%EC%96%B4%EB%96%BB%EA%B2%8C-objectmapper%EA%B0%80-json-field%EC%99%80-java-field%EB%A5%BC-%EB%A7%A4%EC%B9%AD%EC%8B%9C%ED%82%AC%EA%B9%8C
 * https://isaac1102.github.io/2021/04/28/jackson
 * */

@JsonSubTypes({
	@JsonSubTypes.Type(value=ChatRoomEnterMessagePayload.class, name="chatRoomEnter"),
	@JsonSubTypes.Type(value=ChatTempTestMessagePayload.class, name="notForRealCall")
})
public interface MessagePayload {

}