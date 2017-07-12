package com.trs.om.security;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import com.trs.om.bean.EnvironmentVariable;
import com.trs.om.bean.User;
import com.trs.om.service.EnvironmentVariableService;
import com.trs.om.service.UserLogService;
import com.trs.om.service.UserService;

public class AuthnEventListener implements
		ApplicationListener<AbstractAuthenticationEvent> {
	// fields ---------------------------------------------------------------
	private UserLogService userLogService;
	private UserService userService;
	private EnvironmentVariableService environmentVariableService;

	// methods --------------------------------------------------------------
	private void log(Authentication authentication,String userAct){
		String userName=authentication.getName();
		CustomWebAuthenticationDetails details=(CustomWebAuthenticationDetails) authentication.getDetails();
		userLogService.log(userName, userAct, details.getOriginalClientIP(), details.getIpLocation());
	}
	public void onApplicationEvent(AbstractAuthenticationEvent event) {
		EnvironmentVariable env=environmentVariableService.getEnvironmentVariableByName("AUTHN.RETRYLIMIT.ENABLE");
		if(event instanceof AuthenticationSuccessEvent){
			log(event.getAuthentication(),"登录成功");
			if(StringUtils.equals("on",env.getValue())){
				User user=userService.getUser(event.getAuthentication().getName());
				if(null!=user){
					user.setRetryCount(0);
					userService.updateUser(user);
				}
			}
		}else if(event instanceof AuthenticationFailureBadCredentialsEvent){
			log(event.getAuthentication(),"登录失败：密码错误");
			if(StringUtils.equals("on",env.getValue())){
				User user=userService.getUser(event.getAuthentication().getName());
				if(null!=user){
					user.setRetryCount(user.getRetryCount()+1);
					userService.updateUser(user);
				}
			}
		}else if(event instanceof AuthenticationFailureDisabledEvent){
			log(event.getAuthentication(),"登录失败：账号被停用");
		}else if(event instanceof AuthenticationFailureLockedEvent){
			log(event.getAuthentication(),"登录失败：登录失败次数超过设定的阀值，账号被锁定");
		}else if(event instanceof AbstractAuthenticationFailureEvent){
			log(event.getAuthentication(),"登录失败："+((AbstractAuthenticationFailureEvent)event).getException().getMessage());
		}
	}

	// accessors ------------------------------------------------------------
	public UserLogService getUserLogService() {
		return userLogService;
	}
	public void setUserLogService(UserLogService userLogService) {
		this.userLogService = userLogService;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public EnvironmentVariableService getEnvironmentVariableService() {
		return environmentVariableService;
	}
	public void setEnvironmentVariableService(
			EnvironmentVariableService environmentVariableService) {
		this.environmentVariableService = environmentVariableService;
	}

}
