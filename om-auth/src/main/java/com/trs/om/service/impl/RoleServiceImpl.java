package com.trs.om.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.Permission;
import com.trs.om.bean.Privilege;
import com.trs.om.bean.Role;
import com.trs.om.bean.Session;
import com.trs.om.bean.User;
import com.trs.om.bean.UserCriterion;
import com.trs.om.bean.UserGroup;
import com.trs.om.common.ObjectContainer;
import com.trs.om.common.PermissionConstants;
import com.trs.om.exception.OTMException;
import com.trs.om.exception.TRSOMException;
import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IPermissionManager;
import com.trs.om.rbac.IPrivilegeManager;
import com.trs.om.rbac.IRoleManager;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.security.SecurityConstants;
import com.trs.om.service.EncryptService;
import com.trs.om.service.PermissionService;
import com.trs.om.service.RoleService;
import com.trs.om.service.UserGroupService;
import com.trs.om.service.UserLogService;
import com.trs.om.service.UserService;
import com.trs.otm.authentication.HttpAuthnUtils;
import com.trs.otm.authorization.AuthorizationManager;

public class RoleServiceImpl implements RoleService {
	private IRoleManager roleManager;
	private UserLogService userLogService;
	private IPermissionManager permissionManager;
	private UserService userService;
	private UserGroupService userGroupService;
	private ISessionManager sessionManager;
	private PermissionService permissionService;
	
	public RoleServiceImpl() {
		EncryptService obj;
		try {
			obj = EncryptService.getInstance();
			obj.checkLicence();
		} catch (Exception e) {
			throw new TRSOMException("无法获得或者校验注册码："+e.getMessage(),e);
		}
	}

	@Transactional
	public void deleteRoles(Long[] ids) {
		try {
			for(Long id:ids){
				Role role=roleManager.getRole(id);
				if(role==null)
					continue;
				if(!AuthorizationManager.isSystemRole(role.getName())){
					this.deleteGroupRoleByRole(role);
					roleManager.deleteRole(role.getId());
					userLogService.log("删除角色"+role.getName());
				}else{
					userLogService.log("试图删除系统角色"+role.getName());
				}
			}
		} catch (AuthorizationException e) {
			throw new OTMException(e.getMessage(),e);
		}
	}

	@Transactional
	public void deleteGroupRoleByRole(Role role) {
		List<UserGroup> groups=userGroupService.listGroupsByRole(role);
		for(UserGroup group:groups){
			group.getRoles().remove(role);
			userGroupService.updateUserGroup(group);
		}
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}
	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}
	public UserLogService getUserLogService() {
		return userLogService;
	}
	public void setUserLogService(UserLogService userLogService) {
		this.userLogService = userLogService;
	}
	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}
	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
	public UserGroupService getUserGroupService() {
		return userGroupService;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}
	public ISessionManager getSessionManager() {
		return sessionManager;
	}
	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	//==================================================================
	/* (non-Javadoc)
	 * @see com.trs.om.service.RoleService#getRole(java.lang.String)
	 */
	@Transactional
	public Role getRole(Long roleId) {
		try {
			return roleManager.getRole(roleId);
		} catch (AuthorizationException e) {
			throw new TRSOMException(e.getMessage(),e);
		}
	}
	/* (non-Javadoc)
	 * @see com.trs.om.service.RoleService#listRoles()
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Role> listRoles() {
		try {
			return roleManager.listRoles();
		} catch (AuthorizationException e) {
			throw new TRSOMException(e.getMessage(),e);
		}
	}
	/* (non-Javadoc)
	 * @see com.trs.om.service.RoleService#getRoleByName(java.lang.String)
	 */
	@Transactional
	public Role getRoleByName(String roleName) {
		try {
			return roleManager.getRoleByName(roleName);
		} catch (AuthorizationException e) {
			throw new TRSOMException(e.getMessage(),e);
		}
	}
	/* (non-Javadoc)
	 * @see com.trs.om.service.RoleService#addRole(com.trs.om.rbac.bo.Role, com.trs.om.bean.UserLog)
	 */
	@Transactional
	public String addRole(Role role) {
		try{
			Role r = roleManager.getRoleByName(role.getName());
			if(r!=null){
				return RoleService.ROLE_EXIST;
			}
			roleManager.addNewRole(role.getName(), role.getDesc());
			userLogService.log("创建角色"+role.getName());
			return RoleService.SUCCESS;
		}catch(AuthorizationException e){
			throw new TRSOMException(e.getMessage(),e);
		}
	}
	/* (non-Javadoc)
	 * @see com.trs.om.service.RoleService#updateRole(com.trs.om.rbac.bo.Role, com.trs.om.bean.UserLog)
	 */
	@Transactional
	public String updateRole(Role role) {
		try{
			Role r = roleManager.getRole(role.getId());
			for(String d : RoleService.DEFAULT_ROLE_NAMES){
				if(d.equals(r.getName())){
					return RoleService.MSG;
				}
			}

			r.setDesc(role.getDesc());
			r.setName(role.getName());
			roleManager.updateRole(r);
			//记录日志
			userLogService.log("修改角色"+role.getName());
			return RoleService.SUCCESS;
		}catch(AuthorizationException e){
			throw new TRSOMException(e.getMessage(),e);
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.service.RoleService#accredit(java.lang.String, java.lang.String[], com.trs.om.bean.UserLog)
	 */
	@Transactional
	public void accredit(Long roleId, Long[] accreditIds) {
		try{
			IPermissionManager authorityDao = (IPermissionManager) ObjectContainer.getBean("permissionManager");//.getPermissionManager();
			IPrivilegeManager previligeManager = (IPrivilegeManager) ObjectContainer.getBean("privilegeManager");//ObjectContainer.getPrivilegeManager();
			Role role=roleManager.getRole(roleId);
			//添加判断如果是系统管理员角色，就不更改角色权限
			/*if(role==null ){
				return;
			}
			if( RoleService.DEFAULT_ADMIN.equals(role.getName()))
			{
				userLogService.log("尝试修改系统管理员角色权限。");
				return;
			}*/
			//清空该角色权限
			List<com.trs.om.bean.Privilege> list = (List<Privilege>)previligeManager.findPrivileges(null, role.getId());
			for(Privilege p:list){
				Permission permission =authorityDao.getPermission(p.getPermissionId());
				if(permission==null||permission.getObject().matches("^\\d*$"))
					continue;
				previligeManager.deletePrivilege(p.getId());
			}
			//置入权限

			if(null==accreditIds||0==accreditIds.length)
				userLogService.log("置空角色权限"+role.getName());
			else{
				for(Long id:accreditIds){
					previligeManager.addNewPrivilege(roleId,id);
				}
				StringBuilder logBuilder=new StringBuilder();
				logBuilder.append("修改角色权限<")
					.append(role.getName()).append(">[");
				for(int i=0;i<accreditIds.length;i++){
					Permission permission = permissionManager.getPermission(accreditIds[i]);
					if(i>0) logBuilder.append(',');
					logBuilder.append(permission.getObject()+"-"+permission.getOperation());
				}
				logBuilder.append(']');
				userLogService.log(logBuilder.toString());
			}
		}catch(AuthorizationException e){
			throw new TRSOMException(e.getMessage(),e);
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.service.RoleService#deleteUsersFromRole(java.lang.String, java.lang.String[], com.trs.om.bean.LoginUser)
	 */
//	public void deleteUsersFromRole(Long roleId,Long[] userIds){
//		ISessionManager sessionManager= ObjectContainer.getSessionManager();
//		try{
//			for(int i=0;i<userIds.length;i++){
//				Long userId = userIds[i];
//				if(userId!=null){
//					Session s =sessionManager.getSession(userId,roleId);
//					if(null!=s){
//						sessionManager.deleteSession(s.getId());
//					}
//				}
//			}
//			StringBuilder logBuilder = new StringBuilder();
//			logBuilder.append("从角色中删除用户[");
//			logBuilder.append(roleManager.getRole(roleId).getName());
//			logBuilder.append("]");
//			logBuilder.append(userIds);
//			userLogService.log(logBuilder.toString());
//		}catch (AuthorizationException e) {
//			throw new TRSOMException(e.getMessage(),e);
//		}
//	}

	@Override
	@Transactional
	public void deleteUsersFromRole(Long roleId, Map<Long, Set<Long>> userIds) {
		ISessionManager sessionManager= (ISessionManager) ObjectContainer.getBean("sessionManager");//.getSessionManager();
		try{
			for(Long groupId:userIds.keySet()){
				for(Long userId:userIds.get(groupId))
				if(userId!=null){
					Session s =sessionManager.getSession(userId,roleId,groupId);
					if(null!=s){
						sessionManager.deleteSession(s.getId());
					}
				}
			}
			StringBuilder logBuilder = new StringBuilder();
			logBuilder.append("从角色中删除用户[");
			logBuilder.append(roleManager.getRole(roleId).getName());
			logBuilder.append("]");
			logBuilder.append(userIds);
			userLogService.log(logBuilder.toString());
		}catch (AuthorizationException e) {
			throw new TRSOMException(e.getMessage(),e);
		}
	}

	@Override
	@Transactional
	public void addUsersToRole(Long roleId, Map<Long, Set<Long>> userIds) {
		ISessionManager sessionManager=  (ISessionManager) ObjectContainer.getBean("sessionManager");//.getSessionManager();
		for(Long groupId:userIds.keySet()){
			for(Long userId:userIds.get(groupId)){
				try{
						if(userId!=null){
							Session s =sessionManager.getSession(userId,roleId,groupId);
							if(null==s){
								sessionManager.addNewSession(roleId,userId,groupId);
							}
						}
					StringBuilder logBuilder = new StringBuilder();
					logBuilder.append("向角色中添加用户[");
					logBuilder.append(roleManager.getRole(roleId).getName());
					logBuilder.append("]");
					logBuilder.append(userIds.toString());
					userLogService.log(logBuilder.toString());
				}catch (AuthorizationException e) {
					throw new TRSOMException(e.getMessage(),e);
				}
			}
		}
	}
//	/* (non-Javadoc)
//	 * @see com.trs.om.service.RoleService#addUsersToRole(java.lang.String, java.lang.String[], com.trs.om.bean.LoginUser)
//	 */
//	public void addUsersToRole(Long roleId,Long[] userIds){
//		ISessionManager sessionManager= ObjectContainer.getSessionManager();
//		try{
//			for(int i=0;i<userIds.length;i++){
//				Long userId = userIds[i];
//				if(userId!=null){
//					Session s =sessionManager.getSession(userId,roleId);
//					if(null==s){
//						sessionManager.addNewSession(roleId, userId);
//					}
//				}
//			}
//			StringBuilder logBuilder = new StringBuilder();
//			logBuilder.append("向角色中添加用户[");
//			logBuilder.append(roleManager.getRole(roleId).getName());
//			logBuilder.append("]");
//			logBuilder.append(userIds.toString());
//			userLogService.log(logBuilder.toString());
//		}catch (AuthorizationException e) {
//			throw new TRSOMException(e.getMessage(),e);
//		}
//	}
//	public void addUsersToRole(Long roleId, String[] userNames) {
//		Long[] userIds=new Long[userNames.length];
//		for(int i=0;i<userNames.length;i++){
//			User user=userService.getUser(userNames[i]);
//			userIds[i]=user==null?null:user.getId();
//		}
//		this.addUsersToRole(roleId, userIds);
//	}
//
//	@Override
//	public void configUserGroups(Long roleId, String groupIds) {
//		if(StringUtils.isBlank(groupIds))
//			return;
//		try{
//			Role role=roleManager.getRole(roleId);
//			String[] ids = groupIds.split(",");
//			if(role!=null){
//				role.getUserGroups().clear();
//				for(int i=0;i<ids.length;i++){
//					if(ids[i]!=null&&ids[i].trim().length()>0){
//						Long id = Long.valueOf(ids[i]);
//						UserGroup ug = userGroupService.get(id);
//						role.getUserGroups().add(ug);
//					}
//				}
//				this.updateRole(role);
//			}
//		}catch(AuthorizationException e){
//			throw new TRSOMException(e.getMessage(),e);
//		}
//	}

	@Override
	@Transactional
	public List<Role> listAvailableRolesByGroup(UserGroup userGroup) {
		Long parentId=userGroup.getParentId();
		if(parentId==null||parentId==0l)
			return this.listRoles();
		else{
			UserGroup parent=userGroupService.get(parentId);
			Set<Role> roleSet=parent.getRoles();
			List<Role> list=new ArrayList<Role>();
			list.addAll(roleSet);
			return list;
		}
	}
	@Override
	@Transactional
	public Map<UserGroup,Set<Role>> listAvailableRolesByUser(User user) {
		Set<UserGroup> userGroups=user.getUserGroups();
		Set<Long> inIds=new HashSet<Long>();
		for(UserGroup ug:userGroups){
			inIds.add(ug.getId());
		}
		Map<UserGroup,Set<Role>> groupRoles=new HashMap<UserGroup,Set<Role>>();
		User cu=HttpAuthnUtils.getLoginUser();
		if(!cu.getUserName().equals(SecurityConstants.SYSTEM_DEFAULT_ADMIN)){//如果是系统管理员就不用管了，全部列出，否则得判定当前用户是哪些组的管理员
			//Set<Long> ugs=this.listGroupIdsForPermission(cu.getId());//当前用户在哪些组是管理员
			Set<Long> ugs=this.permissionService.listGroupIdsForPermission(cu, PermissionConstants.USER_ROLE_PERMISSION);
			for(Long id:inIds){
				if(!ugs.contains(id))
					userGroups.remove(userGroupService.get(id));
			}
		}
		for(UserGroup ug:userGroups){
			if(!ug.isDisabled())
				groupRoles.put(ug, ug.getRoles());
		}
		return groupRoles;
	}

//	@Override
//	public Set<Long> listGroupManagerByUser(Long userId){
//		Set<Long> groups=new HashSet<Long>();
//		User user=this.userService.get(userId);
//		if(user!=null){
//			List<Session> sessionList = new ArrayList<Session>();
//			try {
//				sessionList = sessionManager.findSessionsByUser(userId);
//			} catch (AuthorizationException e) {
//				throw new TRSOMException(e.getMessage());
//			}
//			Set<Long> cuGroupManagerSet=new HashSet<Long>(); //当前用户在哪些组是组管理员
//			for(int i=0;i<sessionList.size();i++){
//				Session mysession=sessionList.get(i);
//				Role role=this.getRole(mysession.getRoleId());
//				if(role!=null&&role.getName().equals(RoleService.DEFAULT_GROUP_ADMIN))
//					cuGroupManagerSet.add(mysession.getGroupId());
//			}
//		}
//		return groups;
//	}
	@Override
	@Transactional
	public Map<UserGroup,Set<Role>> listRolesByUser(User user) {
		Set<UserGroup> userGroups=user.getUserGroups();
		Map<UserGroup,Set<Role>> groupRoles=new HashMap<UserGroup,Set<Role>>();
		User cu=HttpAuthnUtils.getLoginUser();
		if(!cu.getUserName().equals(SecurityConstants.SYSTEM_DEFAULT_ADMIN)){//如果是系统管理员就不用管了，全部列出，否则得判定当前用户是哪些组的管理员
			Set<Long> ugs=this.permissionService.listGroupIdsForPermission(cu, PermissionConstants.USER_ROLE_PERMISSION);//.listGroupManagerByUser(cu.getId());
			for(UserGroup ug:userGroups){
				if(!ugs.contains(ug.getId()))
					userGroups.remove(ug);
			}
		}
		for(UserGroup ug:userGroups){
			List<Session> sessionList = new ArrayList<Session>();
			try {
				sessionList = sessionManager.findSessionsByUserAndGroup(user.getId(),ug.getId());
			} catch (AuthorizationException e) {
				throw new TRSOMException(e.getMessage());
			}
			Set<Role> roleSet=new HashSet<Role>();
			for(int i=0;i<sessionList.size();i++){
				Session mysession=sessionList.get(i);
				Role role=this.getRole(mysession.getRoleId());
				roleSet.add(role);
			}
			groupRoles.put(ug, roleSet);
		}
		return groupRoles;
	}
	
	public List<User> listUsersByRoleName(String roleName){
		Role role = this.getRoleByName(roleName);
		List<Session> sessionList = new ArrayList<Session>();
		try {
			sessionList = sessionManager.findSessions(null,role.getId(),null);
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<Long> inIds = new HashSet<Long>();
		for(Session session:sessionList){
			inIds.add(session.getUserId());
		}
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setInIds(inIds);
		return userService.listUsers(userCriterion);
	}
	
	public boolean checkUserAndRole(Long userId,String roleName){
		Role role = this.getRoleByName(roleName);
		List<Session> sessionList = new ArrayList<Session>();
		try {
			sessionList = sessionManager.findSessions(userId,role.getId(),null);
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sessionList.size()>0;
	}
	public PermissionService getPermissionService() {
		return permissionService;
	}
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
}
