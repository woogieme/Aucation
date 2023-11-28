package com.example.aucation.member.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DetailResponse {

	private String message;

	private static final String CHANGE_DETAIL="정상적으로 소개글을 바꿨습니다.";
	public static DetailResponse of() {
		return DetailResponse.builder().message(CHANGE_DETAIL).build();
	}
}
