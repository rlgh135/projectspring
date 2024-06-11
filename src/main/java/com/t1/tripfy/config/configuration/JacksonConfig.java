package com.t1.tripfy.config.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@Configuration
public class JacksonConfig {
    @Bean
    ObjectMapper objectMapper() {
		return new ObjectMapper()
				.registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
//				.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE); //매핑 실패시 null 대입
	}
}

/*
 * jackson LocalDatetime 포맷 관련
 * https://ksh-coding.tistory.com/107
 * 일단 어노테이션 박아둠
 * */
