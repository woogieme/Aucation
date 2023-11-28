package com.example.aucation.member.api.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.aucation.common.dto.EmailResponse;
import com.example.aucation.common.service.FCMService;
import com.example.aucation.common.support.AuthorizedVariable;
import com.example.aucation.member.api.dto.DeleteRequest;
import com.example.aucation.member.api.dto.DeleteResponse;
import com.example.aucation.member.api.dto.DetailRequest;
import com.example.aucation.member.api.dto.DetailResponse;
import com.example.aucation.member.api.dto.FCMTokenReq;
import com.example.aucation.member.api.dto.FCMTokenRes;
import com.example.aucation.member.api.dto.ImageResponse;
import com.example.aucation.member.api.dto.LikePageRequest;
import com.example.aucation.member.api.dto.MemberPageRequest;
import com.example.aucation.member.api.dto.MemberPkResponse;
import com.example.aucation.member.api.dto.MyDiscountResponse;
import com.example.aucation.member.api.dto.MyLikeResponse;
import com.example.aucation.member.api.dto.MyReverseResponse;
import com.example.aucation.member.api.dto.MypageResponse;
import com.example.aucation.member.api.dto.NicknameRequest;
import com.example.aucation.member.api.dto.NicknameResponse;
import com.example.aucation.member.api.dto.SignupRequest;
import com.example.aucation.member.api.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
@Slf4j
public class MemberController {

	private final MemberService memberService;

	private final FCMService fcmService;

	@PostMapping("/saveFCM")
	public ResponseEntity<FCMTokenRes> saveToken(@AuthorizedVariable long memberPk,@RequestBody FCMTokenReq fcmTokenReq) throws Exception {
		return ResponseEntity.ok().body(fcmService.saveToken(memberPk,fcmTokenReq));
	}

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
		memberService.signup(signupRequest);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/certification/email/{email}")
	public ResponseEntity<EmailResponse> cerifyemail(@PathVariable("email") String memberEmail) throws Exception {
		return ResponseEntity.ok().body(memberService.certifyEmail(memberEmail));
	}

	@GetMapping("/verification/id/{memberId}")
	public ResponseEntity<Void> verifyId(@PathVariable("memberId") String memberId) throws Exception {
		memberService.verifyId(memberId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/verification/nickname/{nickname}")
	public ResponseEntity<Void> verifynick(@PathVariable("nickname") String memberNickname) {
		memberService.verifynick(memberNickname);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/verification/email/{email}")
	public ResponseEntity<Void> verifyemail(@PathVariable("email") String memberEmail) throws Exception {
		memberService.validateMemberEmail(memberEmail);
		return ResponseEntity.ok().build();
	}

	//마이페이지-경매
	@PostMapping("/mypage/auction")
	public ResponseEntity<MypageResponse> myauction(@AuthorizedVariable Long memberPk, @RequestBody MemberPageRequest memberPageRequest) {
		return ResponseEntity.ok().body(memberService.myauction(memberPk,memberPageRequest));
	}

	//마이페이지-역경매
	@PostMapping("/mypage/reauction")
	public ResponseEntity<MyReverseResponse> myreacution(@AuthorizedVariable Long memberPk, @RequestBody MemberPageRequest memberPageRequest) {
		return ResponseEntity.ok().body(memberService.myreacution(memberPk,memberPageRequest));
	}

	//마이페이지-할인
	@PostMapping("/mypage/discount")
	public ResponseEntity<MyDiscountResponse> mydiscount(@AuthorizedVariable Long memberPk, @RequestBody MemberPageRequest memberPageRequest) {
		return ResponseEntity.ok().body(memberService.mydiscount(memberPk,memberPageRequest));
	}

	//마이페이지-좋아요
	@PostMapping("/mypage/likeauction")
	public ResponseEntity<MyLikeResponse> likeauction(@AuthorizedVariable Long memberPk,@RequestBody LikePageRequest likePageRequest) {
		return ResponseEntity.ok().body(memberService.likeauction(memberPk,likePageRequest));
	}

	//마이페이지 좋아요 게시글
	@PostMapping("/mypage/like")
	public ResponseEntity<MypageResponse> mypageLike(@AuthorizedVariable Long memberPk) {
		return ResponseEntity.ok().body(memberService.mypageLike(memberPk));
	}

	//마이페이지 할인 게시글
	@PatchMapping("/modify/nickname")
	public ResponseEntity<NicknameResponse> changenick(@AuthorizedVariable Long memberPk,@RequestBody NicknameRequest nicknameRequest) {
		return ResponseEntity.ok().body(memberService.changenick(memberPk,nicknameRequest));
	}

	@PatchMapping("/modify/detail")
	public ResponseEntity<DetailResponse> changedetail(@AuthorizedVariable Long memberPk,@RequestBody DetailRequest detailRequest) {
		return ResponseEntity.ok().body(memberService.changedetail(memberPk,detailRequest));
	}

	@PatchMapping("/modify/image")
	public ResponseEntity<ImageResponse> changeimage(@AuthorizedVariable Long memberPk, MultipartFile multipartFile) throws
		IOException {
		return ResponseEntity.ok().body(memberService.changeimage(memberPk,multipartFile));
	}

	@GetMapping("/get")
	public ResponseEntity<MemberPkResponse> test(@AuthorizedVariable Long memberPk) {
		return ResponseEntity.ok().body(MemberPkResponse.builder().memberPk(memberPk).build());
	}

	@GetMapping("/mainPage")
	public ResponseEntity<?> getMainPage(@AuthorizedVariable Long memberPk){
		return ResponseEntity.ok().body(memberService.getMainPageInfo(memberPk));
	}

	@DeleteMapping("/delete")
	public ResponseEntity<DeleteResponse> deleteprod(@AuthorizedVariable Long memberPk,@RequestBody DeleteRequest deleteRequest){
		return ResponseEntity.ok().body(memberService.deleteprod(memberPk,deleteRequest));
	}

}
