package com.trs.om.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.trs.om.bean.Role;
import com.trs.om.bean.User;
import com.trs.om.bean.UserGroup;


/**
 * 角色服务
 * @author jwcn
 *
 */
public interface RoleService {
	/**
	 * 系统管理员角色名，其权限不允许修改
	 */
	String DEFAULT_ADMIN="系统管理员";
	/**
	 *  组管理员角色名，其权限仅用于系统管理员修改
	 * */
	String DEFAULT_GROUP_ADMIN="组管理员";
	/**
	 * 常量，系统内置角色名称
	 */
	String[] DEFAULT_ROLE_NAMES=new String[]{"匿名用户","总编辑","普通用户","系统管理员","组管理员","事件中心管理员","简报编辑","简报审核","栏目编辑","文章编辑","文章审核","网站管理员","人物中心管理员","预警中心管理员","公共收藏管理员","基础数据库管理员","微博管理员","基础分析管理员","微博任务管理员"};
	/**
	 * 常量 "添加角色成功"
	 */
	String SUCCESS="成功";
	/**
	 * 常量 "角色已存在"
	 */
	String ROLE_EXIST="角色已存在";
	/**
	 * 常量 "系统内置角色不可修改"
	 */
	String MSG="系统内置角色不可修改";

	//====================================================
	/**根据角色id获得角色
	 * @param roleId
	 * @return
	 * Role
	 */
	Role getRole(Long roleId);
	/**角色列表
	 * @return
	 * List<Role>
	 */
	List<Role> listRoles();
	/**
	 * 列出可以分配给用户的角色，需要考虑当前登录系统的用户的角色。按组分开
	 * @param user
	 * */
	Map<UserGroup,Set<Role>> listAvailableRolesByUser(User user);
	/**列出某用户组可用的角色列表（即其直接上级组所获得的角色列表）
	 * @return
	 * List<Role>
	 */
	List<Role> listAvailableRolesByGroup(UserGroup userGroup);
	/**根据角色名称获得角色
	 * @param roleName
	 * @return
	 * Role
	 */
	Role getRoleByName(String roleName);
	/**添加新角色，并记录日志
	 * @param role
	 * @return
	 * String
	 */
	String addRole(Role role);
	/**更新用户
	 * @param role
	 * @return
	 * String
	 */
	String updateRole(Role role);
	/**删除角色
	 * @param roleIds
	 * void
	 */
	void deleteRoles(Long[] roleIds);

	void deleteGroupRoleByRole(Role role);
	/**给角色配置权限
	 * @param roleId
	 * @param accreditIds
	 * void
	 */
	void accredit(Long roleId,Long[] accreditIds);

	/**从角色中删除用户
	 * @param roleId
	 * @param userIds
	 */
	//void deleteUsersFromRole(Long roleId,Long[] userIds);
	/**从角色中删除用户
	 * @param roleId
	 * @param userIds
	 */
	void deleteUsersFromRole(Long roleId,Map<Long,Set<Long>> userIds);
	/**向角色中添加用户
	 * @param roleId
	 * @param userNames
	 */
	void addUsersToRole(Long roleId,Map<Long,Set<Long>> userIds);
//	/**向角色中添加用户
//	 * @param roleId
//	 * @param userNames
//	 */
//	void addUsersToRole(Long roleId,String[] userNames);
//	void addUsersToRole(Long roleId,Long[] userIds);
//	/**
//	 * 列出用户在哪些组是组管理员
//	 * @param userId
//	 * */
	//Set<Long> listGroupManagerByUser(Long userId);
	/**
	 * 列出用户的角色配置
	 * @param user
	 * */
	Map<UserGroup,Set<Role>> listRolesByUser(User user);
	
	/**
	 * 列出具有某个角色的用户
	 * @param roleName
	 * @return
	 */
	List<User> listUsersByRoleName(String roleName);
	
	/**
	 * 用户是否具有某个角色
	 * @param userId
	 * @param roleName
	 * @return
	 */
	boolean checkUserAndRole(Long userId,String roleName);
}
