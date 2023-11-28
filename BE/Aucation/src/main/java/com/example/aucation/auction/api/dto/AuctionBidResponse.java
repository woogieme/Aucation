package com.example.aucation.auction.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionBidResponse {
    private Long auctionPk;
    private String auctionTitle;
    private String auctionOwnerNickname;
    private Integer auctionConfirmPrice;
}
