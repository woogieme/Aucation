// package com.example.aucation_chat.common.redis.util;
//
// import java.util.concurrent.TimeUnit;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate;
//
// import com.example.aucation_chat.common.redis.dto.RedisChatMessage;
//
// import lombok.NoArgsConstructor;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @RequiredArgsConstructor
// @Slf4j
// public class SetTTLUtil {
// 	private static final RedisTemplate<String, String> myStringRedisTemplate;
// 	private final static RedisTemplate<String, RedisChatMessage> redisTemplate;
//
// 	/** 리스트에 처음으로 push됐을 때만  ex:{redisKeyBase}:{chatUUID}를 키로 하는 expire전용 키 생성*/
// 	public static void set(String redisKeyBase, String session) {
// 		if (redisTemplate.opsForList().size(redisKeyBase + session) == 1) { // O(1)시간에 .size() 수행
// 			log.info(" ******************** SET TTL start!!!!");
// 			myStringRedisTemplate.opsForValue()
// 				.set("ex:" + redisKeyBase + session, "key for expire", 30, TimeUnit.MINUTES); // ex:redisKeyBase:session
// 			log.info(" ******************** ex:"+redisKeyBase+session+" 키의 만료시간이 설정되었습니다");
//
// 		}
// 	}
// }
