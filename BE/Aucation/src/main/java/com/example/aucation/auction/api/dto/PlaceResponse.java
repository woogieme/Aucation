package com.example.aucation.auction.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.member.db.entity.Address;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.entity.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlaceResponse {

	private long memberPk;
	private int memberPoint;
	private String title;
	private String detail;
	private String ownerNickname;
	private List<String> picture;
	private String ownerPicture;
	private long ownerPk;
	private Role ownerType;
	private int nowPrice;
	private int askPrice;
	private LocalDateTime enterTime;
	private LocalDateTime endTime;
	private int headCnt;
	private boolean highBid;
	private String myNickname;
	private Address address;

	// public PlaceResponse(long memberPk, int memberPoint, String title, String detail, String ownerNickname,
	// 	List<String> picture, String ownerPicture, long ownerPk, Role ownerType, int nowPrice, int askPrice,
	// 	LocalDateTime enterTime, LocalDateTime endTime, int headCnt, boolean highBid, String myNickname) {
	// 	this.memberPk = memberPk;
	// 	this.memberPoint = memberPoint;
	// 	this.title = title;
	// 	this.detail = detail;
	// 	this.ownerNickname = ownerNickname;
	// 	this.picture = picture;
	// 	this.ownerPicture = ownerPicture;
	// 	this.ownerPk = ownerPk;
	// 	this.ownerType = ownerType;
	// 	this.nowPrice = nowPrice;
	// 	this.askPrice = askPrice;
	// 	this.enterTime = enterTime;
	// 	this.endTime = endTime;
	// 	this.headCnt = headCnt;
	// 	this.highBid = highBid;
	// 	this.myNickname = myNickname;
	// }
}
