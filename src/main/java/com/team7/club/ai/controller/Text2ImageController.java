package com.team7.club.ai.controller;




import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team7.club.ai.service.AIService;
import com.team7.club.ai.service.GoogleService;
import com.team7.club.ai.service.StorageService;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class Text2ImageController {
	private final AIService aiService;
	private final StorageService s3Service;

	private final GoogleService googleService;
	@GetMapping("")
	public String test(@RequestBody String word){
		String testWord = googleService.translateText(word);
		System.out.println(testWord);
		return testWord;
	}
	@PostMapping("/generate")
	public ResponseEntity<?> generateImage(@RequestBody String prompt) throws Exception {
		String word= googleService.translateText(prompt);

		return new ResponseEntity<>(aiService.generateImage(word), HttpStatus.OK);
	}

	@GetMapping("/get-history")
	public ResponseEntity<?> getHistory() {

		return new ResponseEntity<>(s3Service.listObjects(), HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteImage(@RequestParam("name") String name) {

		s3Service.deleteObject(name);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
}

