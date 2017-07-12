package com.trs.om.security;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.trs.otm.authentication.MD5;

public class TokenAuthenticationProvider implements AuthenticationProvider {

	private UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		TokenAuthenticationToken authRequest=(TokenAuthenticationToken) authentication;
		UserDetails user=userDetailsService.loadUserByUsername(authRequest.getName());
		if (!user.isAccountNonLocked()) {
            throw new LockedException("User account is locked", user);
        }

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled", user);
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired", user);
        }

		String expectedDigest=MD5.md5(authRequest.getName()+":"+user.getPassword());
		if(!StringUtils.equals(authRequest.getCredentials().toString(), expectedDigest))
			throw new BadCredentialsException("Bad credentials");

		if (!user.isCredentialsNonExpired()) {
			throw new CredentialsExpiredException("User credentials have expired", user);
		}
		TokenAuthenticationToken newAuthn=new TokenAuthenticationToken(user, authRequest.getCredentials(),user.getAuthorities());
		newAuthn.setDetails(authRequest.getDetails());
		return newAuthn;
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return TokenAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}


}
