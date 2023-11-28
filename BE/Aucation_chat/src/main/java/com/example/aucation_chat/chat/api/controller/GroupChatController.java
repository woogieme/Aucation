package com.example.aucation_chat.chat.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aucation_chat.chat.api.dto.response.ChatResponse;
import com.example.aucation_chat.chat.api.service.GroupChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/groupchat")
public class GroupChatController {

	private final GroupChatService chatService;

	@GetMapping("/enter/{auctionUUID}/{memberPk}")
	public ResponseEntity<List<ChatResponse>> enter (@PathVariable String auctionUUID, @PathVariable long memberPk) {
		List<ChatResponse> chatList = chatService.enter(auctionUUID, memberPk);
		return ResponseEntity.ok().body(chatList);
	}
}
