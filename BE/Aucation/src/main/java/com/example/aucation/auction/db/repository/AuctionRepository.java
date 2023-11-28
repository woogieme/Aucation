package com.example.aucation.auction.db.repository;

import java.util.List;
import java.util.Optional;

import com.example.aucation.auction.api.dto.*;
import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.reauction.api.dto.ReAuctionDetailResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.aucation.auction.db.entity.Auction;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long>, AuctionRepositoryCustom {
	Optional<Auction> findByAuctionUUID(String auctionUUID);
	AuctionListResponse searchPreAucToCondition(Member member, int pageNum,
												AuctionSortRequest sortRequest, Pageable pageable);
	AuctionListResponse searchIngAucToCondition(Member memberPk, int pageNum,
												AuctionSortRequest sortRequest, Pageable pageable);
	AuctionListResponse searchReAucToCondition(Member member, int pageNum,
											   AuctionSortRequest searchCondition, Pageable pageable);
	AuctionDetailResponse searchDetailAuc(Auction auction, Long memberPk,int auctionCondition,Member member);

	List<AuctionDetailItem> searchDetailItems(Long memberPk,Auction auction,Member member);

	ReAuctionDetailResponse searchDetailReAuc(Auction auction, Long memberPk, int checkTime,Member member);
	List<AuctionIngResponseItem> searchHotAuctionToMainPage(Long memberPk,Member memeber);
	List<ReAuctionResponseItem> searchRecentReAucToMainPage(Long memberPk, Member member);

	@Query("SELECT a FROM Auction a WHERE a.id = :prodPk AND a.auctionStartDate > CURRENT_TIMESTAMP")
	Optional<Auction> findByIdAndThinkDate(Long prodPk);
}
