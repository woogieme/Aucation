package com.example.aucation.member.db.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.aucation.auction.db.entity.AuctionStatus;
import com.example.aucation.auction.db.entity.QAuction;
import com.example.aucation.auction.db.entity.QAuctionHistory;
import com.example.aucation.common.entity.HistoryStatus;
import com.example.aucation.discount.db.entity.QDiscount;
import com.example.aucation.discount.db.entity.QDiscountHistory;
import com.example.aucation.disphoto.db.entity.QDisPhoto;
import com.example.aucation.like.db.entity.QLikeAuction;
import com.example.aucation.like.db.entity.QLikeDiscount;
import com.example.aucation.member.api.dto.LikePageRequest;
import com.example.aucation.member.api.dto.MemberPageRequest;
import com.example.aucation.member.api.dto.MyDiscountItemsResponse;
import com.example.aucation.member.api.dto.MyDiscountResponse;
import com.example.aucation.member.api.dto.MyLikeItemsResponse;
import com.example.aucation.member.api.dto.MyLikeResponse;
import com.example.aucation.member.api.dto.MyReverseItemsResponse;
import com.example.aucation.member.api.dto.MyReverseResponse;
import com.example.aucation.member.api.dto.MypageItemsResponse;
import com.example.aucation.member.api.dto.MypageResponse;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.entity.QMember;
import com.example.aucation.photo.db.QPhoto;
import com.example.aucation.reauction.db.entity.QReAuctionBid;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QAuction qAuction = QAuction.auction;

	private final QLikeAuction qLikeAuction = QLikeAuction.likeAuction;

	private final QAuctionHistory qAuctionHistory = QAuctionHistory.auctionHistory;

	private final QReAuctionBid qReAuctionBid = QReAuctionBid.reAuctionBid;

	private final QDiscount qDiscount = QDiscount.discount;

	private final QLikeDiscount qLikeDiscount = QLikeDiscount.likeDiscount;

	private final QDiscountHistory qDiscountHistory = QDiscountHistory.discountHistory;

	private final QPhoto qPhoto = QPhoto.photo;

	private final QDisPhoto qDisPhoto = QDisPhoto.disPhoto;

	private final QMember qMember = QMember.member;

	@Override
		public MypageResponse searchMyAuctionPage(Member member, MemberPageRequest memberPageRequest,
		Pageable pageable) {
		//경매인지 아닌지 필터 ->
		//경매전, 경매중, 경매완료 인지 -> startDate로 판단
		//
		JPAQuery<MypageItemsResponse> query = jpaQueryFactory
			.select(
				Projections.bean(MypageItemsResponse.class,
					qAuction.auctionTitle.as("auctionTitle"),
					qAuction.auctionStartDate.as("auctionStartDate"),
					qAuction.auctionStartPrice.as("auctionStartPrice"),
					qAuction.auctionEndDate.as("auctionEndDate"),
					qAuction.auctionEndPrice.as("auctionSuccessPay"),
					qAuction.owner.id.as("ownerPk"),
					qAuction.owner.memberNickname.as("ownerNickname"),
					qAuction.customer.id.as("customerPk"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					qAuction.auctionUUID.as("auctionUUID"),
					qAuction.id.as("auctionPk"),
					qAuction.auctionStatus.as("auctionStatus"),
					qAuction.auctionType.as("auctionType"),
					qAuction.createdAt.as("registerDate"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction)
									.and(qLikeAuction.member.id.eq(member.getId())))
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike"),
					qAuctionHistory.historyStatus.as("auctionHistory"),
					qAuctionHistory.historyDateTime.as("historyDateTime"),
					qAuctionHistory.historyDoneDateTime.as("historyDoneDateTime"),
					qPhoto.imgUrl.min().as("imgfile")
				))
			.from(qAuction)
			.leftJoin(qAuctionHistory)
			.on(qAuctionHistory.auction.id.eq(qAuction.id))
			.leftJoin(qPhoto)
			.on(qPhoto.auction.id.eq(qAuction.id))
			.where(chooseAuctionStatus(memberPageRequest.getAuctionStatus(), member))
			.groupBy(qAuction, qAuctionHistory.historyStatus, qAuctionHistory.historyDateTime,
				qAuctionHistory.historyDoneDateTime);

		long count = query.fetchCount();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(chooseFilter(memberPageRequest.getProductFilter()));


		List<MypageItemsResponse> result = query.fetch();
		double totalPage = Math.ceil((double)count / 5);
		return MypageResponse.builder()
			.memberRole(member.getMemberRole())
			.memberDetail(member.getMemberDetail())
			.memberPoint(member.getMemberPoint())
			.imgURL(member.getImageURL())
			.memberNickname(member.getMemberNickname())
			.currentPage(memberPageRequest.getMyPageNum() - 1)
			.totalPage((int)totalPage)
			.count(count)
			.mypageItems(result)
			.build();

	}

	private Predicate chooseAuctionStatus(String auctionStatus, Member member) {
		LocalDateTime now = LocalDateTime.now();
		if ("경매전".equals(auctionStatus)) {
			return qAuction.owner.id.eq(member.getId())
				.and(qAuction.auctionStartDate.after(now))
				.and(qAuction.auctionStatus.eq(AuctionStatus.BID));
				//.and(qAuctionHistory.id.isNull());
		} else if ("경매중".equals(auctionStatus)) {
			// "판매" 및 "경매중" 경우에 대한 조건 추가
			return qAuction.owner.id.eq(member.getId())
				.and(qAuction.auctionStartDate.before(now)
					.and(qAuction.auctionEndDate.after(now)))
				.and(qAuction.auctionStatus.eq(AuctionStatus.BID))
				.and(qAuctionHistory.id.isNull());
		} else if ("경매완료".equals(auctionStatus)) {
			// "판매" 및 "경매완료" 경우에 대한 조건 추가
			return qAuction.owner.id.eq(member.getId())
				.and(qAuction.auctionEndDate.before(now))
				.and(qAuction.auctionStatus.eq(AuctionStatus.BID))
				.and(qAuctionHistory.id.isNotNull());
		} else if ("낙찰".equals(auctionStatus)) {
			return qAuction.customer.id.eq(member.getId())
				.and(qAuctionHistory.historyStatus.eq(HistoryStatus.BEFORE_CONFIRM))
				.and(qAuction.auctionStatus.eq(AuctionStatus.BID))
				.and(qAuctionHistory.historyDoneDateTime.isNull());
		} else if ("구매완료".equals(auctionStatus)) {
			// "구매" 및 "구매완료" 경우에 대한 조건 추가
			return qAuction.customer.id.eq(member.getId())
				.and(qAuctionHistory.historyStatus.eq(HistoryStatus.AFTER_CONFIRM))
				.and(qAuction.auctionStatus.eq(AuctionStatus.BID))
				.and(qAuctionHistory.historyDoneDateTime.isNotNull());
		}
		return null;
	}

	//역경매
	@Override
	public MyReverseResponse searchMyReversePage(Member member, MemberPageRequest memberPageRequest,
		Pageable pageable) {

		JPAQuery<MyReverseItemsResponse> query = jpaQueryFactory
			.select(
				Projections.bean(MyReverseItemsResponse.class,
					qAuction.auctionTitle.as("auctionTitle"),
					qAuction.auctionStartPrice.as("auctionStartPrice"),
					qAuction.auctionEndPrice.as("auctionSuccessPay"),
					qAuction.owner.id.as("ownerPk"),
					qAuction.customer.id.as("customerPk"),
					qAuction.auctionUUID.as("auctionUUID"),
					qAuction.id.as("auctionPk"),
					qAuction.auctionStatus.as("auctionStatus"),
					qAuction.owner.memberNickname.as("ownerNickname"),
					qAuction.auctionType.as("auctionType"),
					qAuction.createdAt.as("registerDate"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction)
									.and(qLikeAuction.member.id.eq(member.getId())))
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike"),
					qReAuctionBid.reAucBidDatetime.as("reAucBidDateTime"),
					qAuctionHistory.historyStatus.as("auctionHistory"),
					qAuctionHistory.historyDateTime.as("historyDateTime"),
					qAuctionHistory.historyDoneDateTime.as("historyDoneDateTime"),
					qReAuctionBid.reAucBidPrice.min().as("reAucBidPrice"),
					qPhoto.imgUrl.min().as("imgfile"),
					qReAuctionBid.id.count().intValue().as("reauctionCount")
				))
			.from(qAuction)
			.leftJoin(qAuctionHistory)
			.on(qAuctionHistory.auction.eq(qAuction))
			.leftJoin(qReAuctionBid)
			.on(qReAuctionBid.auction.eq(qAuction).and(isReauctionMethod(memberPageRequest,member.getId())))
			.leftJoin(qPhoto)
			.on(qPhoto.auction.eq(qAuction))
			.where(
				//판매인가 구매인가
				chooseReverseStatus(memberPageRequest.getAuctionStatus(), member)
			)
			.groupBy(qAuction, qReAuctionBid.reAucBidDatetime,
				qAuctionHistory.historyDateTime, qAuctionHistory.historyDoneDateTime, qAuctionHistory.historyStatus);
		long count = query.fetchCount();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(chooseReverseFilter(memberPageRequest.getProductFilter()));

		List<MyReverseItemsResponse> result = query.fetch();

		double totalPage = Math.ceil((double)count / 5);

		return MyReverseResponse.builder()
			.memberRole(member.getMemberRole())
			.memberDetail(member.getMemberDetail())
			.memberPoint(member.getMemberPoint())
			.imgURL(member.getImageURL())
			.memberNickname(member.getMemberNickname())
			.currentPage(memberPageRequest.getMyPageNum() - 1)
			.totalPage((int)totalPage)
			.mypageItems(result)
			.count(count)
			.build();
	}

	private Predicate isReauctionMethod(MemberPageRequest memberPageRequest, Long id) {

		if(memberPageRequest.getProductStatus().equals("구매")){
			if(memberPageRequest.getAuctionStatus().equals("경매중")){
				return null;
			}
			return qAuction.customer.id.eq(qReAuctionBid.member.id);
		}
		return qReAuctionBid.member.id.eq(id);
	}

	private Predicate chooseReverseStatus(String auctionStatus, Member member) {
		//역경매 - 판매 : 내가 팔려고 경매 참여하는것
		if ("입찰중".equals(auctionStatus)) {
			return qAuctionHistory.isNull()
					.and(qReAuctionBid.member.id.eq(member.getId()))
					.and(qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID));
		} else if ("낙찰".equals(auctionStatus)) {
			// "판매" 및 "경매중" 경우에 대한 조건 추가
			return qAuction.customer.id.eq(member.getId())
				.and(qAuctionHistory.isNotNull().and(qAuctionHistory.historyStatus.eq(HistoryStatus.BEFORE_CONFIRM)))
				.and(qAuctionHistory.historyDateTime.isNotNull())
				.and(qAuctionHistory.historyDoneDateTime.isNull())
				.and(qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID));
		} else if ("거래완료".equals(auctionStatus)) {
			// "판매" 및 "경매완료" 경우에 대한 조건 추가
			return qAuction.customer.id.eq(member.getId())
				.and(qAuctionHistory.isNotNull().and(qAuctionHistory.historyStatus.eq(HistoryStatus.AFTER_CONFIRM)))
				.and(qAuctionHistory.historyDateTime.isNotNull())
				.and(qAuctionHistory.historyDoneDateTime.isNotNull())
				.and(qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID));

		//역경매 - 구매 : 내가 살려고 경매 자의로 올린것
		} else if ("경매중".equals(auctionStatus)) {
			return qAuction.owner.id.eq(member.getId())
				.and(qAuctionHistory.isNull())
				.and(qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID));

		} else if ("입찰완료".equals(auctionStatus)) {
			// "구매" 및 "구매완료" 경우에 대한 조건 추가
			return qAuction.owner.id.eq(member.getId())
				.and(qReAuctionBid.isNotNull())
				.and(qAuctionHistory.isNotNull().and(qAuctionHistory.historyStatus.eq(HistoryStatus.BEFORE_CONFIRM)))
				.and(qAuction.auctionEndPrice.ne(-1))
				.and(qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID));

		} else if ("경매종료".equals(auctionStatus)) {
			// "구매" 및 "구매완료" 경우에 대한 조건 추가
			return qAuction.owner.id.eq(member.getId())
				.and(qReAuctionBid.isNotNull())
				.and(qAuctionHistory.isNotNull().and(qAuctionHistory.historyStatus.eq(HistoryStatus.AFTER_CONFIRM)))
				.and(qAuction.auctionEndPrice.ne(-1))
				.and(qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID));
		}
		return null;
	}

	//판매자 판매 입장에선 구매자를 봐야함
	//구매 입장에선 판매자를 봐야함
	@Override
	public MyDiscountResponse searchMyDiscountPage(Member member, MemberPageRequest memberPageRequest,
		Pageable pageable) {

		JPAQuery<MyDiscountItemsResponse> query = jpaQueryFactory
			.select(
				Projections.bean(MyDiscountItemsResponse.class,
					qDiscount.discountStart.as("discountStart"),
					qDiscount.discountTitle.as("discountTitle"),
					qDiscount.discountDiscountedPrice.as("discountDiscountedPrice"),
					qDiscount.discountPrice.as("discountPrice"),
					qDiscount.discountRate.as("discountRate"),
					qDiscount.discountEnd.as("discountEnd"),
					qDiscount.owner.id.as("ownerPk"),
					qDiscount.customer.id.as("customerPk"),
					qDiscount.owner.memberNickname.as("ownerNickname"),
					qDiscount.id.as("discountPk"),
					qDiscount.address.city.as("mycity"),
					qDiscount.address.zipcode.as("zipcode"),
					qDiscount.address.street.as("street"),
					qDiscount.discountUUID.as("discountUUID"),
					qDiscount.createdAt.as("registerDate"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeDiscount)
								.where(qLikeDiscount.discount.eq(qDiscount)
									.and(qLikeDiscount.member.id.eq(member.getId())))
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike"),
					qDiscountHistory.historyDatetime.as("historyDatetime"),
					qDiscountHistory.historyDoneDatetime.as("historyDoneDatetime"),
					qDiscountHistory.historyStatus.as("historyStatus"),
					qDisPhoto.imgUrl.min().as("imgfile")
				))
			.from(qDiscount)
			.leftJoin(qDiscountHistory)
			.on(qDiscount.id.eq(qDiscountHistory.discount.id))
			.leftJoin(qDisPhoto)
			.on(qDisPhoto.discount.eq(qDiscount))
			.where(isDiscountMember(memberPageRequest, member),chooseDiscountStatus(memberPageRequest.getAuctionStatus(), member))
			.groupBy(qDiscount, qDiscountHistory.historyDatetime, qDiscountHistory.historyDoneDatetime
				, qDiscountHistory.historyStatus);

		long count = query.fetchCount();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(chooseDiscount(memberPageRequest.getProductFilter()));

		List<MyDiscountItemsResponse> result = query.fetch();

		double totalPage = Math.ceil((double)count / 5);

		return MyDiscountResponse.builder()
			.memberRole(member.getMemberRole())
			.memberDetail(member.getMemberDetail())
			.memberPoint(member.getMemberPoint())
			.imgURL(member.getImageURL())
			.memberNickname(member.getMemberNickname())
			.currentPage(memberPageRequest.getMyPageNum() - 1)
			.totalPage((int)totalPage)
			.mypageItems(result)
			.count(count)
			.build();

	}


	// private Predicate isAuction(MemberPageRequest memberPageRequest, Member member) {
	// 	if (memberPageRequest.getProductStatus().equals("판매")) {
	// 		return qAuction.eq(qAuctionHistory.auction).and(qAuctionHistory.owner.id.eq(member.getId()));
	// 	} else {
	// 		return qAuction.eq(qAuctionHistory.auction).and(qAuctionHistory.customer.id.eq(member.getId()));
	// 	}
	// }
	private Predicate isAuctionMember(MemberPageRequest memberPageRequest, Member member) {
		if (memberPageRequest.getProductStatus().equals("판매")) {
			return qAuction.owner.eq(member);
		} else {
			return qAuction.customer.eq(member);
		}
	}

	private Predicate isDiscountMember(MemberPageRequest memberPageRequest, Member member) {
		if (memberPageRequest.getProductStatus().equals("판매")) {
			return qDiscount.owner.id.eq(member.getId());
		} else {
			return qDiscount.customer.id.eq(member.getId());
		}
	}

	private Predicate chooseDiscountStatus(String auctionStatus, Member member) {
		if ("판매중".equals(auctionStatus)) {
			return qDiscount.owner.id.eq(member.getId());
				//.and(qDiscountHistory.id.isNull());
		} else if ("판매대기".equals(auctionStatus)) {

			return qDiscount.owner.id.eq(member.getId())
				.and(qDiscountHistory.id.isNotNull())
				.and(qDiscountHistory.historyDoneDatetime.isNull());
		} else if ("판매완료".equals(auctionStatus)) {

			return qDiscount.owner.id.eq(member.getId())
				.and(qDiscountHistory.id.isNotNull())
				.and(qDiscountHistory.historyDoneDatetime.isNotNull());
		}

		else if ("예약중".equals(auctionStatus)) {
			return qDiscount.customer.id.eq(member.getId())
				.and(qDiscountHistory.isNotNull())
				.and(qDiscountHistory.historyStatus.eq(HistoryStatus.BEFORE_CONFIRM))
				.and(qDiscountHistory.historyDoneDatetime.isNull());
		} else if ("구매완료".equals(auctionStatus)) {
			return qDiscount.customer.id.eq(member.getId())
				.and(qDiscountHistory.isNotNull())
				.and(qDiscountHistory.historyStatus.eq(HistoryStatus.AFTER_CONFIRM))
				.and(qDiscountHistory.historyDoneDatetime.isNotNull());
		}
		return null;
	}

	private OrderSpecifier chooseFilter(String productFilter) {
		if (productFilter.equals("최신순")) {
			// 높은 가격 순
			return new OrderSpecifier<>(Order.ASC, qAuction.auctionStartDate);
		} else if (productFilter.equals("고가순")) {
			// 낮은 가격 순
			return new OrderSpecifier<>(Order.DESC, qAuction.auctionStartPrice);
		} else if (productFilter.equals("저가순")) {
			// 기본 정렬 방식
			return new OrderSpecifier<>(Order.ASC, qAuction.auctionStartPrice);
		}
		return null;
	}

	private OrderSpecifier chooseReverseFilter(String productFilter) {
		if (productFilter.equals("최신순")) {
			// 높은 가격 순
			return new OrderSpecifier<>(Order.DESC, qAuction.auctionStartDate);
		} else if (productFilter.equals("고가순")) {
			// 낮은 가격 순
			return new OrderSpecifier<>(Order.DESC, qAuction.auctionStartPrice);
		} else if (productFilter.equals("저가순")) {
			// 기본 정렬 방식
			return new OrderSpecifier<>(Order.ASC, qAuction.auctionStartPrice);
		}
		return null;
	}


	private OrderSpecifier chooseDiscount(String productFilter) {
		if (productFilter.equals("최신순")) {
			// 높은 가격 순
			return new OrderSpecifier<>(Order.ASC, qDiscount.discountStart);
		} else if (productFilter.equals("고가순")) {
			// 낮은 가격 순
			return new OrderSpecifier<>(Order.DESC, qDiscount.discountPrice);
		} else if (productFilter.equals("저가순")) {
			// 기본 정렬 방식
			return new OrderSpecifier<>(Order.ASC, qDiscount.discountPrice);
		}
		return null;
	}

	@Override
	public MyLikeResponse searchMyAucLIke(Member member, LikePageRequest likePageRequest, Pageable pageable) {
		JPAQuery<MyLikeItemsResponse> query = jpaQueryFactory
			.select(
				Projections.bean(MyLikeItemsResponse.class,
					qAuction.auctionStartPrice.as("auctionStartPrice"),
					qAuction.auctionStartDate.as("auctionStartDate"),
					qAuction.auctionEndDate.as("auctionEndDate"),
					qAuction.auctionStatus.as("auctionStatus"),
					qAuction.auctionTitle.as("auctionTitle"),
					qAuction.auctionUUID.as("auctionUUID"),
					qAuction.id.as("auctionPk"),
					qAuction.owner.id.as("ownerPk"),
					qAuction.owner.memberNickname.as("ownerNickname"),
					qAuction.customer.id.as("customerPk"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					qAuctionHistory.historyStatus.as("historyStatus"),
					qLikeAuction.createdAt.as("likeDateTime"),
					qPhoto.imgUrl.min().as("imgfile")
				))
			.from(qAuction)
			.leftJoin(qPhoto).on(qAuction.id.eq(qPhoto.auction.id))
			.leftJoin(qLikeAuction).on(qLikeAuction.auction.id.eq(qAuction.id))
			.leftJoin(qAuctionHistory).on(qAuctionHistory.auction.id.eq(qAuction.id))
			.where(qLikeAuction.member.id.eq(member.getId())
				.and(isAucAndReAuc(likePageRequest.getProductStatus())))  // 수정된 부분: 8로 고정
			.groupBy(qAuction, qAuctionHistory.historyStatus, qLikeAuction.createdAt);

		long count = query.fetchCount();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(new OrderSpecifier<>(Order.DESC, qAuction.auctionStartDate));

		List<MyLikeItemsResponse> result = query.fetch();

		double totalPage = Math.ceil((double)count / pageable.getPageSize());

		return MyLikeResponse.builder()
			.memberRole(member.getMemberRole())
			.memberDetail(member.getMemberDetail())
			.memberPoint(member.getMemberPoint())
			.imgURL(member.getImageURL())
			.memberNickname(member.getMemberNickname())
			.currentPage(likePageRequest.getMyPageNum() - 1)
			.totalPage((int)totalPage)
			.mypageItems(result)
			.count(count)
			.build();

	}

	private Predicate isAucAndReAuc(String productStatus) {
		if (productStatus.equals("경매")) {
			return qAuction.auctionStatus.eq(AuctionStatus.BID);
		}
		return qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID);
	}

	@Override
	public MyLikeResponse searchMyDisLike(Member member, LikePageRequest likePageRequest, Pageable pageable) {
		JPAQuery<MyLikeItemsResponse> query = jpaQueryFactory
			.select(
				Projections.bean(MyLikeItemsResponse.class,
					qDiscount.discountDiscountedPrice.as("auctionStartPrice"),
					qDiscount.discountStart.as("auctionStartDate"),
					qDiscount.discountEnd.as("auctionEndDate"),
					qDiscount.discountTitle.as("auctionTitle"),
					qDiscount.discountUUID.as("auctionUUID"),
					qDiscount.id.as("auctionPk"),
					qDiscount.owner.id.as("ownerPk"),
					qDiscount.customer.id.as("customerPk"),
					qDiscount.address.city.as("mycity"),
					qDiscount.address.zipcode.as("zipcode"),
					qDiscount.address.street.as("street"),
					qDiscountHistory.historyStatus.as("historyStatus"),
					qLikeDiscount.createdAt.as("likeDateTime"),
					qDisPhoto.imgUrl.min().as("imgfile")
				))
			.from(qDiscount)
			.leftJoin(qDisPhoto).on(qDisPhoto.discount.id.eq(qDiscount.id))
			.leftJoin(qLikeDiscount).on(qLikeDiscount.discount.id.eq(qDiscount.id))
			.leftJoin(qDiscountHistory).on(qDiscountHistory.discount.id.eq(qDiscount.id))
			.where(qLikeDiscount.member.id.eq(member.getId()))  // 수정된 부분: 8로 고정
			.groupBy(qDiscount, qDiscountHistory.historyStatus, qLikeDiscount.createdAt);

		long count = query.fetchCount();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(new OrderSpecifier<>(Order.DESC, qDiscount.discountStart));

		List<MyLikeItemsResponse> result = query.fetch();

		double totalPage = Math.ceil((double)count / 8);

		return MyLikeResponse.builder()
			.memberDetail(member.getMemberDetail())
			.memberRole(member.getMemberRole())
			.memberPoint(member.getMemberPoint())
			.imgURL(member.getImageURL())
			.memberNickname(member.getMemberNickname())
			.currentPage(likePageRequest.getMyPageNum() - 1)
			.totalPage((int)totalPage)
			.mypageItems(result)
			.count(count)
			.build();
	}
}