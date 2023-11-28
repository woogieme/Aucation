package com.example.aucation.member.api.dto;

import java.time.LocalDateTime;

import com.example.aucation.common.entity.HistoryStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyDiscountItemsResponse {
	private int discountPrice;
	private int discountDiscountedPrice;
	private Long discountPk;
	private String discountUUID;
	private String discountOwnerPk;
	private String discountCustomerPk;
	private LocalDateTime discountEnd;
	private LocalDateTime historyDatetime;
	private LocalDateTime historyDoneDatetime;
	private HistoryStatus historyStatus;
	private String customerNickname;
	private String imgfile;
	private String mycity;
	private String zipcode;
	private String street;
	private Boolean isLike;
	private Long customerPk;
	private LocalDateTime discountStart;
	private String discountTitle;
	private int discountRate;
	private LocalDateTime registerDate;
}
