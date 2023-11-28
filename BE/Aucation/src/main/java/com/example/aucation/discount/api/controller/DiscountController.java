package com.example.aucation.discount.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.aucation.auction.api.dto.PlaceResponse;
import com.example.aucation.auction.api.dto.RegisterRequest;
import com.example.aucation.common.support.AuthorizedVariable;
import com.example.aucation.discount.api.dto.AccessResponse;
import com.example.aucation.discount.api.dto.ConfirmResponse;
import com.example.aucation.discount.api.dto.DiscountRequest;
import com.example.aucation.discount.api.dto.DiscountResponse;
import com.example.aucation.discount.api.dto.DiscountSortRequest;
import com.example.aucation.discount.api.dto.EnterResponse;
import com.example.aucation.discount.api.dto.PurchaseResponse;
import com.example.aucation.discount.api.service.DiscountService;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discount")
public class DiscountController {

	private final DiscountService discountService;

	@GetMapping("/place/{prodPk}")
	private ResponseEntity<EnterResponse> place(@AuthorizedVariable Long memberPk,
		@PathVariable("prodPk") Long prodPk) {
		return ResponseEntity.ok().body(discountService.place(memberPk, prodPk));
	}

	@PostMapping("/register")
	private ResponseEntity<DiscountResponse> register(@AuthorizedVariable Long memberPk,
		DiscountRequest discountRequest, List<MultipartFile> multipartFiles) throws
		IOException {
		return ResponseEntity.ok().body(discountService.register(memberPk,discountRequest,multipartFiles));
	}

	@GetMapping("/purchase/{discountUUID}")
	private ResponseEntity<PurchaseResponse> purchase(@AuthorizedVariable Long memberPk, @PathVariable("discountUUID") String discountUUID) throws
		FirebaseMessagingException, ExecutionException, InterruptedException {
		return ResponseEntity.ok().body(discountService.purchase(memberPk,discountUUID));
	}

	////////////// 리스트 /////////////////////////////////

	@PostMapping("list/{pageNum}")
	private ResponseEntity<?> getDiscountList(@AuthorizedVariable Long memberPk, @PathVariable int pageNum, @RequestBody(required = false) DiscountSortRequest sortRequest){
		return ResponseEntity.ok().body(discountService.getDiscountList(memberPk, pageNum, sortRequest));
	}

	@GetMapping("/like/{discountPk}")
	private ResponseEntity<?> likeAuction(@AuthorizedVariable Long memberPk, @PathVariable Long discountPk) throws Exception {
		discountService.setLikeDiscount(memberPk,discountPk);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/confirm/{discountUUID}")
	private ResponseEntity<ConfirmResponse> confirm(@PathVariable("discountUUID") String discountUUID){
		return ResponseEntity.ok().body(discountService.confirm(discountUUID));
	}
}
