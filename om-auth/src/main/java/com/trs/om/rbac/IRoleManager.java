package com.trs.om.rbac;

import java.util.List;

import com.trs.om.bean.Role;


/**
 * 负责RBAC模型中的角色管理
 * 
 */
public interface IRoleManager {

	/**
	 * 添加角色
	 * @param roleName 角色名称
	 * @param roleDescritpion 角色描述
	 * @return 添加的角色对象
	 */
	public abstract Role addNewRole(String roleName, String roleDesc);

	/**
	 * 添加角色
	 * @param role 新的角色
	 */
	public abstract void addNewRole(Role role);

	/**
	 * 删除角色，同时删除与角色有关的Session和Privilege
	 * @param roleId 待删除的角色标识
	 * @throws AuthorizationException 抛出删除过程中的异常
	 */
	public abstract void deleteRole(Long roleId)
			throws AuthorizationException;

	/**
	 * 更新角色
	 * @param roleId 待更新的角色标识
	 * @param roleName 待更新的角色名称
	 * @param roleDesc 待更新的角色描述
	 * @throws AuthorizationException 抛出更新过程中的异常
	 */
	public abstract Role updateRole(Long roleId, String roleName,
			String roleDesc) throws AuthorizationException;

	/**
	 * 更新角色
	 * @param role 待更新的角色
	 * @throws AuthorizationException 抛出更新过程中的异常
	 */
	public abstract void updateRole(Role role) throws AuthorizationException;

	/**
	 * 获取角色
	 * @param roleId 角色标识
	 * @return 角色对象
	 * @throws AuthorizationException 抛出获取过程中的异常
	 */
	public abstract Role getRole(Long roleId) throws AuthorizationException;

	/**
	 * 根据名称获取角色
	 * @param roleName 角色名称
	 * @return 角色对象
	 * @throws AuthorizationException 抛出获取过程中的异常 
	 */
	public abstract Role getRoleByName(String roleName)
			throws AuthorizationException;

	/**
	 * 获取角色列表
	 * @return 角色列表，元素为角色对象
	 * @throws AuthorizationException 抛出获取过程中的异常
	 */
	public abstract List listRoles() throws AuthorizationException;
	
	/**
	 * 
	 * @param orderBy
	 * @return
	 * @throws AuthorizationException
	 */
	public abstract List listRoles(String orderBy) throws AuthorizationException;

	/**
	 * 根据用户名获取用户相关的角色
	 * @param userId
	 * @return 角色列表，元素为角色对象
	 * @throws AuthorizationException 抛出获取过程中的异常
	 */
	public abstract List getRolesByUser(Long userId)
			throws AuthorizationException;

	/**
	 * @throws AuthorizationException 
	 * 
	 */
	public abstract void deleteAll() throws AuthorizationException;

}