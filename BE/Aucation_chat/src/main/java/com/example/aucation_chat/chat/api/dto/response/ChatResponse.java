package com.example.aucation_chat.chat.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@NoArgsConstructor
@Getter
/**
 * 채팅방에서 구독자에게 브로드캐스팅하는 메세지 dto
 * */
public class ChatResponse {
	long memberPk; // 멤버PK
	String memberNickname;	// 보낸사람 닉네임
	String messageContent;		// 채팅 내용
	String imageURL;		// 보낸사람 프사
	String messageTime; 	// 보낸시간

	@Builder
	public ChatResponse(long memberPk, String memberNickname, String messageContent, String imageURL,
		String messageTime) {
		this.memberPk = memberPk;
		this.memberNickname = memberNickname;
		this.messageContent = messageContent;
		this.imageURL = imageURL;
		this.messageTime = messageTime;
	}
}
