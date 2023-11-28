package com.example.aucation_chat.common.redis.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.example.aucation_chat.common.redis.dto.RedisChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {
	private final RedisTemplate<String, RedisChatMessage> redisTemplate;

	// Redis Topic 에 메시지 발행.  메시지를 발행 후, 대기 중이던 redis 구독 서비스(RedisSubscriber)가 메시지를 처리
	public void publish(ChannelTopic topic, RedisChatMessage message) {
		log.info(" redis publisher start ...............................");
		redisTemplate.convertAndSend(topic.getTopic(), message);
	}
}
