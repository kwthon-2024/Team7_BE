package com.team7.club.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team7.club.ai.service.AIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AIController {
	private final AIService aiService;

	@PostMapping("/generate-image)")
	public ResponseEntity<String> generateImage(@RequestBody String prompt) throws Exception {
		String imageUrl = String.valueOf(aiService.generateImage(prompt));
		return ResponseEntity.ok(imageUrl);
	}
}