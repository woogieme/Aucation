package com.example.aucation.auction.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AuctionSortRequest {
    private String auctionCatalog;  // 카테고리
    private int auctionCondition;  // 저가,고가, 최근 ,좋아요 순

    private int searchType;      // 판매자(0), 제목(1)
    private String searchKeyword;   // 키워드 검색
}
