package com.example.aucation.reauction.api.controller;


import com.example.aucation.auction.api.dto.RegisterRequest;
import com.example.aucation.common.support.AuthorizedVariable;
import com.example.aucation.reauction.api.dto.ReAuctionBidRequest;
import com.example.aucation.reauction.api.dto.ReAuctionConfirmRequest;
import com.example.aucation.reauction.api.dto.ReAuctionSelectRequest;
import com.example.aucation.reauction.api.dto.ReAuctionSelectResponse;
import com.example.aucation.reauction.api.service.ReAuctionService;
import com.example.aucation.reauction.db.entity.ReAuctionBid;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/reauction")
@RequiredArgsConstructor
@Slf4j
public class ReAuctionController {
    private final ReAuctionService reAuctionService;
    @PostMapping("/bid")
    private ResponseEntity<?> bid(@AuthorizedVariable Long memberPk,
                                     ReAuctionBidRequest reAuctionBidRequest, @RequestPart(value="multipartFiles")
    List<MultipartFile> multipartFiles) throws
            IOException, Exception {
        return ResponseEntity.ok().body(reAuctionService.bidReAuction(memberPk,reAuctionBidRequest,multipartFiles));
    }

    @PostMapping("/select")
    private ResponseEntity<?> selectReAuctionBid(@AuthorizedVariable Long memberPk,
                                                 @RequestBody ReAuctionSelectRequest request) throws
        FirebaseMessagingException, ExecutionException, InterruptedException {
        ReAuctionSelectResponse response = reAuctionService.selectBid(memberPk,request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/confirm")
    private ResponseEntity<?> confirmReAuctionBid(@AuthorizedVariable Long memberPk,
                                                 @RequestBody ReAuctionConfirmRequest request){
        return ResponseEntity.ok().body(reAuctionService.confirmBid(memberPk,request));
    }
}
