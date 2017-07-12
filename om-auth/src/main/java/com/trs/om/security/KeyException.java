package com.trs.om.security;

import org.springframework.security.core.AuthenticationException;

public class KeyException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	protected KeyException(String msg, Object extraInformation) {
		super(msg, extraInformation);
	}

	protected KeyException(String msg, Throwable t) {
		super(msg, t);
	}

	protected KeyException(String msg) {
		super(msg);
	}
}
