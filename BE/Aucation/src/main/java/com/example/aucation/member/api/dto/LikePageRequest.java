package com.example.aucation.member.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikePageRequest {

	//역경매
	//경매
	//할인
	//역경매+경매
	//역경매+할인
	//경매+할인
	//전체
	private String productStatus;
	private int myPageNum;
}
