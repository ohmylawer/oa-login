package com.trs.om.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 当由于验证码错误导致登录失败时,将抛出该异常。
 */
public class BadVerifyCodeException extends AuthenticationException {

	private static final long serialVersionUID = 5367778451954768662L;

	protected BadVerifyCodeException(String msg, Object extraInformation) {
		super(msg, extraInformation);
	}

	protected BadVerifyCodeException(String msg, Throwable t) {
		super(msg, t);
	}

	protected BadVerifyCodeException(String msg) {
		super(msg);
	}

}
