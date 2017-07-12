package com.trs.otm.authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationManager {
	private static final Logger log=LoggerFactory.getLogger(AuthorizationManager.class);
	public static final String APPLICATION="OTM";
	public static boolean isSystemRole(String roleName){
		if(roleName!=null&&(roleName=roleName.trim()).length()>0){
			if(roleName.equals("系统管理员")||roleName.equals("编辑")||roleName.equals("普通用户"))
				return true;
		}
		return false;
	}
}
