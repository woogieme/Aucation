package com.example.aucation.auction.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReAuctionResponse {
    private boolean isLike;
    private int likeCnt;
    private String reAuctionTitle;
    private int reAuctionStartPrice;
    private int reAuctionBidCnt;
    private int reAuctionTopBidPrice;
    private boolean reAuctionOwnerIsShop;
    private String reAuctionOwnerNickname;
    private LocalDateTime nowTime;
    private LocalDateTime reAuctionEndTime;
}
