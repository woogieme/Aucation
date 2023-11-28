package com.example.aucation.reauction.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnReAucBidResponse {
    private Long reAuctionPk;
    private String reAuctionTitle;
    private String reAuctionOwnerNickname;
}
