package com.trs.om.service;

import java.util.Set;

import com.trs.om.bean.Permission;
import com.trs.om.bean.User;
import com.trs.om.rbac.AuthorizationException;

/**
 * 权限服务
 * @author changguanghua
 * 2012-5-11 14:41:13
 * */
public interface PermissionService {

	/**
	 * 判断用户在哪些组具有权限。
	 * 对于admin用户（不属于任何组），列出所有已经分配了该权限（具有该权限的角色）的用户组
	 * */
	 Set<Long> listGroupIdsForPermission(User user,String[] permissionString);
	 /**
	  * 列出哪些角色中授予了权限
	  * */
	 Set<Long> listRoleIdsForPermission(String[] permissionString);

	/**
	 * 根据用户Id列出某个用户拥有的权限列表
	 * @param userId
     * @return
     */
	Set<Permission> listPermissionsForUser(Long userId) throws AuthorizationException;

}
