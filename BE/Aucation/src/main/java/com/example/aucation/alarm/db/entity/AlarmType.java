package com.example.aucation.alarm.db.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {

	AUCTION ("경매"),
	DISCOUNT("할인");

	private final String message;

}
