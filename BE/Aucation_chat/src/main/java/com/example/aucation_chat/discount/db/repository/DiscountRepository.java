package com.example.aucation_chat.discount.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aucation_chat.discount.db.entity.Discount;
import com.example.aucation_chat.member.db.entity.Member;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {
	Optional<Discount> findByDiscountUUID(String discountUUID);
	Optional<Discount> findByDiscountPk(long discountPK);
}
