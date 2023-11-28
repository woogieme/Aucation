package com.example.aucation.auction.api.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.example.aucation.auction.db.entity.AuctionStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	private AuctionStatus auctionStatus;
	private String auctionTitle;
	private String auctionType;
	private double auctionMeetingLat;
	private double auctionMeetingLng;
	private int auctionStartPrice;
	private String auctionDetail;
	private long auctionStartAfterTime;
}
