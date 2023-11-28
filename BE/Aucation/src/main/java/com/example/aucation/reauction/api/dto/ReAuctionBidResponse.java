package com.example.aucation.reauction.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReAuctionBidResponse {
    private Long reAuctionPk;
    private String reAuctionTitle;
    private String reAuctionOwnerNickname;
    private Integer reAuctionConfirmPrice;
}
