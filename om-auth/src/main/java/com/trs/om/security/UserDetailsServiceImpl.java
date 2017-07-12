package com.trs.om.security;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.trs.om.rbac.client.IAuthorizationService;
import com.trs.om.bean.User;
import com.trs.om.service.EnvironmentVariableService;
import com.trs.om.service.UserGroupService;
import com.trs.om.service.UserService;

public class UserDetailsServiceImpl implements UserDetailsService {

	// fields ---------------------------------------------------------------
	private UserService userService;
	private IAuthorizationService authorizationService;
	private UserGroupService groupService;
//	private String permissionPrefix="PERMISSION_";
	private EnvironmentVariableService environmentVariableService;

	// methods --------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		//获取用户信息
		User user=userService.getUser(username);
		if(null==user)
			throw new UsernameNotFoundException("用户名\""+username+"\"还未注册");
		boolean accountNonExpired=true;
		if(user.getDueTime()!=null)//如果用户到期时间不是“永不到期”（值为null）
			if(user.getDueTime().before(new Date()))
				accountNonExpired=false;
		//获取用户权限
		//List permissions;
//		try {
//			permissions=authorizationService.getPermissions(user.getId(), AuthorizationManager.APPLICATION);
//		} catch (AuthorizationException e) {
//			throw new AuthenticationServiceException("无法读取用户权限", e);
//		}
//		Collection<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
//		if(null!=permissions){
//			for(int i=0;i<permissions.size();i++){
//				Permission permission=(Permission) permissions.get(i);
//				authorities.add(new GrantedAuthorityImpl(permissionPrefix+permission.getOperation()+"-"+permission.getObject()));
//			}
//		}
		//启用SSO时，通过权限自动屏蔽用户的某些维护操作
//		if("false".equals(SystemUtil.getProperty("security.enableSSO"))){
//			authorities.add(new GrantedAuthorityImpl(permissionPrefix+"NONE-SSO"));
//		}
		boolean enabled=!user.getDisabled();  //enabled==true     当前用户未被停用
		/*if(!"admin".equals(user.getUserName())&&enabled){//登陆用户非admin且未被停用时，考察该用户所在的用户组是否有被停用的，如果有，则该用户也被被停用
			Set<UserGroup>  groups=user.getUserGroups();
			for (UserGroup  group:groups) {
				if(group.isDisabled()){
					enabled=false;
					break;
				}
				enabled=true;
			}
		}*/
		boolean notLocked=true;
		if(StringUtils.equals("on", environmentVariableService.getEnvironmentVariableByName("AUTHN.RETRYLIMIT.ENABLE").getValue())){
			int retryLimit=Integer.valueOf(environmentVariableService.getEnvironmentVariableByName("AUTHN.RETRYLIMIT").getValue());
			notLocked=user.getRetryCount()<retryLimit;
		}
		return new OMUser(user.getId(),username, user.getUserPassword(), enabled, accountNonExpired, true, notLocked, new ArrayList<GrantedAuthority>());
	}

	// accessors ------------------------------------------------------------
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 允许重写默认的许可前缀。
	 *
	 * @param permissionPrefix 新的前缀。
	 */
	public IAuthorizationService getAuthorizationService() {
		return authorizationService;
	}
	public void setAuthorizationService(IAuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public UserGroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(UserGroupService groupService) {
		this.groupService = groupService;
	}

	public EnvironmentVariableService getEnvironmentVariableService() {
		return environmentVariableService;
	}

	public void setEnvironmentVariableService(
			EnvironmentVariableService environmentVariableService) {
		this.environmentVariableService = environmentVariableService;
	}


}
