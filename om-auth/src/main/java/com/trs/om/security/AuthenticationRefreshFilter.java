package com.trs.om.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import com.trs.om.exception.OTMException;

/**
 * 刷新Authentication中的principal、authorities。
 */
public class AuthenticationRefreshFilter extends GenericFilterBean {

	// fields ---------------------------------------------------------------
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthenticationRefreshFilter.class);
	static final String FILTER_APPLIED = "__"+AuthenticationRefreshFilter.class.getName()+".applied";
	private UserDetailsService userDetailsService;
	private String userNotFoundErrorUrl = "/j_spring_security_logout";
	private String userDisabledErrorUrl = "/j_spring_security_logout";
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	// methods --------------------------------------------------------------
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (httpRequest.getAttribute(FILTER_APPLIED) != null) {
			// ensure that filter is only applied once per request
			chain.doFilter(httpRequest, httpResponse);
			return;
		}

		httpRequest.setAttribute(FILTER_APPLIED, Boolean.TRUE);

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (null != auth && auth.isAuthenticated()) {
			boolean isUsernamePasswordAuthenticationToken=auth instanceof UsernamePasswordAuthenticationToken;
			boolean isPreAuthenticatedAuthenticationToken=auth instanceof PreAuthenticatedAuthenticationToken;
			if (isUsernamePasswordAuthenticationToken||isPreAuthenticatedAuthenticationToken) {
				UserDetails user;
				try {
					user = userDetailsService
							.loadUserByUsername(auth.getName());
				} catch (UsernameNotFoundException e) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("用户" + auth.getName() + "已被删除，需要重新登录.", e);
					redirectStrategy.sendRedirect(httpRequest, httpResponse,
							userNotFoundErrorUrl);
					return;
				} catch (DataAccessException e) {
					LOGGER.error("无法获取用户信息：访问数据库出现异常.", e);
					throw new OTMException("无法获取用户信息:" + e.getMessage(), e);
				}
				if (null == user) {
					LOGGER
							.error("org.springframework.security.core.userdetails.UserDetailsService实现不符合接口约定，loadUserByUsername方法不能返回null.");
					throw new OTMException("无法获取用户信息：内部程序错误.");
				}
				if (!user.isEnabled()) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("用户" + auth.getName() + "已被停用，需要重新登录.");
					redirectStrategy.sendRedirect(httpRequest, httpResponse,
							userDisabledErrorUrl);
					return;
				}
				if(isUsernamePasswordAuthenticationToken){
					UsernamePasswordAuthenticationToken newToken = new UsernamePasswordAuthenticationToken(
							user, auth.getCredentials(), user.getAuthorities());
					newToken.setDetails(auth.getDetails());
					SecurityContextHolder.getContext().setAuthentication(newToken);
				}else if(isPreAuthenticatedAuthenticationToken){
					PreAuthenticatedAuthenticationToken newToken = new PreAuthenticatedAuthenticationToken(
							user, auth.getCredentials(), user.getAuthorities());
					newToken.setDetails(auth.getDetails());
					SecurityContextHolder.getContext().setAuthentication(newToken);
				}

			}
		}
		chain.doFilter(request, response);
		httpRequest.removeAttribute(FILTER_APPLIED);

	}

	// accessors ------------------------------------------------------------
	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public String getUserNotFoundErrorUrl() {
		return userNotFoundErrorUrl;
	}

	public void setUserNotFoundErrorUrl(String userNotFoundErrorUrl) {
		this.userNotFoundErrorUrl = userNotFoundErrorUrl;
	}

	public String getUserDisabledErrorUrl() {
		return userDisabledErrorUrl;
	}

	public void setUserDisabledErrorUrl(String userDisabledErrorUrl) {
		this.userDisabledErrorUrl = userDisabledErrorUrl;
	}

}
