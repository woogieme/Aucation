package com.example.aucation.shop.db.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.follow.db.entity.Follow;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "shop_pk"))
public class Shop extends BaseEntity {

	private String ShopName;
	private String ShopType;
	private String ShopTaxId;
	private String ShopInfo;
	private String ShopIsFood;

	@OneToMany(mappedBy = "shop", cascade = CascadeType.PERSIST)
	private List<Follow> followList = new ArrayList<>();
}
