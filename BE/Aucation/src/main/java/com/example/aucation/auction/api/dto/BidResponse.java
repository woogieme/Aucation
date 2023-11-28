package com.example.aucation.auction.api.dto;

import com.example.aucation.auction.db.entity.BIDStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BidResponse {

	private long firstUser;

	private int firstBid;

	private int firstUserPoint;

	private long secondUser;

	private int	secondUserPoint;

	private int askPrice;

	private int headCnt;

	private String messageType;

	// {
	//   "askPrice":500 // 호가
	//   "firstUser" : "jinseo",  // 최고입찰자
	//   "firstBid" : 10,    // 최고입찰금
	//   "firstUserPoint" : 5 // 10 입찰해서 최고입찰자의 포인트가 5로 깎였다는 뜻
	//   "secondUser": "Jaewook",  // 최고입찰자가 등장하기 전의 최고입찰자
	//   "secondUserPoint" : 14 // Jaewook의 원래 포인트
	//   "peopleCount" : 14 // 시청자수
	// }
}
