package com.trs.om.security;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

import com.trs.om.bean.User;
import com.trs.om.common.SystemUtil;
import com.trs.om.service.UserService;
import com.trs.om.util.IPUtils;

public class MyWebSecurityExpressionRoot extends WebSecurityExpressionRoot {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MyWebSecurityExpressionRoot.class);
	private boolean ipRestrictionEnabled;
	private boolean alwaysAllowLocalhost;
	private UserService userService;
	public MyWebSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
		super(a, fi);
	}

	public String getIpRestriction(){
		// 获取用户对象
		User user=userService.getUser(authentication.getName());
		if(user!=null) return user.getClientIpRestraint();
		else return null;
	}

	public boolean restrictClientIp(String ipRestriction) {
		if(!ipRestrictionEnabled) return true;
		if(StringUtils.isBlank(ipRestriction)) return true;
        String originalClientIP=((CustomWebAuthenticationDetails)authentication.getDetails()).getOriginalClientIP();//IPUtils.getOriginalClientIP(request);
        if(StringUtils.isBlank(originalClientIP)){
        	LOGGER.warn("无法获取客户端IP地址");
        	return false;
        }
        if(alwaysAllowLocalhost&&originalClientIP.equals("127.0.0.1"))
        	return true;
        String[] clientIpParts=originalClientIP.split("\\.");
        if(clientIpParts.length!=4){
        	LOGGER.warn("IP地址限制功能咱不支持IPv6的客户端地址:{}",originalClientIP);
        	return false;
        }
        String[] restrictionIpParts=ipRestriction.split("\\.");
        for(int i=0;i<4;i++){
			if(restrictionIpParts[i].equals("*")) continue;
			else{
				Integer clientIpPart;
				try {
					clientIpPart = Integer.valueOf(clientIpParts[i]);
				} catch (NumberFormatException e) {
					LOGGER.error("客户端地址错误：{}",originalClientIP);
					return false;
				}
				Integer restrictionIpPart;
				try {
					restrictionIpPart = Integer.valueOf(restrictionIpParts[i]);
				} catch (NumberFormatException e) {
					LOGGER.error("IP限制错误：{}",ipRestriction);
					return false;
				}
				if(clientIpPart.equals(restrictionIpPart)) continue;
				else return false;
			}
        }
        return true;

    }

	public boolean isSsoDisabled(){
		if("false".equals(SystemUtil.getProperty("security.enableSSO"))){
			return true;
		}else
			return false;
	}

	public boolean hasPermission(String operation,String object){
		// 获取用户对象
		if(authentication.getName().equals(SecurityConstants.SYSTEM_DEFAULT_ADMIN))
			return true;
		User user=userService.getUser(authentication.getName());
		String[] permissionString=new String[2];
		permissionString[0]=operation;
		permissionString[1]=object;
		return userService.hasPermission(permissionString, user);
	}

	public boolean hasPermission(String operation,String object,String application){
		// 获取用户对象
		User user=userService.getUser(authentication.getName());
		String[] permissionString=new String[2];
		permissionString[0]=operation;
		permissionString[1]=object;
		return userService.hasPermission(permissionString, user);
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
