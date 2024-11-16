package com.team7.club.clubs.service;

import com.team7.club.clubs.dto.ClubResposeDto;
import com.team7.club.clubs.entity.Club;
import com.team7.club.clubs.repository.ClubRepository;
import com.team7.club.common.config.http.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final Response response;

    public ResponseEntity<?> getClubInfo(Long clubId) {
        try {
            Club club = clubRepository.getById(clubId);
            ClubResposeDto clubResposeDto = ClubResposeDto.builder()
                    .clubProfessor(club.getClubProfessor())
                    .clubName(club.getClubName())
                    .clubMember(club.getClubMember())
                    .grade(club.getGrade())
                    .clubDepartment(club.getClubDepartment())
                    .chairManDepartment(club.getChairManDepartment())
                    .chairManName(club.getChairManName())
                    .phoneNumber(String.valueOf(club.getPhoneNumber()))
                    .professorDepartment(club.getProfessorDepartment())
                    .studentNumber(String.valueOf(club.getStudentNumber()))
                    .build();
            return response.success(clubResposeDto);
        } catch (Exception e) {
            return response.fail("동아리 조회 오류", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> saveClubInfo(Club club) {
        try {
            clubRepository.save(club);
            return response.success("클럽 정보 변경 성공");
        } catch (Exception e) {
            return response.fail("클럽 정보 변경 오류", HttpStatus.BAD_REQUEST);
        }
    }
}
