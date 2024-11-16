package com.team7.club.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import com.team7.club.clubs.entity.Club;
import com.team7.club.clubs.repository.ClubRepository;
import com.team7.club.common.config.http.Response;
import com.team7.club.user.dto.reponse.UserResponseDto;
import com.team7.club.user.dto.request.UserRequestDto;
import com.team7.club.user.entity.Users;
import com.team7.club.user.jwt.JwtTokenProvider;
import com.team7.club.user.repository.UsersRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {

	private final UsersRepository usersRepository;
	private final Response response;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final RedisTemplate<String, String> redisTemplate;

	private final ClubRepository clubRepository;

	@Transactional
	public ResponseEntity<?> signUp(UserRequestDto.SignUp signUp) {
		if (usersRepository.existsByEmail(signUp.getEmail())) {
			return response.fail("이미 회원가입된 이메일입니다.", HttpStatus.BAD_REQUEST);
		}

		Users users = Users.builder()
			.email(signUp.getEmail())
			.password(passwordEncoder.encode(signUp.getPassword()))
			.name(signUp.getName())
			.clubRole(signUp.getClubRole())
			.studentNumber(signUp.getStudentNumber())
			.build();
		if (!clubRepository.existsByClubName(signUp.getClubName())){
			Club club = Club.builder().
				clubName(signUp.getClubName())
				.build();
			clubRepository.save(club);
		}
		users.setClub(clubRepository.findByClubName(signUp.getClubName()).get());

		usersRepository.save(users);

		return response.success("회원가입에 성공했습니다.");
	}

	public ResponseEntity<?> login(UserRequestDto.Login login) {
		Users users = usersRepository.findByEmail(login.getEmail()).orElseThrow(() -> new NoSuchElementException("유저가 없습니다"));

		if (usersRepository.findByEmail(login.getEmail()).orElse(null) == null) {
			return response.fail("해당 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		if(!passwordEncoder.matches(login.getPassword(), users.getPassword())){
			return response.fail("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// Authentication 객체 생성
		UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

		// 사용자 비밀번호 체크
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// JWT 토큰 생성
		UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		//  RefreshToken Redis 저장
		redisTemplate.opsForValue()
			.set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

		return response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
	}

	public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {
		// Refresh Token 검증
		if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
			return response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// Access Token 에서 email get
		Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

		// Redis 에서 email 을 기반으로 저장된 Refresh Token 값 get
		String refreshToken = redisTemplate.opsForValue().get("RT:" + authentication.getName());
		// (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
		if(ObjectUtils.isEmpty(refreshToken)) {
			return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
		}
		if(!refreshToken.equals(reissue.getRefreshToken())) {
			return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// new 토큰 생성
		UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		// Redis -> RefreshToken 업데이트
		redisTemplate.opsForValue()
			.set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

		return response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
	}


	public ResponseEntity<?> logout(UserRequestDto.Logout logout) {
		//  Access Token 검증
		if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
			return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
		}

		// Access Token 에서 email get
		Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());

		//  Redis 에서 해당 email 로 저장된 Refresh Token 이 있는지 체크 -> 있으면 삭제
		if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
			// Refresh Token 삭제
			redisTemplate.delete("RT:" + authentication.getName());
		}

		// Access Token 만료 ->  BlackList로 저장 (로그아웃과 같은 개념)
		Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
		redisTemplate.opsForValue()
			.set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

		return response.success("로그아웃 되었습니다.");
	}

}
