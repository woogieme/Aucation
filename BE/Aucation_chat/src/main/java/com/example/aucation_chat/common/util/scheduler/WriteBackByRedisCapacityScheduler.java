package com.example.aucation_chat.common.util.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aucation_chat.auction.db.entity.Auction;
import com.example.aucation_chat.auction.db.repository.AuctionRepository;
import com.example.aucation_chat.chat.db.repository.group.GroupChatWriteBackRepository;
import com.example.aucation_chat.chat.db.repository.personal.ChatWriteBackRepository;
import com.example.aucation_chat.common.error.ApplicationError;
import com.example.aucation_chat.common.error.NotFoundException;
import com.example.aucation_chat.common.redis.dto.RedisChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WriteBackByRedisCapacityScheduler {

	private final RedisTemplate<String, RedisChatMessage> redisTemplate;

	private final GroupChatWriteBackRepository groupChatWriteBackRepository;

	private final ChatWriteBackRepository chatWriteBackRepository;

	private final AuctionRepository auctionRepository;

	// 1시간 마다 100사이즈 넘는 Redis -> MySQL
	@Scheduled(cron = "0 0 0/1 * * *") // 1시간마다 실행
	// @Scheduled(cron = "0 0/2 * * * *") // 2분마다 실행
	@Transactional
	public void writeBack() {
		log.info(" ********************* writeBack 스케쥴러 시작 !!!!!");

		List<RedisChatMessage> chatMessages = new ArrayList<>();
		List<RedisChatMessage> groupChatMessages = new ArrayList<>();

		// keys 명령은 O(N) => 싱글스레드인 Redis에 부적합 (다른요청이 와도 key가 많으면 keys명령을 처리하는데만 자원이 소모됨)
		// 따라서 scan 명령을 사용할 것
		ScanOptions scanOptions = ScanOptions.scanOptions().count(1000).build();
		try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
			// scan 명령어로 key를 하나씩 가져옴
			while (cursor.hasNext()) {
				String key = cursor.next();

				// redisKeyBase에 따라 실행될 batch문이 달라짐
				String redisKeyBase = key.split(":")[0];
				String uuid = key.split(":")[1];

				if (redisKeyBase.equals("chat-auc")) {
					Auction auction = auctionRepository.findByAuctionUUID(uuid)
						.orElseThrow(() -> new NotFoundException(ApplicationError.AUCTION_NOT_FOUND));
					if (LocalDateTime.now().isAfter(auction.getAuctionEndDate())) {
						log.info("	*********************** {}의 경매가 끝남 !!", key);
						List<RedisChatMessage> temp = redisTemplate.opsForList().range(key, 0, -1);
						groupChatMessages.addAll(temp);
						redisTemplate.delete(key);

					}

				} else {
					// 100사이즈가 넘는 key에 대해서만 DB로 write
					Long size = redisTemplate.opsForList().size(key);
					if(size>100) {
						log.info("	*********************** {}의 사이즈가 100이 넘음 !!", key);
						List<RedisChatMessage> temp = redisTemplate.opsForList().range(key, 0, -1);
						chatMessages.addAll(temp);
						redisTemplate.delete(key);
					}
				}
			} // end while

			log.info(" *********************** 그룹&개인 메세지 saveAll 시작!!!");
			groupChatWriteBackRepository.saveAll(groupChatMessages);
			chatWriteBackRepository.saveAll(chatMessages);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
