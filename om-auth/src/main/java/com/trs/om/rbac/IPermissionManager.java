package com.trs.om.rbac;

import java.util.List;
import java.util.Set;

import com.trs.om.bean.Permission;

/**
 * 负责RBAC模型中的许可管理（对象与操作的集合）
 * 
 */
public interface IPermissionManager {

	/**
	 * 添加新的许可
	 * 
	 * @param application 待添加的应用名称
	 * @param object 待添加的对象标识
	 * @param operation 待添加的操作标识
	 * @return 新的许可对象
	 * @throws AuthorizationException 抛出添加许可对象过程中的异常
	 * 
	 */
	public abstract Permission addNewPermission(String application,
			String object, String operation) throws AuthorizationException;

	/**
	 * 根据许可的标识删除许可
	 * @param permissionId 待删除的许可标识
	 * @throws AuthorizationException 抛出删除许可过程中的异常
	 */
	public abstract void deletePermission(Long permissionId)
			throws AuthorizationException;

	/**
	 * 更新许可对象
	 * @param permissionId 待更新的许可标识
	 * @param application 应用名称
	 * @param object 待更新的对象标识
	 * @param operation 待更新的操作标识
	 * @throws AuthorizationException 抛出更新许可过程中的异常
	 */
	public abstract void updatePermission(Long permissionId,
			String application, String object, String operation)
			throws AuthorizationException;

	/**
	 * 根据标识获取许可
	 * @param permissionId 待获取的许可标识
	 * @return 获取的许可对象
	 * @throws AuthorizationException 抛出获取过程中的异常 
	 */
	public abstract Permission getPermission(Long permissionId)
			throws AuthorizationException;

	/**
	 * 根据参数信息获取许可对象
	 * @param application 应用名称
	 * @param object 对象标识
	 * @param operation 操作标识
	 * @return 获取的许可对象
	 * @throws AuthorizationException 抛出获取过程中的异常 
	 */
	public abstract Permission getPermission(String application, String object,
			String operation) throws AuthorizationException;

	/**
	 * 删除许可对象
	 * @param application 应用名称
	 * @param object 对象标识
	 * @param operation 操作标识
	 * @throws AuthorizationException 抛出删除过程中的异常 
	 */
	public abstract void deletePermission(String application, String object,
			String operation) throws AuthorizationException;

	/**
	 * 更新许可对象
	 * @param permission 待更新的许可对象
	 * @throws AuthorizationException 抛出更新过程中的异常
	 */
	public abstract void updatePermission(Permission permission)
			throws AuthorizationException;
	/**
	 * 获取指定条件的许可对象
	 * @param application 应用名称，此参数不允许为null
	 * @param object 对象标识，可以为null
	 * @param operation 操作标识，可以为null
	 * @return 许可对象的列表，元素为Permission对象
	 * @throws AuthorizationException 抛出获取过程中的异常
	 */
	public abstract List findPermissions(String application,String object,String operation) throws AuthorizationException;



	 List<Permission> findPermissionsByIds(Set<Long> permissionSet) throws AuthorizationException;

	/**
	 * 根据参数获取指定条件的许可对象
	 * @param application 应用名称，此参数不允许为null
	 * @param role 角色标识，此参数不允许为null
	 * @param excluded true为具备，false为不具备
	 * @return 许可对象的列表，元素为Permission对象
	 * @throws AuthorizationException 
	 */
	public abstract List findNonPermissions(String application,Long roleId) throws AuthorizationException;
	/**
	 * 删除所有的许可数据
	 * @throws AuthorizationException 
	 */
	public abstract void deleteAll() throws AuthorizationException;
}