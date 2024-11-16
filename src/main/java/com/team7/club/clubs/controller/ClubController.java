package com.team7.club.clubs.controller;

import com.team7.club.clubs.entity.Club;
import com.team7.club.clubs.service.ClubService;
import com.team7.club.common.config.AuthUser;
import com.team7.club.common.config.http.Response;
import com.team7.club.user.entity.ClubRole;
import com.team7.club.user.entity.Users;
import com.team7.club.user.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/club")
@RestController
public class ClubController {
    private final ClubService clubService;
    private final Response response;

//  동아리 정보 반환
    @GetMapping
    public ResponseEntity<?> clubInfo(@AuthUser CustomUserPrincipal customUserPrincipal){
        Users users = customUserPrincipal.getUser();
        return clubService.getClubInfo(users.getClub().getId());
    }

//  동아리 정보 수정
    @PutMapping
    public ResponseEntity<?> clubInfoUpdate(@RequestBody Club club, @AuthUser CustomUserPrincipal customUserPrincipal){
        Users users = customUserPrincipal.getUser();
        if(users.getClubRole() == ClubRole.ROLE_MEMBER){
            return response.fail("회장권한 이상 수정이 가능합니다", HttpStatus.BAD_REQUEST);
        }
        club.setId(users.getClub().getId());
        return clubService.saveClubInfo(club);
    }
}
