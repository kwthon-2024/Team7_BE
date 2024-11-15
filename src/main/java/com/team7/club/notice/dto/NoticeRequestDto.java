package com.team7.club.notice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeRequestDto {

    private String writer;
    private String title;
    private String content;

    private String image; // 선택 사항
}
