package com.trs.om.security;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;

public class TokenAuthenticationFilter extends GenericFilterBean {

	private static final String TOKEN_HEADER="x-trsom-authn-token";
	private static final String RESPONSE_HEADER="x-trsom-authn-err";

	private static final int ERR_AUTHN_FAILED=0;
	private static final int ERR_MALFORMED_TOKEN=1;
	private static final int ERR_USERNAME_NOT_FOUND=2;
	private static final int ERR_BAD_CREDENTIALS=3;
	private static final int ERR_ACCOUNT_EXPIRED=4;
	private static final int ERR_CREDENTIALS_EXPIRED=5;
	private static final int ERR_ACCOUNT_DISABLED=6;
	private static final int ERR_ACCOUNT_LOCKED=7;

	private AuthenticationManager authenticationManager;
	private AuthenticationDetailsSource authenticationDetailsSource;
	private static final HashMap<String, Integer> EXCEPTION_ERR_MAP=new HashMap<String, Integer>();

	static{
		EXCEPTION_ERR_MAP.put(UsernameNotFoundException.class.getName(), ERR_USERNAME_NOT_FOUND);
		EXCEPTION_ERR_MAP.put(BadCredentialsException.class.getName(), ERR_BAD_CREDENTIALS);
		EXCEPTION_ERR_MAP.put(AccountExpiredException.class.getName(), ERR_ACCOUNT_EXPIRED);
		EXCEPTION_ERR_MAP.put(CredentialsExpiredException.class.getName(), ERR_CREDENTIALS_EXPIRED);
		EXCEPTION_ERR_MAP.put(DisabledException.class.getName(), ERR_ACCOUNT_DISABLED);
		EXCEPTION_ERR_MAP.put(LockedException.class.getName(), ERR_ACCOUNT_LOCKED);

	}

	private int getErrNum(AuthenticationException e){
		Integer errNum=EXCEPTION_ERR_MAP.get(e.getClass().getName());
		if(errNum!=null)
			return errNum;
		else
			return ERR_AUTHN_FAILED;

	}

	private void sendResponse(HttpServletResponse response,int err) throws IOException{
		response.addHeader(RESPONSE_HEADER, Integer.toString(err));
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentLength(0);
		response.flushBuffer();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		SecurityContext securityContext=SecurityContextHolder.getContext();
		if(securityContext!=null&&securityContext.getAuthentication()!=null&&securityContext.getAuthentication().isAuthenticated()){
			chain.doFilter(request, response);
			return;
		}
		HttpServletRequest httpRequest=(HttpServletRequest) request;
		HttpServletResponse httpResponse=(HttpServletResponse) response;
		String token=httpRequest.getHeader(TOKEN_HEADER);
		if(token==null){
			chain.doFilter(request, response);
			return;
		}
		if(StringUtils.isBlank(token)){
			sendResponse(httpResponse,ERR_MALFORMED_TOKEN);
			return;
		}
		String decodedToken=new String(Base64.decodeBase64(token),"UTF-8");
		int splitIdx=decodedToken.indexOf(':');
		if(splitIdx==-1){
			sendResponse(httpResponse,ERR_MALFORMED_TOKEN);
			return;
		}
		String username=decodedToken.substring(0, splitIdx);
		String digest=decodedToken.substring(splitIdx+1);
		if(StringUtils.isBlank(digest)){
			sendResponse(httpResponse,ERR_MALFORMED_TOKEN);
			return;
		}
		TokenAuthenticationToken authnRequest=new TokenAuthenticationToken(username, digest);
		authnRequest.setDetails(authenticationDetailsSource.buildDetails(httpRequest));
		try {
			authenticationManager.authenticate(authnRequest);
		} catch (AuthenticationException e) {
			sendResponse(httpResponse,getErrNum(e));
			return;
		}
		if(securityContext==null){
			securityContext=SecurityContextHolder.createEmptyContext();
		}
		securityContext.setAuthentication(authnRequest);
		SecurityContextHolder.setContext(securityContext);
		try {
			chain.doFilter(request, response);
		} finally {
			SecurityContextHolder.clearContext();
		}

	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public void setAuthenticationDetailsSource(
			AuthenticationDetailsSource authenticationDetailsSource) {
		this.authenticationDetailsSource = authenticationDetailsSource;
	}

}
