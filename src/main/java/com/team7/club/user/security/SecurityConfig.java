package com.team7.club.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.team7.club.user.jwt.JwtAuthenticationFilter;
import com.team7.club.user.jwt.JwtTokenProvider;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate redisTemplate;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// Swagger 및 정적 리소스 경로 무시
		return (web) -> web.ignoring().requestMatchers(
			"/v3/api-docs/**",
			"/swagger-ui/**",
			"/swagger-ui.html",
			"/swagger-resources/**",
			"/webjars/**",
			"/images/**",
			"/js/**",
			"/favicon.ico"
		);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// CORS 설정
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			// CSRF 비활성화
			.csrf(csrf -> csrf.disable())
			// 세션 설정
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			// 요청 권한 설정
			.authorizeHttpRequests(auth -> auth
				// Swagger UI 관련 경로 허용
				.requestMatchers(
					"/v3/api-docs/**",
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/swagger-resources/**",
					"/webjars/**"
				).permitAll()
				// 공용 엔드포인트 허용
				.requestMatchers(
					"/api/users/sign-up",
					"/api/users/login",
					"/api/users/reissue",
					"/api/users/logout"
				).permitAll()
				// 특정 역할에만 허용되는 엔드포인트
				.requestMatchers("/api/users/userTest").hasRole("USER")
				.requestMatchers("/api/users/adminTest").hasRole("ADMIN")

					// /api/club/notifications 접근 권한 설정 추가
					.requestMatchers(HttpMethod.POST, "/api/club/notifications").hasRole("ADMIN") // POST는 ADMIN만 허용
					.requestMatchers(HttpMethod.PUT, "/api/club/notifications/**").hasRole("ADMIN") // PUT은 ADMIN만 허용
					.requestMatchers(HttpMethod.DELETE, "/api/club/notifications/**").hasRole("ADMIN") // DELETE는 ADMIN만 허용
					.requestMatchers(HttpMethod.GET, "/api/club/notifications/**").permitAll() // GET은 모든 사용자 허용

					// 그 외 모든 요청 인증 필요
				.anyRequest().authenticated()
			)
			// JWT 필터 추가
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate),
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}

	// CORS 설정을 위한 Bean
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 출처 허용
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
		configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L); // preflight 요청 캐시 시간

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	// 비밀번호 암호화를 위한 Bean
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}