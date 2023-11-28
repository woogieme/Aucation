package com.example.aucation.member.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NicknameResponse {
	private String message;

	private static final String CHANGE_NICNKAME="정상적으로 닉네임을 변경하였습니다.";
	public static NicknameResponse of() {
		return NicknameResponse.builder().message(CHANGE_NICNKAME).build();
	}
}
