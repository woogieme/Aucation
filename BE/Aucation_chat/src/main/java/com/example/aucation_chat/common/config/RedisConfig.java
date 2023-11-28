package com.example.aucation_chat.common.config;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.aucation_chat.common.redis.dto.RedisChatMessage;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RedisConfig {
	// @Value("${spring.redis.host}")
	// private String host;
	//
	// @Value("${spring.redis.port}")
	// private int port;
	//
	// @Value("${spring.redis.password}")
	// private String password;

	@Value("${spring.redis.cluster.nodes}")
	private List<String> clusterNodes;

	@Value("${spring.redis.password}")
	private String password;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		// redis의 지속적인 작업을 위함
		LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
			.clientOptions(ClusterClientOptions.builder()
				.topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
					// .refreshPeriod(Duration.ofMinutes(1)) // 1분마다 master의 down감지
					.enableAdaptiveRefreshTrigger()
					.enablePeriodicRefresh(true)  // master가 종료되고 slave가 master로 승격되어 서비스를 계속
					.build())
				.build())

			.readFrom(ReadFrom.REPLICA_PREFERRED) // 복제본 노드에서 읽지만 사용할 수없는 경우 마스터에서 읽음.
			.commandTimeout(Duration.ofSeconds(5)) // 더 많이 허용되는 시간초과 까지의 시간
			// .useSsl()
			.build();

		// 모든 클러스터 노드들 정보를 넣는다.
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
		redisClusterConfiguration.setPassword(password);
		return new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);
	}

	@Bean
	public RedisTemplate<String, RedisChatMessage> redisTemplate() {
		RedisTemplate<String, RedisChatMessage> redisTemplate = new RedisTemplate<>();

		redisTemplate.setConnectionFactory(redisConnectionFactory());

		// StringRedisSerializer: binary 데이터로 저장되기 때문에 이를 String 으로 변환시켜주며(반대로도 가능) UTF-8 인코딩 방식을 사용
		// GenericJackson2JsonRedisSerializer: 객체를 json 타입으로 직렬화/역직렬화를 수행
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisChatMessage.class));

		return redisTemplate;
	}


	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);

		// // PUB/SUB 이벤트를 처리하는 리스너 등록
		// // redisMessageListenerContainer.addMessageListener(redisSubscriber, new PatternTopic("__key*__:*"));
		//
		// // key expiration 이벤트를 처리하는 리스너 등록
		// redisMessageListenerContainer.addMessageListener(redisKeyExpiredListener,
		// 	new PatternTopic("__keyevent@*__:expired"));

		redisMessageListenerContainer.setErrorHandler(
			e -> log.error("There was an error in redis key expiration listener container", e));

		return redisMessageListenerContainer;
	}
}
