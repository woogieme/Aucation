package com.example.aucation.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryStatus {
	BEFORE_CONFIRM, AFTER_CONFIRM, REPORT;
}