package com.example.aucation.auction.db.repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.example.aucation.common.redis.db.repository.RedisRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.aucation.auction.api.dto.AuctionDetailItem;
import com.example.aucation.auction.api.dto.AuctionDetailResponse;
import com.example.aucation.auction.api.dto.AuctionIngResponseItem;
import com.example.aucation.auction.api.dto.AuctionListResponse;
import com.example.aucation.auction.api.dto.AuctionPreResponseItem;
import com.example.aucation.auction.api.dto.AuctionSortRequest;
import com.example.aucation.auction.api.dto.ReAuctionResponseItem;
import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.entity.AuctionStatus;
import com.example.aucation.auction.db.entity.QAuction;
import com.example.aucation.auction.db.entity.QAuctionBid;
import com.example.aucation.common.redis.dto.SaveAuctionBIDRedis;
import com.example.aucation.like.db.entity.QLikeAuction;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.entity.QMember;
import com.example.aucation.member.db.entity.Role;
import com.example.aucation.member.db.repository.MemberRepository;
import com.example.aucation.photo.db.QPhoto;
import com.example.aucation.reauction.api.dto.ReAuctionDetailResponse;
import com.example.aucation.reauction.db.entity.QReAuctionBid;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class 	AuctionRepositoryImpl implements AuctionRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QAuction qAuction = QAuction.auction;
	private final QReAuctionBid qReAuctionBid = QReAuctionBid.reAuctionBid;
	private final QLikeAuction qLikeAuction = QLikeAuction.likeAuction;
	private final QAuctionBid qAuctionBid = QAuctionBid.auctionBid;
	private final QMember qMember = QMember.member;
	private final QPhoto qPhoto = QPhoto.photo;

	private final RedisTemplate<String, SaveAuctionBIDRedis> redisTemplate;

	private final RedisRepository redisRepository;

	@Override
	public AuctionListResponse searchPreAucToCondition(Member member, int pageNum,
		AuctionSortRequest searchCondition, Pageable pageable) {
		// 여기서 가져올꺼임
		NumberPath<Long> likeCnt = Expressions.numberPath(Long.class, "likeCnt");
		JPAQuery<AuctionPreResponseItem> query = queryFactory
			.select(
				Projections.bean(AuctionPreResponseItem.class,
					qAuction.id.as("auctionPk"),
					qAuction.auctionTitle.as("auctionTitle"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					qAuction.auctionStartPrice.as("auctionStartPrice"),
					qAuction.auctionStartDate.as("auctionStartTime"),
					qMember.memberNickname.as("auctionOwnerNickname"),
					qMember.memberRole.eq(Role.SHOP).as("auctionOwnerIsShop"),
					qAuction.auctionType.as("auctionType"),
					qLikeAuction.countDistinct().as(likeCnt),
					qPhoto.imgUrl.min().as("auctionImg"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction))
								.where(qLikeAuction.member.eq(member)) // Replace myUser with your user reference
								.exists()
						)
						.then(true)
						.otherwise(false).as("isLike")
				)
			).from(qAuction)
			.where(qAuction.auctionStartDate.after(LocalDateTime.now()),
				qAuction.auctionStatus.eq(AuctionStatus.BID),
				keywordEq(searchCondition.getSearchType(), searchCondition.getSearchKeyword()),
				catalogEq(searchCondition.getAuctionCatalog()
				),isMyLocation(member)
			).leftJoin(qLikeAuction)
			.on(qLikeAuction.auction.eq(qAuction))
			.leftJoin(qMember)
			.on(qAuction.owner.eq(qMember))
			.leftJoin(qPhoto)
			.on(qPhoto.auction.eq(qAuction))
			.groupBy(qAuction);

		long count = query.fetch().size();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(getSortByCondition(searchCondition.getAuctionCondition(), likeCnt));
		List<AuctionPreResponseItem> result = query.fetch();

		return AuctionListResponse.builder()
			.nowTime(LocalDateTime.now())
			.currentPage(pageNum)
			.totalPage(count)
			.preItems(result)
			.build();
	}

	private BooleanExpression isMyLocation(Member member) {
		return qAuction.address.city.eq(member.getAddress().getCity())
			.and(qAuction.address.zipcode.eq(member.getAddress().getZipcode()));
	}

	@Override
	public AuctionListResponse searchIngAucToCondition(Member member, int pageNum,
		AuctionSortRequest searchCondition, Pageable pageable) {
		NumberPath<Long> likeCnt = Expressions.numberPath(Long.class, "likeCnt");
		JPAQuery<AuctionIngResponseItem> query = queryFactory
			.select(
				Projections.bean(AuctionIngResponseItem.class,
					qAuction.id.as("auctionPk"),
					qAuction.auctionUUID.as("auctionUUID"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					qAuction.auctionTitle.as("auctionTitle"),
					qAuction.auctionStartPrice.as("auctionStartPrice"),
					qAuction.auctionEndDate.as("auctionEndTime"),
					qMember.memberNickname.as("auctionOwnerNickname"),
					qAuction.auctionType.as("auctionType"),
					qLikeAuction.countDistinct().as(likeCnt),
					qPhoto.imgUrl.min().as("auctionImg"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction))
								.where(qLikeAuction.member.eq(member)) // Replace myUser with your user reference
								.exists()
						)
						.then(true)
						.otherwise(false).as("isLike"),
					new CaseBuilder()
						.when(qAuction.owner.memberRole.eq(Role.SHOP))
						.then(true)
						.otherwise(false)
						.as("auctionOwnerIsShop")
				)
			)
			.from(qAuction)
			.where(qAuction.auctionStartDate.before(LocalDateTime.now())
					.and(qAuction.auctionEndDate.after(LocalDateTime.now())),
				qAuction.auctionStatus.eq(AuctionStatus.BID),
				keywordEq(searchCondition.getSearchType(), searchCondition.getSearchKeyword()),
				catalogEq(searchCondition.getAuctionCatalog()),isMyLocation(member)
			)
			.leftJoin(qLikeAuction)
			.on(qLikeAuction.auction.eq(qAuction))
			.leftJoin(qMember)
			.on(qAuction.owner.eq(qMember))
			.leftJoin(qPhoto)
			.on(qPhoto.auction.eq(qAuction))
			.groupBy(qAuction);

		long count = query.fetch().size();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(getSortByCondition(searchCondition.getAuctionCondition(), likeCnt));
		List<AuctionIngResponseItem> result = query.fetch();

		for (AuctionIngResponseItem item : result) {
			Long headCnt = redisRepository.getUserCount(item.getAuctionUUID());
			item.setAuctionCurCnt(headCnt);

			List<SaveAuctionBIDRedis> bidList = redisTemplate.opsForList().range("auc-ing-log:"+item.getAuctionUUID()
				, 0, -1);
			if (bidList == null || bidList.isEmpty()) {
				item.setAuctionTopBidPrice(item.getAuctionStartPrice());
				continue;
			}
			Collections.sort(bidList);
			item.setAuctionTopBidPrice(bidList.get(0).getBidPrice());
		}

		return AuctionListResponse.builder()
			.nowTime(LocalDateTime.now())
			.currentPage(pageNum)
			.totalPage(count)
			.ingItems(result)
			.build();

	}

	@Override
	public AuctionListResponse searchReAucToCondition(Member member, int pageNum,
		AuctionSortRequest searchCondition, Pageable pageable) {
		NumberPath<Long> likeCnt = Expressions.numberPath(Long.class, "likeCnt");
		JPAQuery<ReAuctionResponseItem> query = queryFactory
			.select(
				Projections.bean(ReAuctionResponseItem.class,
					qAuction.id.as("reAuctionPk"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					qAuction.auctionTitle.as("reAuctionTitle"),
					qAuction.auctionStartPrice.as("reAuctionStartPrice"),
					qReAuctionBid.reAucBidPrice.min().as("reAuctionLowBidPrice"),
					qReAuctionBid.reAucBidPrice.countDistinct().as("reAuctionBidCnt"),
					qAuction.auctionType.as("reAuctionType"),
					qPhoto.imgUrl.min().as("reAuctionImg"),
					qAuction.auctionEndDate.as("reAuctionEndTime"),
					qMember.memberNickname.as("reAuctionOwnerNickname"),
					qLikeAuction.countDistinct().as(likeCnt),
					new CaseBuilder()
						.when(qMember.memberRole.eq(Role.SHOP))
						.then(true)
						.otherwise(false)
						.as("reAuctionOwnerIsShop"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction))
								.where(qLikeAuction.member.eq(member)) // Replace myUser with your user reference
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike")
				)
			)
			.from(qAuction)
			.where(qAuction.auctionEndDate.after(LocalDateTime.now()),
				qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID),
				keywordEq(searchCondition.getSearchType(), searchCondition.getSearchKeyword()),
				catalogEq(searchCondition.getAuctionCatalog()),isMyLocation(member)
			)
			.leftJoin(qLikeAuction)
			.on(qLikeAuction.auction.eq(qAuction))
			.leftJoin(qMember)
			.on(qAuction.owner.eq(qMember))
			.leftJoin(qPhoto)
			.on(qPhoto.auction.eq(qAuction))
			.leftJoin(qReAuctionBid)
			.on(qReAuctionBid.auction.eq(qAuction))
			.groupBy(qAuction);

		long count = query.fetch().size();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(getSortByCondition(searchCondition.getAuctionCondition(), likeCnt));
		List<ReAuctionResponseItem> result = query.fetch();

		return AuctionListResponse.builder()
			.nowTime(LocalDateTime.now())
			.currentPage(pageNum)
			.totalPage(count)
			.reItems(result)
			.build();

	}

	@Override
	public AuctionDetailResponse searchDetailAuc(Auction auction, Long memberPk, int auctionCondition,Member member) {

		JPAQuery<AuctionDetailResponse> query = queryFactory
			.select(
				Projections.bean(AuctionDetailResponse.class,
					qAuction.id.as("auctionPk"),
					qAuction.auctionUUID.as("auctionUuid"),
					qAuction.auctionStatus.as("auctionStatus"),
					qAuction.auctionTitle.as("auctionTitle"),
					qAuction.auctionType.as("auctionType"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					qMember.id.as("auctionOwnerPk"),
					qMember.memberRole.as("auctionOwnerMemberRole"),
					qMember.imageURL.as("auctionOwnerPhoto"),
					qMember.memberNickname.as("auctionOwnerNickname"),
					qAuction.auctionMeetingLat.as("auctionMeetingLat"),
					qAuction.auctionMeetingLng.as("auctionMeetingLng"),
					qAuction.auctionDetail.as("auctionInfo"),
					qAuction.auctionStartPrice.as("auctionStartPrice"),
					qAuction.auctionStartDate.as("auctionStartTime"),
					qAuction.auctionEndDate.as("auctionEndTime"),
					qLikeAuction.countDistinct().as("likeCnt"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction)
									.and(qLikeAuction.member.id.eq(memberPk)))
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike"),
					qAuctionBid.AuctionBidPrice.countDistinct().as("auctionBidCnt"),
					qAuction.auctionEndPrice.as("auctionEndPrice"),
					qAuctionBid.AuctionBidPrice.max().as("auctionTopPrice")
				)
			)
			.from(qAuction)
			.where(qAuction.eq(auction),isMyLocation(member))
			.leftJoin(qLikeAuction)
			.on(qLikeAuction.auction.eq(qAuction))
			.leftJoin(qMember)
			.on(qAuction.owner.eq(qMember))
			.leftJoin(qAuctionBid)
			.on(qAuctionBid.auction.eq(qAuction))
			.groupBy(qAuction);
		return query.fetchOne();
	}

	@Override
	public List<AuctionDetailItem> searchDetailItems(Long memberPk, Auction auction,Member member) {
		JPAQuery<AuctionDetailItem> query = queryFactory
			.select(
				Projections.bean(AuctionDetailItem.class,
					qAuction.id.as("auctionPk"),
					qAuction.auctionStatus.as("auctionStatus"),
					qAuction.auctionTitle.as("auctionTitle"),
					qAuction.auctionStartPrice.as("auctionPrice"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction)
									.and(qLikeAuction.member.id.eq(memberPk)))
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike"),
					new CaseBuilder()
						.when(qAuction.auctionStartDate.before(LocalDateTime.now()))
						.then(true)
						.otherwise(false)
						.as("isAuc")
				)
			)
			.from(qAuction)
			.where(
				qAuction.ne(auction),
				qAuction.owner.id.eq(memberPk),
				qAuction.auctionEndDate.after(LocalDateTime.now())
				,isMyLocation(member)
			)
			.orderBy(qAuction.id.desc())
			.limit(4);
		return query.fetch();
	}

	@Override
	public ReAuctionDetailResponse searchDetailReAuc(Auction auction, Long memberPk, int checkTime,Member member) {
		JPAQuery<ReAuctionDetailResponse> query = queryFactory
			.select(
				Projections.bean(ReAuctionDetailResponse.class,
					qAuction.id.as("reAuctionPk"),
					qAuction.auctionStatus.as("reAuctionStatus"),
					qAuction.auctionTitle.as("reAuctionTitle"),
					qAuction.auctionType.as("reAuctionType"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					qMember.id.as("reAuctionOwnerPk"),
					qMember.memberRole.as("reAuctionOwnerMemberRole"),
					qMember.imageURL.as("reAuctionOwnerPhoto"),
					qMember.memberNickname.as("reAuctionOwnerNickname"),
					qAuction.auctionMeetingLat.as("reAuctionMeetingLat"),
					qAuction.auctionMeetingLng.as("reAuctionMeetingLng"),
					qAuction.auctionDetail.as("reAuctionInfo"),
					qAuction.auctionStartPrice.as("reAuctionStartPrice"),
					qAuction.auctionEndPrice.as("reAuctionEndPrice"),
					qAuction.auctionStartDate.as("reAuctionStartTime"),
					qAuction.auctionEndDate.as("reAuctionEndTime"),
					qLikeAuction.countDistinct().as("likeCnt"),
					qReAuctionBid.reAucBidPrice.min().as("reAuctionLowPrice"),
					qReAuctionBid.reAucBidPrice.countDistinct().as("reAuctionBidCnt"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction)
									.and(qLikeAuction.member.id.eq(memberPk)))
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike"),
					new CaseBuilder()
						.when(qAuction.owner.id.eq(memberPk))
						.then(true)
						.otherwise(false)
						.as("isOwner")
				)
			)
			.from(qAuction)
			.where(qAuction.eq(auction),isMyLocation(member))
			.leftJoin(qLikeAuction)
			.on(qLikeAuction.auction.eq(qAuction))
			.leftJoin(qMember)
			.on(qAuction.owner.eq(qMember))
			.leftJoin(qReAuctionBid)
			.on(qReAuctionBid.auction.eq(qAuction))
			.groupBy(qAuction);
		return query.fetchOne();
	}

	@Override
	public List<AuctionIngResponseItem> searchHotAuctionToMainPage(Long memberPk,Member member) {
		NumberPath<Long> likeCnt = Expressions.numberPath(Long.class, "likeCnt");
		JPAQuery<AuctionIngResponseItem> query = queryFactory
			.select(
				Projections.bean(AuctionIngResponseItem.class,
					qAuction.id.as("auctionPk"),
					qAuction.auctionUUID.as("auctionUUID"),
					qAuction.auctionTitle.as("auctionTitle"),
					qAuction.auctionStartPrice.as("auctionStartPrice"),
					qAuction.auctionEndDate.as("auctionEndTime"),
					qAuction.address.city.as("mycity"),
					qAuction.address.zipcode.as("zipcode"),
					qAuction.address.street.as("street"),
					qAuction.auctionType.as("auctionType"),
					qMember.memberNickname.as("auctionOwnerNickname"),
					new CaseBuilder()
						.when(qMember.memberRole.eq(Role.SHOP))
						.then(true)
						.otherwise(false)
						.as("auctionOwnerIsShop"),
					qLikeAuction.countDistinct().as(likeCnt),
					qPhoto.imgUrl.min().as("auctionImg"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction)
									.and(qLikeAuction.member.id.eq(memberPk)))
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike")
				)
			)
			.from(qAuction)
			.where(qAuction.auctionStartDate.before(LocalDateTime.now())
					.and(qAuction.auctionEndDate.after(LocalDateTime.now())),
				qAuction.auctionStatus.eq(AuctionStatus.BID)
				,isMyLocation(member)
			)
			.leftJoin(qLikeAuction)
			.on(qLikeAuction.auction.eq(qAuction))
			.leftJoin(qMember)
			.on(qAuction.owner.eq(qMember))
			.leftJoin(qPhoto)
			.on(qPhoto.auction.eq(qAuction))
			.groupBy(qAuction)
			.orderBy(new OrderSpecifier<>(Order.DESC, likeCnt))
			.limit(16);

		List<AuctionIngResponseItem> result = query.fetch();

		for (AuctionIngResponseItem item : result) {
			Long headCnt = redisRepository.getUserCount(item.getAuctionUUID());
			item.setAuctionCurCnt(headCnt);

			List<SaveAuctionBIDRedis> bidList = redisTemplate.opsForList().range("auc-ing-log:"+item.getAuctionUUID()
				, 0, -1);
			if (bidList == null || bidList.isEmpty()) {
				item.setAuctionTopBidPrice(item.getAuctionStartPrice());
				continue;
			}
			Collections.sort(bidList);
			item.setAuctionTopBidPrice(bidList.get(0).getBidPrice());
		}
		return result;
	}

	@Override
	public List<ReAuctionResponseItem> searchRecentReAucToMainPage(Long memberPk,Member member) {
		NumberPath<Long> likeCnt = Expressions.numberPath(Long.class, "likeCnt");
		JPAQuery<ReAuctionResponseItem> query = queryFactory
			.select(
				Projections.bean(ReAuctionResponseItem.class,
					qAuction.id.as("reAuctionPk"),
					qAuction.auctionTitle.as("reAuctionTitle"),
					qAuction.auctionStartPrice.as("reAuctionStartPrice"),
					qReAuctionBid.reAucBidPrice.min().as("reAuctionLowBidPrice"),
					qReAuctionBid.reAucBidPrice.countDistinct().as("reAuctionBidCnt"),
					qPhoto.imgUrl.min().as("reAuctionImg"),
					qAuction.auctionEndDate.as("reAuctionEndTime"),
					qMember.memberNickname.as("reAuctionOwnerNickname"),
					qAuction.auctionType.as("reAuctionType"),
					qLikeAuction.countDistinct().as(likeCnt),
					new CaseBuilder()
						.when(qMember.memberRole.eq(Role.SHOP))
						.then(true)
						.otherwise(false)
						.as("reAuctionOwnerIsShop"),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeAuction)
								.where(qLikeAuction.auction.eq(qAuction)
									.and(qLikeAuction.member.id.eq(memberPk)))
								.exists()
						)
						.then(true)
						.otherwise(false)
						.as("isLike")
				)
			)
			.from(qAuction)
			.where(qAuction.auctionEndDate.after(LocalDateTime.now()),
				qAuction.auctionStatus.eq(AuctionStatus.REVERSE_BID)
			)
			.leftJoin(qLikeAuction)
			.on(qLikeAuction.auction.eq(qAuction))
			.leftJoin(qMember)
			.on(qAuction.owner.eq(qMember))
			.leftJoin(qPhoto)
			.on(qPhoto.auction.eq(qAuction))
			.leftJoin(qReAuctionBid)
			.on(qReAuctionBid.auction.eq(qAuction))
			.groupBy(qAuction)
			.orderBy(new OrderSpecifier<>(Order.DESC, qAuction.auctionStartDate))
			.limit(4);

		return query.fetch();
	}

	private BooleanExpression catalogEq(String catalog) {
		if (catalog == null) {
			return null;
		}
		return qAuction.auctionType.eq(catalog);
	}

	private BooleanExpression keywordEq(int type, String keyword) {
		if (keyword == null) {
			return null;
		}
		if (type == 0) {
			return qAuction.auctionTitle.contains(keyword);
		} else {
			return qAuction.owner.memberNickname.contains(keyword);
		}

	}

	private OrderSpecifier<?> getSortByCondition(int condition, NumberPath<Long> likeCnt) {
		if (condition == 2) {
			// 높은 가격 순
			return new OrderSpecifier<>(Order.DESC, qAuction.auctionStartPrice);
		} else if (condition == 3) {
			// 낮은 가격 순
			return new OrderSpecifier<>(Order.ASC, qAuction.auctionStartPrice);
		} else if (condition == 4) {
			// 좋아요 순
			return new OrderSpecifier<>(Order.DESC, likeCnt);
		} else {
			// 기본 정렬 방식
			return new OrderSpecifier<>(Order.ASC, qAuction.auctionStartDate);
		}
	}
}
