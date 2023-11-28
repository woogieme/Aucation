package com.example.aucation.discount.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.aucation.common.entity.HistoryStatus;
import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.member.db.entity.Address;
import com.example.aucation.member.db.entity.Member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EnterResponse {

	private String discountTitle;
	private boolean discountStatus;
	private String discountType;
	private double discountLng;
	private double discountLat;
	private LocalDateTime discountEnd;
	private int discountPrice;
	private String discountDetail;
	private int discountDiscountedPrice;
	private List<String> discountImgURL;
	private String discountUUID;
	private String ownerName;
	private long ownerPk;
	private int memberPoint;
	private long memberPk;
	private String myNickname;
	private long discountPk;

	private String ownerURL;
	private LocalDateTime discountCur;
	private boolean isLike;
	private int likeCnt;
	private int discountRate;

	private Address address;
	private String disStatus;

	public static EnterResponse of(List<String> uuidImage, Discount discount, Member member,boolean isFalse, int likeCnt, boolean discountStatus,
		String disStatus) {
		return EnterResponse.builder()
			.discountImgURL(uuidImage)
			.discountType(discount.getDiscountType())
			.discountLat(discount.getDiscountLat())
			.discountLng(discount.getDiscountLng())
			.discountDetail(discount.getDiscountDetail())
			.discountDiscountedPrice(discount.getDiscountDiscountedPrice())
			.discountEnd(discount.getDiscountEnd())
			.discountPrice(discount.getDiscountPrice())
			.discountTitle(discount.getDiscountTitle())
			.discountUUID(discount.getDiscountUUID())
			.ownerPk(discount.getOwner().getId())
			.ownerName(discount.getOwner().getMemberNickname())
			.ownerURL(discount.getOwner().getImageURL())
			.likeCnt(likeCnt)
			.isLike(isFalse)
			.discountCur(LocalDateTime.now())
			.discountRate(discount.getDiscountRate())
			.discountPk(discount.getId())
			.memberPoint(member.getMemberPoint())
			.memberPk(member.getId())
			.myNickname(member.getMemberNickname())
			.address(discount.getAddress())
			.discountStatus(discountStatus)
			.disStatus(disStatus)
			.build();
	}
}
