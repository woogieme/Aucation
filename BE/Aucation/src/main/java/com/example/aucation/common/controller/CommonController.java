package com.example.aucation.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aucation.common.dto.StreetRequest;
import com.example.aucation.common.dto.StreetResponse;
import com.example.aucation.common.service.CommonService;
import com.example.aucation.common.support.AuthorizedVariable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
public class CommonController {

	private final CommonService commonService;

	// @PostMapping("/find/street")
	// public ResponseEntity<StreetResponse> findstreet(@AuthorizedVariable Long memberPk, @RequestBody StreetRequest streetRequest){
	// 	return ResponseEntity.ok().body(commonService.findstreet(memberPk,streetRequest));
	// }
}
