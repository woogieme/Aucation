package com.example.aucation.common.handler;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.example.aucation.auction.api.dto.BIDRequest;
import com.example.aucation.auction.api.dto.EnterResponse;
import com.example.aucation.auction.api.dto.ExitResponse;
import com.example.aucation.auction.api.service.AuctionBidService;
import com.example.aucation.common.redis.db.repository.RedisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class StompHandler implements ChannelInterceptor {

	private final RedisRepository redisRepository;

	private final SimpMessagingTemplate template;
	private static final String COUNT = "count";


	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

			String sessionId = accessor.getSessionId();
			String destination = accessor.getDestination();
			String[] destinationParts = destination.split("/");
			String auctionUUID = destinationParts[destinationParts.length - 1];


			redisRepository.setUserEnterInfo(sessionId,auctionUUID);
			redisRepository.plusUserCount(auctionUUID);
			enterAuction(auctionUUID);
			log.info("SUBSCRIBED {}, {}", sessionId, auctionUUID);
		}else if(StompCommand.DISCONNECT == accessor.getCommand()){
			String sessionId = accessor.getSessionId();
			String auctionUUID = redisRepository.getUserEnterRoomId(sessionId);

			redisRepository.minusUserCount(auctionUUID);

			exitAuction(auctionUUID);

			redisRepository.removeUserEnterInfo(sessionId);
			log.info("DISCONNECT {}, {}", sessionId, auctionUUID);
		}

		return message;
	}

	public void exitAuction(String auctionUUID) {
		int headCnt = (int)redisRepository.getUserCount(auctionUUID);
		//만약 DISCONNECT 된다면
		//모든유저에게 headCnt가 -1가 되는것을 보여줘야함

		ExitResponse exitResponse= ExitResponse.builder()
			.headCnt(headCnt)
			.messageType(COUNT)
			.build();

		//saveBIDRedis("auc-ing-log:" + auction.getAuctionUUID(), );
		template.convertAndSend("/topic/sub/" + auctionUUID, exitResponse);

	}

	public void enterAuction(String auctionUUID) {
		int headCnt = (int)redisRepository.getUserCount(auctionUUID);

		EnterResponse enterRequest = EnterResponse.builder()
			.headCnt(headCnt)
			.messageType(COUNT)
			.build();

		template.convertAndSend("/topic/sub/" + auctionUUID, enterRequest);
	}
}


// log.info("accessor.getSessionId"+accessor.getSessionId());
//log.info("destination"+destination);
// log.info("===========================================");
//
// String a = (String)message.getHeaders().get("simpDestination");
// String b = (String)message.getHeaders().get("simpSessionId");
//
// log.info("이제진짜인가?"+a);
// log.info("이제진짜인가????"+b);
