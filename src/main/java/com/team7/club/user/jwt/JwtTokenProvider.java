package com.team7.club.user.jwt;

import io.jsonwebtoken.*;
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
import java.util.stream.Collectors;

import com.team7.club.user.dto.reponse.UserResponseDto;

@Slf4j
@Component
public class JwtTokenProvider {
	private static final String AUTHORITIES_KEY = "auth";
	private static final String BEARER_TYPE = "Bearer";
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

	private final Key key;

	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

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

		// refeshToken 생성
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

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(key).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			log.info("Invalid JWT Token", e);
		}
		return false;
	}

	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public Long getExpiration(String accessToken) {
		Date expiration = Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody().getExpiration();
		Long now = new Date().getTime();
		return (expiration.getTime() - now);
	}
}