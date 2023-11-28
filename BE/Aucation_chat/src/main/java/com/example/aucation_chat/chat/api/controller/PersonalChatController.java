package com.example.aucation_chat.chat.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aucation_chat.chat.api.dto.request.PersonalChatEnterRequest;
import com.example.aucation_chat.chat.api.dto.response.PersonalChatEnterResponse;
import com.example.aucation_chat.chat.api.service.PersonalChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/chat")
public class PersonalChatController {

	private final PersonalChatService personalChatService;

	// @GetMapping("/start/")

	@PostMapping("/enter")
	public ResponseEntity<PersonalChatEnterResponse> enter (@RequestBody PersonalChatEnterRequest personalChatEnterRequest) {
		PersonalChatEnterResponse personalChatEnterResponse = personalChatService.enter(personalChatEnterRequest);
		return ResponseEntity.ok().body(personalChatEnterResponse);
	}
}
