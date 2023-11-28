package com.example.aucation.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.entity.AuctionStatus;
import com.example.aucation.auction.db.repository.AuctionRepository;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.discount.db.repository.DiscountRepository;
import com.example.aucation.like.db.entity.LikeAuction;
import com.example.aucation.like.db.repository.LikeAuctionRepository;
import com.example.aucation.member.api.dto.FCMTokenReq;
import com.example.aucation.member.api.dto.FCMTokenRes;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.repository.MemberRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {

	private final MemberRepository memberRepository;

	private final AuctionRepository auctionRepository;

	private final DiscountRepository discountRepository;

	private final LikeAuctionRepository likeAuctionRepository;

	private final FirebaseMessaging firebaseMessaging;

	private static final String ALRAM_TITLE = "경매 시작 알림이 도착했습니다";

	private static final String ALRAM_BODY = "라는 경매가 지금 시작되었습니다!";

	private static final String SUCCESS_TOKEN_SAVE = "성공적으로 토큰이 저장되었습니다";

	private static final String HIGH_ALRAM_TITLE = " 의 최고 경매자 바뀜";

	private static final String HIGH_ALRAM_BODY = "최고 경매자가 나타났습니다 경매를 다시하여 상품을 가지세요";

	private static final String END_ALRAM_TITLE = "낙찰완료 ! ";

	private static final String END_ALRAM_BODY = "판매자 혹은 구매자와 채팅을 통해 대화를 시작하세요";

	private static final String REAUCTION_ALRAM_TITLE = "역경매 입찰자가 새로 등장하였습니다! ";

	private static final String REAUCTION_ALRAM_BODY = "해당 금액에 물건을 사실 경우, 알람을 눌러 구매 확정을 지어주세요";


	@Transactional
	public FCMTokenRes saveToken(long memberPk, FCMTokenReq fcmTokenReq) {

		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		member.updateFCMToken(fcmTokenReq.getToken());

		return FCMTokenRes.builder().message(SUCCESS_TOKEN_SAVE).build();

	}

	@Transactional
	@Async("threadExecutor")
	public void setAucAlram(String auctionUUID) throws
		FirebaseMessagingException,
		ExecutionException,
		InterruptedException {
		//UUID로 auction 검색
		Auction auction = auctionRepository.findByAuctionUUID(auctionUUID)
			.orElseThrow(() -> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));
		//auctionPK로 좋아요 테이블 검색
		List<LikeAuction> likeAuctions = likeAuctionRepository.findByAuctionId(auction.getId());

		if (likeAuctions == null) {
			return;
		}
		//각 해당하는 사람들한테 메세지 전송
		for (LikeAuction likeAlram : likeAuctions) {

			if(likeAlram.getMember().getMemberFCMToken()==null){
				continue;
			}
			Map<String, String> data = new HashMap<>();
			data.put("auctionUUID", auction.getAuctionUUID());
			data.put("status", "경매");
			data.put("type", "room");
			Notification notification = Notification.builder()
				.setTitle(ALRAM_TITLE)
				.setBody(auction.getAuctionTitle() + ALRAM_BODY)
				.build();


			com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
				.setToken(likeAlram.getMember().getMemberFCMToken())
				.setNotification(notification)
				.putAllData(data)
				.build();

			this.firebaseMessaging.sendAsync(message).get();
		}

	}

	@Transactional
	@Async("threadExecutor")
	public void setAucEndAlarm(String auctionUUID, Long memberPk) throws
		FirebaseMessagingException,
		ExecutionException,
		InterruptedException {

		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		if(member.getMemberFCMToken()==null){
			return;
		}

		Auction auction = auctionRepository.findByAuctionUUID(auctionUUID)
			.orElseThrow(() -> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));
		String auctionStatus = "";
		if (auction.getAuctionStatus().equals(AuctionStatus.BID)) {
			auctionStatus = "경매";
		} else if (auction.getAuctionStatus().equals(AuctionStatus.REVERSE_BID)) {
			auctionStatus = "역경매";
		}
		Map<String, String> data = new HashMap<>();
		data.put("prodPk", String.valueOf(auction.getId()));
		data.put("status", auctionStatus);
		data.put("type", "chat");
		Notification notification = Notification.builder()
			.setTitle(auction.getAuctionTitle() + END_ALRAM_TITLE)
			.setBody(END_ALRAM_BODY)
			.build();

		com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
			.setToken(member.getMemberFCMToken())
			.setNotification(notification)
			.putAllData(data)
			.build();

		this.firebaseMessaging.sendAsync(message).get();

	}

	@Transactional
	@Async("threadExecutor")
	public void setDisAucAlarm(String discountUUID, Long memberPk) throws
		FirebaseMessagingException,
		ExecutionException,
		InterruptedException {


		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		if(member.getMemberFCMToken()==null){
			return;
		}

		Discount discount = discountRepository.findByDiscountUUID(discountUUID)
			.orElseThrow(() -> new NotFoundException(ApplicationError.DISCOUNT_NOT_FOUND));
		String discountStatus = "할인";
		Map<String, String> data = new HashMap<>();
		data.put("prodPk", String.valueOf(discount.getId()));
		data.put("status", discountStatus);
		data.put("type", "chat");

		Notification notification = Notification.builder()
			.setTitle(discount.getDiscountTitle() + END_ALRAM_TITLE)
			.setBody(END_ALRAM_BODY)
			.build();

		com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
			.setToken(member.getMemberFCMToken())
			.setNotification(notification)
			.putAllData(data)
			.build();

		this.firebaseMessaging.sendAsync(message).get();
	}

	@Transactional
	@Async("threadExecutor")
	public void setAucHighAlram(Member secondUser, String auctionUUID) throws
		FirebaseMessagingException,
		ExecutionException,
		InterruptedException {

		if(secondUser.getMemberFCMToken()==null){
			return;
		}

		Auction auction = auctionRepository.findByAuctionUUID(auctionUUID)
			.orElseThrow(() -> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));

		Map<String, String> data = new HashMap<>();
		data.put("auctionUUID", auction.getAuctionUUID());
		data.put("status","경매");
		data.put("type", "room");
		Notification notification = Notification.builder()
			.setTitle(auction.getAuctionTitle() + HIGH_ALRAM_TITLE)
			.setBody(auction.getAuctionTitle() + HIGH_ALRAM_BODY)
			.build();

		com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
			.setToken(secondUser.getMemberFCMToken())
			.setNotification(notification)
			.putAllData(data)
			.build();

		this.firebaseMessaging.sendAsync(message).get();

	}

	@Transactional
	@Async("threadExecutor")
	public void setReAucAlram(Long auctionId, Long memberPk) throws
		FirebaseMessagingException,
		ExecutionException,
		InterruptedException {

		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		if(member.getMemberFCMToken()==null){
			return;
		}
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));

		Map<String, String> data = new HashMap<>();
		data.put("prodPk", String.valueOf(auction.getId()));
		data.put("status","역경매");
		data.put("type", "room");

		Notification notification = Notification.builder()
			.setTitle(auction.getAuctionTitle()+": "+REAUCTION_ALRAM_TITLE)
			.setBody(auction.getAuctionTitle()+": "+REAUCTION_ALRAM_BODY)
			.build();

		com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
			.setToken(member.getMemberFCMToken())
			.setNotification(notification)
			.putAllData(data)
			.build();


		this.firebaseMessaging.sendAsync(message).get();


	}
}
