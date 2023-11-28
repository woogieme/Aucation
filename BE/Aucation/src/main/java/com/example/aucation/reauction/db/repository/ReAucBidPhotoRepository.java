package com.example.aucation.reauction.db.repository;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.reauction.db.entity.ReAucBidPhoto;
import com.example.aucation.reauction.db.entity.ReAuctionBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReAucBidPhotoRepository extends JpaRepository<ReAucBidPhoto,Long> {
    List<ReAucBidPhoto> findByReAuctionBid(ReAuctionBid reAuctionBid);


	@Modifying
	@Query("DELETE FROM ReAucBidPhoto r WHERE r.reAuctionBid.id = :id")
	void deleteByAuctionId(Long id);

	@Modifying
	@Query("DELETE FROM ReAucBidPhoto a WHERE a.reAuctionBid.id = :reAuctionBid")
	void deleteByReAictionIdAndMemberId(Long reAuctionBid);
}
