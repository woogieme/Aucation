package com.example.aucation.discount.db.repository;

import java.util.List;
import java.util.Optional;

import com.example.aucation.discount.api.dto.DiscountListResponseItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.aucation.auction.api.dto.AuctionSortRequest;
import com.example.aucation.auction.db.repository.AuctionRepositoryCustom;
import com.example.aucation.discount.api.dto.DiscountListResponse;
import com.example.aucation.discount.api.dto.DiscountSortRequest;
import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.member.db.entity.Member;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long>, DiscountRepositoryCustom {
	Optional<Discount> findByDiscountUUID(String discountUUID);


	/////////////////////// custom ///////////////////
	DiscountListResponse searchListByCondition(
		Member member, int pageNum, DiscountSortRequest sortRequest, Pageable pageable);

	List<DiscountListResponseItem> searchDiscountToMainPage(Long memberPk,Member member);

	@Modifying
	@Query("DELETE FROM Discount r WHERE r.id = :id")
	void delete(Long id);
}
