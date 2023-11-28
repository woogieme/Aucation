package com.example.aucation.member.api.dto;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberPageRequest {

	private String productStatus;
	private String auctionStatus;
	private String productFilter;
	private int myPageNum;
}
