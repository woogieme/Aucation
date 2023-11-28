package com.example.aucation.member.db.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

	COMMON("개인"),
	SHOP("소상공인");

	private final String key;
}