package com.example.aucation.member.db.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.entity.AuctionBid;
import com.example.aucation.auction.db.entity.AuctionHistory;
import com.example.aucation.like.db.entity.LikeDiscount;
import com.example.aucation.reauction.db.entity.ReAuctionBid;
import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.follow.db.entity.Follow;
import com.example.aucation.like.db.entity.LikeAuction;
import com.example.aucation.shop.db.entity.Shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Member")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "member_pk")),
	@AttributeOverride(name = "createdAt", column = @Column(name = "member_created_at")),
	@AttributeOverride(name = "lastModifiedAt", column = @Column(name = "member_update_at"))
})
public class Member extends BaseEntity {

	@Column(unique = true)
	private String memberId;

	@Column
	private String memberPw;

	@Column(unique = true)
	private String memberEmail;
	private int memberPoint;
	private String memberNickname;
	private String memberBanned;
	private String memberRefresh;
	private String memberDetail;
	private String memberFCMToken;

	@Column
	private String imageURL;

	@Embedded
	private Address address;

	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	@Column
	private Role memberRole;

	@OneToOne
	@JoinColumn(name = "shop_pk")
	private Shop shop;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Follow> followList = new ArrayList<>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private List<Discount> discountOwnerList = new ArrayList<>();

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Discount> discountCustomerList = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<LikeAuction> likeAuctionList = new ArrayList<>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private List<Auction> auctionOwnerList = new ArrayList<>();

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Auction> auctionCustomerList = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<AuctionBid> auctionBidList = new ArrayList<>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private List<AuctionHistory> auctionHistoryOwnerList = new ArrayList<>();

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<AuctionHistory> auctionHistoryCustomerList = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<ReAuctionBid> reAuctionBidList = new ArrayList<>();
	//	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	//	private List<Alram> alramList = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<LikeDiscount> likeDiscountList = new ArrayList<>();

	@Builder
	public Member(Long id, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModifiedAt,
		Long lastModifiedBy, boolean isDeleted, String memberId, String memberPw, String memberEmail, Role memberRole,
		SocialType socialType, String memberNickname, String imageURL, Address address) {
		super(id, createdAt, createdBy, lastModifiedAt, lastModifiedBy, isDeleted);
		this.memberId = memberId;
		this.memberPw = memberPw;
		this.memberEmail = memberEmail;
		this.memberNickname = memberNickname;
		this.memberRole = memberRole;
		this.socialType = socialType;
		this.imageURL = imageURL;
		this.address = address;
	}

	public void updateRefreshToken(String updateRefreshToken) {
		this.memberRefresh = updateRefreshToken;
	}

	public void updatePoint(int point) {
		this.memberPoint = point;
	}

	public int updatePlusPoint(int count) {
		this.memberPoint += count;
		return this.memberPoint;
	}

	public void updateMemberStatus() {
		this.memberRole = Role.SHOP;
	}

	public void minusUpdatePoint(int memberPoint, int discountDiscountedPrice) {
		this.memberPoint = memberPoint-discountDiscountedPrice;
	}

	public void updateMemberNickname(String memberNickname) {
		this.memberNickname=memberNickname;
	}

	public void updateMemberDetail(String memberDetail) {
		this.memberDetail=memberDetail;
	}

	public void updateImgURL(String uploadImageUrl) {
		this.imageURL=uploadImageUrl;
	}

	public void updateOwnerPoint(int discountDiscountedPrice) {
		this.memberPoint +=discountDiscountedPrice;
	}

	public void updateFCMToken(String token) {
		this.memberFCMToken=token;
	}
}
