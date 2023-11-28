package com.example.aucation_chat.chat.api.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PersonalChatEnterResponse {
	private long sellerPk; //판매자
	private String sellerNickName; //판매자 닉네임
	private String sellerImageURL; // 판매자 프사
	private String prodName; // 제목
	private String prodType; // ex) 역경매
	private String prodCategory; // ex) 전자기기
	private int prodEndPrice; // 낙찰가, 할인가
	private String chatUUID; // 채팅방 UUID
	private List<ChatResponse> chatList;

	@Builder
	public PersonalChatEnterResponse(long sellerPk, String sellerNickName, String sellerImageURL, String prodName,
		String prodType, String prodCategory, int prodEndPrice, String chatUUID,
		List<ChatResponse> chatList) {
		this.sellerPk = sellerPk;
		this.sellerNickName = sellerNickName;
		this.sellerImageURL = sellerImageURL;
		this.prodName = prodName;
		this.prodType = prodType;
		this.prodCategory = prodCategory;
		this.prodEndPrice = prodEndPrice;
		this.chatUUID = chatUUID;
		this.chatList = chatList;
	}
}
