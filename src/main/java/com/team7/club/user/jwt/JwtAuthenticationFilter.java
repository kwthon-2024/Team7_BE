package com.team7.club.user.jwt;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

import com.team7.club.user.security.CustomUserPrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate redisTemplate;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RedisTemplate redisTemplate) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisTemplate = redisTemplate;
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		// JWT 토큰 추출
		String token = resolveToken(request);

		// 토큰 유효성 검사
		if (token != null && jwtTokenProvider.validateToken(token)) {
			String isLogout = (String) redisTemplate.opsForValue().get(token);
			if (isLogout == null) {
				// Authentication을 SecurityContext에 설정
				CustomUserPrincipal customUserPrincipal = jwtTokenProvider.getUserFromToken(token);
				if (customUserPrincipal != null) {
					Authentication authentication = new UsernamePasswordAuthenticationToken(
						customUserPrincipal, null, customUserPrincipal.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}