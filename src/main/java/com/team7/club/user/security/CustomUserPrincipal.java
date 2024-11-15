package com.team7.club.user.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;

import com.team7.club.user.entity.User;

public class CustomUserPrincipal implements UserDetails {
	private String email;
	private String password;
	private String username;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;
	private User user;

	public User getUser() {
		return user;
	}

	public CustomUserPrincipal(String email, String password, Collection<? extends GrantedAuthority> authorities, User user) {
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.user = user;
	}

	public static CustomUserPrincipal create(User user) {
		List<GrantedAuthority> authorities = Collections.
			singletonList(new SimpleGrantedAuthority("ROLE_USER"));

		return new CustomUserPrincipal(
			user.getEmail(),
			user.getPassword(),
			authorities,
			user
		);
	}

	public static CustomUserPrincipal create(User user, Map<String, Object> attributes) {
		CustomUserPrincipal userPrincipal = CustomUserPrincipal.create(user);
		userPrincipal.setAttributes(attributes);
		return userPrincipal;
	}


	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

}
