package com.example.aucation.reauction.api.dto;

import com.example.aucation.auction.api.dto.AuctionDetailItem;
import com.example.aucation.auction.db.entity.AuctionStatus;
import com.example.aucation.member.db.entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReAuctionDetailResponse {

    private Long reAuctionPk;
    private AuctionStatus reAuctionStatus;
    private String reAuctionTitle;
    private String reAuctionType;
    private Long reAuctionOwnerPk;
    private Role reAuctionOwnerMemberRole;
    private String reAuctionOwnerPhoto;
    private String reAuctionOwnerNickname;
    private List<String> reAuctionPhoto;
    private Double reAuctionMeetingLat;
    private Double reAuctionMeetingLng;
    private String reAuctionInfo;
    private Integer reAuctionStartPrice;
    private Integer reAuctionLowPrice;
    private Integer reAuctionEndPrice;  // 경매 후에만 제공
    private Long reAuctionBidCnt;
    private LocalDateTime nowTime;
    private LocalDateTime reAuctionStartTime;
    private LocalDateTime reAuctionEndTime;
    private Boolean isOwner;
    private int isAction;
    private Long likeCnt;
    private Boolean isLike;
    private String mycity;
    private String zipcode;
    private String street;

    private List<AuctionDetailItem> reAuctionDetailItems;
    private List<ReAucBidResponse> reAuctionBidItems;
    private ReAucBidResponse ownBid;
    private ReAucBidResponse selectedBid;

    @Builder
    public ReAuctionDetailResponse(Long reAuctionPk, AuctionStatus reAuctionStatus, String reAuctionTitle, String reAuctionType, Long reAuctionOwnerPk, Role reAuctionOwnerMemberRole, String reAuctionOwnerPhoto, String reAuctionOwnerNickname, List<String> reAuctionPhoto, Double reAuctionMeetingLat, Double reAuctionMeetingLng, String reAuctionInfo, Integer reAuctionStartPrice, Integer reAuctionLowPrice, Integer reAuctionEndPrice, Long reAuctionBidCnt, LocalDateTime nowTime, LocalDateTime reAuctionStartTime, LocalDateTime reAuctionEndTime, Boolean isOwner, int isAction, Long likeCnt, Boolean isLike, List<AuctionDetailItem> reAuctionDetailItems, List<ReAucBidResponse> reAuctionBidItems) {
        this.reAuctionPk = reAuctionPk;
        this.reAuctionStatus = reAuctionStatus;
        this.reAuctionTitle = reAuctionTitle;
        this.reAuctionType = reAuctionType;
        this.reAuctionOwnerPk = reAuctionOwnerPk;
        this.reAuctionOwnerMemberRole = reAuctionOwnerMemberRole;
        this.reAuctionOwnerPhoto = reAuctionOwnerPhoto;
        this.reAuctionOwnerNickname = reAuctionOwnerNickname;
        this.reAuctionPhoto = reAuctionPhoto;
        this.reAuctionMeetingLat = reAuctionMeetingLat;
        this.reAuctionMeetingLng = reAuctionMeetingLng;
        this.reAuctionInfo = reAuctionInfo;
        this.reAuctionStartPrice = reAuctionStartPrice;
        this.reAuctionLowPrice = reAuctionLowPrice;
        this.reAuctionEndPrice = reAuctionEndPrice;
        this.reAuctionBidCnt = reAuctionBidCnt;
        this.nowTime = nowTime;
        this.reAuctionStartTime = reAuctionStartTime;
        this.reAuctionEndTime = reAuctionEndTime;
        this.isOwner = isOwner;
        this.isAction = isAction;
        this.likeCnt = likeCnt;
        this.isLike = isLike;
        this.reAuctionDetailItems = reAuctionDetailItems;
        this.reAuctionBidItems = reAuctionBidItems;
    }
}
