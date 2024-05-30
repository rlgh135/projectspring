package com.t1.tripfy.config.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.tripfy.domain.dto.chat.MessageDTO;
import com.t1.tripfy.domain.dto.chat.MessagePayload;

@Configuration
public class JavaTypeConfig {
	@Autowired
	private ObjectMapper mapper;

    @Bean
    JavaType javaType() {
    	return mapper.getTypeFactory().constructParametricType(MessageDTO.class, MessagePayload.class);
    }
}
