package com.example.aucation.reauction.api.service;


import com.example.aucation.auction.api.dto.*;
import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.entity.AuctionHistory;
import com.example.aucation.auction.db.entity.AuctionStatus;
import com.example.aucation.auction.db.repository.AuctionHistoryRepository;
import com.example.aucation.auction.db.repository.AuctionRepository;
import com.example.aucation.common.entity.HistoryStatus;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.ApplicationException;
import com.example.aucation.common.error.BadRequestException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.common.service.FCMService;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.repository.MemberRepository;
import com.example.aucation.photo.api.service.PhotoService;
import com.example.aucation.photo.db.Photo;
import com.example.aucation.reauction.api.dto.*;
import com.example.aucation.reauction.db.entity.ReAucBidPhoto;
import com.example.aucation.reauction.db.entity.ReAuctionBid;
import com.example.aucation.reauction.db.repository.ReAuctionBidRepository;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReAuctionService {
    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;
    private final PhotoService photoService;
    private final ReAuctionBidRepository reAuctionBidRepository;
    private final ReAucBidPhotoService reAucBidPhotoService;
    private final AuctionHistoryRepository auctionHistoryRepository;
    private final FCMService fcmService;
    private final int COUNT_IN_PAGE = 15;

    public AuctionListResponse getReAuctionList(Long memberPk, int pageNum, AuctionSortRequest sortRequest) {
        Member member = memberRepository.findById(memberPk).orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
        Pageable pageable = PageRequest.of(pageNum - 1, COUNT_IN_PAGE);

        AuctionListResponse response = auctionRepository.searchReAucToCondition(member, pageNum, sortRequest, pageable);
        if(response == null){
            log.info("********************** 경매 정보가 없습니다.");
            throw new NotFoundException(ApplicationError.NOT_EXIST_AUCTION);
        }
        response.getReItems().forEach(item->{
            if(item.getReAuctionLowBidPrice() == null){
                item.setReAuctionLowBidPrice(item.getReAuctionStartPrice());
            }
        });
        return response;
    }
    @Transactional
        public OwnReAucBidResponse bidReAuction(Long memberPk, ReAuctionBidRequest reAuctionBidRequest,
                                            List<MultipartFile> multipartFiles) throws Exception{
        log.info("********************** bidReAuction start");
        Auction auction = auctionRepository.findById(reAuctionBidRequest.getReAuctionPk())
                .orElseThrow(()-> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));
        Member member = memberRepository.findById(memberPk)
                .orElseThrow(()->new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

        log.info("********************** 입찰자 확인 시도");
        if(auction.getOwner().equals(member)){
            throw new ApplicationException(ApplicationError.OWNER_NOT_BID);
        }
        log.info("********************** 입찰자 확인 완료");

        log.info("********************** 역경매 여부 확인 시도");
        if(!auction.getAuctionStatus().equals(AuctionStatus.REVERSE_BID)){
            log.info("********************** 역경매가 아닙니다.");
            throw new Exception("역경매가 아닙니다.");
        }
        log.info("********************** 역경매 여부 확인 완료");

        log.info("********************** 역경매 입찰 가능 시간 여부 확인 시도");
        if(auction.getAuctionEndDate().isBefore(LocalDateTime.now())){
            throw new ApplicationException(ApplicationError.NOT_TIME_BID);
        }
        log.info("********************** 역경매 입찰 가능 시간 여부 확인 완료");
        
        log.info("********************** 선택된 입찰 내역이 존재 여부 확인 시도");
        if(auctionHistoryRepository.existsAuctionHistoryByAuction(auction)){
            throw new ApplicationException(ApplicationError.EXIST_BID_HISTORY);
        }
        log.info("********************** 선택된 입찰 내역 존재 여부 확인 완료");

        log.info("********************** 자신의 입찰 내역 존재 여부 확인 시도");
        if(reAuctionBidRepository.existsAuctionHistoryByAuctionAndMember(auction,member)){
            throw new ApplicationException(ApplicationError.EXIST_OWN_BID);
        }
        log.info("********************** 자신의 입찰 내역 존재 여부 확인 완료");

        log.info("********************** 역경매 입찰 내역 저장 시도");
        ReAuctionBid reAuctionBid = ReAuctionBid.builder()
                .reAucBidPrice(reAuctionBidRequest.getReAuctionBidPrice())
                .reAucBidDetail(reAuctionBidRequest.getReAuctionInfo())
                .reAucBidDatetime(LocalDateTime.now())
                .member(member)
                .auction(auction)
                .build();
        reAuctionBid = reAuctionBidRepository.save(reAuctionBid);
        log.info("********************** 역경매 입찰 내역 저장 성공");
        
        log.info("********************** 역경매 입찰 사진 저장 시도");
        reAucBidPhotoService.upload(multipartFiles,reAuctionBid.getId());
        log.info("********************** 역경매 입찰 사진 저장 성공, 사진 수 = {}", multipartFiles.size());
        log.info("********************** bidReAuction end");

        fcmService.setReAucAlram(auction.getId(),auction.getOwner().getId());

        return OwnReAucBidResponse.builder()
                .reAuctionPk(auction.getId())
                .reAuctionTitle(auction.getAuctionTitle())
                .reAuctionOwnerNickname(auction.getOwner().getMemberNickname())
                .build();
    }

    @Transactional
    public ReAuctionSelectResponse selectBid(Long memberPk, ReAuctionSelectRequest request) throws
        FirebaseMessagingException, ExecutionException, InterruptedException {
        log.info("********************** selectBid start");
        Auction auction = auctionRepository.findById(request.getReAuctionPk())
                .orElseThrow(()-> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));
        Member member = memberRepository.findById(memberPk)
                .orElseThrow(()->new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

        log.info("********************** 입찰 선택 가능 여부 확인 시도");
        if(auctionHistoryRepository.existsAuctionHistoryByAuction(auction)){
            throw new ApplicationException(ApplicationError.EXIST_BID_HISTORY);
        }
        log.info("********************** 입찰 선택 가능 여부 확인 완료");

        log.info("********************** 입찰 선택 가능 인원 확인 시도");
        log.info("*** :{}, {}",auction.getOwner().getMemberNickname(), member.getMemberNickname());
        if(!auction.getOwner().equals(member)){
            log.info("********************** 역경매 등록자가 아닙니다.");
            throw new ApplicationException(ApplicationError.NOT_OWNER);
        }
        log.info("********************** 입찰 선택 가능 인원 확인 완료");

        log.info("********************** 입찰 내역 확인 시도");
        ReAuctionBid reAuctionBid = reAuctionBidRepository.findById(request.getReAuctionBidPk())
                        .orElseThrow(()->new ApplicationException(ApplicationError.NOT_EXIST_BID));
        log.info("********************** 입찰 내역 확인 완료");

        log.info("********************** 입찰 가능 포인트 확인 및 설정 시도");
        if(member.getMemberPoint() < reAuctionBid.getReAucBidPrice()){
            throw new ApplicationException(ApplicationError.MEMBER_NOT_HAVE_MONEY);
        }
        member.updatePoint(member.getMemberPoint() - reAuctionBid.getReAucBidPrice());
        log.info("********************** 입찰 가능 포인트 확인 및 설정 완료");

        log.info("********************** 경매 상태 변경 시도");
        auction.updateReAuctionToEnd(reAuctionBid.getMember(),reAuctionBid.getReAucBidPrice());
        log.info("********************** 경매 상태 변경 완료");

        log.info("********************** 경매 내역 저장 시도");
        AuctionHistory auctionHistory = AuctionHistory.builder()
                .customer(reAuctionBid.getMember())
                .owner(member)
                .historyDateTime(LocalDateTime.now())
                .auction(auction)
                .historyStatus(HistoryStatus.BEFORE_CONFIRM)
                .build();
        auctionHistoryRepository.save(auctionHistory);

        fcmService.setAucEndAlarm(reAuctionBid.getAuction().getAuctionUUID(),reAuctionBid.getMember().getId());



        log.info("********************** 경매 내역 저장 완료");

        log.info("********************** selectBid end");


        return ReAuctionSelectResponse.builder()
                .reAuctionPk(auction.getId())
                .reAuctionSelectBidPrice(auction.getAuctionEndPrice())
                .reAuctionBidOwnerNickname(auction.getOwner().getMemberNickname())
                .build();
    }

    public ReAuctionBidResponse confirmBid(Long memberPk, ReAuctionConfirmRequest request) {
        log.info("********************** confirmBid start");
        Auction auction = auctionRepository.findById(request.getReAuctionPk())
                .orElseThrow(()-> new NotFoundException(ApplicationError.NOT_EXIST_AUCTION));
        Member member = memberRepository.findById(memberPk)
                .orElseThrow(()->new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

        log.info("********************** 선택한 입찰 내역 확인 시도");
        AuctionHistory auctionHistory = auctionHistoryRepository.findByAuction(auction)
                .orElseThrow(()-> new NotFoundException(ApplicationError.NOT_EXIST_HISTORY));
        log.info("********************** 선택한 입찰 내역 확인 완료");

        log.info("********************** 선택한 입찰 내역 상태 확인 시도");
        if(!auctionHistory.getHistoryStatus().equals(HistoryStatus.BEFORE_CONFIRM)){
            throw new BadRequestException(ApplicationError.NOT_EXIST_HISTORY);
        }
        log.info("********************** 선택한 입찰 내역 상태 확인 완료");

        log.info("********************** 입찰 내역 상태 변경 시도");
        auctionHistory.updateToConfirm();
        auctionHistoryRepository.save(auctionHistory);
        log.info("********************** 입찰 내역 상태 변경 완료 DONE-TIME = {}",LocalDateTime.now());

        log.info("********************** 역경매 입찰 등록자 포인트 시도");
        Member customer = auctionHistory.getCustomer();
        log.info("********************** customer ={}, point = {}",
                customer.getMemberNickname(),customer.getMemberPoint());
        customer.updatePoint(customer.getMemberPoint() + auction.getAuctionEndPrice());
        memberRepository.save(customer);
        log.info("********************** 역경매 입찰 등록자 포인트 완료, customer ={}, point = {}",
                customer.getMemberNickname(),customer.getMemberPoint());
        log.info("********************** confirmBid end");
        return ReAuctionBidResponse.builder()
                .reAuctionPk(auction.getId())
                .reAuctionTitle(auction.getAuctionTitle())
                .reAuctionOwnerNickname(member.getMemberNickname())
                .reAuctionConfirmPrice(auction.getAuctionEndPrice())
                .build();
    }

    public Object getDetail(Long memberPk, Auction auction,int checkTime) {

        Member members = memberRepository.findById(memberPk).orElseThrow(()->new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
        log.info("********************** ReAuction : getDetail() start");

        log.info("********************** 경매 정보 가져오기 시도");
        ReAuctionDetailResponse response = auctionRepository.searchDetailReAuc(auction,memberPk,checkTime,members);
        if(response == null ){
            log.info("********************** 경매 정보가 없습니다.");
            throw new NotFoundException(ApplicationError.NOT_EXIST_AUCTION);
        }
        if(response.getReAuctionLowPrice() == null){
            response.setReAuctionLowPrice(response.getReAuctionStartPrice());
        }
        log.info("********************** 경매 정보 가져오기 성공, AuctionPk = {}",response.getReAuctionPk());

        log.info("********************** 경매 사진 가져오기 시도");
        List<String> auctionPhotoUrl = new ArrayList<>();

        photoService.getPhoto(auction.getId()).forEach(photo->{
            auctionPhotoUrl.add(photo.getImgUrl());
        });
        response.setReAuctionPhoto(auctionPhotoUrl);
        log.info("********************** 경매 사진 가져오기 및 설정 성공, 사진 수 ={}", auctionPhotoUrl.size());
        log.info("********************** 해당 판매자 경매 정보 가져오기 시도");
        List<AuctionDetailItem> auctionDetailItems = auctionRepository.searchDetailItems(response.getReAuctionOwnerPk(),auction,members);
        auctionDetailItems.forEach(auctionDetailItem -> {
            Photo photo = photoService.getOnePhoto(auctionDetailItem.getAuctionPk());
            auctionDetailItem.setAuctionPhoto(photo.getImgUrl());
        });
        response.setReAuctionDetailItems(auctionDetailItems);
        log.info("********************** 해당 판매자 경매 정보 가져오기 완료");


        log.info("********************** 경매 상태별 정보 설정 시도");
        if(checkTime<2){
            log.info("********************** 경매 중 정보 설정 시도");
            if(response.getIsOwner()){
                List<ReAuctionBid> bids = reAuctionBidRepository.findByAuction(auction);
                List<ReAucBidResponse> allBids = new ArrayList<>();
                bids.forEach(bid->{
                    List<String> bidPhotos = reAucBidPhotoService.getPhoto(bid);
                    Member customer = bid.getMember();
                    allBids.add(ReAucBidResponse.builder()
                                    .customerPk(customer.getId())
                                    .customerNickName(customer.getMemberNickname())
                                    .customerPhoto(customer.getImageURL())
                                    .bidPk(bid.getId())
                                    .bidDetail(bid.getReAucBidDetail())
                                    .bidPrice(bid.getReAucBidPrice())
                                    .bidPhotos(bidPhotos)
                            .build());
                });
                response.setReAuctionBidItems(allBids);
            }else{
                reAuctionBidRepository.findByMemberIdAndAuction(memberPk,auction)
                        .ifPresent(myBid->{
                            Member member = memberRepository.findById(memberPk)
                                    .orElseThrow(()->new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
                            List<String> bidPhotos = reAucBidPhotoService.getPhoto(myBid);
                            response.setOwnBid(ReAucBidResponse.builder()
                                    .customerPk(memberPk)
                                    .customerNickName(member.getMemberNickname())
                                    .customerPhoto(member.getImageURL())
                                    .bidPk(myBid.getId())
                                    .bidDetail(myBid.getReAucBidDetail())
                                    .bidPrice(myBid.getReAucBidPrice())
                                    .bidPhotos(bidPhotos)
                                    .build());
                        });
            }
            log.info("********************** 경매 중 정보 설정 완료");
        }else{
            log.info("********************** 경매 후 정보 설정 시도");
            Member customer = auction.getCustomer();
            if(response.getIsOwner()){
                if(customer!=null){
                    reAuctionBidRepository.findByMemberIdAndAuction(customer.getId(),auction)
                            .ifPresent(selectedBid->{
                                List<String> bidPhotos = reAucBidPhotoService.getPhoto(selectedBid);
                                response.setSelectedBid(ReAucBidResponse.builder()
                                        .customerPk(customer.getId())
                                        .customerNickName(customer.getMemberNickname())
                                        .customerPhoto(customer.getImageURL())
                                        .bidPk(selectedBid.getId())
                                        .bidDetail(selectedBid.getReAucBidDetail())
                                        .bidPrice(selectedBid.getReAucBidPrice())
                                        .bidPhotos(bidPhotos)
                                        .build());
                            });
                }
            }else{
                if(customer != null && customer.getId().equals(memberPk)){
                    reAuctionBidRepository.findByMemberIdAndAuction(customer.getId(),auction)
                            .ifPresent(selectedBid->{
                                List<String> bidPhotos = reAucBidPhotoService.getPhoto(selectedBid);
                                response.setOwnBid(ReAucBidResponse.builder()
                                        .customerPk(customer.getId())
                                        .customerNickName(customer.getMemberNickname())
                                        .customerPhoto(customer.getImageURL())
                                        .bidPk(selectedBid.getId())
                                        .bidDetail(selectedBid.getReAucBidDetail())
                                        .bidPrice(selectedBid.getReAucBidPrice())
                                        .bidPhotos(bidPhotos)
                                        .build());
                            });
                }
            }
            log.info("********************** 경매 후 정보 설정 완료");
        }
        log.info("********************** 경매 상태별 정보 설정 완료");

        log.info("********************** 추가 정보 설정 시도");
        response.setIsAction(checkTime);
        response.setNowTime(LocalDateTime.now());
        log.info("********************** 추가 정보 설정 완료, 현재 시간 = {}, 경매 상태 = {}"
                ,response.getNowTime(), response.getIsAction());

        log.info("********************** Auction : getDetail() end");
        return response;
    }


    public List<ReAuctionResponseItem> getRecentReAucToMainPage(Long memberPk) {
        Member member = memberRepository.findById(memberPk).orElseThrow(()-> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
        List<ReAuctionResponseItem> response = auctionRepository.searchRecentReAucToMainPage(memberPk,member);
        response.forEach(item->{
            if(item.getReAuctionLowBidPrice() == null){
                item.setReAuctionLowBidPrice(item.getReAuctionStartPrice());
            }
        });
        return response;
    }
}
