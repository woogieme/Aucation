package com.example.aucation.common.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.aucation.common.handler.StompHandler;

import lombok.RequiredArgsConstructor;

@Configuration
// @EnableWebSocket // 웹소켓 활성화
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final StompHandler stompHandler;

	// stomp 설정
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 웹소켓, sockjs 클라이언트가 웹소켓 "핸드셰이크" 커넥션 생성 경로
		registry.addEndpoint("/auc-server")
			// .setAllowedOrigins("*")
			.setAllowedOriginPatterns("*")
			.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {

		// /app 경로로 시작하는 stomp메세지의 destination헤더는 @Controller객체의 @MesssageMapping 메소드로 라우팅.
		// 애플리케이션(서버)로 와야되는 주소의 맨 앞에 붙는 거라고 생각하면 됨
		config.setApplicationDestinationPrefixes("/app");

		// /topic, /queue로 시작하는 destination헤더를 가진 메세지를 브로커로 라우팅
		// 해당 경로를 subscribe하는 클라이언트에게 메세지 전달
		// /topic은 1:N, /queue는 1:1 <= 꼭 지키는 규칙은 아님
		config.enableSimpleBroker("/topic", "/queue");
	}

	//JSON을 객체로 변환
	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		messageConverters.add(new MappingJackson2MessageConverter());
		return false;
	}
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompHandler);
	}

	// stomp가 아닌 websocket방식에서 쓰임
	// @Override
	// public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	// 	registry
	// 		.addHandler(new SttHandler(), "/audio-stream")
	// 		.setAllowedOrigins("*")
	// 		.withSockJS();
	// }
}