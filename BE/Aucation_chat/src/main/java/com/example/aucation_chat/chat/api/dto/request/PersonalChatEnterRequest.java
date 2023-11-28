package com.example.aucation_chat.chat.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonalChatEnterRequest {
	private long memberPk; // 입장하는사람 Pk
	private long prodPk; // 물품 Pk
	private int prodType; // 판매유형
}

