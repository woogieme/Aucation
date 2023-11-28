package com.example.aucation_chat.chat.api.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.aucation_chat.auction.db.entity.Auction;
import com.example.aucation_chat.auction.db.repository.AuctionRepository;
import com.example.aucation_chat.chat.api.dto.response.ChatResponse;
import com.example.aucation_chat.chat.db.entity.group.GroupChatMessage;
import com.example.aucation_chat.chat.db.entity.group.GroupChatParticipant;
import com.example.aucation_chat.chat.db.entity.group.GroupChatRoom;
import com.example.aucation_chat.chat.db.repository.group.GroupChatMessageRepository;
import com.example.aucation_chat.chat.db.repository.group.GroupChatParticipantRepository;
import com.example.aucation_chat.chat.db.repository.group.GroupChatRoomRepository;
import com.example.aucation_chat.common.error.ApplicationError;
import com.example.aucation_chat.common.error.NotFoundException;
import com.example.aucation_chat.common.redis.dto.RedisChatMessage;
import com.example.aucation_chat.common.util.DateFormatPattern;
import com.example.aucation_chat.member.db.entity.Member;
import com.example.aucation_chat.member.db.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupChatService {

	private final GroupChatRoomRepository groupChatRoomRepository;
	private final GroupChatMessageRepository groupChatMessageRepository;
	private final GroupChatParticipantRepository groupChatParticipantRepository;
	private final MemberRepository memberRepository;
	private final AuctionRepository auctionRepository;
	private final WebSocketChatService webSocketChatService;
	private final RedisTemplate<String, RedisChatMessage> redisTemplate;
	private final RedisTemplate<String, String> myStringRedisTemplate;

	/**
	 * 채팅 목록 보여줌
	 * */
	public List<ChatResponse> enter(String auctionUUID, long memberPk) {
		log.info("******************** groupchat ENTER!!!!!!!");

		// auctionUUID로 채팅방 찾고 없으면 생성
		GroupChatRoom groupChatRoom = findChatRoomByUUID(auctionUUID);

		// 참여한 적 없다면 참여자로 생성
		participate(memberPk, groupChatRoom);

		// redis에서 채팅 내역 빼오기
		List<RedisChatMessage> redisChatMessages = redisTemplate.opsForList().range("chat-auc:" + auctionUUID, 0, -1);

		// 채팅 내역 있으면 반환
		if (!redisChatMessages.isEmpty()) {
			return redisListToChatResponseList(redisChatMessages);
		}
		// 채팅 내역 없으면 MySQL에서 찾아오기 : Read-through --------
		else {
			return cacheAside(auctionUUID);
		} // Read-Through 끝

	}


	private void participate(long memberPk, GroupChatRoom groupChatRoom) {
		// memberPk로 유효성검사
		memberRepository.findByMemberPk(memberPk).orElseThrow(()-> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		// member가 지금 채팅방에 들어가있는지 검사
		Optional<GroupChatParticipant> temp = groupChatParticipantRepository.findByChatRoom_ChatPkAndAndMemberPk(
			groupChatRoom.getChatPk(), memberPk);
		// 안들어가 있을때만 participant로 insert
		if(temp.isEmpty()) {
			GroupChatParticipant chatParticipant = GroupChatParticipant.builder()
				.chatRoom(groupChatRoom)
				.memberPk(memberPk)
				.particiJoin(LocalDateTime.now())
				.build();
			groupChatParticipantRepository.save(chatParticipant);
		}
	}

	private GroupChatRoom findChatRoomByUUID(String auctionUUID) {
		Auction searchedAuction = auctionRepository.findByAuctionUUID(auctionUUID)
			.orElseThrow(() -> new NotFoundException(ApplicationError.AUCTION_NOT_FOUND));
		Optional<GroupChatRoom> chatRoom = groupChatRoomRepository.findByChatSession(auctionUUID);
		if(chatRoom.isEmpty()){
			return createNotExistChatRoom(auctionUUID);
		}else{
			return chatRoom.get();
		}
	}

	private GroupChatRoom createNotExistChatRoom(String auctionUUID) {
		log.info(" ************ 방이 없어 생성, "+auctionUUID);
		GroupChatRoom temp = GroupChatRoom.builder()
			.chatSession(auctionUUID)
			.chatCreate(LocalDateTime.now())
			.build();
		groupChatRoomRepository.save(temp);
		log.info(" *********** 생성 끝 ");

		return temp;
	}

	private List<ChatResponse> cacheAside(String auctionUUID) {
		log.info("****************************** cache side 실행!!!!!!!");

    /*
		1. MySQL에서 chatMessage들 가져오기
		- auctionUUID로 chatPk 찾아서 chatPk로 chat_message 테이블 조회
	*/
		List<GroupChatMessage> chatList = getChatsFromDB(auctionUUID);

		// 2. MySQL 에도 없음 => 새로 만들어진 채팅방이라는 뜻
		if (chatList.isEmpty()) {
			log.info("************************ cache-aside: DB에도 없슴 !!!!!!!");
			return new ArrayList<>();
		}

		// 3. MySQL에 있음 => Redis에 저장 후 반환
		log.info("************************ cache-aside: DB에서 변환 시작 !!!!!!!");

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
		redisTemplate.opsForList().rightPushAll("chat-auc:" + auctionUUID, dbToRedisChats);
		myStringRedisTemplate.opsForValue() // 30분 TTL설정
			.set("ex:" + "chat-auc:" + auctionUUID, "key for expire", 30, TimeUnit.MINUTES); // ex:redisKeyBase:session

		log.info("************************ cache-aside완료 !!!!!!!");

		return chatResponses;
	}

	private List<GroupChatMessage> getChatsFromDB(String auctionUUID) {
		GroupChatRoom searchedChat = groupChatRoomRepository.findByChatSession(auctionUUID)
			.orElseThrow(() -> new NotFoundException(
				ApplicationError.CHATTING_ROOM_NOT_FOUND));

		// List<ChatMessage> chatList = chatMessageRepository.findTop50ByChatRoom_ChatPk_OrderByMessageTimeDesc(
		// 	searchedChat.getChatPk());
		List<GroupChatMessage> chatList = groupChatMessageRepository.findByChatRoom_ChatPk_OrderByMessageTimeDesc(searchedChat.getChatPk());
		return chatList;
	}

	// RedisChatMessages로 이루어진 List를 ChatResponse리스트로 변환
	private List<ChatResponse> redisListToChatResponseList(List<RedisChatMessage> redisChatMessages) {
		List<ChatResponse> temp = new ArrayList<>();
		for(RedisChatMessage r: redisChatMessages){
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

	private void setTTL(String redisKeyBase, String session) {
		if (redisTemplate.opsForList().size(redisKeyBase + session) == 1) { // O(1)시간에 .size() 수행
			log.info(" ******************** SET TTL start!!!!");
			myStringRedisTemplate.opsForValue()
				.set("ex:" + redisKeyBase + session, "key for expire", 30, TimeUnit.MINUTES); // ex:redisKeyBase:session
			log.info(" ******************** ex:"+redisKeyBase+session+" 키의 만료시간이 설정되었습니다");

		}
	}

}
