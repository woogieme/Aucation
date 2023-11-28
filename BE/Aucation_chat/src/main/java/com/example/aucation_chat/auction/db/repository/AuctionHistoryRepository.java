package com.example.aucation_chat.auction.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aucation_chat.auction.db.entity.Auction;
import com.example.aucation_chat.auction.db.entity.AuctionHistory;

@Repository
public interface AuctionHistoryRepository extends JpaRepository<AuctionHistory,Long> {
    boolean existsAuctionHistoryByAuction(Auction auction);
    Optional<AuctionHistory> findByAuction_AuctionPk(long auctionPk);
}
