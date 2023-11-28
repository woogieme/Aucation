package com.example.aucation.auction.db.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum AuctionStatus {
	BID, REVERSE_BID;
}