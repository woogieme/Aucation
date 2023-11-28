package com.example.aucation.member.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.aucation.member.api.dto.LikePageRequest;
import com.example.aucation.member.api.dto.MemberPageRequest;
import com.example.aucation.member.api.dto.MyDiscountResponse;
import com.example.aucation.member.api.dto.MyLikeResponse;
import com.example.aucation.member.api.dto.MyReverseResponse;
import com.example.aucation.member.api.dto.MypageResponse;
import com.example.aucation.member.db.entity.Member;

@Repository
public interface MemberRepositoryCustom {

	MypageResponse searchMyAuctionPage(Member member, MemberPageRequest memberPageRequest, Pageable pageable);

	MyReverseResponse searchMyReversePage(Member member, MemberPageRequest memberPageRequest, Pageable pageable);

	MyDiscountResponse searchMyDiscountPage(Member member, MemberPageRequest memberPageRequest, Pageable pageable);

	MyLikeResponse searchMyAucLIke(Member member, LikePageRequest likePageRequest, Pageable pageable);

	MyLikeResponse searchMyDisLike(Member member, LikePageRequest likePageRequest, Pageable pageable);

}
