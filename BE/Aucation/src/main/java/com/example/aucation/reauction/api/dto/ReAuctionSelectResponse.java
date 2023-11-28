package com.example.aucation.reauction.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReAuctionSelectResponse {
    private Long reAuctionPk;
    private Integer reAuctionSelectBidPrice;
    private String reAuctionBidOwnerNickname;
}
