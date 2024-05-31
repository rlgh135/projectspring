package com.t1.tripfy.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;
import com.t1.tripfy.domain.dto.chat.payload.ChatRoomEnterMessagePayload;

/* 위치?
 * @Component 붙이고 configuration로 옮기기?
 * 근데 임마는 아마 리플렉션+어노테이션으로 호출되는거같아서 굳이 그럴 필요는 없어보임
 * 
 * MessageDTO 형식?
 * 거 그냥 제네릭 대신 인터페이스 타입을 박으면 되는거 아닌가
 *   안됨 안되드라 아마 어떤 구현체가 들어갈지 명시가 안되서 그런가싶긴 한데
 * */
public class ChatMessageDTODeserializer extends JsonDeserializer<MessageDTO<? extends MessagePayload>> {
	@Override
	public MessageDTO<? extends MessagePayload> deserialize(JsonParser par, DeserializationContext ctxt)
			throws IOException, JacksonException {
		ObjectMapper mapper = (ObjectMapper) par.getCodec();
		JsonNode node = mapper.readTree(par);
		
		MessageDTO<MessagePayload> msgDTO = new MessageDTO<>();
		
		String act = node.get("act").asText();
		msgDTO.setAct(act);
		
		JsonNode payloadNode = node.get("payload");
		MessagePayload payload;
		
		switch(act) {
		case "chatRoomEnter":
			payload = mapper.treeToValue(payloadNode, ChatRoomEnterMessagePayload.class);
			break;
		default:
			throw new IllegalArgumentException("Invalid act value=" + act);
		}
		
		msgDTO.setPayload(payload);
		
		return msgDTO;
	}
	
}