package com.example.aucation.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailResponse {

	private String code;

	public static EmailResponse of(String code) {
		// 새로운 EmailResponse 객체를 생성하고 code를 설정하여 반환합니다.
		return new EmailResponse(code);
	}
}
