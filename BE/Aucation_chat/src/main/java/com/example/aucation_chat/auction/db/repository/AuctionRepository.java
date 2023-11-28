package com.example.aucation_chat.auction.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aucation_chat.auction.db.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
	Optional<Auction> findByAuctionUUID(String auctionUUID);
	Optional<Auction> findByAuctionPk(long auctionPk);
}
