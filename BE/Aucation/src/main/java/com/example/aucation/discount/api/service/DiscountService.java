package com.example.aucation.discount.api.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.aucation.common.dto.StreetResponse;
import com.example.aucation.common.entity.HistoryStatus;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.BadRequestException;
import com.example.aucation.common.error.ExistsException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.common.service.CommonService;
import com.example.aucation.common.service.FCMService;
import com.example.aucation.common.util.PasswordGenerator;
import com.example.aucation.discount.api.dto.ConfirmResponse;
import com.example.aucation.discount.api.dto.DiscountListResponse;
import com.example.aucation.discount.api.dto.DiscountListResponseItem;
import com.example.aucation.discount.api.dto.DiscountRequest;
import com.example.aucation.discount.api.dto.DiscountResponse;
import com.example.aucation.discount.api.dto.DiscountSortRequest;
import com.example.aucation.discount.api.dto.EnterResponse;
import com.example.aucation.discount.api.dto.PurchaseResponse;
import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.discount.db.entity.DiscountHistory;
import com.example.aucation.discount.db.repository.DiscountHistoryRepository;
import com.example.aucation.discount.db.repository.DiscountRepository;
import com.example.aucation.disphoto.api.service.DisPhotoService;
import com.example.aucation.disphoto.db.entity.DisPhoto;
import com.example.aucation.like.db.entity.LikeDiscount;
import com.example.aucation.like.db.repository.LikeDiscountRepository;
import com.example.aucation.member.db.entity.Address;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.entity.Role;
import com.example.aucation.member.db.repository.MemberRepository;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {

	private final MemberRepository memberRepository;

	private final DiscountRepository discountRepository;

	private final DisPhotoService disPhotoService;

	private final DiscountHistoryRepository discountHistoryRepository;

	private final LikeDiscountRepository likeDiscountRepository;

	private final FCMService fcmService;

	private final CommonService commonService;

	private static final String SUCCESS_REGISTER_MESSAGE = "할인 상품이 등록되었습니다.";

	private static final String SUCCESS_PURCHASE_MESSAGE = "할인 상품을 구매 예약했습니다 상품을 받으러 가세요(구매 예약)";

	private static final String PERFECT_PURCHASE_MESSAGE = "할인 상품을 구매 확정지었습니다 (구매 확정)";

	private final int COUNT_IN_PAGE = 15;

	@Transactional
	/** 할인상품 등록 */
	public DiscountResponse register(Long memberPk, DiscountRequest discountRequest,
		List<MultipartFile> multipartFiles) throws
		IOException {

		if(discountRequest.getDiscountDiscountedPrice()<1000){
			throw new BadRequestException(ApplicationError.LESS_MORE_THAN_START_PRICE);
		}
		// memberPk 검증
		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		isSmallBusiness(member);

		// 검증 후 UUID 생성
		String discountUUID = PasswordGenerator.generate();

		// 할인율구하기 : (정가-할인가)*100/정가
		//할인가
		int discountedPrice = discountRequest.getDiscountDiscountedPrice();
		//정가
		int originalPrice = discountRequest.getDiscountPrice();

		isdiscountedPirce(originalPrice, discountedPrice);
		int discountRate = (int)Math.ceil(((double)((originalPrice - discountedPrice) * 100) / originalPrice));

		// 할인 마감시간
		String dateString = discountRequest.getDiscountEnd();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS");
		LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);

		log.info("discountRequest.getDiscountLng(): " + discountRequest.getDiscountLng());
		log.info("discountRequest.getDiscountLat(): " + discountRequest.getDiscountLat());
		Address address = setMemberAddress(discountRequest.getDiscountLng(), discountRequest.getDiscountLat());
		// 할인상품 등록
		Discount discount = Discount.builder()
			.discountType(discountRequest.getDiscountType())
			.discountDetail(discountRequest.getDiscountDetail())
			.discountLng(discountRequest.getDiscountLng())
			.discountLat(discountRequest.getDiscountLat())
			.discountDiscountedPrice(discountRequest.getDiscountDiscountedPrice())
			.discountStart(LocalDateTime.now())
			.discountEnd(dateTime)
			.address(address)
			.discountTitle(discountRequest.getDiscountTitle())
			.discountPrice(discountRequest.getDiscountPrice())
			.discountUUID(discountUUID)
			.discountRate(discountRate)
			.owner(member)
			.build();
		discountRepository.save(discount);
		disPhotoService.uploadDiscount(multipartFiles, discountUUID);

		// 메세지 반환
		return DiscountResponse.builder()
			.message(SUCCESS_REGISTER_MESSAGE)
			.discountUUID(discountUUID)
			.prodPk(discount.getId())
			.build();
	}

	private Address setMemberAddress(double discountLng, double discountLat) {
		StreetResponse streetResponse = commonService.findstreet(discountLng, discountLat);
		return Address.builder()
			.city(streetResponse.getCity())
			.zipcode(streetResponse.getZipcode())
			.street(streetResponse.getStreet())
			.build();
	}

	private void isdiscountedPirce(int originalPrice, int discountedPrice) {
		if (originalPrice < discountedPrice) {
			throw new BadRequestException(ApplicationError.NOT_CHEAPER_PRODUCT_PRICE);
		}
	}

	@Transactional
	/** 할인 매장 상세페이지 */
	public EnterResponse place(Long memberPk, Long prodPk) {

		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		Discount discount = discountRepository.findById(prodPk).orElseThrow(() -> new NotFoundException(
			ApplicationError.DISCOUNT_NOT_FOUND));

		String disStatus ="아무도 사지 않음";
		////Discount에서 해당 discout.customer가 존재하는지? (구매했는가?)
		if(discount.getCustomer()!=null){
			DiscountHistory discountHistory = discountHistoryRepository.findByDiscountIdAndMemberId(discount.getCustomer().getId(),discount.getId())
				.orElseThrow(()->new NotFoundException(ApplicationError.DISCOUNT_HISTORY_NOT_FOUND));

			if(discountHistory.getHistoryStatus().equals(HistoryStatus.BEFORE_CONFIRM)){
				disStatus="거래 대기중";
			}else if(discountHistory.getHistoryStatus().equals(HistoryStatus.AFTER_CONFIRM)){
				disStatus="거래 완료";
			}

		}

		//존재한다면 그게 memberPk랑 똑같은지?
		//존재한다면 ? 구매했다 => 현재 DiscountHistory 상태를 보내준다
		//존재하지않았다 ? 아직 아무도 존재하지않았다 그냥 값을 던지자


		List<DisPhoto> disPhotos = disPhotoService.getPhoto(discount.getId());

		List<String> UUIDImage = disPhotos.stream()
			.map(DisPhoto::getImgUrl)
			.collect(Collectors.toList());

		boolean isFalse = likeDiscountRepository.existsByDiscountAndMember(discount, member);
		int likeCnt = likeDiscountRepository.countByDiscountAndMember(discount, member);

		boolean discountStatus = discount.getCustomer() != null;
		return EnterResponse.of(UUIDImage, discount, member, isFalse, likeCnt,discountStatus,disStatus);
	}

	public void isSmallBusiness(Member member) {
		if (member.getMemberRole().equals(Role.COMMON)) {
			throw new BadRequestException(ApplicationError.NOT_VERIFY_SMALL_BUSINESS);
		}
	}

	private void isOwnerPurchase(Member member, Discount discount) {
		if (Objects.equals(member.getId(), discount.getOwner().getId())) {
			throw new BadRequestException(ApplicationError.YOU_ARE_OWNER);
		}
	}

	private void isHaveCustomerMoney(Member member, Discount discount) {

		if (member.getMemberPoint() < discount.getDiscountDiscountedPrice()) {
			throw new BadRequestException(ApplicationError.MEMBER_NOT_HAVE_MONEY);
		}
	}

	private void isAlreadPurchase(Discount discount) {

		if (discountHistoryRepository.existsByDiscountId(discount.getId())) {
			throw new ExistsException(ApplicationError.ALREADY_EXISTS_PURCHASE_USER);
		}
	}

	@Transactional
	public void setLikeDiscount(Long memberPk, Long discountPk) throws Exception {
		log.info("********************** setLikeDiscount() 시작");

		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		Discount discount = discountRepository.findById(discountPk)
			.orElseThrow(() -> new Exception("AUCTION NO EXIST"));

		likeDiscountRepository.findByDiscountAndMember(discount, member)
			.ifPresentOrElse(
				likeDiscount -> {
					log.info("********************** 좋아요 존재 O");
					likeDiscountRepository.delete(likeDiscount);
					log.info("********************** memberPk : {} -> auctionPk : {} 좋아요 설정 취소"
						, memberPk, discountPk);
				},
				() -> {
					log.info("********************** 좋아요 존재 X");
					likeDiscountRepository.save(LikeDiscount
						.builder()
						.member(member)
						.discount(discount)
						.build());
					log.info("********************** memberPk : {} -> disountPk : {} 좋아요 설정"
						, memberPk, discountPk);
				}
			);
		log.info("********************** setLikeDiscount() 완료");
	}

	//////////////////////////////////////////////////////////////////////
	//////////////////////////////// 리스트 ////////////////////////////////
	//////////////////////////////////////////////////////////////////////

	public DiscountListResponse getDiscountList(Long memberPk, int pageNum, DiscountSortRequest sortRequest) {
		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		Pageable pageable = PageRequest.of(pageNum - 1, COUNT_IN_PAGE);
		DiscountListResponse discounts = discountRepository.searchListByCondition(member, pageNum, sortRequest,
			pageable);
		return discounts;
	}

	public List<DiscountListResponseItem> getDiscountToMainPage(Long memberPk) {

		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		List<DiscountListResponseItem> response = discountRepository.searchDiscountToMainPage(memberPk, member);
		return response;
	}

	@Transactional
	public PurchaseResponse purchase(Long memberPk, String discountUUID) throws
		FirebaseMessagingException,
		ExecutionException,
		InterruptedException {

		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		Discount discount = discountRepository.findByDiscountUUID(discountUUID).orElseThrow(() -> new NotFoundException(
			ApplicationError.DISCOUNT_NOT_FOUND));

		isOwnerPurchase(member, discount);
		isAlreadPurchase(discount);
		isHaveCustomerMoney(member, discount);
		member.minusUpdatePoint(member.getMemberPoint(), discount.getDiscountDiscountedPrice());

		discount.updateCustomer(member);
		DiscountHistory discountHistory = DiscountHistory.builder()
			.historyDatetime(LocalDateTime.now())
			.customer(member)
			.owner(discount.getOwner())
			.discount(discount)
			.historyStatus(HistoryStatus.BEFORE_CONFIRM)
			.build();
		discountHistoryRepository.save(discountHistory);

		fcmService.setDisAucAlarm(discount.getDiscountUUID(), discount.getOwner().getId());

		return PurchaseResponse.builder().message(SUCCESS_PURCHASE_MESSAGE).build();
	}

	@Transactional
	public ConfirmResponse confirm(String discountUUID) {
		//구매확정은 구매자가 누르는것

		Discount discount = discountRepository.findByDiscountUUID(discountUUID)
			.orElseThrow(() -> new NotFoundException(ApplicationError.DISCOUNT_NOT_FOUND));

		Member owner = memberRepository.findById(discount.getOwner().getId())
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		DiscountHistory discountHistory = discountHistoryRepository.findByDiscountId(discount.getId())
			.orElseThrow(() -> new NotFoundException(ApplicationError.DISCOUNT_HISTORY_NOT_FOUND));

		discountHistory.updateStatusSell();
		discountHistory.updateStatusTime();
		owner.updateOwnerPoint(discount.getDiscountDiscountedPrice());

		return ConfirmResponse.builder().message(PERFECT_PURCHASE_MESSAGE).build();

	}

}
