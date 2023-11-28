package com.example.aucation_chat.discount.db.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
// @AttributeOverrides({
// 	@AttributeOverride(name = "id", column = @Column(name = "discount_pk")),
// 	@AttributeOverride(name = "createdAt", column = @Column(name = "discount_created_at"))
// })
public class Discount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "discount_pk")
	private	long discountPk;

	private String discountTitle;
	private String discountCategory;
	private LocalDateTime discountStart;
	private LocalDateTime discountEnd;
	private int discountPrice;
	private String discountDetail;
	private int discountDiscountedPrice;
	private String discountImgURL;
	private String discountUUID;
	private int discountRate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Member owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Member customer;

}
