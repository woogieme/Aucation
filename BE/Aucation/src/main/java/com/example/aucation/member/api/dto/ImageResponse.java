package com.example.aucation.member.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponse {

	private String message;

	private static final String SUCCESS_IMAGE_SUBMIT="이미지 등록이 완료됐습니다.";

	public static ImageResponse of() {
		return ImageResponse.builder().message(SUCCESS_IMAGE_SUBMIT).build();
	}
}
