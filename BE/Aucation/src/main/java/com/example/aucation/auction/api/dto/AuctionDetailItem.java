package com.example.aucation.auction.api.dto;

import com.example.aucation.auction.db.entity.AuctionStatus;
import lombok.Data;

@Data
public class AuctionDetailItem {
    private Long auctionPk;
    private AuctionStatus auctionStatus;
    private Boolean isAuc;
    private String auctionTitle;
    private Integer auctionPrice;
    private String auctionPhoto;
    private Boolean isLike;
    private String mycity;
    private String zipcode;
    private String street;


}
