package com.example.aucation.common.kakaopay;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aucation.common.support.AuthorizedVariable;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PayController {

	private final PayService payService;

	//포인트 충전
	@PostMapping("/verifyIamport")
	public ResponseEntity<PaymentResponse> paymentByImpUid(@AuthorizedVariable long memberPk, @RequestBody PayRequest payRequest) throws
		IamportResponseException,
		IOException {
		return ResponseEntity.ok().body(payService.chargePoint(memberPk,payRequest));
	}

	//포인트
}
