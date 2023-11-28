package com.example.aucation_chat.auction.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.aucation_chat.member.db.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
// @AttributeOverrides({
// 	@AttributeOverride(name = "id",column = @Column(name="auction_pk")),
// 	@AttributeOverride(name="createdAt",column = @Column(name="auction_created_at"))
// })
public class Auction{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auction_pk")
	private	long auctionPk;

	@Column(name = "auctionuuid")
	private String auctionUUID;

	private AuctionStatus auctionStatus;
	private String auctionTitle;
	private String auctionType; // 카테고리
	private int auctionStartPrice;
	private int auctionEndPrice;
	private double auctionMeetingLat;
	private double auctionMeetingLng;
	private String auctionDetail;
	private LocalDateTime auctionStartDate;
	private LocalDateTime auctionEndDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="auction_owner_pk")
	private Member owner;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="auction_customer_pk")
	private Member customer;
}
