package com.example.aucation.member.api.dto;

import java.time.LocalDateTime;

import com.example.aucation.auction.db.entity.AuctionHistory;
import com.example.aucation.auction.db.entity.AuctionStatus;
import com.example.aucation.common.entity.HistoryStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MypageItemsResponse {

	//경매 이름
	private String auctionTitle;

	//경매 종료일시
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime auctionEndDate;

	//옥션 UUID
	private Long auctionPk;

	//옥션 UUID
	private String auctionUUID;

	//경매 최종낙찰가
	private int auctionSuccessPay;
	//시작가
	private int auctionStartPrice;
	// 판매
	private Long ownerPk;
	//경매 - 판매
	private LocalDateTime registerDate;

	private LocalDateTime auctionStartDate;

	private AuctionStatus auctionStatus;

	private String auctionType;

	private HistoryStatus auctionHistory;

	private LocalDateTime historyDateTime;

	private LocalDateTime historyDoneDateTime;

	private String imgfile;
	private String mycity;
	private String zipcode;
	private String street;
	private String ownerNicknname;
	private Boolean isLike;
	private Long customerPk;
}
					//
					//
					// qAuction.auctionTitle.as("auctionTitle"),
					// 	qAuction.auctionStartDate.as("auctionStartDate"),
					// 	qAuction.auctionStartPrice.as("auctionStarePrice"),
					// 	qAuction.auctionEndDate.as("auctionEndDate"),
					// 	qAuction.auctionEndPrice.as("auctionSuccessPay"),
					// 	qAuction.owner.id.as("ownerPk"),
					// 	qAuction.owner.memberNickname.as("ownerNicknname"),
					// 	qAuction.customer.memberNickname.as("customerNicknname"),
					// 	qAuction.address.city.as("mycity"),
					// 	qAuction.address.zipcode.as("zipcode"),
					// 	qAuction.address.street.as("street"),
					// 	qAuction.auctionUUID.as("auctionUUID"),
					// 	qAuction.id.as("auctionPk"),
					// 	qAuction.auctionStatus.as("auctionStatus"),
					// 	qAuction.auctionType.as("auctionType"),
					// 	qAuction.createdAt.as("registerDate"),
					// 	new CaseBuilder()
					// 	.when(
					// 	JPAExpressions.selectOne()
					// 	.from(qLikeAuction)
					// 	.where(qLikeAuction.auction.eq(qAuction)
					// 	.and(qLikeAuction.member.id.eq(member.getId())))
					// 	.exists()
					// 	)
					// 	.then(true)
					// 	.otherwise(false)
					// 	.as("isLike"),
					// 	qAuctionHistory.historyStatus.as("auctionHistory"),
					// 	qAuctionHistory.historyDateTime.as("historyDateTime"),
					// 	qAuctionHistory.historyDoneDateTime.as("historyDoneDateTime")