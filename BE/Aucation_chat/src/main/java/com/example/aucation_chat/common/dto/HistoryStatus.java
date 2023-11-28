package com.example.aucation_chat.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryStatus {
	BEFORE_CONFIRM, AFTER_CONFIRM, REPORT;
}