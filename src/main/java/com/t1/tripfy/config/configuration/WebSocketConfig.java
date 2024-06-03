package com.t1.tripfy.config.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.t1.tripfy.config.handler.WebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	@Autowired
	private WebSocketHandler webSocketHandler;
	
	//웹소켓 핸들러 등록
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler, "/wschat")
				.addInterceptors(new HttpSessionHandshakeInterceptor())
				.setAllowedOrigins("*"); //CORS
	}
	
	//아마 웹소켓 설정
	//참조
	//https://velog.io/@umtuk/%EB%A0%88%EC%8B%9C%ED%94%BC-5-4-%EC%9B%B9%EC%86%8C%EC%BC%93
	//https://devel-repository.tistory.com/39
//	@Bean
//	private ServletServerContainerFactoryBean configureWebSocketContainer() {
//		ServletServerContainerFactoryBean fac = new ServletServerContainerFactoryBean();
//		fac.setMaxBinaryMessageBufferSize(1024 * 16); //16KB - 바이너리 메시지의 최대 버퍼 크기
//		fac.setMaxTextMessageBufferSize(1024 * 16); //16KB - 텍스트 메시지의 최대 버퍼 크기
//		fac.setMaxSessionIdleTimeout(TimeUnit.MINUTES.convert(30, TimeUnit.MILLISECONDS));
//		fac.setAsyncSendTimeout(TimeUnit.SECONDS.convert(5, TimeUnit.MILLISECONDS));
//		// 각각 비동기 세션 타임아웃 - 30분, 비동기 전송 타임아웃 - 5초
//		// 핑퐁 보낼거 생각해서 변경해줘야 함
//		return fac;
//	} 오류로 인해 일단 주석처리함
}