package com.team7.club.clubs.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ClubResposeDto {
    private String clubName; //동아리 이름
    private Integer clubMember; //동아리 인원
    private String clubProfessor; //동아리 지도 교수
    private String clubDepartment; //동아리 소속학부
    private String professorDepartment; //동아리 지도 교수 학부
    private String chairManName; //회장 이름
    private String chairManDepartment; //회장 학부
    private Integer grade; // 학년
    private String studentNumber; //회장 학번
    private String phoneNumber; // 회장 전화번호
}
