package com.example.aucation.discount.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.discount.db.entity.DiscountHistory;

@Repository
public interface DiscountHistoryRepository extends JpaRepository<DiscountHistory,Long> {
	Optional<DiscountHistory> findByDiscountId(Long id);

	boolean existsByDiscountId(Long id);

	@Modifying
	@Query("DELETE FROM DiscountHistory r WHERE r.discount.id = :discount")
	void delete(Long discount);


	boolean existsDiscountHistoryByDiscount(Discount discount);

	@Query("SELECT disHistory FROM DiscountHistory disHistory WHERE disHistory.customer.id = :memberId AND disHistory.discount.id = :discountId")
	Optional<DiscountHistory> findByDiscountIdAndMemberId(Long memberId, Long discountId);
}
