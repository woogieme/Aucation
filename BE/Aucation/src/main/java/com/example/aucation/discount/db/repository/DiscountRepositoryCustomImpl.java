package com.example.aucation.discount.db.repository;


import java.time.LocalDateTime;
import java.util.List;

import com.example.aucation.discount.db.entity.QDiscountHistory;
import com.example.aucation.disphoto.db.entity.QDisPhoto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import com.example.aucation.discount.api.dto.DiscountListResponse;
import com.example.aucation.discount.api.dto.DiscountListResponseItem;
import com.example.aucation.discount.api.dto.DiscountSortRequest;
import com.example.aucation.discount.db.entity.QDiscount;
import com.example.aucation.like.db.entity.QLikeDiscount;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.entity.QMember;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
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
public class DiscountRepositoryCustomImpl implements DiscountRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QDiscount qDiscount = QDiscount.discount;
	private final QDisPhoto qDisPhoto = QDisPhoto.disPhoto;
	private final QMember qMember = QMember.member;
	private final QLikeDiscount qLikeDiscount = QLikeDiscount.likeDiscount;

	private final QDiscountHistory qDiscountHistory = QDiscountHistory.discountHistory;


	@Override
	public DiscountListResponse searchListByCondition(Member member, int pageNum, DiscountSortRequest sortRequest,
		Pageable pageable) {
		NumberPath<Long> likeCnt = Expressions.numberPath(Long.class,"likeCnt");
		JPAQuery<DiscountListResponseItem> query = queryFactory
			.select(
				Projections.bean(DiscountListResponseItem.class,
					qDiscount.id.as("discountPk"),
					qDiscount.discountTitle.as("discountTitle"),
					qDiscount.discountType.as("discountType"),
					qDiscount.discountDiscountedPrice.as("discountedPrice"),
					qDiscount.discountPrice.as("originalPrice"),
					qDiscount.discountRate.as("discountRate"),
					qDiscount.address.city.as("mycity"),
					qDiscount.address.zipcode.as("zipcode"),
					qDiscount.address.street.as("street"),
					qDisPhoto.imgUrl.min().as("discountImg"),
					qDiscount.discountEnd.as("discountEnd"),
					qDiscount.discountUUID.as("discountUUID"),
					qMember.memberNickname.as("discountOwnerNickname"),
					qLikeDiscount.countDistinct().as(likeCnt),
					new CaseBuilder()
						.when(
							JPAExpressions.selectOne()
								.from(qLikeDiscount)
								.where(qLikeDiscount.discount.eq(qDiscount))
								.where(qLikeDiscount.member.eq(member)) // Replace myUser with your user reference
								.exists()
						)
						.then(true)
						.otherwise(false).as("isLike")
				)
			).from(qDiscount)
			.where(
					qDiscount.discountEnd.after(LocalDateTime.now()),
				catalogEq(sortRequest.getDiscountCategory()),
				keywordEq(sortRequest.getSearchType(), sortRequest.getSearchKeyword())
				,isMyDiscount(member)
			)
			.leftJoin(qMember)
				.on(qDiscount.owner.eq(qMember))
			.leftJoin(qLikeDiscount)
				.on(qLikeDiscount.discount.eq(qDiscount))
			.leftJoin(qDisPhoto)
				.on(qDisPhoto.discount.eq(qDiscount))
			.groupBy(qDiscount);

		long count = query.fetch().size();

		query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(getSortByCondition(sortRequest.getDiscountCondition(), likeCnt));

		List<DiscountListResponseItem> result = query.fetch();

		return DiscountListResponse.builder()
			.items(result)
			.nowTime(LocalDateTime.now())
			.totalPage(count)
			.currentPage(pageNum)
			.build();
	}

	private Predicate isMyDiscount(Member member) {
		return qDiscount.address.city.eq(member.getAddress().getCity()).and(qDiscount.address.zipcode.eq((member.getAddress().getZipcode())));
	}

	@Override
	public List<DiscountListResponseItem> searchDiscountToMainPage(Long memberPk,Member member) {
		JPAQuery<DiscountListResponseItem> query = queryFactory
				.select(
						Projections.bean(DiscountListResponseItem.class,
								qDiscount.id.as("discountPk"),
								qDiscount.discountTitle.as("discountTitle"),
								qDiscount.discountDiscountedPrice.as("discountedPrice"),
								qDiscount.discountPrice.as("originalPrice"),
								qDiscount.discountRate.as("discountRate"),
								qDiscount.discountType.as("discountType"),
								qDisPhoto.imgUrl.min().as("discountImg"),
								qDiscount.discountEnd.as("discountEnd"),
								qDiscount.discountUUID.as("discountUUID"),
								qMember.memberNickname.as("discountOwnerNickname"),
								qLikeDiscount.countDistinct().as("likeCnt"),
								new CaseBuilder()
										.when(
												JPAExpressions.selectOne()
														.from(qLikeDiscount)
														.where(qLikeDiscount.discount.eq(qDiscount))
														.where(qLikeDiscount.member.id.eq(memberPk)) // Replace myUser with your user reference
														.exists()
										)
										.then(true)
										.otherwise(false).as("isLike")
						)
				).from(qDiscount)
				.where(
					qDiscountHistory.historyStatus.isNull(),
					qDiscount.discountEnd.after(LocalDateTime.now())
					,isMyDiscount(member)
				)
				.leftJoin(qMember)
				.on(qDiscount.owner.eq(qMember))
				.leftJoin(qLikeDiscount)
				.on(qLikeDiscount.discount.eq(qDiscount))
				.leftJoin(qDisPhoto)
				.on(qDisPhoto.discount.eq(qDiscount))
				.leftJoin(qDiscountHistory)
				.on(qDiscountHistory.discount.eq(qDiscount))
				.groupBy(qDiscount)
				.orderBy(new OrderSpecifier<>(Order.DESC, qDiscount.createdAt))
				.limit(4);

		return query.fetch();
	}

	private BooleanExpression catalogEq(String category) {
		if (category == null) {
			return null;
		}
		return qDiscount.discountType.eq(category);
	}

	private BooleanExpression keywordEq(int type, String keyword) {
		if (keyword == null) {
			return null;
		}
		if(type == 0){
			return qDiscount.discountTitle.contains(keyword);
		}else{
			return qDiscount.owner.memberNickname.contains(keyword);
		}

	}

	private OrderSpecifier<?> getSortByCondition(int condition, NumberPath<Long> likeCnt) {
		if (condition == 2) {
			// 할인율 높은 순
			return new OrderSpecifier<>(Order.DESC, qDiscount.discountRate);
		} else if (condition == 3) {
			// 할인가 낮은 가격 순
			return new OrderSpecifier<>(Order.ASC, qDiscount.discountDiscountedPrice);
		} else if (condition == 4) {
			// 좋아요 순
			return new OrderSpecifier<>(Order.DESC, likeCnt);
		} else {
			// 기본 정렬 방식
			return new OrderSpecifier<>(Order.DESC, qDiscount.createdAt);
		}
	}
}
