package com.example.aucation.follow.db.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.shop.db.entity.Shop;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id",column = @Column(name="follow_pk"))
public class Follow extends BaseEntity {


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Shop shop;

	@Builder
	public Follow(Long id, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModifiedAt,
		Long lastModifiedBy, boolean isDeleted, Member member, Shop shop) {
		super(id, createdAt, createdBy, lastModifiedAt, lastModifiedBy, isDeleted);
		addMember(member);
		addShop(shop);
	}

	private void addShop(Shop shop) {
		if(this.shop!= null){
			this.shop.getFollowList().remove(this);
		}
		this.shop=shop;
		shop.getFollowList().add(this);
	}

	private void addMember(Member member){
		if(this.member!= null){
			this.member.getFollowList().remove(this);
		}
		this.member=member;
		member.getFollowList().add(this);
	}
}
