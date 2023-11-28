package com.example.aucation.auction.api.service;

import static com.example.aucation.common.util.RangeValueCalculator.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aucation.auction.api.dto.BidResponse;
import com.example.aucation.auction.api.dto.EnterResponse;
import com.example.aucation.auction.api.dto.ExitResponse;
import com.example.aucation.auction.api.dto.WebSocketErrorMessage;
import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.entity.AuctionHistory;
import com.example.aucation.auction.db.repository.AuctionBidRepository;
import com.example.aucation.auction.db.repository.AuctionHistoryRepository;
import com.example.aucation.auction.db.repository.AuctionRepository;
import com.example.aucation.common.entity.HistoryStatus;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.ApplicationException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.common.redis.db.repository.RedisRepository;
import com.example.aucation.common.redis.dto.SaveAuctionBIDRedis;
import com.example.aucation.common.service.FCMService;
import com.example.aucation.common.util.DateFormatPattern;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.repository.MemberRepository;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class AuctionBidService {

	private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

	private final AuctionService auctionService;

	private final StringRedisTemplate stringRedisTemplate;

	private final AuctionRepository auctionRepository;

	private final MemberRepository memberRepository;

	private final AuctionHistoryRepository auctionHistoryRepository;

	private final AuctionBidRepository auctionBidRepository;

	private final RedisTemplate<String, SaveAuctionBIDRedis> redisTemplate;

	private final SimpUserRegistry simpUserRegistry;

	private final FCMService fcmService;

	private final RedisRepository redisRepository;
	private static final String HAVE_NO_MONEY = "돈이 없습니다 빨리 충전해주세요";

	private static final String HIGH_BID_NO_BID = "최고 입찰자입니다 당신은 지금 입찰하지못합니다.";

	private static final String ERROR = "error";

	private static final String COMPLETE = "pass";

	private static final String COUNT = "count";

	@Transactional
	public BidResponse isService(long highPurchasePk, String auctionUUID) throws Exception {
		Member member = memberRepository.findById(highPurchasePk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		Auction auction = auctionRepository.findByAuctionUUID(auctionUUID).orElseThrow(() -> new Exception("히히하하"));

		auctionService.isExistAuction(auction);

		//Owner가 member인지 판단하기
		if (isOwnerBid(highPurchasePk, auction.getOwner().getId(), auctionUUID)) {
			return null;
		}

		// 경매 시작 시간 이전 or 이후 error 발생
		if (LocalDateTime.now().isBefore(auction.getAuctionStartDate())) {
			throw new Exception("경매 시간 이전");
		} else if (LocalDateTime.now().isAfter(auction.getAuctionEndDate())) {
			throw new Exception("경매 시간 이후");
		}

		// 지금 입찰하기위해서 돈이 충분한지 확인하기
		int firstPoint = member.getMemberPoint();

		//지금 현재 입찰중인 상황이 있는지 확인하기
		List<SaveAuctionBIDRedis> bids = redisTemplate.opsForList().range("auc-ing-log:" + auctionUUID, 0, -1);

		//지금 호가 알아보기
		// int bid = calculateValue(auction.getAuctionStartPrice());
		int peopleCount = getNumberOfSubscribersInChannel(auctionUUID);

		//입찰자가 아무도 없다
		if (bids == null || bids.isEmpty()) {
			return processFirstBid(member, firstPoint, calculateValue(auction.getAuctionStartPrice()), auction,
				peopleCount, auctionUUID);
		}
		// 입찰자가 한명이라도 존재한다.
		else {
			return processNotFirstBid(member, bids, firstPoint, auction, peopleCount, auctionUUID);
		}
	}

	private BidResponse processFirstBid(Member member, int firstPoint, int bid, Auction auction, int peopleCount,
		String auctionUUID) {
		//처음 입찰이니까 현재있는돈에서 입찰가를 뺀다 (금액을 지불한다)
		int headCnt = (int)redisRepository.getUserCount(auctionUUID);
		firstPoint -= auction.getAuctionStartPrice();
		//애초에 0이면 돈없으니까 빠꾸
		if (firstPoint < 0) {
			//throw new BadRequestException(ApplicationError.MEMBER_NOT_HAVE_MONEY);
			WebSocketErrorMessage webSocketErrorMessage = WebSocketErrorMessage.builder()
				.errMessage(HAVE_NO_MONEY)
				.messageType(ERROR)
				.memberPk(member.getId())
				.build();
			template.convertAndSend("/topic/sub/" + auctionUUID, webSocketErrorMessage);
		} else {
			//돈있으면 0원 저장하기.
			member.updatePoint(firstPoint);
			int curBid = calculateValue(auction.getAuctionStartPrice());
			//Redis는
			//1. 언제 입찰했는지
			//2. 지금 최고가가 얼마인지
			//3. 호가가 얼마인지
			//4. 구매자가누구인지
			//5. 현재 인원수가 몇명있는지
			SaveAuctionBIDRedis saveAuctionBIDRedis = SaveAuctionBIDRedis.builder()
				.bidTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatPattern.get())))
				.bidPrice(auction.getAuctionStartPrice())
				.askPrice(curBid)
				.purchasePk(member.getId())
				.build();
			saveBIDRedis("auc-ing-log:" + auction.getAuctionUUID(), saveAuctionBIDRedis);

			//무엇을 봐야하는가?
			//1.firstPoint 	(현재 내 포인트 - 나)
			//2.firstBid 	(현재 최고 입찰가 - 나)
			//3.firstUser	(현재 최고 입찰자 - 나)
			//4.ask		(현재 최고 입찰가 - 나)

			return BidResponse.builder()
				.firstUserPoint(firstPoint)
				.firstBid(auction.getAuctionStartPrice())
				.firstUser(member.getId())
				.secondUserPoint(0)
				.secondUser(0)
				.headCnt(headCnt)
				.askPrice(auction.getAuctionStartPrice() + curBid)
				.messageType(COMPLETE)
				.build();
		}
		return null;
	}

	private BidResponse processNotFirstBid(Member member, List<SaveAuctionBIDRedis> bids, int firstPoint,
		Auction auction, int peopleCount, String auctionUUID) throws
		FirebaseMessagingException, ExecutionException, InterruptedException {
		int headCnt = (int)redisRepository.getUserCount(auctionUUID);
		// 최고 입찰가와 최고 입찰자 현재 입찰가를 확인해야함
		int highBidPrice = 0;
		Long highPurchasePk = 0L;

		Collections.sort(bids);

		highBidPrice = bids.get(0).getBidPrice();
		highPurchasePk = bids.get(0).getPurchasePk();

		//내가 최고 입찰자면 입찰 못합니다.
		boolean isHighBidOwner = isHighBidOwner(member.getId(), highPurchasePk, auctionUUID);

		if (!isHighBidOwner) {
			//입찰하기전에 원래가격의 호가를 알아본다.
			int preBid = bids.get(0).getAskPrice();

			//하지만 원래가격의호가+원래가격 => 다음 입찰을한다는 가정하의 호가가격임
			int curBid = calculateValue(preBid + highBidPrice);

			//입찰가+호가 보다 커야 입찰가능
			firstPoint -= (highBidPrice + preBid);

			//입찰하지못하면 나가야함.
			if (firstPoint < 0) {
				//throw new BadRequestException(ApplicationError.MEMBER_NOT_HAVE_MONEY);
				WebSocketErrorMessage webSocketErrorMessage = WebSocketErrorMessage.builder()
					.errMessage(HAVE_NO_MONEY)
					.messageType(ERROR)
					.memberPk(member.getId())
					.build();
				template.convertAndSend("/topic/sub/" + auctionUUID, webSocketErrorMessage);
			} else {
				//입찰할수있으니 저장함.
				member.updatePoint(firstPoint);

				//입찰할수있다는건 현재 제일 높은 입찰가의 돈을 돌려줘야함.
				Member secondUser = memberRepository.findById(highPurchasePk)
					.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

				//돈을 돌려주기로 선택.
				int secondUserPoint = secondUser.updatePlusPoint(highBidPrice);

				//Redis 에서 서장
				//1. 입찰시간이 언제인지
				//2. 지금 호가가 얼마인지
				//3. 지금 최고 입찰가가 얼마인지
				//4. 구매자가 누구인지
				//5. 인원수가 몇명인지
				SaveAuctionBIDRedis saveAuctionBIDRedis = SaveAuctionBIDRedis.builder()
					.bidTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatPattern.get())))
					.askPrice(curBid)
					.bidPrice(preBid + highBidPrice)
					.purchasePk(member.getId())
					.build();
				saveBIDRedis("auc-ing-log:" + auction.getAuctionUUID(), saveAuctionBIDRedis);

				//무엇을 봐야하는가?
				//1.firstPoint 	(현재 내 포인트 - 나)
				//2.firstBid 	(현재 최고 입찰가 - 나)
				//3.firstUser	(현재 최고 입찰자 - 나)
				//4.secondUser (최고였던 입찰자 - 어떤누군가
				//5.secondUserPoint (최고였던 입찰자의 모든 돈 - 어떤 누군가)
				//6.ask		(현재 호가 - 나)

				fcmService.setAucHighAlram(secondUser, auctionUUID);

				return BidResponse.builder()
					.firstUserPoint(firstPoint)
					.firstBid(highBidPrice + preBid)
					.firstUser(member.getId())
					.secondUser(secondUser.getId())
					.secondUserPoint(secondUserPoint)
					.headCnt(headCnt)
					.askPrice(highBidPrice + preBid + curBid)
					.messageType(COMPLETE)
					.build();
			}
		}
		return null;
	}

	public void startAuction(String aucUuid) throws FirebaseMessagingException,
		ExecutionException,
		InterruptedException {
		log.info("*********************** startAuction START !!");

		// 경매 중인지를 파악하는 redis key 값
		String key = "auc-ing-ttl:" + aucUuid;

		stringRedisTemplate.opsForValue().set(key, "This key is prepared Key");
		// 경매시간 30분으로 고정
		stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);

		fcmService.setAucAlram(aucUuid);

		log.info("*********************** startAuction END !!");

	}

	@Transactional
	@SuppressWarnings("unchecked")
	public void endAuction(String aucUuid) throws Exception {// 키가 삭제되었을 때 보조 인덱스에서 해당 키의 데이터를 가져옵니다.
		log.info("*********************** endAuction START !!");

		Auction auction = auctionRepository.findByAuctionUUID(aucUuid)
			.orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXIST_AUCTION));
		// 경매 내역을 가지는 redis key 값
		String key = "auc-ing-log:" + aucUuid;

		// 입찰 내역 List
		List<SaveAuctionBIDRedis> auctionBidList = redisTemplate.opsForList().range(key, 0, -1);

		// 최고가 판별 ( 최고가 + 가장 먼저 입찰한 사용자)
		if (auctionBidList == null || auctionBidList.isEmpty()) {
			log.info("*********************** 경매 낙찰자 없음 !!");
			auction.updateAuctionToEndCustomerIsNone(-1);
			return;
		} else if (auctionBidList.size() > 1) {
			Collections.sort(auctionBidList);
		}
		log.info("*********************** 경매 낙찰자 있음 !!");

		log.info("*********************** 입찰 거래 내역 저장 시도 !!");

		log.info("*********************** 입찰 시간 확인 !!");
		int topPrice = 0;
		for (int i = 0; i < auctionBidList.size(); i++) {
			if (LocalDateTime.parse(auctionBidList.get(i).getBidTime()
					, DateTimeFormatter.ofPattern(DateFormatPattern.get()))
				.isBefore(auction.getAuctionEndDate())) {
				topPrice = i;
				break;
			}
		}
		log.info("*********************** 입찰 시간 확인 완료!!");

		log.info("*********************** 입찰 거래 내역 저장 시도 !!");

		Member customer = memberRepository.findById(auctionBidList.get(topPrice).getPurchasePk())
			.orElseThrow(() -> new ApplicationException(ApplicationError.MEMBER_NOT_FOUND));

		AuctionHistory auctionHistory = AuctionHistory.builder()
			.historyDateTime(LocalDateTime.parse(auctionBidList.get(topPrice).getBidTime(),
				DateTimeFormatter.ofPattern(DateFormatPattern.get())))
			.owner(auction.getOwner())
			.customer(customer)
			.auction(auction)
			.historyStatus(HistoryStatus.BEFORE_CONFIRM)
			.build();
		auctionHistoryRepository.save(auctionHistory);
		log.info("*********************** 입찰 거래 내역 저장 성공 !!");

		log.info("*********************** 입찰 내역 저장 시도 !!");
		auctionBidRepository.saveAll(auctionBidList, auction.getId());
		log.info("*********************** 입찰 내역 저장 성공 !!");

		log.info("*********************** 경매 정보 저장 시도 !!");
		auction.updateAuctionToEnd(auctionBidList.get(topPrice).getBidPrice(), customer);
		log.info("*********************** 경매 정보 저장 성공 !!");

		log.info("*********************** REDIS LOG DATA DELETE !!");
		redisTemplate.delete(key);
		log.info("*********************** REDIS LOG DATA DELETE DONE !!");

		log.info("*********************** REDIS AUCTION CURRENT CNT DATA DELETE !!");
		redisTemplate.delete("USER_COUNT_" + aucUuid);
		log.info("*********************** REDIS AUCTION CURRENT CNT DATA DELETE DONE !!");

		log.info("*********************** endAuction START !!");
	}

	public int getNumberOfSubscribersInChannel(String channelName) {
		// 특정 채널에 대한 구독자 수를 세기 위해 "/topic/sub/" + auctionUUID와 같은 채널 이름을 지정
		String channel = "/topic/sub/" + channelName;

		// simpUserRegistry를 사용하여 해당 채널의 구독자 수를 가져옴
		Set<SimpSubscription> sessionIds = simpUserRegistry.findSubscriptions(
			subscription -> subscription.getDestination().equals(channel)
		);
		log.info(channelName);
		return sessionIds.size();
	}

	public void saveBIDRedis(String auctionUUID, SaveAuctionBIDRedis content) {
		redisTemplate.opsForList().rightPush(auctionUUID, content);
	}

	private boolean isOwnerBid(Long highPurchasePk, Long ownerPk, String auctionUUID) {
		if (Objects.equals(highPurchasePk, ownerPk)) {
			WebSocketErrorMessage webSocketErrorMessage = WebSocketErrorMessage.builder()
				.errMessage(HIGH_BID_NO_BID)
				.messageType(ERROR)
				.memberPk(highPurchasePk)
				.build();
			template.convertAndSend("/topic/sub/" + auctionUUID, webSocketErrorMessage);
			return true;
		}
		return false;
	}

	private boolean isHighBidOwner(Long highPurchasePk, Long ownerPk, String auctionUUID) {
		if (Objects.equals(highPurchasePk, ownerPk)) {
			//throw new DuplicateException(ApplicationError.DUPLICATE_NOT_BID);

			WebSocketErrorMessage webSocketErrorMessage = WebSocketErrorMessage.builder()
				.errMessage(HIGH_BID_NO_BID)
				.messageType(ERROR)
				.memberPk(highPurchasePk)
				.build();
			template.convertAndSend("/topic/sub/" + auctionUUID, webSocketErrorMessage);
			return true;
		}
		return false;
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
		//template.convertAndSend("/topic/sub/" + auctionUUID, exitResponse);

	}

	public void enterAuction(String auctionUUID) {
		int headCnt = (int)redisRepository.getUserCount(auctionUUID);

		EnterResponse enterRequest = EnterResponse.builder()
			.headCnt(headCnt)
			.messageType(COUNT)
			.build();

		//template.convertAndSend("/topic/sub/" + auctionUUID, enterRequest);
	}
}
