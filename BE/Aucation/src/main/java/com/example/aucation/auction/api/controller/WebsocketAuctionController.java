package com.example.aucation.auction.api.controller;

import com.example.aucation.auction.api.dto.BidResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.aucation.auction.api.dto.BIDRequest;
import com.example.aucation.auction.api.service.AuctionBidService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class WebsocketAuctionController {

	private final AuctionBidService auctionBidService;

	private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

	// 클라이언트가 send 하는 경로
	//stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
	// /app/send/{crId}
	//옥션 페이지에 들어간다면

	//일단 입찰 버튼을 누름
	@MessageMapping("/send/register/{auctionUUID}")
	public void streamText(@Payload BIDRequest bidRequest, @DestinationVariable("auctionUUID") String auctionUUID) throws
			Exception {
		BidResponse bidResponse = auctionBidService.isService(bidRequest.getMemberPk(),auctionUUID);
		if (bidResponse != null) {
			template.convertAndSend("/topic/sub/" + auctionUUID, bidResponse);
		}

	}
}
