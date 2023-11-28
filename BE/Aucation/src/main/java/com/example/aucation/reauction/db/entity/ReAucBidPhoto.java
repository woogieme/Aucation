package com.example.aucation.reauction.db.entity;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.photo.db.PhotoStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverrides({
	@AttributeOverride(name = "id",column = @Column(name="re_auc_bid_photo_pk")),
	@AttributeOverride(name="createdAt",column = @Column(name="re_auc_bid_photo_created_at"))
})
public class ReAucBidPhoto extends BaseEntity {

	@Column(name = "img_url")
	private String imgUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="re_auction_bid_pk")
	private ReAuctionBid reAuctionBid;

	@Builder
	public ReAucBidPhoto(Long id, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModifiedAt, Long lastModifiedBy, boolean isDeleted, String imgUrl, ReAuctionBid reAuctionBid) {
		super(id, createdAt, createdBy, lastModifiedAt, lastModifiedBy, isDeleted);
		this.imgUrl = imgUrl;
		addReAuctionBid(reAuctionBid);
	}

	private void addReAuctionBid(ReAuctionBid reAuctionBid) {
		if(this.reAuctionBid != null) {
			// team에서 해당 Entity를 제거
			this.reAuctionBid.getReAucBidPhotoList().remove(this);
		}
		// 해당 member Entity에 파라미터로 들어온 team 연관 관계 설정
		this.reAuctionBid = reAuctionBid;
		// 파라미터로 들어온 team Entity에 member 연관 관계 설정
		reAuctionBid.getReAucBidPhotoList().add(this);
	}
}
