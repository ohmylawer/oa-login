package com.trs.otm.authentication;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.trs.om.bean.User;
import com.trs.om.security.CustomWebAuthenticationDetails;
import com.trs.om.security.OMUser;
import com.trs.om.security.SecurityConstants;

public class HttpAuthnUtils {
	/**
	 * 获得当前登陆用户信息，仅包括用户id和用户名，不包括用户的其他信息，所以如果需要获得用户的其他信息，比如用户组，请不要使用此方法。
	 * @return 当前登陆用户信息，仅包括用户id和用户名
	 */
	public static User getLoginUser(){
		SecurityContext securityContext=SecurityContextHolder.getContext();
		if(null!=securityContext&&null!=securityContext.getAuthentication()){
			OMUser omUser=(OMUser)(securityContext.getAuthentication().getPrincipal());
			User user=new User();
			user.setId(omUser.getUserId());
			user.setUserName(omUser.getUsername());
			return user;
		}else
			return null;
	}
	
	public static Long getLoginUserId(){
		SecurityContext securityContext=SecurityContextHolder.getContext();
		if(null!=securityContext&&null!=securityContext.getAuthentication())
			return ((OMUser)(securityContext.getAuthentication().getPrincipal())).getUserId();
		else
			return null;
	}
	
	public static String getLoginUserName(){
		SecurityContext securityContext=SecurityContextHolder.getContext();
		if(null!=securityContext&&null!=securityContext.getAuthentication())
			return securityContext.getAuthentication().getName();
		else
			return null;
	}
	
	public static CustomWebAuthenticationDetails getWebAuthenticationDetails(){
		SecurityContext securityContext=SecurityContextHolder.getContext();
		if(null!=securityContext&&null!=securityContext.getAuthentication())
			return (CustomWebAuthenticationDetails) securityContext.getAuthentication().getDetails();
		else
			return null;
	}
	
	public static boolean isAdmin(){
		return SecurityConstants.SYSTEM_DEFAULT_ADMIN.equals(getLoginUserName());
	}
}
