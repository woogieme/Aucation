package com.example.aucation_chat.chat.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.aucation_chat.chat.api.dto.request.ChatRequestPubSub;
import com.example.aucation_chat.chat.api.dto.response.ChatResponse;
import com.example.aucation_chat.common.redis.dto.RedisChatMessage;
import com.example.aucation_chat.chat.api.dto.request.ChatRequest;
import com.example.aucation_chat.chat.api.service.WebSocketChatService;
import com.example.aucation_chat.common.redis.pubsub.RedisPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ChatController는 채팅하는 것 자체를 담당함.
 * 웹소켓 기반으로 채팅
 * */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChat {

	@Autowired
	private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
	private final WebSocketChatService webSocketChatService;
	private final RedisPublisher redisPublisher;

	// 클라이언트가 send 하는 경로
	//stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
	// /pub/multichat/{crId}
	@MessageMapping("/groupchat/{sessionId}")
	public void streamText(@Payload ChatRequest chatRequest,
		@DestinationVariable("sessionId") String auctionUUID
		) {
		log.info("=======================웹소켓 컨트롤러===============");
		log.info("sender: " + chatRequest.getMemberPk());
		log.info("content: " + chatRequest.getContent());
		log.info("auction id: " + auctionUUID);
		log.info("subscribe 주소: /sub/"+auctionUUID);

		/***** redis에 메세지 저장 후 redis에 저장되는 메세지 객체 리턴*****/
		ChatResponse message = webSocketChatService.saveAndReturn(chatRequest, auctionUUID);

		// // 특정 sessionId를 가지는 채팅방에 broad casting
		template.convertAndSend("/sub/" + auctionUUID, message);
	}

	// /pub/multichat
	@MessageMapping("/chat")
	public void streamPubSub(@Payload ChatRequestPubSub chatRequest
	) {
		log.info("=======================웹소켓 컨트롤러===============");
		log.info("sender: " + chatRequest.getMemberPk());
		log.info("content: " + chatRequest.getContent());

		// chat-auc:auctionUUID로 topic(채팅방) 등록. redisSubscriber(리스너)와 연동
		webSocketChatService.createTopic(chatRequest.getChatUUID());

		/***** redis에 메세지 저장*****/
		RedisChatMessage message = webSocketChatService.saveAndReturn2(chatRequest);

		// chatRequest를 RedisChatMessage로 가공해 구독한 클라이언트에게 브로드캐스팅
		redisPublisher.publish(webSocketChatService.getTopic(chatRequest.getChatUUID()), message);
	}
}
