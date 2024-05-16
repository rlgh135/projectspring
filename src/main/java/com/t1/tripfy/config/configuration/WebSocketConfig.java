<<<<<<< HEAD
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
		registry.addHandler(webSocketHandler, "/test/*")
				.addInterceptors(new HttpSessionHandshakeInterceptor()) //웹소켓 세션 attribute로 HttpSession 넣어주기
				.setAllowedOrigins("*"); //CORS
	}
	
	//아마 웹소켓 설정
	//참조
	//https://velog.io/@umtuk/%EB%A0%88%EC%8B%9C%ED%94%BC-5-4-%EC%9B%B9%EC%86%8C%EC%BC%93
	//https://devel-repository.tistory.com/39
=======
//package com.t1.tripfy.config.configuration;
//
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
//import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
//
//import com.t1.tripfy.config.handler.WebSocketHandler;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//	@Autowired
//	private WebSocketHandler webSocketHandler;
//	
//	//웹소켓 핸들러 등록
//	@Override
//	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//		registry.addHandler(webSocketHandler, "/test/*")
//				.addInterceptors(new HttpSessionHandshakeInterceptor()) //웹소켓 세션 attribute로 HttpSession 넣어주기
//				.setAllowedOrigins("*"); //CORS
//	}
//	
//	//아마 웹소켓 설정
//	//참조
//	//https://velog.io/@umtuk/%EB%A0%88%EC%8B%9C%ED%94%BC-5-4-%EC%9B%B9%EC%86%8C%EC%BC%93
//	//https://devel-repository.tistory.com/39
>>>>>>> origin/main
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
<<<<<<< HEAD
//	} 오류로 인해 일단 주석처리함
	
	/* #메모 240515
	 *  진행사항
	 *   패키지 생성
	 *    com.t1.tripfy.config
	 *     ㄴ configuration
	 *     ㄴ handler
	 *    com.t1.tripfy.service
	 *     ㄴ chat
	 *   컨피그 클래스 생성
	 *    com.t1.tripfy.config.configuration.WebSocketConfig
	 *     registerWebSocketHandlers(WebSocketHandlerRegistry registry) 오버라이드
	 *      WebSocketHandler 핸들러(커스텀) 등록
	 *      HttpSessionHandshakeInterceptor 인터셉터 등록
	 *      CORS 관련(setAllowedOrigins("*")) 등록
	 *     configureWebSocketContainer() @Bean 선언
	 *      바이너리, 텍스트 메시지 최대 버퍼 크기 설정 - 왜 하는건지는 아직 모름
	 *      `비동기 세션, 전송 타임아웃` 설정 - 이것도 좀 긴가민가함
	 *      wip중
	 *   핸들러 클래스 생성
	 *    com.t1.tripfy.config.handler.WebSocketHandler
	 *     웹소켓 세션 저장용 HashMap 객체 선언
	 *     메서드 세개 오버라이드
	 *      afterConnectionEstablished(WebSocketSession session)
	 *      handleTextMessage(WebSocketSession session, TextMessage message)
	 *      afterConnectionClosed(WebSocketSession session, CloseStatus status)
	 *       각각 연결시, 메시지 전송시, 연결 해제시임
	 *       wip중
	 *   채팅 서비스 인터페이스 생성
	 *    com.t1.tripfy.service.chat.ChatService
	 *     wip중
	 *   채팅 관련 DTO 수정
	 *    com.t1.tripfy.domain.dto.chat.ChatDTO, ChatDetailDTO
	 *     long 필드들을 Long형으로 수정
	 * */
}
=======
//	}
//	
//	/* #메모 240515
//	 *  진행사항
//	 *   패키지 생성
//	 *    com.t1.tripfy.config
//	 *     ㄴ configuration
//	 *     ㄴ handler
//	 *    com.t1.tripfy.service
//	 *     ㄴ chat
//	 *   컨피그 클래스 생성
//	 *    com.t1.tripfy.config.configuration.WebSocketConfig
//	 *     registerWebSocketHandlers(WebSocketHandlerRegistry registry) 오버라이드
//	 *      WebSocketHandler 핸들러(커스텀) 등록
//	 *      HttpSessionHandshakeInterceptor 인터셉터 등록
//	 *      CORS 관련(setAllowedOrigins("*")) 등록
//	 *     configureWebSocketContainer() @Bean 선언
//	 *      바이너리, 텍스트 메시지 최대 버퍼 크기 설정 - 왜 하는건지는 아직 모름
//	 *      `비동기 세션, 전송 타임아웃` 설정 - 이것도 좀 긴가민가함
//	 *      wip중
//	 *   핸들러 클래스 생성
//	 *    com.t1.tripfy.config.handler.WebSocketHandler
//	 *     웹소켓 세션 저장용 HashMap 객체 선언
//	 *     메서드 세개 오버라이드
//	 *      afterConnectionEstablished(WebSocketSession session)
//	 *      handleTextMessage(WebSocketSession session, TextMessage message)
//	 *      afterConnectionClosed(WebSocketSession session, CloseStatus status)
//	 *       각각 연결시, 메시지 전송시, 연결 해제시임
//	 *       wip중
//	 *   채팅 서비스 인터페이스 생성
//	 *    com.t1.tripfy.service.chat.ChatService
//	 *     wip중
//	 *   채팅 관련 DTO 수정
//	 *    com.t1.tripfy.domain.dto.chat.ChatDTO, ChatDetailDTO
//	 *     long 필드들을 Long형으로 수정
//	 * */
//}
>>>>>>> origin/main
