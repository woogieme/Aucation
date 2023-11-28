package com.example.aucation_chat.chat.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/** 채팅방에 보낼 내용 */
public class ChatRequest {  
	private long memberPk;	// 보낸사람 pk
	private String content;		// 채팅내용
}



