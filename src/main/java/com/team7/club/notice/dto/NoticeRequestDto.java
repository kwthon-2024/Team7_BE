package com.team7.club.notice.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class NoticeRequestDto {

    private String title; // 제목
    private String content; // 내용
}