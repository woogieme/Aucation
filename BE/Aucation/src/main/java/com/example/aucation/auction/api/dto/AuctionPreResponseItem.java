package com.example.aucation.auction.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuctionPreResponseItem{
    private long auctionPk;
    private Boolean isLike; //
    private Long likeCnt;   //
    private String auctionTitle; //
    private Integer auctionStartPrice; //
    private boolean auctionOwnerIsShop;
    private String auctionOwnerNickname; //
    private LocalDateTime auctionStartTime;  //
    private String auctionImg;  //
    private String mycity;
    private String zipcode;
    private String street;
    private String auctionType;
}