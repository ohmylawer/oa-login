package com.trs.om.rbac;

import java.util.List;

import com.trs.om.bean.Session;


/**
 * 负责RBAC模型中的用户和角色的关系维护
 * 
 */
public interface ISessionManager {

	/**
	 * 设置用户的角色
	 * @param roleId 角色标识
	 * @param userId 用户标识
	 * @return 新的用户角色关系
	 * @throws AuthorizationException 抛出设置过程中的异常
	 */
//	public abstract Session addNewSession(Long roleId, Long userId)
//			throws AuthorizationException;
	/**
	 * 设置用户的角色
	 * @param roleId 角色标识
	 * @param userId 用户标识
	 * @param groupId 用户组标识
	 * @return 新的用户角色关系
	 * @throws AuthorizationException 抛出设置过程中的异常
	 */
	public abstract Session addNewSession(Long roleId, Long userId,Long groupId)
			throws AuthorizationException;
	/**
	 * 设置用户的角色
	 * @param session 新的用户角色关系
	 * @throws AuthorizationException 抛出设置过程中的异常 
	 */
	public abstract void addNewSession(Session session)
			throws AuthorizationException;

	/**
	 * 批量添加角色到用户角色关联表上，为有角色的用户添加新角色。
	 * @param roleId
	 * @throws AuthorizationException
	 */
	public abstract void addBatchNewSession(Long roleId) throws AuthorizationException;
	/**
	 * 删除用户的角色关系
	 * @param sessionId 待删除的用户角色关系
	 * @throws AuthorizationException 抛出删除过程中的异常
	 */
	public abstract void deleteSession(Long sessionId)
			throws AuthorizationException;

	/**
	 * 更新
	 * @param sessionId 用户的角色关系标识
	 * @param userId 用户id
	 * @param roleId 角色id
	 * @return 更新后的用户角色关系
	 * @throws AuthorizationException 抛出更新过程中的异常 
	 */
	public abstract Session updateSession(Long sessionId, Long userId,
			Long roleId) throws AuthorizationException;

	/**
	 * 
	 * @param sessionId
	 * @return
	 * @throws AuthorizationException 
	 */
	public abstract Session getSession(Long sessionId)
			throws AuthorizationException;

	/**
	 * 
	 * @param session
	 * @throws AuthorizationException 
	 */
	public abstract void updateSession(Session session)
			throws AuthorizationException;

	/**
	 * 
	 * @param user
	 * @return
	 * @throws AuthorizationException 
	 */
	public abstract List findSessionsByUser(Long userId)
			throws AuthorizationException;
	/**
	 * 
	 * @param user
	 * @return
	 * @throws AuthorizationException 
	 */
	public abstract List findSessionsByUserAndGroup(Long userId,Long groupId)
			throws AuthorizationException;
//	/**
//	 * 
//	 * @param userId
//	 * @param roleId
//	 * @return
//	 * @throws AuthorizationException 
//	 */
//	public abstract Session getSession(Long userId, Long roleId)
//			throws AuthorizationException;
	/**
	 * 
	 * @param userId
	 * @param roleId
	 * @param groupId
	 * @return
	 * @throws AuthorizationException 
	 */
	public abstract Session getSession(Long userId, Long roleId,Long groupId)
			throws AuthorizationException;
	/**
	 * @throws AuthorizationException 
	 * 
	 */
	public abstract void deleteAll() throws AuthorizationException;

	/**
	 * 
	 * @param Long
	 * @throws AuthorizationException 
	 */
	public abstract List findSessionsByRole(Long roleId) throws AuthorizationException;
	/**
	 * 
	 * @param Long
	 * @param Long
	 * @throws AuthorizationException 
	 */
	public abstract List findSessionsByRoleAndGroup(Long roleId,Long groupId) throws AuthorizationException;
	
	public abstract List findSessions(List<Long> userIds,List<Long> roleIds,List<Long> groupIds) throws AuthorizationException;
	
	public abstract List findSessions(Long userId,Long roleId,Long groupId) throws AuthorizationException;
	
}