package com.example.aucation.shop.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aucation.common.support.AuthorizedVariable;
import com.example.aucation.shop.api.dto.SmallRequest;
import com.example.aucation.shop.api.dto.SmallResponse;
import com.example.aucation.shop.api.service.ShopService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

	private final ShopService shopService;

	@PostMapping("/verify/business")
	public SmallResponse isSmall(@AuthorizedVariable long memberPk, @RequestBody SmallRequest smallRequest) {
		return shopService.isSmall(memberPk,smallRequest);
	}
}
