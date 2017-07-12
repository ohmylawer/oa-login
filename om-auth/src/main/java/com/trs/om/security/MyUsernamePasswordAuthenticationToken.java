package com.trs.om.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class MyUsernamePasswordAuthenticationToken extends
		UsernamePasswordAuthenticationToken {

	// fields ---------------------------------------------------------------
	/** 密码是否已经加密过了. */
	private boolean credentialsAlreadyEncoded=false;

	// methods --------------------------------------------------------------
	public MyUsernamePasswordAuthenticationToken(Object principal,
			Object credentials,boolean credentialsAlreadyEncoded,
			Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
		this.credentialsAlreadyEncoded=credentialsAlreadyEncoded;
	}

	public MyUsernamePasswordAuthenticationToken(Object principal,
			Object credentials,boolean credentialsAlreadyEncoded) {
		super(principal, credentials);
		this.credentialsAlreadyEncoded=credentialsAlreadyEncoded;
	}

	// accessors ------------------------------------------------------------
	public boolean isCredentialsAlreadyEncoded() {
		return credentialsAlreadyEncoded;
	}

}
