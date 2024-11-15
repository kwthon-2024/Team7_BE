package com.team7.club.user.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;


import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_TYPE = "Bearer";

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate redisTemplate;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
		ServletException {

		//  Request Header 에서 JWT 토큰 추출
		String token = resolveToken((HttpServletRequest) request);

		//  토큰 유효성 검사
		if (token != null && jwtTokenProvider.validateToken(token)) {
			// accessToken logout 여부 확인
			String isLogout = (String)redisTemplate.opsForValue().get(token);
			if (ObjectUtils.isEmpty(isLogout)) {
				// 토큰이 유효할 경우
				Authentication authentication = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		chain.doFilter(request, response);
	}

	// Request Header 에서 토큰 정보 추출
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
