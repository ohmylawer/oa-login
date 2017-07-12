package com.trs.om.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class AnonymousAuthenticationFilter
		extends
		org.springframework.security.web.authentication.AnonymousAuthenticationFilter {

	// fields ---------------------------------------------------------------
	private AuthenticationDetailsSource authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private UserDetailsService userDetailsService;
	private String anonymousUserName = "anonymous";

	// methods --------------------------------------------------------------
	@Override
	public void afterPropertiesSet() {
		// just ignore
	}

	@Override
	protected boolean applyAnonymousForThisRequest(HttpServletRequest request) {
		UserDetails userDetails = userDetailsService
				.loadUserByUsername(anonymousUserName);
		return null != userDetails && userDetails.isEnabled()
				&& userDetails.isAccountNonExpired()
				&& userDetails.isAccountNonLocked();
	}

	@Override
	protected Authentication createAuthentication(HttpServletRequest request) {
		UserDetails userDetails = userDetailsService
				.loadUserByUsername(anonymousUserName);
		if (null == userDetails)
			return null;
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new GrantedAuthorityImpl("PERMISSION_anonymous"));
		authorities.addAll(userDetails.getAuthorities());
		AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(
				getKey(), userDetails, authorities);
		auth.setDetails(authenticationDetailsSource.buildDetails(request));
		return auth;

	}

	// accessors ------------------------------------------------------------
	@Override
	public void setAuthenticationDetailsSource(
			AuthenticationDetailsSource authenticationDetailsSource) {
		super.setAuthenticationDetailsSource(authenticationDetailsSource);
		this.authenticationDetailsSource = authenticationDetailsSource;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

}
