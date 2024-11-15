package com.team7.club.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.team7.club.common.config.http.Response;
import com.team7.club.common.config.lib.Helper;
import com.team7.club.user.dto.request.UserRequestDto;
import com.team7.club.user.service.UsersService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UsersController {

	private final UsersService usersService;
	private final Response response;


	//회원가입 API
	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@Validated @RequestBody UserRequestDto.SignUp signUp, Errors errors) {
		if (errors.hasErrors()) {
			return response.invalidFields(Helper.refineErrors(errors));
		}
		return usersService.signUp(signUp);
	}

	//로그인 API
	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody  UserRequestDto.Login login, Errors errors) {
		if (errors.hasErrors()) {
			return response.invalidFields(Helper.refineErrors(errors));
		}
		return usersService.login(login);
	}

	//토큰 재발급 API
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(@Validated UserRequestDto.Reissue reissue, Errors errors) {
		// validation check
		if (errors.hasErrors()) {
			return response.invalidFields(Helper.refineErrors(errors));
		}
		return usersService.reissue(reissue);
	}

	//로그아웃 API
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@Validated UserRequestDto.Logout logout, Errors errors) {
		// validation check
		if (errors.hasErrors()) {
			return response.invalidFields(Helper.refineErrors(errors));
		}
		return usersService.logout(logout);
	}

}
