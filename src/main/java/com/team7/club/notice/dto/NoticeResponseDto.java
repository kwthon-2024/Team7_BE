package com.team7.club.notice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeResponseDto {

    private Long notificationId; // 공지 식별 번호
    private Long userId; // 사용자 ID
    private String title; // 제목
    private String content; // 내용
    private LocalDateTime createdDate; // 생성일
    private LocalDateTime updatedDate; // 수정일

    public NoticeResponseDto(Long notificationId, Long userId, String title, String content,
                             LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
