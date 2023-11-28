package com.example.aucation.auction.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebSocketErrorMessage {

	long memberPk;
	String errMessage;
	String messageType;
}
