package com.trs.om.service.impl;

import com.trs.om.rbac.IAuthorization;
import com.trs.om.rbac.IAuthorizationServer;
import com.trs.om.rbac.client.impl.LocalAuthorizationService;
import com.trs.om.service.UserService;

public class AuthorizationServiceImpl extends LocalAuthorizationService {
	private UserService userService;

	public AuthorizationServiceImpl(IAuthorizationServer authorizationServer) {
		super(authorizationServer);
	}
	public int canOperate(String username,String application,String object,String operation){
		if("admin".equals(username)){
			return IAuthorization.OPERATION_ALLOWED;
		}
		Long userId=this.userService.getUser(username).getId();
		return super.canOperate(userId, application, object, operation);
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public UserService getUserService() {
		return userService;
	}

}
