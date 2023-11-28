package com.example.aucation.auction.db.repository;

import com.example.aucation.auction.api.dto.*;
import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.reauction.api.dto.ReAuctionDetailResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepositoryCustom {
    AuctionListResponse searchPreAucToCondition(Member member, int pageNum,
                                                AuctionSortRequest sortRequest, Pageable pageable);
    AuctionListResponse searchIngAucToCondition(Member member, int pageNum,
                                                AuctionSortRequest sortRequest, Pageable pageable);
    AuctionListResponse searchReAucToCondition(Member member, int pageNum,
                                               AuctionSortRequest searchCondition, Pageable pageable);
    AuctionDetailResponse searchDetailAuc(Auction auction, Long memberPk,int auctionCondition,Member member);
    List<AuctionDetailItem> searchDetailItems(Long memberPk, Auction auction,Member member);
    ReAuctionDetailResponse searchDetailReAuc(Auction auction, Long memberPk, int checkTime,Member member);

    List<AuctionIngResponseItem> searchHotAuctionToMainPage(Long memberPk,Member member);
    List<ReAuctionResponseItem> searchRecentReAucToMainPage(Long memberPk,Member member);
}
