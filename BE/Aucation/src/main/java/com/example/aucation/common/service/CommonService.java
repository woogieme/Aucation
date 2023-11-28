package com.example.aucation.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aucation.common.dto.StreetRequest;
import com.example.aucation.common.dto.StreetResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonService {

	@Transactional
	public StreetResponse findstreet(double longitude, double latitude) {
		String apiKey = "641818b805857c5d7f1d3a8885668274";

		String jsonString = null;

		try {
			String addr = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=" + longitude + "&y=" + latitude;
			URL url = new URL(addr);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Authorization", "KakaoAK " + apiKey);

			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			StringBuffer docJson = new StringBuffer();
			String line;

			while ((line=rd.readLine()) != null) {
				docJson.append(line);
			}

			jsonString = docJson.toString();
			rd.close();
			ObjectMapper objectMapper = new ObjectMapper();

			JsonNode rootNode = objectMapper.readTree(jsonString);

			// documents 배열의 첫 번째 객체 선택
			JsonNode firstDocument = rootNode.path("documents").get(0);

			// 필요한 정보 추출
			String city = firstDocument.path("region_1depth_name").asText();
			String zipcode = firstDocument.path("region_2depth_name").asText();
			String street = firstDocument.path("region_3depth_name").asText();
			return StreetResponse.builder().city(city).zipcode(zipcode).street(street).build();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
