package com.example.aucation.auction.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExitResponse {

	private String messageType;
	private int headCnt;

	@Builder
	public ExitResponse(String messageType, int headCnt) {
		this.messageType = messageType;
		this.headCnt = headCnt;
	}
}
