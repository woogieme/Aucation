package com.example.aucation.discount.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class DiscountListResponse {
	private LocalDateTime nowTime;
	private int currentPage;
	private Long totalPage;
	private List<DiscountListResponseItem> items;

	@Builder
	public DiscountListResponse(LocalDateTime nowTime, int currentPage, Long totalPage,
		List<DiscountListResponseItem> items) {
		this.nowTime = nowTime;
		this.currentPage = currentPage;
		this.totalPage = totalPage;
		this.items = items;
	}
}

