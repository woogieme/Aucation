package com.example.aucation.common.kakaopay;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.BadRequestException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.repository.MemberRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;

@Service
public class PayService {

	@Value("${iamport.key}")
	private String restApiKey;
	@Value("${iamport.secret}")
	private String restApiSecret;

	private static final String POINT_CHARGER ="포인트가 충전되었습니다.";

	private MemberRepository memberRepository;

	private IamportClient iamportClient;


	public PayService(MemberRepository memberRepository) {
		this.iamportClient = new IamportClient("1161083051413771", "2F1PV6Uctr2JOyqWLvDelOuGYBTNs6Zrn5Ptqi3MOmwFKA2xnf4STh1gYoCWeUshsb6lESJ5OjuZEMYW");
		this.memberRepository = memberRepository;
	}

	@Transactional
	public PaymentResponse chargePoint(long memberPk, PayRequest payRequest) throws
		IamportResponseException,
		IOException {
		Member member = memberRepository.findById(memberPk).orElseThrow(()-> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		IamportResponse<Payment> iamResponse = iamportClient.paymentByImpUid(payRequest.getImpUID());

		member.updatePlusPoint(iamResponse.getResponse().getAmount().intValue());
		return PaymentResponse.builder().message(POINT_CHARGER).build();
	}
}
