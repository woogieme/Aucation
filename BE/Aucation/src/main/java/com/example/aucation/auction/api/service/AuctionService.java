package com.example.aucation.auction.api.service;

import static com.example.aucation.common.util.RangeValueCalculator.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.example.aucation.auction.api.dto.*;
import com.example.aucation.auction.db.entity.AuctionHistory;
import com.example.aucation.auction.db.repository.AuctionHistoryRepository;
import com.example.aucation.common.dto.StreetResponse;
import com.example.aucation.common.entity.HistoryStatus;
import com.example.aucation.common.redis.db.repository.RedisRepository;
import com.example.aucation.common.service.CommonService;
import com.example.aucation.common.service.FCMService;
import com.example.aucation.member.db.entity.Address;
import com.example.aucation.reauction.api.dto.ReAuctionBidResponse;
import com.example.aucation.reauction.api.service.ReAucBidPhotoService;
import com.example.aucation.reauction.api.service.ReAuctionService;
import com.example.aucation.reauction.db.repository.ReAuctionBidRepository;
import com.example.aucation.common.error.BadRequestException;
import com.example.aucation.like.db.entity.LikeAuction;
import com.example.aucation.like.db.repository.LikeAuctionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.aucation.auction.api.dto.PlaceResponse;
import com.example.aucation.auction.api.dto.RegisterRequest;
import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.entity.AuctionStatus;
import com.example.aucation.auction.db.repository.AuctionRepository;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.common.redis.dto.SaveAuctionBIDRedis;
import com.example.aucation.common.util.PasswordGenerator;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.repository.MemberRepository;
import com.example.aucation.photo.api.service.PhotoService;
import com.example.aucation.photo.db.Photo;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

	private final StringRedisTemplate stringRedisTemplate;

	private final ReAuctionService reAuctionService;

	private final AuctionRepository auctionRepository;

	private final MemberRepository memberRepository;

	private final LikeAuctionRepository likeAuctionRepository;

	private final RedisTemplate<String, SaveAuctionBIDRedis> redisTemplate;

	private final PhotoService photoService;

	private final CommonService commonService;

	private final RedisRepository redisRepository;

	private final AuctionHistoryRepository auctionHistoryRepository;
	private final int COUNT_IN_PAGE = 15;


	@Transactional
	public PlaceResponse place(Long memberPk, String auctionUUID) throws Exception {
		Auction auction = auctionRepository.findByAuctionUUID(auctionUUID)
				.orElseThrow(() -> new Exception("auction이 없습니다"));

		int headCnt = (int)redisRepository.getUserCount(auctionUUID);
		headCnt+=1;

		isNotStartAuction(auction);
		isExistAuction(auction);

		Member member = memberRepository.findById(memberPk)
				.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		List<SaveAuctionBIDRedis> bids = redisTemplate.opsForList().range("auc-ing-log:" + auctionUUID, 0, -1);

		List<Photo> photoList = photoService.getPhoto(auction.getId());

		List<String> UUIDImage = new ArrayList<>();

		for (Photo photo : photoList) {
			UUIDImage.add(photo.getImgUrl());
		}

		int nowPrice = 0;
		int askPrice = 0;
		boolean isBid = false;

		if (bids.isEmpty()) {
			nowPrice = auction.getAuctionStartPrice();
			askPrice = nowPrice;
		} else {
			Collections.sort(bids);
			nowPrice = bids.get(0).getBidPrice();
			askPrice = nowPrice + bids.get(0).getAskPrice();
			if (Objects.equals(bids.get(0).getPurchasePk(), memberPk)) {
				isBid = true;
			}
		}

		log.info(auction.getOwner().getMemberId());

		return PlaceResponse.builder()
				.memberPoint(member.getMemberPoint())
				.memberPk(memberPk)
				.myNickname(member.getMemberNickname())
				.title(auction.getAuctionTitle())
				.detail(auction.getAuctionDetail())
				.ownerNickname(auction.getOwner().getMemberNickname())
				.ownerPk(auction.getOwner().getId())
				.ownerType(auction.getOwner().getMemberRole())
				.enterTime(auction.getAuctionStartDate())
				.endTime(auction.getAuctionStartDate().plusMinutes(30))
				.ownerPicture(auction.getOwner().getImageURL())
				.picture(UUIDImage)
				.highBid(isBid)
				.headCnt(headCnt)
				.nowPrice(nowPrice)
				.askPrice(askPrice)
				.address(auction.getAddress())
				.build();
	}

	private void isNotStartAuction(Auction auction) {
		LocalDateTime now = LocalDateTime.now();
		if(now.isBefore(auction.getAuctionStartDate())){
			throw new BadRequestException(ApplicationError.EARLY_START_AUCTION);
		}
	}

	@Transactional
	public RegisterResponse register(Long memberPk, RegisterRequest registerRequest, List<MultipartFile> multipartFiles) throws IOException {
		Member member = memberRepository.findById(memberPk)
				.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		if(registerRequest.getAuctionStartPrice()<1000){
			throw new BadRequestException(ApplicationError.LESS_MORE_THAN_START_PRICE);
		}
		String auctionUUID = PasswordGenerator.generate();

		Address address = setMemberAddress(registerRequest.getAuctionMeetingLng(), registerRequest.getAuctionMeetingLat());
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime auctionStartDate = now.plusMinutes(registerRequest.getAuctionStartAfterTime());
		LocalDateTime auctionEndDate = auctionStartDate.plusMinutes(30);
		//경매라면
		if (registerRequest.getAuctionStatus().equals(AuctionStatus.BID)) {
			Auction auction = Auction.builder()
					.auctionDetail(registerRequest.getAuctionDetail())
					.auctionStatus(AuctionStatus.BID)
					.auctionType(registerRequest.getAuctionType())
					.auctionTitle(registerRequest.getAuctionTitle())
					.auctionMeetingLat(registerRequest.getAuctionMeetingLat())
					.auctionMeetingLng(registerRequest.getAuctionMeetingLng())
					.auctionEndPrice(0)
					.address(address)
					.auctionStartDate(auctionStartDate)
					.auctionEndDate(auctionEndDate)
					.auctionStartPrice(registerRequest.getAuctionStartPrice())
					.auctionUUID(auctionUUID)
					.owner(member)
					.build();
			auction = auctionRepository.save(auction);
			photoService.upload(multipartFiles, auctionUUID);
			String key = "auc-pre:" + auctionUUID;
			stringRedisTemplate.opsForValue().set(key, "This is a token for Prepared_Auction");
			stringRedisTemplate.expire(key, registerRequest.getAuctionStartAfterTime(), TimeUnit.MINUTES);
			return RegisterResponse.builder().auctionPk(auction.getId()).build();
		}
		//역 경매라면
		else if (registerRequest.getAuctionStatus().equals(AuctionStatus.REVERSE_BID)) {
			Auction auction = Auction.builder()
					.auctionDetail(registerRequest.getAuctionDetail())
					.auctionStatus(AuctionStatus.REVERSE_BID)
					.auctionType(registerRequest.getAuctionType())
					.auctionTitle(registerRequest.getAuctionTitle())
					.auctionMeetingLat(registerRequest.getAuctionMeetingLat())
					.auctionMeetingLng(registerRequest.getAuctionMeetingLng())
					.auctionEndPrice(0)
					.auctionStartDate(now)
					.auctionEndDate(auctionStartDate)
					.auctionStartPrice(registerRequest.getAuctionStartPrice())
					.address(address)
					.auctionUUID(auctionUUID)
					.owner(member)
					.build();
			auction = auctionRepository.save(auction);
			photoService.upload(multipartFiles, auctionUUID);
			return RegisterResponse.builder().auctionPk(auction.getId()).build();
		}
		return null;
	}

	public AuctionListResponse getAuctionPreList(Long memberPk, int pageNum, AuctionSortRequest sortRequest) {
		Member member = memberRepository.findById(memberPk).orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		if (pageNum < 1) {
			pageNum = 1;
		}
		Pageable pageable = PageRequest.of(pageNum - 1, COUNT_IN_PAGE);
		AuctionListResponse auctions = auctionRepository.searchPreAucToCondition(member, pageNum, sortRequest, pageable);
		return auctions;
	}

	public AuctionListResponse getAuctionIngList(Long memberPk, int pageNum, AuctionSortRequest sortRequest) {
		Member member = memberRepository.findById(memberPk).orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		Pageable pageable = PageRequest.of(pageNum - 1, COUNT_IN_PAGE);
		AuctionListResponse auctions = auctionRepository.searchIngAucToCondition(member, pageNum, sortRequest, pageable);
		return auctions;
	}

	@Transactional
	public Object getDetail(Long memberPk, Long auctionPk) throws Exception {

		Member member = memberRepository.findById(memberPk).orElseThrow(()-> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		Auction auction = auctionRepository.findById(auctionPk)
				.orElseThrow(() -> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));
		int checkTime = getAuctionTime(auction);

		if (auction.getAuctionStatus().equals(AuctionStatus.REVERSE_BID)) {
			return reAuctionService.getDetail(memberPk, auction, checkTime);
		}
		log.info("********************** Auction : getDetail() start");

		log.info("********************** 경매 정보 가져오기 시도");
		AuctionDetailResponse response = auctionRepository.searchDetailAuc(auction, memberPk, checkTime,member);
		if(response == null ){
			log.info("********************** 경매 정보가 없습니다.");
			throw new NotFoundException(ApplicationError.NOT_EXIST_AUCTION);
		}
		log.info("********************** 경매 정보 가져오기 성공, AuctionPk = {}", response.getAuctionPk());

		log.info("********************** 경매 사진 가져오기 시도");
		List<String> auctionPhotoUrl = new ArrayList<>();

		photoService.getPhoto(auctionPk).forEach(photo -> {
			auctionPhotoUrl.add(photo.getImgUrl());
		});
		response.setAuctionPhoto(auctionPhotoUrl);
		log.info("********************** 경매 사진 가져오기 및 설정 성공, 사진 수 ={}", auctionPhotoUrl.size());
		log.info("********************** 해당 판매자 경매 정보 가져오기 시도");
		List<AuctionDetailItem> auctionDetailItems = auctionRepository.searchDetailItems(response.getAuctionOwnerPk(), auction,member);
		auctionDetailItems.forEach(auctionDetailItem -> {
			Photo photo = photoService.getOnePhoto(auctionDetailItem.getAuctionPk());
			auctionDetailItem.setAuctionPhoto(photo.getImgUrl());
		});
		response.setAuctionDetailItems(auctionDetailItems);
		log.info("********************** 해당 판매자 경매 정보 가져오기 완료");


		log.info("********************** 경매 상태별 정보 설정 시도");
		if (checkTime < 1) {
			log.info("********************** 경매 전 정보 설정 시도");
			response.setAuctionAskPrice(response.getAuctionStartPrice());
			response.setAuctionTopPrice(null);
			response.setAuctionEndPrice(null);
			response.setAuctionBidCnt(null);
			log.info("********************** 경매 전 정보 설정 완료");
		} else if (checkTime < 2) {
			log.info("********************** 경매 중 정보 설정 시도");
			response.setAuctionAskPrice(response.getAuctionStartPrice());
			response.setAuctionBidCnt(redisRepository.getUserCount(response.getAuctionUuid()));
			List<SaveAuctionBIDRedis> bids =
					redisTemplate.opsForList().range("auc-ing-log:" + response.getAuctionUuid(), 0, -1);
			if (bids != null && !bids.isEmpty()) {
				Collections.sort(bids);
				response.setAuctionTopPrice(bids.get(0).getBidPrice());
				response.setAuctionAskPrice(bids.get(0).getAskPrice() + bids.get(0).getBidPrice());
			}
			response.setAuctionEndPrice(null);
			log.info("********************** 경매 중 정보 설정 완료");
		} else {
			log.info("********************** 경매 후 정보 설정 시도");
			response.setAuctionAskPrice(null);
			if (response.getAuctionTopPrice() == null) {
				response.setAuctionTopPrice(-1);
			}
			log.info("********************** 경매 후 정보 설정 완료");
		}
		log.info("********************** 경매 상태별 정보 설정 완료");

		log.info("********************** 경매 중인 경우 현재 최고가 환인 완료, 최고가 = {}, 다음 입찰가 = {}"
				, response.getAuctionTopPrice(), response.getAuctionAskPrice());


		log.info("********************** 추가 정보 설정 시도");
		response.setIsAction(checkTime);
		response.setNowTime(LocalDateTime.now());
		log.info("********************** 추가 정보 설정 완료, 현재 시간 = {}, 경매 상태 = {}"
				, response.getNowTime(), response.getIsAction());

		log.info("********************** Auction : getDetail() end");
		return response;
	}

	@Transactional
	public void setLikeAuction(Long memberPk, Long auctionPk) throws Exception {
		log.info("********************** setLikeAuction() 시작");
		Member member = memberRepository.findById(memberPk)
				.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		Auction auction = auctionRepository.findById(auctionPk)
				.orElseThrow(() -> new Exception("AUCTION NO EXIST"));

		likeAuctionRepository.findByAuctionAndMember(auction, member)
				.ifPresentOrElse(
						likeAuction -> {
							log.info("********************** 좋아요 존재 O");
							likeAuctionRepository.delete(likeAuction);
							log.info("********************** memberPk : {} -> auctionPk : {} 좋아요 설정 취소"
									, memberPk, auctionPk);
						},
						() -> {
							log.info("********************** 좋아요 존재 X");
							likeAuctionRepository.save(LikeAuction
									.builder()
									.member(member)
									.auction(auction)
									.build());
							log.info("********************** memberPk : {} -> auctionPk : {} 좋아요 설정"
									, memberPk, auctionPk);
						}
				);
		log.info("********************** setLikeAuction() 완료");
	}

	public List<AuctionIngResponseItem> getHotAuctionsToMainPage(Long memberPk) {
		Member member = memberRepository.findById(memberPk).orElseThrow(()->new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		List<AuctionIngResponseItem> response = auctionRepository.searchHotAuctionToMainPage(memberPk,member);
		return response;
	}

	public void isExistAuction(Auction auction) {
		if (auction.getAuctionEndDate().isBefore(LocalDateTime.now())) {
			throw new BadRequestException(ApplicationError.CLOSE_THE_AUCTION);
		}
	}

	/**
	 * 경매 상태를 반환하는 메소드
	 * 현재 시간과 비교하여 상태를 반환한다.
	 *
	 * @return 0 : 경매 전
	 * 1 : 경매 중
	 * 2 : 경매 후
	 */
	public int getAuctionTime(Auction auction) {
		if (auction.getAuctionStartDate().isAfter(LocalDateTime.now())) {
			return 0;
		} else if (auction.getAuctionStartDate().isBefore(LocalDateTime.now())
				&& auction.getAuctionEndDate().isAfter(LocalDateTime.now())) {
			return 1;
		} else {
			return 2;
		}

	}

	private Address setMemberAddress(double auctionMeetingLng, double auctionMeetingLat) {
		StreetResponse streetResponse = commonService.findstreet(auctionMeetingLng, auctionMeetingLat);
		return Address.builder()
			.city(streetResponse.getCity())
			.zipcode(streetResponse.getZipcode())
			.street(streetResponse.getStreet())
			.build();
	}

	public AuctionBidResponse confirmBid(Long memberPk, AuctionConfirmRequest request) {
		log.info("********************** confirmBid start");
		Auction auction = auctionRepository.findById(request.getAuctionPk())
				.orElseThrow(()-> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));
		Member member = memberRepository.findById(memberPk)
				.orElseThrow(()->new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		log.info("********************** 선택한 입찰 내역 확인 시도");
		AuctionHistory auctionHistory = auctionHistoryRepository.findByAuction(auction)
				.orElseThrow(()-> new NotFoundException(ApplicationError.NOT_EXIST_HISTORY));
		log.info("********************** 선택한 입찰 내역 확인 완료");

		log.info("********************** 선택한 입찰 내역 상태 확인 시도");
		if(!auctionHistory.getHistoryStatus().equals(HistoryStatus.BEFORE_CONFIRM)){
			throw new BadRequestException(ApplicationError.NOT_EXIST_HISTORY);
		}
		log.info("********************** 선택한 입찰 내역 상태 확인 완료");

		log.info("********************** 입찰 내역 상태 변경 시도");
		auctionHistory.updateToConfirm();
		auctionHistoryRepository.save(auctionHistory);
		log.info("********************** 입찰 내역 상태 변경 완료 DONE-TIME = {}",LocalDateTime.now());

		log.info("********************** 경매 입찰 등록자 포인트 시도");

		Member owner = auctionHistory.getOwner();
		log.info("********************** owner ={}, point = {}",
				owner.getMemberNickname(),owner.getMemberPoint());
		owner.updatePoint(owner.getMemberPoint() + auction.getAuctionEndPrice());
		memberRepository.save(owner);
		log.info("********************** 경매 입찰 등록자 포인트 완료, customer ={}, point = {}",
				owner.getMemberNickname(),owner.getMemberPoint());
		log.info("********************** confirmBid end");
		return AuctionBidResponse.builder()
				.auctionPk(auction.getId())
				.auctionTitle(auction.getAuctionTitle())
				.auctionOwnerNickname(member.getMemberNickname())
				.auctionConfirmPrice(auction.getAuctionEndPrice())
				.build();
	}
}
