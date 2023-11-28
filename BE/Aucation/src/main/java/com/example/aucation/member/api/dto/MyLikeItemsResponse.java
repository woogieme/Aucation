package com.example.aucation.member.api.dto;

import java.time.LocalDateTime;

import com.example.aucation.auction.db.entity.AuctionStatus;
import com.example.aucation.common.entity.HistoryStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyLikeItemsResponse {
	private int auctionStartPrice;
	private AuctionStatus auctionStatus;
	private String auctionTitle;
	private String auctionUUID;
	private Long auctionPk;
	private Long ownerPk;
	private HistoryStatus historyStatus;
	private LocalDateTime likeDateTime;
	private String imgfile;

	private String mycity;
	private String zipcode;
	private String street;

	private String ownerNicknname;
	private Long customerPk;
	private Boolean isLike;
	private LocalDateTime auctionStartDate;
	private LocalDateTime auctionEndDate;
}
