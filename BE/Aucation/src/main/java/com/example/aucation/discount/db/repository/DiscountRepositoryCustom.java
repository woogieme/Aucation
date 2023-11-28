package com.example.aucation.discount.db.repository;

import com.example.aucation.discount.api.dto.DiscountListResponseItem;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Repository;

import com.example.aucation.discount.api.dto.DiscountListResponse;
import com.example.aucation.discount.api.dto.DiscountSortRequest;
import com.example.aucation.member.db.entity.Member;

import java.util.List;

@Repository
public interface DiscountRepositoryCustom {
	DiscountListResponse searchListByCondition(Member member, int pageNum, DiscountSortRequest sortRequest, Pageable pageable);

	List<DiscountListResponseItem> searchDiscountToMainPage(Long memberPk,Member member);

}
