package com.example.aucation.auction.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReAuctionResponseItem {
    private Long reAuctionPk;
    private Boolean isLike; //
    private Long likeCnt;   //
    private String reAuctionTitle; //
    private Integer reAuctionStartPrice; //
    private Integer reAuctionLowBidPrice;
    private Long reAuctionBidCnt;
    private boolean reAuctionOwnerIsShop;
    private String reAuctionOwnerNickname; //
    private LocalDateTime reAuctionEndTime;  //
    private String reAuctionImg;  //
    private String mycity;
    private String zipcode;
    private String street;
    private String reAuctionType;
}
