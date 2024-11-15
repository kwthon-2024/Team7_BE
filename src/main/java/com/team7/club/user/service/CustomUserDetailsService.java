package com.team7.club.user.service;

import java.util.Collection;
import java.util.Collections;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.team7.club.user.entity.Users;
import com.team7.club.user.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UsersRepository usersRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return usersRepository.findByEmail(username)
			.map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
	}

	// 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
	private UserDetails createUserDetails(Users user) {
		// 여기서 user.getEmail() 이 이메일을 반환하도록 확인
		String email = user.getEmail(); // 이메일을 사용자명으로 사용
		String password = user.getPassword(); // 비밀번호
		// 권한을 List<GrantedAuthority>로 변환 (예시: role을 "ROLE_USER" 같은 문자열로 받는 경우)
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getClubRole().name()); // 역할을 받는 필드로 변경

		// UserDetails 객체 반환
		return new org.springframework.security.core.userdetails.User(email, password, Collections.singleton(authority));
	}
}
