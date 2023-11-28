package com.example.aucation.auction.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterResponse {

	private String messageType;
	private int headCnt;

	@Builder
	public EnterResponse(String messageType, int headCnt) {
		this.messageType = messageType;
		this.headCnt = headCnt;
	}
}
