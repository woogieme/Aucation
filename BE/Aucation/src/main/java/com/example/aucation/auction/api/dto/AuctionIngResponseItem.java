package com.example.aucation.auction.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuctionIngResponseItem {
    private Boolean isLike;    //
    private Long likeCnt;  //
    private long auctionPk;   //
    private String auctionUUID;   //
    private String auctionTitle;  //
    private Integer auctionStartPrice;  //
    private Integer auctionTopBidPrice;
    private Long auctionCurCnt;
    private Boolean auctionOwnerIsShop; //
    private String auctionOwnerNickname; //
    private LocalDateTime auctionEndTime; //
    private String auctionImg;//
    private String mycity;
    private String zipcode;
    private String street;
    private String auctionType;
}