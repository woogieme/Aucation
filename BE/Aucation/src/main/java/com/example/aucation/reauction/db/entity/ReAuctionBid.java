package com.example.aucation.reauction.db.entity;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.entity.AuctionBid;
import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.member.db.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ReAuctionBid")
@Getter
@Setter
@NoArgsConstructor
@AttributeOverrides({
		@AttributeOverride(name = "id", column = @Column(name = "re_auction_bid_pk")),
		@AttributeOverride(name = "createdAt", column = @Column(name = "re_auction_bid_created_at")),
		@AttributeOverride(name = "lastModifiedAt", column = @Column(name = "re_auction_bid_update_at"))
})
public class ReAuctionBid extends BaseEntity {
	private int reAucBidPrice;
	private LocalDateTime reAucBidDatetime;
	private String reAucBidDetail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Auction auction;
	@OneToMany(mappedBy = "reAuctionBid", cascade = CascadeType.PERSIST)
	List<ReAucBidPhoto> reAucBidPhotoList = new ArrayList<>();

	@Builder
	public ReAuctionBid(Long id, LocalDateTime createdAt, Long createdBy,
						LocalDateTime lastModifiedAt, Long lastModifiedBy,
						boolean isDeleted, int reAucBidPrice,
						LocalDateTime reAucBidDatetime, String reAucBidDetail,
						Member member, Auction auction) {
		super(id, createdAt, createdBy, lastModifiedAt, lastModifiedBy, isDeleted);
		this.reAucBidPrice = reAucBidPrice;
		this.reAucBidDatetime = reAucBidDatetime;
		this.reAucBidDetail = reAucBidDetail;
		addAuction(auction);
		addMember(member);
	}

	private void addAuction(Auction auction) {
		if (this.auction != null) {
			this.auction.getReAuctionBidList().remove(this);
		}
		this.auction = auction;
		auction.getReAuctionBidList().add(this);
	}

	private void addMember(Member member) {
		if (this.member != null) {
			this.member.getReAuctionBidList().remove(this);
		}
		this.member = member;
		member.getReAuctionBidList().add(this);
	}

}
