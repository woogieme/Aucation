package com.example.aucation.auction.db.repository;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.entity.AuctionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuctionHistoryRepository extends JpaRepository<AuctionHistory,Long> {
    boolean existsAuctionHistoryByAuction(Auction auction);
    Optional<AuctionHistory> findByAuction(Auction auction);
}
