package com.example.aucation.shop.api.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.common.util.StringToJson;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.entity.Role;
import com.example.aucation.member.db.repository.MemberRepository;
import com.example.aucation.shop.api.dto.SmallRequest;
import com.example.aucation.shop.api.dto.SmallResponse;
import com.example.aucation.shop.db.repository.ShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import okhttp3.*;

import java.util.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

	private final ShopRepository shopRepository;

	private final MemberRepository memberRepository;

	private final String apikey="rt349Vt3hOlhx8Qhv7GE5ucj3Lq4mXu7RH7UsTJtVgQ42IdZI%2BSvVVhGWyXGfytYwpKFJHmgEDts3gzNZO3j3A%3D%3D";

	private static final String NOT_SMALL_BUSINESS ="국세청에 등록되지 않은 사업자등록번호입니다.";

	private static final String IS_NOT_SMALL_NUMBER ="올바르지 않은 사업자번호입니다.";

	private static final String IS_SMALL_BUSINESS="사업자가 등록되었습니다. 할인제품을 올려보세요";

	private static final String ALREADY_SMALL_BUSINESS="이미 사업자 등록을 하셨습니다.";


	@Transactional
	public SmallResponse isSmall(long memberPk, SmallRequest smallRequest) {

		Member member = memberRepository.findById(memberPk).orElseThrow(()-> new NotFoundException(
			ApplicationError.MEMBER_NOT_FOUND));

		//resttemplate통신이 안되서 okhttp3으로 통신
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		//요청형식이 json 배열임
		List<String> arry = new ArrayList<>();
		arry.add(smallRequest.getSmallkey());

		JSONObject requestBody=new JSONObject();
		requestBody.put("b_no",arry);
		RequestBody body = RequestBody.create(requestBody.toString(),mediaType);
		Request request = new Request.Builder()
			.url("http://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey="+apikey)
			.method("POST", body)
			.addHeader("Content-Type", "application/json")
			.build();
		//결과 탐색
		try {
			Response response = client.newCall(request).execute();

			JSONObject jsonObject = StringToJson.stringToJson(response.body().string());

			JSONArray dataArray = (JSONArray) jsonObject.get("data");

			// 배열의 첫 번째 요소 (인덱스 0) 가져오기
			JSONObject dataObject = (JSONObject) dataArray.get(0);

			// "tax_type" 키에 해당하는 값을 얻기
			String taxType = (String) dataObject.get("tax_type");

			if(taxType.equals(NOT_SMALL_BUSINESS)){
				return SmallResponse.builder().message(IS_NOT_SMALL_NUMBER).build();
			}
			if(member.getMemberRole().equals(Role.SHOP)){
				return SmallResponse.builder().message(ALREADY_SMALL_BUSINESS).build();
			}

			member.updateMemberStatus();

			return SmallResponse.builder().message(IS_SMALL_BUSINESS).build();
		} catch (Exception e) {

			return null;
		}


	}
}
