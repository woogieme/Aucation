package com.example.aucation.member.api.dto;

import java.util.List;

import com.example.aucation.member.db.entity.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MypageResponse {

	private String memberNickname;

	private Role memberRole;

	private String memberDetail;

	private String imgURL;

	private int memberPoint;

	private int currentPage;

	private int totalPage;

	private long count;

	private List<MypageItemsResponse> mypageItems;

	@Builder
	public MypageResponse(String memberNickname, Role memberRole, String memberDetail, String imgURL, int memberPoint,
		int currentPage, int totalPage, List<MypageItemsResponse> mypageItems,long count) {
		this.memberNickname = memberNickname;
		this.memberRole = memberRole;
		this.memberDetail = memberDetail;
		this.imgURL = imgURL;
		this.memberPoint = memberPoint;
		this.currentPage = currentPage;
		this.totalPage = totalPage;
		this.mypageItems = mypageItems;
		this.count=count;
	}
}
