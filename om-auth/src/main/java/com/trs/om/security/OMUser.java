package com.trs.om.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class OMUser extends User {
	private Long userId;
	private String userName;
	public OMUser(Long userId,String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, authorities);
		this.userId=userId;
		this.userName=username;
	}
	public Long getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
 
}
