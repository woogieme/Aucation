package com.example.aucation.discount.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscountSortRequest {
	private String discountCategory; 	// 카테고리
	private int discountCondition;		// 최근(1), 할인률(2),저가(3), 좋아요(4) 순
	private int searchType; 			// 제목(0) 판매자(1)
	private String searchKeyword; 		// 키워드

	@Builder
	public DiscountSortRequest(String discountCategory, int discountCondition, int searchType,
		String searchKeyword) {
		this.discountCategory = discountCategory;
		this.discountCondition = discountCondition;
		this.searchType = searchType;
		this.searchKeyword = searchKeyword;
	}
}
