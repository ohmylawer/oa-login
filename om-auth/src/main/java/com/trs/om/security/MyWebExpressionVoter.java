package com.trs.om.security;

import org.springframework.security.web.access.expression.WebSecurityExpressionHandler;

public class MyWebExpressionVoter  extends org.springframework.security.web.access.expression.WebExpressionVoter {
	private WebSecurityExpressionHandler expressionHandler = new MyWebSecurityExpressionHandler();
}
