package com.example.aucation.auction.api.dto;

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
public class AuctionDetailResponse {

    private Long auctionPk;
    private String auctionUuid;
    private AuctionStatus auctionStatus;
    private String auctionTitle;
    private String auctionType;
    private Long auctionOwnerPk;
    private Role auctionOwnerMemberRole;
    private String auctionOwnerPhoto;
    private String auctionOwnerNickname;
    private List<String> auctionPhoto;
    private Double auctionMeetingLat;
    private Double auctionMeetingLng;
    private String auctionInfo;
    private Integer auctionStartPrice;
    private String mycity;
    private String zipcode;
    private String street;

    private LocalDateTime nowTime;
    private LocalDateTime auctionStartTime;
    private LocalDateTime auctionEndTime;
    private int isAction;

    private Long likeCnt;
    private Boolean isLike;


    private Integer auctionAskPrice;

    private List<AuctionDetailItem> auctionDetailItems = new ArrayList<>();

    // 경매 중 제공 데이터
    private Integer auctionTopPrice;

    // 경매 후 제공 데이터
    private Integer auctionEndPrice;
    private Long auctionBidCnt;

    @Builder
    public AuctionDetailResponse(Long auctionPk, String auctionUuid, AuctionStatus auctionStatus, String auctionTitle, String auctionType, Long auctionOwnerPk, Role auctionOwnerMemberRole, String auctionOwnerPhoto, String auctionOwnerNickname, List<String> auctionPhoto, Double auctionMeetingLat, Double auctionMeetingLng, String auctionInfo, Integer auctionStartPrice, LocalDateTime nowTime, LocalDateTime auctionStartTime, LocalDateTime auctionEndTime, int isAction, Long likeCnt, Boolean isLike, Integer auctionAskPrice, List<AuctionDetailItem> auctionDetailItems, Integer auctionTopPrice, Integer auctionEndPrice, Long auctionBidCnt) {
        this.auctionPk = auctionPk;
        this.auctionUuid = auctionUuid;
        this.auctionStatus = auctionStatus;
        this.auctionTitle = auctionTitle;
        this.auctionType = auctionType;
        this.auctionOwnerPk = auctionOwnerPk;
        this.auctionOwnerMemberRole = auctionOwnerMemberRole;
        this.auctionOwnerPhoto = auctionOwnerPhoto;
        this.auctionOwnerNickname = auctionOwnerNickname;
        this.auctionPhoto = auctionPhoto;
        this.auctionMeetingLat = auctionMeetingLat;
        this.auctionMeetingLng = auctionMeetingLng;
        this.auctionInfo = auctionInfo;
        this.auctionStartPrice = auctionStartPrice;
        this.nowTime = nowTime;
        this.auctionStartTime = auctionStartTime;
        this.auctionEndTime = auctionEndTime;
        this.isAction = isAction;
        this.likeCnt = likeCnt;
        this.isLike = isLike;
        this.auctionAskPrice = auctionAskPrice;
        this.auctionDetailItems = auctionDetailItems;
        this.auctionTopPrice = auctionTopPrice;
        this.auctionEndPrice = auctionEndPrice;
        this.auctionBidCnt = auctionBidCnt;
    }
}
