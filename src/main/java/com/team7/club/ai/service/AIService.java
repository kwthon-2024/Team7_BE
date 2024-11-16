package com.team7.club.ai.service;



import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team7.club.common.config.http.Response;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {
	private final StorageService storageService;
	private static final String API_URL = "https://api.stability.ai/v1/generation/stable-diffusion-v1-6/text-to-image";
	@Value("${image.api.key}")
	private String API_KEY;
	private final Response responses;

	public ResponseEntity<?> generateImage(String prompt) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(API_KEY);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // Set Accept header to application/json

		// Prepare request body
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("text_prompts", List.of(Map.of("text", prompt)));
		requestBody.put("cfg_scale", 7);
		requestBody.put("height", 512);
		requestBody.put("width", 512);
		requestBody.put("samples", 1);

		// Prepare HTTP entity with headers and body
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		try {
			// Make the API request
			ResponseEntity<String> response = restTemplate.exchange(
				API_URL,
				HttpMethod.POST,
				entity,
				String.class
			);

			// Check if the response is OK
			if (response.getStatusCode() == HttpStatus.OK) {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonResponse = objectMapper.readTree(response.getBody());

				// Extract the base64 encoded image
				String base64Image = jsonResponse.get("artifacts").get(0).get("base64").asText();
				if (base64Image != null && !base64Image.isEmpty()) {
					// Decode the base64 string to binary data
					byte[] imageBytes = Base64.getDecoder().decode(base64Image);

					// Upload the image to S3 and get the URL
					String imageUrl = storageService.uploadImage(imageBytes);

					return responses.success(imageUrl, "이미지 생성에 성공했습니다.", HttpStatus.OK);
				} else {
					throw new RuntimeException("No base64 image data found in the response.");
				}
			} else {
				// Log the error response if status code isn't OK
				throw new RuntimeException("API returned error: " + response.getStatusCode());
			}
		} catch (Exception e) {
			log.error("Error generating image", e);
			throw new RuntimeException("Error generating image: " + e.getMessage(), e);
		}
	}
}

//
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.node.ObjectNode;
//
// import lombok.extern.slf4j.Slf4j;
// import reactor.core.publisher.Mono;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;
//
// @Service
// @Slf4j
// public class AIService {
//
// 	private final WebClient webClient;
//
// 	public AIService(@Value("${openai.api.key}") String apiKey) {
// 		this.webClient = WebClient.builder()
// 			.baseUrl("https://api.openai.com/v1")
// 			.defaultHeader("Authorization", "Bearer " + apiKey)
// 			.build();
// 	}
//
// 	public Mono<String> generateImage(String prompt) {
// 		ObjectMapper mapper = new ObjectMapper();
// 		ObjectNode requestBody = mapper.createObjectNode();
// 		requestBody.put("prompt", prompt);
// 		requestBody.put("n", 1);  // 요청할 이미지 수
// 		requestBody.put("size", "512x512");  // 이미지 사이즈 지정
//
// 		log.info("Sending request to OpenAI API with prompt: {}", prompt); // 요청 전 로그 추가
//
// 		return webClient.post()
// 			.uri("https://api.openai.com/v1/images/generations")  // 올바른 엔드포인트
// 			.bodyValue(requestBody)
// 			.retrieve()
// 			.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
// 				clientResponse -> clientResponse.bodyToMono(String.class)
// 					.flatMap(errorBody -> {
// 						log.error("Error response from API: {}", errorBody);
// 						return Mono.error(new RuntimeException("Error generating image: " + errorBody));
// 					})
// 			)
// 			.bodyToMono(JsonNode.class)  // JSON 응답을 받는다
// 			.doOnTerminate(() -> log.info("Request finished"))  // 요청 종료 로그 추가
// 			.map(response -> {
// 				log.info("Full response from OpenAI: {}", response);  // 전체 응답 로그로 확인
//
// 				// 'data' 필드에서 이미지 URL을 추출
// 				if (response.has("data") && response.get("data").size() > 0) {
// 					String imageUrl = response.get("data").get(0).get("url").asText();
// 					log.info("Generated Image URL: {}", imageUrl);
// 					return imageUrl;  // 생성된 이미지 URL을 반환
// 				} else {
// 					// 이미지 URL이 없다면 오류 로그 출력
// 					log.error("No image URL found in response: {}", response);
// 					return null;
// 				}
// 			})
// 			.doOnError(e -> log.error("Error generating image: ", e))  // 오류 발생 시 로그
// 			.doOnTerminate(() -> log.info("Request finished with status"));
// 	}
// }
