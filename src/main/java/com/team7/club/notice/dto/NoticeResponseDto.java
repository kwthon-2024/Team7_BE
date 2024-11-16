package com.team7.club.notice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoticeResponseDto {

    private Long notificationId; // 공지 식별 번호
    private Long userId; // 사용자 ID
    private String title; // 제목
    private String content; // 내용
    private List<String> images; // 이미지 경로 리스트

    public NoticeResponseDto(Long notificationId, Long userId, String title, String content, List<String> images) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.images = images;
    }
}
