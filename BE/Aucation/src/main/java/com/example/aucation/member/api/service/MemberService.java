package com.example.aucation.member.api.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.aucation.auction.api.dto.AuctionIngResponseItem;
import com.example.aucation.auction.api.dto.ReAuctionResponseItem;
import com.example.aucation.auction.api.service.AuctionService;
import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.auction.db.repository.AuctionHistoryRepository;
import com.example.aucation.auction.db.repository.AuctionRepository;
import com.example.aucation.common.dto.EmailResponse;
import com.example.aucation.common.dto.StreetResponse;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.BadRequestException;
import com.example.aucation.common.error.DuplicateException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.common.service.CommonService;
import com.example.aucation.common.service.RegisterMail;
import com.example.aucation.discount.api.dto.DiscountListResponseItem;
import com.example.aucation.discount.api.service.DiscountService;
import com.example.aucation.discount.db.entity.Discount;
import com.example.aucation.discount.db.repository.DiscountHistoryRepository;
import com.example.aucation.discount.db.repository.DiscountRepository;
import com.example.aucation.disphoto.db.repository.DisPhotoRepository;
import com.example.aucation.like.db.repository.LikeAuctionRepository;
import com.example.aucation.like.db.repository.LikeDiscountRepository;
import com.example.aucation.member.api.dto.DeleteRequest;
import com.example.aucation.member.api.dto.DeleteResponse;
import com.example.aucation.member.api.dto.DetailRequest;
import com.example.aucation.member.api.dto.DetailResponse;
import com.example.aucation.member.api.dto.ImageResponse;
import com.example.aucation.member.api.dto.LikePageRequest;
import com.example.aucation.member.api.dto.MainPageResponse;
import com.example.aucation.member.api.dto.MemberPageRequest;
import com.example.aucation.member.api.dto.MyDiscountResponse;
import com.example.aucation.member.api.dto.MyLikeResponse;
import com.example.aucation.member.api.dto.MyReverseResponse;
import com.example.aucation.member.api.dto.MypageResponse;
import com.example.aucation.member.api.dto.NicknameRequest;
import com.example.aucation.member.api.dto.NicknameResponse;
import com.example.aucation.member.api.dto.SignupRequest;
import com.example.aucation.member.db.entity.Address;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.entity.Role;
import com.example.aucation.member.db.entity.SocialType;
import com.example.aucation.member.db.repository.MemberRepository;
import com.example.aucation.photo.db.repository.PhotoRepository;
import com.example.aucation.reauction.api.service.ReAuctionService;
import com.example.aucation.reauction.db.entity.ReAuctionBid;
import com.example.aucation.reauction.db.repository.ReAucBidPhotoRepository;
import com.example.aucation.reauction.db.repository.ReAuctionBidRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

	private final AuctionHistoryRepository auctionHistoryRepository;

	private final ReAucBidPhotoRepository reAucBidPhotoRepository;

	private final DiscountHistoryRepository discountHistoryRepository;

	private final DisPhotoRepository disPhotoRepository;

	private final LikeAuctionRepository likeAuctionRepository;

	private final LikeDiscountRepository likeDiscountRepository;

	private final ReAuctionBidRepository reAuctionBidRepository;

	private final DiscountRepository discountRepository;

	private final PhotoRepository photoRepository;

	private final AuctionRepository auctionRepository;

	private final PasswordEncoder passwordEncoder;

	private final MemberRepository memberRepository;

	private final AuctionService auctionService;

	private final DiscountService discountService;

	private final ReAuctionService reAuctionService;

	private final RegisterMail registerMail;

	private final CommonService commonService;
	private final int COUNT_IN_PAGE = 5;

	private final int COUNT_IN_PAGE_LIKE = 8;

	private final String dirName = "profile";

	private final AmazonS3Client amazonS3Client;

	private static final String AUCTION = "경매";
	private static final String RE_AUCTION = "역경매";
	private static final String CONFIRM = "입찰";

	private static final String SUCCESS_REMOVE_ITEMS = "성공적으로 상품을 삭제하였습니다";

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	private static final String DEFAULT_IMAGE_URL = "https://aucation-bucket.s3.ap-northeast-2.amazonaws.com/bear.jpg";

	@Transactional
	public void signup(SignupRequest signupRequest) {
		validateMemberId(signupRequest.getMemberId());
		validateMemberEmail(signupRequest.getMemberEmail());
		verifynick(signupRequest.getMemberNickname());

		Address address = setMemberAddress(signupRequest.getMemberLng(), signupRequest.getMemberLat());

		Member member = Member.builder()
			.memberId(signupRequest.getMemberId())
			.memberPw(passwordEncoder.encode(signupRequest.getMemberPw()))
			.memberEmail(signupRequest.getMemberEmail())
			.memberNickname(signupRequest.getMemberNickname())
			.memberRole(Role.COMMON)
			.address(address)
			.imageURL(DEFAULT_IMAGE_URL)
			.socialType(SocialType.NORMAL)
			.build();
		memberRepository.save(member);
	}

	@Transactional
	public NicknameResponse changenick(Long memberPk, NicknameRequest nicknameRequest) {
		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		verifynick(nicknameRequest.getMemberNickname());
		member.updateMemberNickname(nicknameRequest.getMemberNickname());
		return NicknameResponse.of();
	}

	@Transactional
	public DetailResponse changedetail(Long memberPk, DetailRequest detailRequest) {
		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		member.updateMemberDetail(detailRequest.getMemberDetail());
		return DetailResponse.of();
	}

	@Transactional
	public ImageResponse changeimage(Long memberPk, MultipartFile multipartFile) throws IOException {
		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		upload(multipartFile, member);
		return ImageResponse.of();
	}

	//마이페이지 - 경매
	@Transactional
	public MypageResponse myauction(Long memberPk, MemberPageRequest memberPageRequest) {
		Member member = existsMemberPk(memberPk);
		Pageable pageable = PageRequest.of(memberPageRequest.getMyPageNum() - 1, COUNT_IN_PAGE);
		return memberRepository.searchMyAuctionPage(member, memberPageRequest, pageable);
	}

	//마이페이지 - 역경매
	@Transactional
	public MyReverseResponse myreacution(Long memberPk, MemberPageRequest memberPageRequest) {
		Member member = existsMemberPk(memberPk);
		Pageable pageable = PageRequest.of(memberPageRequest.getMyPageNum() - 1, COUNT_IN_PAGE);
		return memberRepository.searchMyReversePage(member, memberPageRequest, pageable);
	}

	//마이페이지 - 할인
	@Transactional
	public MyDiscountResponse mydiscount(Long memberPk, MemberPageRequest memberPageRequest) {
		Member member = existsMemberPk(memberPk);
		Pageable pageable = PageRequest.of(memberPageRequest.getMyPageNum() - 1, COUNT_IN_PAGE);
		return memberRepository.searchMyDiscountPage(member, memberPageRequest, pageable);
	}

	//마이페이지 - 좋아요
	@Transactional
	public MyLikeResponse likeauction(Long memberPk, LikePageRequest likePageRequest) {
		Member member = existsMemberPk(memberPk);
		Pageable pageable = PageRequest.of(likePageRequest.getMyPageNum() - 1, COUNT_IN_PAGE_LIKE);
		if (likePageRequest.getProductStatus().equals("할인")) {
			return memberRepository.searchMyDisLike(member, likePageRequest, pageable);
		}
		return memberRepository.searchMyAucLIke(member, likePageRequest, pageable);
	}

	public EmailResponse certifyEmail(String memberEmail) throws Exception {
		validateMemberEmail(memberEmail);
		String code = registerMail.sendSimpleMessage(memberEmail, "회원가입");
		return EmailResponse.of(code);
	}

	public void verifynick(String memberNickname) {
		if (memberRepository.existsByMemberNickname(memberNickname)) {
			throw new DuplicateException(ApplicationError.DUPLICATE_NICKNAME);
		}
	}

	public void validateMemberEmail(String memberEmail) {
		if (memberRepository.existsByMemberEmail(memberEmail)) {
			throw new DuplicateException(ApplicationError.DUPLICATE_MEMBER_EMAIL);
		}
	}

	private void validateMemberId(String memberId) {
		if (memberRepository.existsByMemberId(memberId)) {
			throw new DuplicateException(ApplicationError.DUPLICATE_USERNAME);
		}

	}

	private Member existsMemberPk(Long memberPk) {
		return memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
	}

	public MypageResponse mypageLike(Long memberPk) {
		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		return null;
	}

	public void verifyId(String memberId) {
		if (memberRepository.existsByMemberId(memberId)) {
			throw new DuplicateException(ApplicationError.DUPLICATE_USERNAME);
		}

	}

	public void upload(MultipartFile files, Member member) throws IOException {

		File uploadFile = convertToFile(files)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 변환에 실패했습니다."));

		String fileName = dirName + "/" + member.getMemberNickname() + "/" + " " + uploadFile.getName();

		String uploadImageUrl = putS3(uploadFile, fileName);

		removeFile(uploadFile);

		member.updateImgURL(uploadImageUrl);
	}

	public String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	public Optional<File> convertToFile(MultipartFile file) throws IOException, FileNotFoundException {
		File uploadFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		FileOutputStream fos = new FileOutputStream(uploadFile);
		fos.write(file.getBytes());
		fos.close();
		return Optional.of(uploadFile);
	}

	public void removeFile(File targetFile) {
		if (targetFile.exists()) {
			if (targetFile.delete()) {
				log.info("파일이 삭제되었습니다.");
			} else {
				log.info("파일이 삭제되지 않았습니다.");
			}
		}
	}

	public MainPageResponse getMainPageInfo(Long memberPk) {
		List<AuctionIngResponseItem> hotAuctions = auctionService.getHotAuctionsToMainPage(memberPk);
		List<DiscountListResponseItem> discounts = discountService.getDiscountToMainPage(memberPk);
		List<ReAuctionResponseItem> recentAuctions = reAuctionService.getRecentReAucToMainPage(memberPk);
		return MainPageResponse.builder()
			.nowTime(LocalDateTime.now())
			.hotAuctions(hotAuctions)
			.discounts(discounts)
			.recentAuctions(recentAuctions)
			.build();
	}

	private Address setMemberAddress(double memberLng, double memberLat) {
		StreetResponse streetResponse = commonService.findstreet(memberLng, memberLat);
		return Address.builder()
			.city(streetResponse.getCity())
			.zipcode(streetResponse.getZipcode())
			.street(streetResponse.getStreet())
			.build();
	}

	@Transactional
	public DeleteResponse deleteprod(Long memberPk, DeleteRequest deleteRequest) {

		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		//경매일때
		if (deleteRequest.getStatus().equals(AUCTION)) {
			Auction auction = auctionRepository.findByIdAndThinkDate(deleteRequest.getProdPk())
				.orElseThrow(() -> new BadRequestException(ApplicationError.STARTED_AUCTION));
			likeAuctionRepository.deleteByAuctionId(auction.getId());
			photoRepository.deleteByAuctionId(auction.getId());
			auctionRepository.deleteById(auction.getId());
			return DeleteResponse.builder().message(SUCCESS_REMOVE_ITEMS).build();

		} else if (deleteRequest.getStatus().equals(RE_AUCTION)) {
			Auction auction = auctionRepository.findById(deleteRequest.getProdPk())
				.orElseThrow(() -> new BadRequestException(ApplicationError.NOT_EXIST_AUCTION));

			if (auctionHistoryRepository.existsAuctionHistoryByAuction(auction)) {
				throw new BadRequestException(ApplicationError.NOT_SELL_REAUCTION);
			}
			likeAuctionRepository.deleteByAuctionId(auction.getId());
			//역경매 내가 올린거 삭제
			photoRepository.deleteByAuctionId(auction.getId());
			//만약 auctionHistory가 존재한다면 남이올린다고 올린사진 삭제
			reAucBidPhotoRepository.deleteByAuctionId(auction.getId());
			reAuctionBidRepository.deleteByAuctionAndAuctionId(auction.getId());

			auctionRepository.deleteById(auction.getId());
			return DeleteResponse.builder().message(SUCCESS_REMOVE_ITEMS).build();
		}else if (deleteRequest.getStatus().equals(CONFIRM)){
			Auction auction = auctionRepository.findById(deleteRequest.getProdPk())
				.orElseThrow(() -> new BadRequestException(ApplicationError.NOT_EXIST_AUCTION));

			ReAuctionBid reAuctionBid = reAuctionBidRepository.findReAuctionBidByAuctionId(auction.getId())
				.orElseThrow(() -> new BadRequestException(ApplicationError.NOT_EXIST_AUCTION));

			reAucBidPhotoRepository.deleteByReAictionIdAndMemberId(reAuctionBid.getId());
			reAuctionBidRepository.deleteByReAuctionIdAndMemberId(auction.getId(),member.getId());

			return DeleteResponse.builder().message(SUCCESS_REMOVE_ITEMS).build();
		}

		Discount discount = discountRepository.findById(deleteRequest.getProdPk())
			.orElseThrow(() -> new NotFoundException(ApplicationError.DISCOUNT_NOT_FOUND));

		if (discountHistoryRepository.existsDiscountHistoryByDiscount(discount)) {
			throw new BadRequestException(ApplicationError.NOT_SELL_DISCOUNT);
		}
		likeDiscountRepository.delete(discount.getId());
		discountHistoryRepository.delete(discount.getId());
		disPhotoRepository.delete(discount.getId());
		discountRepository.delete(discount.getId());
		return DeleteResponse.builder().message(SUCCESS_REMOVE_ITEMS).build();

	}
}
