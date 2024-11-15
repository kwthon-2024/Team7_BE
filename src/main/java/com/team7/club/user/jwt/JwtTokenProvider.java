package com.team7.club.user.jwt;


import com.team7.club.user.entity.Users;
import com.team7.club.user.repository.UsersRepository;
import com.team7.club.user.security.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import com.team7.club.user.dto.reponse.UserResponseDto;

@Slf4j
@Component
public class JwtTokenProvider {
	private static final String AUTHORITIES_KEY = "auth";
	private static final String BEARER_TYPE = "Bearer";
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일
	private final Key key;

	@Value("${jwt.secret}")
	private String secretKey;

	private final UsersRepository usersRepository;

	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UsersRepository usersRepository) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.usersRepository = usersRepository;
	}

	// 토큰 생성
	public UserResponseDto.TokenInfo generateToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

		// accessToken 생성
		String accessToken = Jwts.builder()
			.setSubject(authentication.getName())
			.claim(AUTHORITIES_KEY, authorities)
			.setExpiration(accessTokenExpiresIn)
			.signWith(SignatureAlgorithm.HS256, key)
			.compact();

		// refreshToken 생성
		String refreshToken = Jwts.builder()
			.setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
			.signWith(SignatureAlgorithm.HS256, key)
			.compact();

		return UserResponseDto.TokenInfo.builder()
			.grantType(BEARER_TYPE)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
			.build();
	}

	// JWT에서 사용자 정보 추출
	public CustomUserPrincipal getUserFromToken(String token) {
		Claims claims = parseClaims(token);
		String email = claims.getSubject();  // JWT의 subject가 이메일이라고 가정

		// 이메일로 사용자 정보 조회
		Optional<Users> users = usersRepository.findByEmail(email); // findByEmail 사용
		if (users == null) {
			throw new RuntimeException("사용자를 찾을 수 없습니다.");  // 사용자 미존재 시 처리
		}

		return new CustomUserPrincipal(users.get());  // CustomUserPrincipal 반환
	}

	// Authentication 객체 생성
	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);

		if (claims.get(AUTHORITIES_KEY) == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.toList();

		UserDetails principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	// 토큰에서 Claims 파싱
	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	// 토큰 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.error("JWT 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	// 액세스 토큰 만료 시간 확인
	public Long getExpiration(String accessToken) {
		Claims claims = parseClaims(accessToken);
		Date expiration = claims.getExpiration();
		Long now = new Date().getTime();
		return (expiration.getTime() - now);
	}
}