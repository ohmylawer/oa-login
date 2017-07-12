package com.trs.om.service;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.trs.om.bean.Role;
import com.trs.om.bean.User;
import com.trs.om.bean.UserGroup;
import com.trs.om.bean.UserGroupCriterion;
import com.trs.om.util.PagedArrayList;

public interface UserGroupService {
	/**
	 * 用户组全名中各组名间分隔符
	 * */
	String GROUP_NAME_SEPERATOR="/";
	//list=====================================================================================
	PagedArrayList<UserGroup> listAll(int page,int limit);
	PagedArrayList<UserGroup> listAll(String searchUserGroupName,int page,int limit);
	/**
	 * 获取所有的用户组。
	 * @return 用户组列表。
	 */
	List<UserGroup> listAll();
	/**
	 * 列出一级用户组
	 * @return 一级用户组列表。
	 */
	List<UserGroup> listMainGroups();
	/**
	 * 列出子用户组
	 * @return 子用户组列表。
	 */
	List<UserGroup> listSubGroups(Long id);
	/**
	 * 列出用户组
	 * @return 用户组列表。
	 */
	List<UserGroup> listGroups(UserGroupCriterion userGroupCriterion);
	/**
	 * 根据用户组id列出用户组
	 * @return 用户组列表。
	 */
	List<UserGroup> listGroups(Collection<Long> groupIds);
	/**查询该用户所在的所有组
	 * @param page
	 * @param limit
	 * @param userId
	 * @return
	 */
	PagedArrayList<UserGroup> listByUserId(int page,int limit,Long userId);
	/**
	 * 列出角色的用户组配置
	 * @param user
	 * */
	List<UserGroup> listGroupsByRole(Role role);
	/**
	 * 列出该用户组对哪些用户组可见
	 * @param fromId 用户组id
	 * */
	List<UserGroup> listVisibleToGroups(Long fromId);
	/**
	 * 列出该用户组可以看到哪些用户组
	 * @param toId 用户组id
	 * */
	List<UserGroup> listVisibleGroups(Long toId);
	//get============================================================
	/**
	 * 判断用户组是否被停用，该组或其上级组被停用时返回true
	 * */
	boolean isGroupDisabled(UserGroup userGroup);
	/**
	 * 上级用户组是否停用
	 * @param userGroup用户组
	 * */
	boolean isUpperGroupDisabled(UserGroup userGroup);
	/**
	 * 根据用户组名称获得用户组
	 * @param groupName
	 * @return
	 * UserGroup
	 */
	UserGroup getByName(String groupName);
	/**
	 * 根据用户组完整路径获得用户组
	 * @param path 用户组完整路径
	 * @return
	 */
	UserGroup getByFullPath(String path);
	/**
	 * 根据上级用户组id和用户组名称获得用户组
	 * @param groupName
	 * @param parentId 父节点id
	 * @return
	 * UserGroup
	 */
	UserGroup getByNameAndParentId(String groupName,Long parentId);
	/**查询该组下所有的用户
	 * @param page
	 * @param GroupName
	 * @return
	 */
	PagedArrayList<User> getUsersByUserGroup(int page,  String groupName);
	/**
	 * 在用户组id集合中找出其中级别最高的组（没有上下级关系的组之间不区分级别）
	 *
	 * */
	Set<UserGroup> findTopGroups(Set<Long> groupIds);
	/**
	 * 判断用户组在其兄弟组中是否同名
	 * @param userGroup
	 * */
	boolean isNameExistedInSublings(UserGroup userGroup);
	/**
	 * 获得用户组全名，即包含各上级用户组的名称，用GROUP_NAME_SEPERATOR分隔
	 * @param  userGroup 用户组
	 * */
	String getGroupFullName(UserGroup userGroup);

	//========================================================================
	//void saveOrUpdate(UserGroup userGroup);
	UserGroup addUserGroup(UserGroup userGroup);
	UserGroup updateUserGroup(UserGroup userGroup);
	UserGroup get(Long id);
	void delete(UserGroup userGroup);

	void deleteGroupsByIds(String[] groupIds);
	/**根据组id停用只在本组的用户，并记录操作
	 * @param userGroupId
	 * void
	 */
	void disableUsersByGroupId(Long userGroupId);
	/**启用所有在本组的用户，并记录操作
	 * @param userGroupId
	 * void
	 */
	void enableUsersByGroupId(Long userGroupId);

	boolean alignGroup(Long userGroupId, Long refId, String type);
	/**批量删除用户组,这里将会删除用户组及其下所有子孙用户组
	 * @param groupIds 由用户组id链接成的字符串，","分隔
	 */
	void deleteGroupsByIdsString(String groupIds);
	/**批量停用用户组(多级用户组情形)
	 * @param userGroupIds 由用户组id链接成的字符串，","分隔
	 */
	void disableGroupsByIdsString(String userGroupIds);
	/**
	 * 批量启用用户组(多级用户组情形)
	 * @param userGroupIds 由用户组id链接成的字符串，","分隔
	 */
	void enableGroupsByIdsString(String userGroupIds);
//	/**
//	 * 批量设置用户组视野,因为比较复杂，暂不提供
//	 * @param fromIds 由用户组id链接成的字符串，","分隔，表示视野范围
//	 * @param toIds 待设置视野的用户组id链接成的字符串，","分隔
//	 */
	//void setVisibility(String fromIds, String toIds);
	/**
	 * 设置用户组视野
	 * @param fromIds 由用户组id链接成的字符串，","分隔，表示视野范围
	 * @param toId 待设置视野的用户组id
	 */
	void setVisibility(String fromIds, Long toId);
	/**
	 * 取消用户组的可见性
	 * */
	void cancelVisibility(Long fromId, Long toId);
	/**删除下放到用户组的某些角色
	 * @param roleIds
	 * @param userGroupId
	 */
	void deleteRolesInGroup(String roleIds,Long userGroupId);
	/**给指定用户组下放某些角色（用户组管理中用到）
	 * @param roleIds 角色id拼接的字符串，半角逗号分隔
	 * @param userGroupId 用户组id
	 */
	void configRoles(String roleIds, Long userGroupId);
	/**调整角色的用户组分配，（角色管理页面中角色的组配置用到）
	 * @param roleId  角色id
	 * @param groupIds  用户组id拼接的字符串，半角逗号分隔
	 */
	void configRoleToGroups(Long roleId, String groupIds);
	/**从用户组中移除用户
	 * @param userId
	 * @param groupId
	 */
	void removeUser(Long userId, Long groupId);
	/**查看该组下的用户
	 * @param userId
	 * @param groupId
	 */
	User selectUser(Long userId, Long groupId);
	/**
	 * 移除用户组中的部分角色配置
	 * @param userGroup 用户组
	 * @param removedRoles 待删除的角色配置
	 * */
	void removeGroupRoles(UserGroup userGroup, Collection<Role> removedRoles);
	/**
	 * 导出用户组数据
	 * @param ids
	 * @param configRoot，导出数据根目录
	 * @return
	 */
	void exportGroups(String ids, File configRoot);
	/**
	 * 导入用户组数据。
	 * @param type:对导入文件中包含重名数据的处理：0,导入文件如果出现重名数据则终止导入;1,重名数据不导入，利用系统中原有同名数据完成导入;2,重名数据覆盖系统中原有同名数据完成导入
	 * @param configRoot 用户组数据文件根文件夹。
	 */
	String importGroups(int type,File configRoot);
	/**
	 * 太原用户同步加的增加用户组方法不考虑插入组时附加权限
	 * @param userGroup
	 * @return
	 */
	UserGroup addUserGroupTY(UserGroup userGroup);
	List<User> getUsersByUserGroup(String groupName);
	
	String getUserGroupJson(Long selectedNodeId);
    String getUserGroupFullJson();
    String getTopGroupJson(Set<UserGroup> topGroups);
}
