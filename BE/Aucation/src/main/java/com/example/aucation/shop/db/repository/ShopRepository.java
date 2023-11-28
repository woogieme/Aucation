package com.example.aucation.shop.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aucation.shop.db.entity.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
}
