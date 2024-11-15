package com.team7.club.notice.dto;
import lombok.Getter;

@Getter
public class NoticeResponseDto {

    private Long notificationId;
    private String writer;
    private String title;
    private String content;
    private String image;

    public NoticeResponseDto(Long notificationId, String writer, String title, String content, String image) {
        this.notificationId = notificationId;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.image = image;
    }
}
