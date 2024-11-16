package com.team7.club.activity.controller;

import com.team7.club.activity.dto.request.ActivityRequestDto;
import com.team7.club.activity.entity.Activity;
import com.team7.club.activity.service.ActivityService;
import com.team7.club.common.config.AuthUser;
import com.team7.club.common.config.http.Response;
import com.team7.club.common.config.lib.Helper;
import com.team7.club.community.dto.request.PostRequestDto;
import com.team7.club.community.entity.Post;
import com.team7.club.photo.util.S3Util;
import com.team7.club.user.entity.Users;
import com.team7.club.user.security.CustomUserPrincipal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/club/activity")
@RestController
public class ActivityController {
    private final ActivityService activityService;
    private final Response response;
    private final S3Util s3Util;

//  활동기록 등록
    @PostMapping
    public ResponseEntity<?> postActivity(
            @Validated @RequestPart(value = "activity") ActivityRequestDto.Post activity,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Errors errors,
            @AuthUser CustomUserPrincipal customUserPrincipal) {

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        Users users = customUserPrincipal.getUser();

// S3에 업로드한 이미지 URL 리스트 생성
        List<String> imageUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) { // 파일이 비어있는지 확인
                    System.out.println(file);
                    String imageUrl = s3Util.upload(file);
                    imageUrls.add(imageUrl);
                } else {
                    System.out.println("Empty file detected: " + file);
                }
            }
        }



        Activity savedActivity = Activity.builder()
                .title(activity.getTitle())
                .content(activity.getContent())
                .date(activity.getDate())
                .build();


        if(!imageUrls.isEmpty()){
            savedActivity.setImage(String.join(",", imageUrls));
        }

        return activityService.savePost(savedActivity, users);
    }

//  활동기록 전체조회
    @GetMapping
    public ResponseEntity<?> getActivity(@AuthUser CustomUserPrincipal customUserPrincipal){
        Users users= customUserPrincipal.getUser();
        return activityService.getActivityList(users.getClub().getId());
    }
}
