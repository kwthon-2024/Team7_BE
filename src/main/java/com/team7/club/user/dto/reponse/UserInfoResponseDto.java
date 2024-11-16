package com.team7.club.user.dto.reponse;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long id;            // 사용자 ID
    private String email;       // 사용자 이메일
    private String name;        // 사용자 이름
    private String studentNumber; // 학번
    private String clubName;

}
