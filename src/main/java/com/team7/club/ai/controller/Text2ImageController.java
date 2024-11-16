package com.team7.club.ai.controller;




import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team7.club.ai.annotation.AiApi;
import com.team7.club.ai.service.AIService;
import com.team7.club.ai.service.GoogleService;
import com.team7.club.ai.service.StorageService;

@AiApi
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor

public class Text2ImageController {
	private final AIService aiService;

	private final GoogleService googleService;

	@Operation(summary = "이미지 생성", description = "이미지 생성")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이미지 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PostMapping("/generate")
	public ResponseEntity<?> generateImage(@RequestBody String prompt) throws Exception {
		String word= googleService.translateText(prompt);

		return new ResponseEntity<>(aiService.generateImage(word), HttpStatus.OK);
	}

}

