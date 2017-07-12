package com.trs.om.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.Permission;
import com.trs.om.bean.Privilege;
import com.trs.om.bean.Role;
import com.trs.om.bean.Session;
import com.trs.om.bean.User;
import com.trs.om.bean.UserGroup;
import com.trs.om.exception.TRSOMException;
import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IPermissionManager;
import com.trs.om.rbac.IPrivilegeManager;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.security.SecurityConstants;
import com.trs.om.service.EncryptService;
import com.trs.om.service.PermissionService;
import com.trs.om.service.RoleService;
import com.trs.om.service.UserGroupService;
import com.trs.otm.authorization.AuthorizationManager;

public class PermissionServiceImpl implements PermissionService{
	private IPermissionManager permissionManager;
	private IPrivilegeManager privilegeManager;
	private ISessionManager sessionManager;
	private RoleService roleService;
	private UserGroupService userGroupService;



	public PermissionServiceImpl() {
		EncryptService obj;
		try {
			obj = EncryptService.getInstance();
			obj.checkLicence();
		} catch (Exception e) {
			throw new TRSOMException("无法获得或者校验注册码："+e.getMessage(),e);
		}
	}

	@Override
	@Transactional
	public Set<Long> listGroupIdsForPermission(User user,String[] permissionString) {
		//找出哪些角色具有该权限
		Set<Long> roleIds=this.listRoleIdsForPermission(permissionString);
		Set<Long> groupIds=new HashSet<Long>();
		if(user.getUserName().equals(SecurityConstants.SYSTEM_DEFAULT_ADMIN)){
			//如果是系统管理员:找出这个角色分配到哪些组中即可
			for(Long roleId:roleIds){
				Role role=roleService.getRole(roleId);
				if(role!=null){
					List<UserGroup> groups=userGroupService.listGroupsByRole(role);//role.getUserGroups();
					for(UserGroup g:groups){
						if(!g.isDisabled())//停用了的用户组不予考虑
							groupIds.add(g.getId());
					}
				}
			}
		}else{
			for(Long roleId:roleIds){
				try {
					List<Session> list=sessionManager.findSessions(user.getId(),roleId,null);
					for(Session s:list){
						if(s.getGroupId()!=null)
							groupIds.add(s.getGroupId());
					}
					//对于停用了的用户组，在groupIds中去掉。
					Set<UserGroup> groups=new HashSet<UserGroup>();
					for(Long id:groupIds){
						UserGroup ug=userGroupService.get(id);
						groups.add(ug);
					}
					for(UserGroup ug:groups){
						if(ug.isDisabled())
							groupIds.remove(ug.getId());
					}
				} catch (AuthorizationException e) {
					e.printStackTrace();
				}
			}
		}
		return groupIds;
	}

	@Override
	@Transactional
	public Set<Long> listRoleIdsForPermission(String[] permissionString) {
		Set<Long> roleSet=new HashSet<Long>();
		try {
			Permission permission=permissionManager.getPermission(AuthorizationManager.APPLICATION, permissionString[1], permissionString[0]);
			if(permission!=null){
				List<Privilege> privileges=(List<Privilege>)privilegeManager.findPrivileges(permission.getId(), null);
				for(Privilege p:privileges){
					roleSet.add(p.getRoleId());
				}
			}
		} catch (AuthorizationException e) {
			e.printStackTrace();
		}
		return roleSet;
	}

	@Override
	public Set<Permission> listPermissionsForUser(Long userId) throws AuthorizationException {
		Set<Permission>  permissionSet = null;
		List<Privilege>  privilegeList = null;
		List<Permission> permissionList = null;
		Set<Long> roleIdSet = null;
		Set<Long> permissionIdSet = null;

		if(null == userId){
   			return  permissionSet;
		}

		List<Session>  userSessions =  sessionManager.findSessionsByUser(userId);
		if(null !=  userSessions){
			roleIdSet = new HashSet<>();
			for(Session session :  userSessions){
				roleIdSet.add(session.getRoleId());
			}
			privilegeList  = privilegeManager.findPrivilegesByRoleIds(roleIdSet);
			if(null != privilegeList){
				permissionIdSet = new HashSet<>();
				for(Privilege privilege : privilegeList){
					permissionIdSet.add(privilege.getPermissionId());
				}
				permissionList = permissionManager.findPermissionsByIds(permissionIdSet);
				for(Permission permission : permissionList){
					permissionSet.add(permission);
				}
			}
		}

		return permissionSet;
	}


	//====================================================================
	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public IPrivilegeManager getPrivilegeManager() {
		return privilegeManager;
	}

	public void setPrivilegeManager(IPrivilegeManager privilegeManager) {
		this.privilegeManager = privilegeManager;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public RoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public UserGroupService getUserGroupService() {
		return userGroupService;
	}

	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

}
