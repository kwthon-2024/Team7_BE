package com.team7.club.user.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.team7.club.user.entity.Users;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserPrincipal implements UserDetails {

	private final Users users;

	public CustomUserPrincipal(Users users) {
		this.users = users;
	}

	@Override
	public String getUsername() {
		return users.getEmail();
	}

	@Override
	public String getPassword() {
		return users.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(users.getClubRole().name()));
		return authorities;
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

	public Users getUser() {
		return users;
	}

	public Long getId() {
		return users.getId();
	}
}