package com.trs.om.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;

import com.trs.om.util.HttpRequestUtils;

public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

	// fields ---------------------------------------------------------------
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthenticationEntryPointImpl.class);
	private String loginFormUrls;
	private String digestAuthnUrlPrefixs;
	private Map<String,String> exceptionMessageMap=new HashMap<String, String>();
	private String defaultExceptionMessage="当前会话已经注销或超时，请重新登录！";
	private String key;
	private String realmName;
	private int nonceValiditySeconds = 300;
	private DigestAuthenticationEntryPoint digestAuthenticationEntryPoint;
	// methods --------------------------------------------------------------
	private String getExceptionMappedMessage(AuthenticationException authException){
		String msg=exceptionMessageMap.get(authException.getClass().getName());
		return null==msg?defaultExceptionMessage:msg;
	}

	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		if(StringUtils.isNotBlank(digestAuthnUrlPrefixs)){
			String []pres=digestAuthnUrlPrefixs.split(",");
			boolean digestAuthn=false;
			for(String pre:pres){
				if(request.getRequestURI().startsWith(request.getContextPath()+pre)){
					digestAuthn=true;
					break;
				}
			}
			/*if(digestAuthn){
				digestAuthenticationEntryPoint.commence(request, response, authException);
				return;
			}*/
		}
		Cookie cookie=CookieUtil.getCookieByName(request, "LOGINFORM");
		String []pres=loginFormUrls.split(",");
		String lastLoginFormUrl=pres[0];
		if(pres.length>1&&cookie!=null){
			String cv=cookie.getValue();
			for(int i=1;i<pres.length;i++){
				if(cv.equals(pres[i])){
					lastLoginFormUrl=pres[i];
					break;
				}
			}
		}

		if(HttpRequestUtils.isAjaxRequest(request)){
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			String loginpage=HttpRequestUtils.resolveToAbsoluteUrl(request, lastLoginFormUrl);
			String errmsg=getExceptionMappedMessage(authException);
			response.addHeader("x-trsom-loginpage", URLEncoder.encode(loginpage,"UTF-8"));
			response.addHeader("x-trsom-errmsg", URLEncoder.encode(errmsg,"UTF-8"));
			response.flushBuffer();
			LOGGER.debug("Ajax请求\"{}\"的认证信息异常，异常信息\"{}\"，返回状态码{}，头部[x-trsom-loginpage={},x-trsom-errmsg={}]",
					new Object[] { request.getRequestURI(),authException.getMessage(),
						HttpServletResponse.SC_UNAUTHORIZED,loginpage, errmsg });
		}else{
			response.sendRedirect(request.getContextPath()+lastLoginFormUrl);
//			System.out.println(request.getRequestURI());
//			System.out.println(authException.getMessage());
			LOGGER.debug("普通请求\"{}\"的认证信息异常，异常信息\"{}\"，重定向到登录页面{}",
					new Object[] { request.getRequestURI(),
							authException.getMessage(), lastLoginFormUrl });
		}

	}

	// accessors ------------------------------------------------------------
	public String getLoginFormUrls() {
		return loginFormUrls;
	}

	public void setLoginFormUrls(String loginFormUrls) {
		this.loginFormUrls = loginFormUrls;
	}

	public Map<String, String> getExceptionMessageMap() {
		return exceptionMessageMap;
	}

	public void setExceptionMessageMap(Map<String, String> exceptionMessageMap) {
		this.exceptionMessageMap = exceptionMessageMap;
	}

	public String getDefaultExceptionMessage() {
		return defaultExceptionMessage;
	}

	public void setDefaultExceptionMessage(String defaultExceptionMessage) {
		this.defaultExceptionMessage = defaultExceptionMessage;
	}

	public String getDigestAuthnUrlPrefixs() {
		return digestAuthnUrlPrefixs;
	}

	public void setDigestAuthnUrlPrefixs(String digestAuthnUrlPrefixs) {
		this.digestAuthnUrlPrefixs = digestAuthnUrlPrefixs;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	public int getNonceValiditySeconds() {
		return nonceValiditySeconds;
	}

	public void setNonceValiditySeconds(int nonceValiditySeconds) {
		this.nonceValiditySeconds = nonceValiditySeconds;
	}

	public DigestAuthenticationEntryPoint getDigestAuthenticationEntryPoint() {
		return digestAuthenticationEntryPoint;
	}

	public void setDigestAuthenticationEntryPoint(
			DigestAuthenticationEntryPoint digestAuthenticationEntryPoint) {
		this.digestAuthenticationEntryPoint = digestAuthenticationEntryPoint;
	}
}
