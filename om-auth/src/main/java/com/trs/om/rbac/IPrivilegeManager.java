package com.trs.om.rbac;

import java.util.List;
import java.util.Set;

import com.trs.om.bean.Privilege;


/**
 * 负责RBAC模型中的角色与许可之间的维护
 * 
 */
public interface IPrivilegeManager {

	/**
	 * 添加新的关系
	 * 
	 * @param roleId 角色标识
	 * @param permissionId 许可标识
	 * @return 新的角色与许可关系
	 * @throws AuthorizationException 抛出添加过程中发生的异常 
	 * 
	 */
	public abstract Privilege addNewPrivilege(Long roleId, Long permissionId)
			throws AuthorizationException;

	/**
	 * 根据标识删除角色授权信息
	 * 
	 * @param privilegeId 待删除的角色授权信息
	 * @throws AuthorizationException 
	 * 
	 */
	public abstract void deletePrivilege(Long privilegeId)
			throws AuthorizationException;

	/**
	 * 更新角色授权信息
	 * 
	 * @param privilege 待更新的角色授权信息
	 * 
	 * @throws AuthorizationException 抛出更新过程中发生的异常
	 * 
	 */
	public abstract void updatePrivilege(Privilege privilege)
			throws AuthorizationException;

	/**
	 * 根据参数获取角色授权信息
	 * @param permission 许可标识
	 * @param role 角色标识
	 * @return 角色授权信息对象
	 * @throws AuthorizationException 抛出获取对象过程中的异常
	 */
	public abstract Privilege getPrivilege(Long permissionId, Long roleId)
			throws AuthorizationException;

	/**
	 * 根据参数获取角色授权信息列表
	 * @param permission 许可标识
	 * @param role 角色标识
	 * @return 角色授权信息列表，元素为Privilege对象
	 * @throws AuthorizationException  抛出获取列表过程中的异常
	 */
	public abstract List findPrivileges(Long permissionId, Long roleId)
			throws AuthorizationException;

	/**
	 *根据角色Id数据获得这批角色的所有授权
	 * @param roleIds
	 * @return
	 * @throws AuthorizationException
     */
	public abstract List findPrivilegesByRoleIds(Set roleIds)
			throws AuthorizationException;


	/**
	 * @throws AuthorizationException 
	 * 
	 */
	public abstract void deleteAll() throws AuthorizationException;




}