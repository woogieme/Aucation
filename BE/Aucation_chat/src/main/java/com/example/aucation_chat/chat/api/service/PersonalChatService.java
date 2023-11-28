package com.example.aucation_chat.chat.api.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aucation_chat.auction.db.entity.Auction;
import com.example.aucation_chat.auction.db.entity.AuctionHistory;
import com.example.aucation_chat.auction.db.entity.AuctionStatus;
import com.example.aucation_chat.auction.db.repository.AuctionHistoryRepository;
import com.example.aucation_chat.auction.db.repository.AuctionRepository;
import com.example.aucation_chat.chat.api.dto.request.PersonalChatEnterRequest;
import com.example.aucation_chat.chat.api.dto.response.PersonalChatEnterResponse;
import com.example.aucation_chat.chat.api.dto.response.ChatResponse;
import com.example.aucation_chat.chat.db.entity.personal.ChatMessage;
import com.example.aucation_chat.chat.db.entity.personal.ChatParticipant;
import com.example.aucation_chat.chat.db.entity.personal.ChatRoom;
import com.example.aucation_chat.chat.db.repository.personal.ChatMessageRepository;
import com.example.aucation_chat.chat.db.repository.personal.ChatParticipantRepository;
import com.example.aucation_chat.chat.db.repository.personal.ChatRoomRepository;
import com.example.aucation_chat.common.dto.HistoryStatus;
import com.example.aucation_chat.common.error.ApplicationError;
import com.example.aucation_chat.common.error.ApplicationException;
import com.example.aucation_chat.common.error.NotFoundException;
import com.example.aucation_chat.common.redis.dto.RedisChatMessage;
import com.example.aucation_chat.common.util.DateFormatPattern;
import com.example.aucation_chat.common.util.PasswordGenerator;
import com.example.aucation_chat.common.util.RandomNumberUtil;
import com.example.aucation_chat.discount.db.entity.Discount;
import com.example.aucation_chat.discount.db.entity.DiscountHistory;
import com.example.aucation_chat.discount.db.repository.DiscountHistoryRepository;
import com.example.aucation_chat.discount.db.repository.DiscountRepository;
import com.example.aucation_chat.member.db.entity.Member;
import com.example.aucation_chat.member.db.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalChatService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatParticipantRepository chatParticipantRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final AuctionRepository auctionRepository;
	private final DiscountRepository discountRepository;
	private final MemberRepository memberRepository;
	private final AuctionHistoryRepository auctionHistoryRepository;
	private final DiscountHistoryRepository discountHistoryRepository;
	private final RedisTemplate<String, RedisChatMessage> redisTemplate;
	private final RedisTemplate<String, String> myStringRedisTemplate;

	//////////////////////////////////////////////////////////////////
	// @Transactional
	public PersonalChatEnterResponse enter(PersonalChatEnterRequest request) {
		log.info("************************ 개인채팅방 enter 시작 !!!!!!!");

		long prodPk = request.getProdPk();  // 물품 PK
		int type = request.getProdType(); // 판매유형
		int endPrice; // 최종가격
		String sellerNickName;
		String sellerImageURL;
		String prodType = "경매";
		String prodCategory; // 물품 카테고리
		String prodName;
		List<ChatResponse> chatList = new ArrayList<>();
		long sellerPk;
		ChatRoom chatRoom;

		// prodPk와 prodType으로 물품의 카테고리, 최종가격, 타입, 제목, 채팅내역, 판매자정보, 참여 결정
		if (type < 2) { // 경매, 역경매일 때
			log.info("************************ 경매&역경매 물품 채팅방입니다");

			log.info("************************ 경매&역경매 유효성 검사");
			Auction auction = auctionRepository.findByAuctionPk(prodPk)
				.orElseThrow(() -> new NotFoundException(ApplicationError.AUCTION_NOT_FOUND));
			// type과 pk 간의 일치 검사
			if(type==0){
				if(auction.getAuctionStatus() != AuctionStatus.BID)
					throw new ApplicationException(ApplicationError.INVALID_PRODUCT);
			} else if(type==1){
				if(auction.getAuctionStatus() != AuctionStatus.REVERSE_BID)
					throw new ApplicationException(ApplicationError.INVALID_PRODUCT);
			}
			// if(!(type== 0 && auction.getAuctionStatus() == AuctionStatus.BID))
			// 	throw new ApplicationException(ApplicationError.INVALID_PRODUCT);
			// else if(!(type==1 && auction.getAuctionStatus() == AuctionStatus.REVERSE_BID))
			// 	throw new ApplicationException(ApplicationError.INVALID_PRODUCT);

			log.info("************************ 끝난 경매인가요?");
			AuctionHistory history = isEndAuction(auction);

			// prodPk 랑 prodType을 가지는 채팅방 조회
			log.info("************************ Find Chat Room: prodPk={}, prodType={}", prodPk, type);
			chatRoom = findChatRoom(prodPk, type);

			// 내가 정당한 참여자인지 검사
			if (isValidParticipant(request.getMemberPk(), history.getCustomer().getMemberPk(),
				history.getOwner().getMemberPk()))
				participate(chatRoom, request.getMemberPk());
			else
				throw new ApplicationException(ApplicationError.FORBIDDEN_PARTICIPANT);

			endPrice = auction.getAuctionEndPrice();
			log.info("************************ 최종가 " + endPrice);

			prodCategory = auction.getAuctionType();
			log.info("************************ 카테고리 " + prodCategory);

			prodName = auction.getAuctionTitle();
			log.info("************************ 물품 이름 " + prodName);

			Member owner = auction.getOwner();
			sellerImageURL = owner.getImageURL();
			log.info("************************ 프사 주소 " + sellerImageURL);

			sellerNickName = owner.getMemberNickname();
			log.info("************************ 파는사람 닉네임 " + sellerNickName);

			sellerPk = owner.getMemberPk();
			if (type == 1) { // 역경매일 때 채팅가져오기
				prodType = "역경매";
				chatList = getChatList("chat-re-bid:", chatRoom.getChatSession());
			} else { // 경매일 때 채팅가져오기
				chatList = getChatList("chat-bid:", chatRoom.getChatSession());
			}
		} else if(type==2) {  // 할인판매일 때
			log.info("************************ 할인판매 물품 채팅방입니다");

			log.info("************************ 할인판매 유효성 검사");
			Discount discount = discountRepository.findByDiscountPk(prodPk)
				.orElseThrow(() -> new NotFoundException(ApplicationError.DISCOUNT_NOT_FOUND));

			// history조회 => 구매끝난건지 검사
			log.info("************************ 끝난 경매인가요?");
			DiscountHistory history = isEndDiscount(discount);

			// prodPk 랑 prodType을 가지는 채팅방 조회
			log.info("************************ Find Chat Room: prodPk={}, prodType={}", prodPk, type);
			chatRoom = findChatRoom(prodPk, type);

			// 내가 정당한 참여자인지 검사
			if (isValidParticipant(request.getMemberPk(), history.getCustomer().getMemberPk(),
				discount.getOwner().getMemberPk())) {
				participate(chatRoom, request.getMemberPk());
			}
			else {
				throw new ApplicationException(ApplicationError.FORBIDDEN_PARTICIPANT);
			}

			endPrice = discount.getDiscountDiscountedPrice(); // 할인가 조회
			log.info("************************ 할인가 " + endPrice);

			prodCategory = discount.getDiscountCategory();
			log.info("************************ 카테고리 " + prodCategory);

			prodName = discount.getDiscountTitle();
			log.info("************************ 물품 이름 " + prodName);

			Member seller = discount.getOwner();
			sellerImageURL = seller.getImageURL();
			log.info("************************ 프사 주소 " + sellerImageURL);

			sellerNickName = seller.getMemberNickname();
			log.info("************************ 파는사람 닉네임 " + sellerNickName);

			sellerPk = seller.getMemberPk();
			prodType = "할인판매";
			chatList = getChatList("chat-dis:", chatRoom.getChatSession());
		} else{
			throw new ApplicationException(ApplicationError.INVALID_PRODUCT);
		}

		PersonalChatEnterResponse resp = PersonalChatEnterResponse.builder()
			.sellerPk(sellerPk)
			.chatUUID(chatRoom.getChatSession())
			.chatList(chatList)
			.prodCategory(prodCategory)
			.prodEndPrice(endPrice)
			.prodName(prodName)
			.prodType(prodType)
			.sellerImageURL(sellerImageURL)
			.sellerNickName(sellerNickName)
			.build();
		log.info("************************ 입장 끝!");
		return resp;
	}

	private AuctionHistory isEndAuction(Auction auction) {
		AuctionHistory history = auctionHistoryRepository.findByAuction_AuctionPk(auction.getAuctionPk())
			.orElseThrow(() -> new NotFoundException(ApplicationError.AUCTION_HISTORY_NOT_FOUND));

		HistoryStatus status = history.getHistoryStatus();
		// 구매확정된 물품일 때 채팅방 입장하지 않음
		if (status == HistoryStatus.AFTER_CONFIRM)
			throw new ApplicationException(ApplicationError.PRODUCT_CONFIRMED);

		return history;
	}

	private DiscountHistory isEndDiscount(Discount discount) {
		DiscountHistory history = discountHistoryRepository.findByDiscount_DiscountPk(discount.getDiscountPk())
			.orElseThrow(() -> new NotFoundException(ApplicationError.DISCOUNT_HISTORY_NOT_FOUND));

		HistoryStatus status = history.getHistoryStatus();
		// 구매확정된 물품일 때 채팅방 입장하지 않음
		if (status == HistoryStatus.AFTER_CONFIRM)
			throw new ApplicationException(ApplicationError.PRODUCT_CONFIRMED);

		return history;
	}

	private boolean isValidParticipant(long memberPk, long customerPk, long ownerPk) {
		if (memberPk == customerPk || memberPk == ownerPk)
			return true;
		return false;
	}

	// ----------------------- service안에 들어가는 메소드들 --------------------------- //
	private List<ChatResponse> getChatList(String redisKeyBase, String chatSession) {
		log.info("************************ "+redisKeyBase+chatSession+"에서 채팅 내역을 찾는중.....");

		List<RedisChatMessage> redisChatMessages = redisTemplate.opsForList().range(redisKeyBase + chatSession, 0, -1);

		// cache-aside
		if (!redisChatMessages.isEmpty()) {
			return redisListToChatResponseList(redisChatMessages);
		} else {
			return cacheAside(redisKeyBase, chatSession);
		}
	}

	private List<ChatResponse> cacheAside(String redisKeyBase, String chatSession) {
		log.info("****************************** 개인채팅 cache side 실행!!!!!!!");

    /*
		1. MySQL에서 chatMessage들 가져오기
		- auctionUUID로 chatPk 찾아서 chatPk로 chat_message 테이블 조회
	*/
		List<ChatMessage> chatList = getChatsFromDB(chatSession);

		// 2. MySQL 에도 없음 => 새로 만들어진 채팅방이라는 뜻
		if (chatList.isEmpty()) {
			log.info("************************ cache-aside: DB에도 없슴 !!!!!!!");
			return new ArrayList<>();
		}

		// 3. MySQL에 있음 => Redis에 저장 후 반환
		List<RedisChatMessage> dbToRedisChats = new ArrayList<>(); // MySQL에서 redis에 들어갈 채팅메세지들
		List<ChatResponse> chatResponses = new ArrayList<>(); // MySQL -> chatResponse

		// chat entity -> redis -> 저장 -> chat response 반환
		for (int i = chatList.size() - 1; i > 0; i--) { //DB에서 보낸시간 기반 내림차순해서 가져왔기 때문에 뒤에서부터 조회해 오름차순으로 넣어야함

			// message의 memberPk로 닉네임 찾아야함
			Member member = memberRepository.findByMemberPk(chatList.get(i).getMemberPk())
				.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

			// 보낸시간을 string으로 바꾸어야 함
			String messageTime = chatList.get(i).getMessageTime()
				.format(DateTimeFormatter.ofPattern(DateFormatPattern.get()));

			long memberPk = chatList.get(i).getMemberPk();
			String messageContent = chatList.get(i).getMessageContent();

			// MySQL데이터를 Redis로 옮기는 과정
			RedisChatMessage temp = RedisChatMessage.builder()
				.memberPk(memberPk)
				.messageContent(messageContent)
				.memberNickname(member.getMemberNickname())
				.messageTime(messageTime)
				.imageURL(member.getImageURL())
				.cachedFromDB(true)
				.build();

			dbToRedisChats.add(temp);

			// MySQL데이터를 ChatResponse로 옮기는 과정
			ChatResponse resp = ChatResponse.builder()
				.memberPk(memberPk)
				.messageContent(messageContent)
				.memberNickname(member.getMemberNickname())
				.messageTime(messageTime)
				.imageURL(member.getImageURL())
				.build();
			chatResponses.add(resp);
		}
		redisTemplate.opsForList().rightPushAll(redisKeyBase + chatSession, dbToRedisChats);
		myStringRedisTemplate.opsForValue() // 30분 TTL설정
			.set("ex:" + redisKeyBase + chatSession, "key for expire", 30, TimeUnit.MINUTES); // ex:redisKeyBase:session

		log.info("************************ cache-aside 완료 !!!!!!!");

		return chatResponses;
	}

	/** chatSesssion을 가지는 채팅방에서 채팅목록 빼오기 */
	private List<ChatMessage> getChatsFromDB(String chatSession) {
		ChatRoom searchedChat = chatRoomRepository.findByChatSession(chatSession)
			.orElseThrow(() -> new NotFoundException(
				ApplicationError.CHATTING_ROOM_NOT_FOUND));

		// List<ChatMessage> chatList = chatMessageRepository.findTop50ByChatRoom_ChatPk_OrderByMessageTimeDesc(
		// 	searchedChat.getChatPk());
		List<ChatMessage> chatList = chatMessageRepository.findByChatRoom_ChatPk_OrderByMessageTimeDesc(
			searchedChat.getChatPk());
		return chatList;
	}

	// RedisChatMessages로 이루어진 List를 ChatResponse리스트로 변환
	private List<ChatResponse> redisListToChatResponseList(List<RedisChatMessage> redisChatMessages) {
		List<ChatResponse> temp = new ArrayList<>();
		for (RedisChatMessage r : redisChatMessages) {
			temp.add(redisToChatResponse(r));
		}
		return temp;
	}

	// RedisChatMessage -> ChatResponse
	private ChatResponse redisToChatResponse(RedisChatMessage r) {
		ChatResponse temp = ChatResponse.builder()
			.memberPk(r.getMemberPk())
			.memberNickname(r.getMemberNickname())
			.messageContent(r.getMessageContent())
			.imageURL(r.getImageURL())
			.messageTime(r.getMessageTime())
			.build();
		return temp;
	}

	private void participate(ChatRoom chatRoom, long memberPk) {
		// 멤버의 유효성 검사
		memberRepository.findByMemberPk(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		// member가 지금 채팅방에 들어가있는지 검사
		Optional<ChatParticipant> temp = chatParticipantRepository.findByChatRoom_ChatPkAndMemberPk(
			chatRoom.getChatPk(), memberPk);
		// 안들어가 있을때만 participant로 insert
		if (temp.isEmpty()) {
			ChatParticipant chatParticipant = ChatParticipant.builder()
				.chatRoom(chatRoom)
				.memberPk(memberPk)
				.particiJoin(LocalDateTime.now())
				.build();
			chatParticipantRepository.save(chatParticipant);
		}
	}


	/** 채팅방 찾기 */
	@Transactional
	public ChatRoom findChatRoom(long prodPk, int prodType) {
		Optional<ChatRoom> chatRoom = chatRoomRepository.findByProdPkAndProdType(prodPk, prodType);
		if(chatRoom.isPresent()){
			log.info("************************ 채팅방 있음 !!!!!!!!!");
			return chatRoom.get();
		}
		else { // 채팅한적 없으면 생성
			log.info("************************ 채팅방 생성 !!!!!!!!!");

			String chatSession = RandomNumberUtil.getRandomNumber();
			log.info("************************ 세션 생성 = {} !!!!!!!!!", chatSession);

			ChatRoom temp = ChatRoom.builder()
				.chatSession(chatSession)
				.chatCreate(LocalDateTime.now())
				.prodPk(prodPk)
				.prodType(prodType)
				.build();
			log.info("************************ 채팅방 만듬 !!!!!!!!!");

			chatRoomRepository.save(temp);
			log.info("************************ 채팅방 저장 !!!!!!!!!");

			return temp;
		}

	}

	/** 참가자가 유효한 멤버인지 검사 */
	private Member getValidMember(long memberPk) {
		return memberRepository.findByMemberPk(memberPk).orElseThrow(() -> new NotFoundException(
			ApplicationError.MEMBER_NOT_FOUND));
	}

	private void setTTL(String redisKeyBase, String session) {
		if (redisTemplate.opsForList().size(redisKeyBase + session) == 1) { // O(1)시간에 .size() 수행
			log.info(" ******************** SET TTL start!!!!");
			myStringRedisTemplate.opsForValue()
				.set("ex:" + redisKeyBase + session, "key for expire", 30, TimeUnit.MINUTES); // ex:redisKeyBase:session
			log.info(" ******************** ex:" + redisKeyBase + session + " 키의 만료시간이 설정되었습니다");

		}
	}
}
