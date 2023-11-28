package com.example.aucation.reauction.db.repository;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.reauction.db.entity.ReAuctionBid;
import com.example.aucation.member.db.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReAuctionBidRepository  extends JpaRepository<ReAuctionBid,Long> {

    Optional<ReAuctionBid> findById(ReAuctionBid reAuctionBid);
    Optional<ReAuctionBid> findByMemberIdAndAuction(Long memberPk, Auction auction);
    boolean existsAuctionHistoryByAuctionAndMember(Auction auction,Member member);
    List<ReAuctionBid> findByAuction(Auction auction);


	@Modifying
	@Query("DELETE FROM ReAuctionBid r WHERE r.auction.id = :id")
	void deleteByAuctionAndAuctionId(Long id);

	@Modifying
	@Query("DELETE FROM ReAuctionBid r WHERE r.auction.id = :auctionId AND r.member.id = :memberId")
	void deleteByReAuctionIdAndMemberId(Long auctionId, Long memberId);

	Optional<ReAuctionBid> findReAuctionBidByAuctionId(Long auction);
}
