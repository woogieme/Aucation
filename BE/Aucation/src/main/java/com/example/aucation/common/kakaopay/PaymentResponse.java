package com.example.aucation.common.kakaopay;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentResponse {
	private String message;
}
