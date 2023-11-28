package com.example.aucation.discount.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.checkerframework.checker.units.qual.N;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountListResponseItem{
	private long discountPk;
	private Boolean isLike; //
	private Long likeCnt;   //
	private String discountTitle; //
	private String discountType; //
	private Integer originalPrice; //
	private Integer discountedPrice;
	private Integer discountRate;
	private String discountOwnerNickname; //
	private LocalDateTime discountEnd;  //
	private String discountImg;  //
	private String discountUUID;
	private String mycity;
	private String zipcode;
	private String street;

}