package com.team7.club.user.controller;

import com.team7.club.user.dto.reponse.UserInfoResponseDto;
import com.team7.club.user.dto.reponse.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.team7.club.common.config.AuthUser;
import com.team7.club.common.config.http.Response;
import com.team7.club.common.config.lib.Helper;
import com.team7.club.user.annotation.UserApi;
import com.team7.club.user.dto.request.UserRequestDto;
import com.team7.club.user.entity.Users;
import com.team7.club.user.security.CustomUserPrincipal;
import com.team7.club.user.service.UsersService;
import com.team7.club.clubs.entity.Club;
@UserApi
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UsersController {

	private final UsersService usersService;
	private final Response response;

	@Operation(summary = "회원가입", description = "회원가입")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원가입 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@Validated @RequestBody UserRequestDto.SignUp signUp, Errors errors) {
		if (errors.hasErrors()) {
			return response.invalidFields(Helper.refineErrors(errors));
		}
		return usersService.signUp(signUp);
	}

	@Operation(summary = "로그인", description = "사용자 로그인")
	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody UserRequestDto.Login login, Errors errors) {
		if (errors.hasErrors()) {
			return response.invalidFields(Helper.refineErrors(errors));
		}
		return usersService.login(login);
	}

	@Operation(summary = "토큰 재발급", description = "액세스 토큰 재발급")
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(@Validated @RequestBody UserRequestDto.Reissue reissue, Errors errors) {
		if (errors.hasErrors()) {
			return response.invalidFields(Helper.refineErrors(errors));
		}
		return usersService.reissue(reissue);
	}

	@Operation(summary = "로그아웃", description = "사용자 로그아웃 처리")
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@Validated @RequestBody UserRequestDto.Logout logout, Errors errors) {
		if (errors.hasErrors()) {
			return response.invalidFields(Helper.refineErrors(errors));
		}
		return usersService.logout(logout);
	}

	@Operation(summary = "테스트", description = "테스트 API")
	@PostMapping("/test")
	public void test(@AuthUser CustomUserPrincipal customUserPrincipal) {
		Users users = customUserPrincipal.getUser();
		System.out.println(users.getName());
	}

	@Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 반환합니다.")
	@GetMapping("/me")
	public ResponseEntity<?> getUserInfo(@AuthUser CustomUserPrincipal customUserPrincipal) {
		Users user = customUserPrincipal.getUser();
		String clubName = user.getClub() != null ? user.getClub().getClubName() : "No Club";

		UserInfoResponseDto userInfo = new UserInfoResponseDto(
				user.getId(),
				user.getEmail(),
				user.getName(),
				user.getStudentNumber(),
				clubName // 클럽 이름 추가
		);

		return ResponseEntity.ok(userInfo);
	}



}