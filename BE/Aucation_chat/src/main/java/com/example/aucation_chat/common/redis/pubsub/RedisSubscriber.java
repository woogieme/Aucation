package com.example.aucation_chat.common.redis.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.example.aucation_chat.chat.api.dto.response.ChatResponse;
import com.example.aucation_chat.common.error.ApplicationError;
import com.example.aucation_chat.common.error.ApplicationException;
import com.example.aucation_chat.common.redis.dto.RedisChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {
	private final ObjectMapper objectMapper;
	private final RedisTemplate redisTemplate;
	private final SimpMessageSendingOperations messagingTemplate;

	// 2. Redis 에서 메시지가 발행(publish)되면, listener 가 해당 메시지를 읽어서 처리
	@Override
	public void onMessage(Message message, byte[] pattern) { // 메세지 수신할 때마다 호출
		try {
			log.info(" subscriber listeneing ...............................");
			// Redis에서 발행된 메시지의 채널 이름을 가져오는 부분
			String channel = (String)redisTemplate.getStringSerializer().deserialize(message.getChannel());
			log.info("      channel: "+channel); // sub:chat-auc:12345678


			// message.getBody() : byte[] => String
			String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

			// String으로 이루어진 객체를 매핑
			RedisChatMessage chat = objectMapper.readValue(publishMessage, RedisChatMessage.class);
			log.info("      넘어온 Redis chat: "+chat.toString());


			// RedisChatMessage -> ChatResponse
			ChatResponse res = ChatResponse.builder()
				.memberPk(chat.getMemberPk())
				.imageURL(chat.getImageURL())
				.messageTime(chat.getMessageTime())
				.messageContent(chat.getMessageContent())
				.memberNickname(chat.getMemberNickname())
				.build();
			log.info("      ChatResponse: "+res.toString());


			// Websocket 구독자에게 채팅 메시지 전송
			String auctionUUID = "";
			try {
				auctionUUID = channel.split(":")[2];
			} catch (Exception e) {
				throw new ApplicationException(ApplicationError.INVALID_REDIS_KEY);
			}
			log.info("      auctionUUID: "+auctionUUID);
			messagingTemplate.convertAndSend("/sub/" + auctionUUID,  res);
			log.info(" subscriber listeneing 끝 ...............................");

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
