package com.trs.om.security;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import com.trs.om.service.UserService;

public class MyWebSecurityExpressionHandler extends
		DefaultWebSecurityExpressionHandler {

	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	private ExpressionParser expressionParser = new SpelExpressionParser();
	private RoleHierarchy roleHierarchy;
	private boolean ipRestrictionEnabled;
	private boolean alwaysAllowLocalhost;
	private UserService userService;

	public ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	public EvaluationContext createEvaluationContext(
			Authentication authentication, FilterInvocation fi) {
		StandardEvaluationContext ctx = new StandardEvaluationContext();
		MyWebSecurityExpressionRoot root = new MyWebSecurityExpressionRoot(
				authentication, fi);
		root.setIpRestrictionEnabled(ipRestrictionEnabled);
		root.setAlwaysAllowLocalhost(alwaysAllowLocalhost);
		root.setUserService(userService);
		root.setTrustResolver(trustResolver);
		root.setRoleHierarchy(roleHierarchy);
		ctx.setRootObject(root);

		return ctx;
	}

	public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
		this.roleHierarchy = roleHierarchy;
	}

	public void setIpRestrictionEnabled(boolean ipRestrictionEnabled) {
		this.ipRestrictionEnabled = ipRestrictionEnabled;
	}

	public void setAlwaysAllowLocalhost(boolean alwaysAllowLocalhost) {
		this.alwaysAllowLocalhost = alwaysAllowLocalhost;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
