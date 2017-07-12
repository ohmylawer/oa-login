package com.trs.om.security;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

import com.trs.om.service.EnvironmentVariableService;
import com.trs.om.util.HttpRequestUtils;

public class MyAuthenticationFailureHandler implements
		AuthenticationFailureHandler, ServletContextAware {

	// fields ---------------------------------------------------------------
	protected static final Logger LOGGER = LoggerFactory.getLogger(MyAuthenticationFailureHandler.class);
	private String defaultFailureUrls;
	private boolean defaultHttps = false;
	private boolean defaultHttp = false;
	private RedirectStrategy redirectStrategy;
	public static final String LAST_LOGIN_ERROR_KEY = "LAST_LOGIN_ERROR";
	private EnvironmentVariableService environmentVariableService;
	private ServletContext servletContext;

	// methods --------------------------------------------------------------
	public MyAuthenticationFailureHandler() {
	}

	public MyAuthenticationFailureHandler(String defaultFailureUrls) {
		setDefaultFailureUrls(defaultFailureUrls);
	}

	protected String determineDefaultFailureUrl(HttpServletRequest request,
			HttpServletResponse response) {
		String []urls=defaultFailureUrls.split(",");
		Cookie cookie=CookieUtil.getCookieByName(request, "LOGINFORM");
		String failureUrl=urls[0];
		//String parameter=request.getParameter("LOGINFORM");
		//String uri=request.getRequestURI();
		if(urls.length>1&&cookie!=null){//&&parameter!=null
			String cv=cookie.getValue();
			for(int i=1;i<urls.length;i++){
				if(cv.equals(urls[i])){
					failureUrl=urls[i];
					break;
				}
			}
		}
		if (!request.isSecure() && defaultHttps) {
			return HttpRequestUtils.getSecureUrl(servletContext, request,
					response, failureUrl);
		} else if (request.isSecure() && defaultHttp) {
			return HttpRequestUtils.getInsecureUrl(servletContext, request,
					response, failureUrl);
		} else
			return failureUrl;
	}

	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		if (exception instanceof BadVerifyCodeException) {
			request.getSession().setAttribute(LAST_LOGIN_ERROR_KEY, "验证码错误");
		} else if (exception instanceof BadCredentialsException) {
			request.getSession().setAttribute(LAST_LOGIN_ERROR_KEY,
					"用户名不存在或密码错误");
		} else if (exception instanceof DisabledException) {
			request.getSession().setAttribute(LAST_LOGIN_ERROR_KEY, "用户账号已被停用");
		} else if (exception instanceof LockedException) {
			int retryLimit = Integer.valueOf(environmentVariableService
					.getEnvironmentVariableByName("AUTHN.RETRYLIMIT")
					.getValue());
			request.getSession().setAttribute(LAST_LOGIN_ERROR_KEY,
					"登录失败次数超过" + retryLimit + "次，用户账号已被锁定");
		} else if (exception instanceof SessionAuthenticationException) {
			request.getSession().setAttribute(LAST_LOGIN_ERROR_KEY,
					"系统繁忙，请稍候重试");
		} else if(exception instanceof AccountExpiredException) {
			request.getSession().setAttribute(LAST_LOGIN_ERROR_KEY,
			"用户账号已过期");
		} else if(exception instanceof KeyException) {
			request.getSession().setAttribute(LAST_LOGIN_ERROR_KEY,
					exception.getMessage());
		}

		if (defaultFailureUrls == null) {
			LOGGER.debug("No failure URL set, sending 401 Unauthorized error");

			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Authentication Failed: " + exception.getMessage());
		} else {
			String redirectUrl = determineDefaultFailureUrl(request, response);
			LOGGER.debug("Redirecting to " + redirectUrl);
			redirectStrategy.sendRedirect(request, response, redirectUrl);
		}
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext=servletContext;

	}

	// accessors ------------------------------------------------------------
	/**
	 * The URL which will be used as the failure destination.
	 *
	 * @param defaultFailureUrl
	 *            the failure URL, for example "/loginFailed.jsp".
	 */
	public void setDefaultFailureUrls(String defaultFailureUrls) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrls), "'"
				+ defaultFailureUrls + "' is not a valid redirect URL");
		this.defaultFailureUrls = defaultFailureUrls;
	}

	/**
	 * Allows overriding of the behaviour when redirecting to a target URL.
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	public boolean isDefaultHttps() {
		return defaultHttps;
	}

	public void setDefaultHttps(boolean defaultHttps) {
		this.defaultHttps = defaultHttps;
	}

	public boolean isDefaultHttp() {
		return defaultHttp;
	}

	public void setDefaultHttp(boolean defaultHttp) {
		this.defaultHttp = defaultHttp;
	}

	public EnvironmentVariableService getEnvironmentVariableService() {
		return environmentVariableService;
	}

	public void setEnvironmentVariableService(
			EnvironmentVariableService environmentVariableService) {
		this.environmentVariableService = environmentVariableService;
	}

	public String getDefaultFailureUrls() {
		return defaultFailureUrls;
	}


	public ServletContext getServletContext() {
		return servletContext;
	}
}
