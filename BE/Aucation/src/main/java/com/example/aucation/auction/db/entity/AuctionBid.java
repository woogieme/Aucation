package com.example.aucation.auction.db.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.member.db.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuctionBid{
	@Id
	private String auctionBidPk;
	private int AuctionBidPrice;
	private LocalDateTime AuctionBidDatetime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Auction auction;

	@Builder
	public AuctionBid(String auctioBidPk, int auctionBidPrice, LocalDateTime auctionBidDatetime, Member member, Auction auction) {
		this.auctionBidPk = auctioBidPk;
		AuctionBidPrice = auctionBidPrice;
		AuctionBidDatetime = auctionBidDatetime;
		this.member = member;
		this.auction = auction;
		addMember(member);
		addAuction(auction);
	}

	private void addAuction(Auction auction) {
		if (this.auction != null) {
			this.auction.getAuctionBidList().remove(this);
		}
		this.auction = auction;
		auction.getAuctionBidList().add(this);
	}

	private void addMember(Member member) {
		if (this.member != null) {
			this.member.getAuctionBidList().remove(this);
		}
		this.member = member;
		member.getAuctionBidList().add(this);
	}

}
