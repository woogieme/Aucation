package com.example.aucation.auction.db.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BIDStatus {

	FIRST_BID("최초입찰"),
	NOT_FIRST_BID("중간입찰");

	private final String key;
}
