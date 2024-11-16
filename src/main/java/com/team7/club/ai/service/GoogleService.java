package com.team7.club.ai.service;


import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;
import com.google.common.collect.Lists;

@Service
public class GoogleService {

	public String translateText(String inputText) {
		try {
			// API 키 파일 로드
			InputStream keyFile = ResourceUtils.getURL("classpath:translation/cedar-booth-441904-s9-ace0d699d4dd.json").openStream();
			GoogleCredentials credentials = GoogleCredentials.fromStream(keyFile)
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

			TranslationServiceSettings settings = TranslationServiceSettings.newBuilder()
				.setCredentialsProvider(() -> credentials)
				.build();

			try (TranslationServiceClient client = TranslationServiceClient.create(settings)) {
				// 프로젝트 ID 및 위치 설정
				String projectId = "cedar-booth-441904-s9"; // Google Cloud 프로젝트 ID
				String location = "global"; // 번역 API 서비스 위치

				// 번역 요청 설정
				TranslateTextRequest request = TranslateTextRequest.newBuilder()
					.setParent(String.format("projects/%s/locations/%s", projectId, location))
					.setTargetLanguageCode("en") // 한국어 (ko)
					.addContents(inputText) // 입력 텍스트
					.build();

				// 번역 요청 수행
				TranslateTextResponse response = client.translateText(request);

				// 번역 결과 반환
				return response.getTranslationsList().get(0).getTranslatedText();
			}
		} catch (Exception e) {
			// 오류 처리
			e.printStackTrace();
			return "번역 중 오류가 발생했습니다: " + e.getMessage();
		}
	}
}