package com.example.aucation.reauction.api.dto;

import com.example.aucation.auction.db.entity.AuctionStatus;
import lombok.Data;

@Data
public class ReAuctionBidRequest {
	private Long reAuctionPk;
	private String reAuctionInfo;
	private Integer reAuctionBidPrice;
}
