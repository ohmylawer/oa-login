package com.trs.om.service;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.PermissionCheck;
import com.trs.om.bean.User;
import com.trs.om.bean.UserCriterion;
import com.trs.om.bean.UserGroup;
import com.trs.om.bean.UserKey;
import com.trs.om.resource.Resource;
import com.trs.om.util.PagedArrayList;

public interface UserService {

	/**
	 * 获得附加在每个组共享行后面的组下用户共享列表String
	 * @param resourceId
	 * @param groupId
	 * @param resourceType
	 * */
	String generateAppendStringForResourceUserAclByGroup(Long resourceId,Long groupId, Integer resourceType);
	/**
	 * 批量检索用户对资源的操作权限
	 * @param user 用户
	 * @param resources 待判定的资源集合（非user本人创建的资源）
	 * */
	<T extends Resource> Map<Long, PermissionCheck> batchCheckPermission(Long userId,Collection<T> resources);
	
	/**
	 * 判断用户对资源是否有某个操作权限，不必考虑该资源是否是用户创建
	 * @param user 用户资源
	 * @param operate 操作类型，参见{@link PermissionService}中的OPERATE常量
	 * @param resource 资源
	 * @return
	 * */
	<T extends Resource> boolean hasPermission(Long userId, String operate, T resource);
	/**
	 *判断用户对资源是否有某个操作权限，需要注意：该资源不应该是user创建的
	 * @param user 用户
	 * @param operate 操作类型，参见{@link PermissionService}中的OPERATE常量
	 * @param resource 资源
	 * @return
	 */
	boolean hasPermissionFromAcl(Long userId,String operate,Resource resource);

	/**
	 * 列出用户对于哪些组可以进行ACL控制
	 * @param user
	 * @param clazz
	 * */
	Set<UserGroup> listAclableGroups(Long userId,Class clazz);

//	/**
//	 * 对于传入的resourceIds，批量检查用户是否有权限。本方法传入的resourceIds是非本人创建的
//	 * @param user用户
//	 * @param operate 操作类型，参见{@link PermissionService}中的OPERATE常量
//	 * @param resources 待判定的资源集合（非user本人创建的资源）
//	 * @return
//	 * 		     有操作权限的resourceId集合
//	 * */
//	<T extends Resource> Collection<Long> pickOperatableResouceIds(User user,String operate,Collection<T> resources);

	//	/**
//	 * 找到用户可以共享的资源id列表
//	 * @param user 用户
//	 * @param resources 资源集合
//	 * @return
//	 */
//	<T extends Resource> Set<Long> batchGetShareableIds(Long userId, Collection<T> resources);

	/**
	 * 找到用户可以操作的对象id列表
	 * @param user 用户
	 * @param operate 操作，参见{@link PermissionService}中的OPERATE常量
	 * @param resoureces 待挑选的资源集合
	 * */
	<T extends Resource> Set<Long> pickOperateableResourceIds(Long userId, String operate, Collection<T> resources);

	PagedArrayList<User> getUsersByPageAndName(int page, String name);

	PagedArrayList<User> getUsersByPageAndDate(int page, String date);

	PagedArrayList<User> getUsersByPageAndName(int page, String name, String[] excludes);

	/**
	 * 列出用户组下全部用户
	 * @param groupId 用户组id
	 * */
	List<User> listUsersInGroup(Long groupId);

	/**
	 * 下拉用户组时查看用户组下所有用户时用户列表的string拼接
	 * @param groupId 用户组id
	 * */
	String generateAppendStringForGroupUsers(Long groupId);

	/**
	 * 根据用户名、分组ID、角色ID检索用户列表。
	 * @param name 用户名。
	 * @param groupId 分组ID。
	 * @param dbpage 页号。
	 * @param pageSize 每页的记录数。
	 * @return 用户列表。
	 */
	PagedArrayList<User> listUsers(String name, Long groupId, Collection<Long> excludeIds, int dbpage, int pageSize);

	/**
	 * getUsersByPageAndName的另一版本，有用户组条件in
	 * @param name
	 * @param excludes
	 * @param page
	 * @param groupIds
	 * @return
	 */
	PagedArrayList<User> getUsersByPageAndNameInGroups(String name, String[] excludes, int page, Set<Long> groupIds);

	/**
	 * 根据用户属性获得用户信息
	 * @param page
	 * @param userCriterion
	 * @return
	 */
	public PagedArrayList<User> getUsersByCriterion(int page, UserCriterion userCriterion);

	PagedArrayList<User> getUsersByPage(int page);

	List<User> getAllUsers();

	boolean deleteUser(String userName);

	User getUser(String userName);

	boolean updateUser(User user);

	boolean addUser(User user);

	/**
	 * 添加一个用户，赋予角色，并记录日志
	 * @param user
	 * @param roleIds
	 * @return String
	 */
	String addUser(User user, Long[] roleIds);

	/**
	 * 根据用户id获得用户
	 * @param userId
	 * @return User
	 */
	User get(Long userId);

	/**
	 * 批量删除用户，并记录日志
	 * @param ids
	 * @param log void
	 */
	void deleteUsersByIds(String[] ids);

	/**
	 * 更新用户信息，不包括密码
	 * @param user void
	 */
	void updateUserInfo(User user);

	/**
	 * 重设用户密码
	 * @param user
	 * @param log void
	 */
	void resetUserPassword(User user);

	// /**更新用户角色
	// * @param userId
	// * @param roleIds
	// * @param log
	// * void
	// */
	// void updateUserRoles(Long userId,Long[] roleIds);
	/**
	 * 新版更新用户角色：需要同时记录这个角色是用户在哪个组中获得的
	 * @param userId 用户id
	 * @param rolesInGroup 用户组中角色配置
	 * @param groups
	 * */
	void updateUserRoles(Long userId, Map<Long, Set<Long>> rolesInGroup, Long[] groups);

	/**
	 * 根据用户id获得该用户所在组的集合List<Long>
	 * @param userId
	 * @return String
	 */
	List<Long> getGroupIds(Long userId);

	/**
	 * 给用户配置用户组
	 * @param userId
	 * @param groupIds 1,2,3,4 void
	 */
	void configUserGroups(Long userId, String groupIds);

	/**
	 * 批量停用帐户,自动记录操作历史
	 * @param ids void
	 */

	void disableUsers(String ids);

	/**
	 * 批量重置组配置 void
	 * @param ids roleIds
	 */
	void ChangeGroupConfigs(String ids, String roleIds);

	/**
	 * 批量重置用户到期时间 boolean
	 * @param ids dueTime
	 */
	boolean changeEpireTimes(String ids, Date dueTime);

	/**
	 * 批量重置角色配置 void
	 * @param ids groupIds
	 */
	// void ChangeRoleConfigs(String ids,String roleIds);

	/**
	 * 更新用户的到期日期
	 * @param userId
	 * @param dueTime
	 * @return
	 */
	boolean changeDueTime(Long userId, Date dueTime);

	/**
	 * 批量启用帐户，记录操作历史
	 * @param ids void
	 */
	void enableUsers(String ids);

	/**
	 * 根据用户id获得用户实体（包括用户分组以及分组中的用户）[由于懒加载]
	 * @deprecated 2010-06-13
	 * @param userId
	 * @return User
	 */
	User getWithGroupsUsers(Long userId);

	/**
	 * 根据用户id获得用户实体（包括所有的用户分组以及分组中的用户）[由于懒加载]
	 * @deprecated 2010-06-13
	 * @param userId
	 * @return User
	 */
	User getWithAllGroupsUsers(Long userId);

	/**
	 * 重置用户登录次数.
	 *
	 * @param ids 以英文逗号","分隔的用户ID串。
	 */
	void resetUserRetryCount(String ids);

	/**
	 * 获取用户列表，返回指定分页.
	 *
	 * @param userCriterion 检索条件。
	 * @param pageCriterion 分页条件。
	 * @return 用户列表。
	 */
	PagedArrayList<User> listUsers(UserCriterion userCriterion, PageCriterion pageCriterion);

	/**
	 * 获取用户列表，返回指定分页.
	 *
	 * @param userCriterion 检索条件。
	 * @param pageCriterion 分页条件。
	 * @return 用户列表。
	 */
	List<User> listUsers(UserCriterion userCriterion);

	/**
	 * 导出用户数据，记录操作历史
	 * @param ids
	 * @param type:0,仅用户信息;1,仅包含用户组信息;2,仅包含用户角色信息;3同时包含用户组和用户角色信息 void
	 */
	void exportUsers(String ids, int type, File configRoot);

	/**
	 * 导入用户数据。
	 * @param type:对导入文件中包含重名数据的处理：0,导入文件如果出现重名数据则终止导入;1,重名数据不导入，利用系统中原有同名数据完成导入;2,重名数据覆盖系统中原有同名数据完成导入
	 * @param configRoot 用户数据文件根文件夹。
	 */
	String importUsers(int type, File configRoot);
	/**
	 * 导入用户数据，该用户数据是excel文件保存的
	 * @param configRoot
	 * @return
	 */
	String importUsersFromCsv( File configRoot);
	/**
	 * 校验用户信息
	 * @param user
	 * @return
	 */
	String validatorUserInfo(User user);

	String validatorUserBaseInfo(User user);

	String validatorUserPasswordInfo(User user);

	/**
	 * 列出某一组下具有某权限的所有用户
	 * */
	Set<User> listUsersInGroupWithPermission(Long groupId, String[] permissionString);

	/**
	 * 列出某一组下不具有某权限的所有用户
	 * */
	Set<User> listUsersInGroupWithoutPermission(Long groupId, String[] permissionString);

	/**
	 * 列出组下具有角色的所有用户
	 * */
	Set<User> listUsersInGroupsWithRoles(List<Long> groupIds, List<Long> roleIds);

	/**
	 * 列出某一组下具有某角色的所有用户
	 * */
	Set<User> listUsersInGroupWithRole(Long groupId, Long roleId);

	/**
	 * 列出某一组下不具有某角色的所有用户
	 * */
	Set<User> listUsersInGroupWithoutRole(Long groupId, Long roleId);

	/**
	 * 判断用户在某个组下是否有某个权限
	 * @param permissionString 权限，参见@{link PermissionService}权限数组常量定义
	 * @param user 用户
	 * @param groupId 用户组id，为null表示不限制组
	 * */
	boolean hasPermission(String[] permissionString, User user, Long groupId);

	/**
	 * 判断用户是否有某个权限
	 * @param permissionString 权限，参见@{link PermissionService}权限数组常量定义
	 * @param user 用户
	 * */
	boolean hasPermission(String[] permissionString, User user);
	/**
	 * 判断用户是否有数据权限
	 * @param userId 用户id。
	 * @param groupId 组id
	 * */
	boolean hasDataPermission(Long userId, Long groupId);

	/**
	 * 根据用户id批量获得用户名
	 * */
	Map<Long, String> batchGetUserNames(Set<Long> userIds);

	/**
	 * 根据用户id批量获得用户真实姓名
	 * */
	Map<Long, String> batchGetUserNicknames(Set<Long> userIds);
	
	PagedArrayList<User> getUsersByPageAndName(int page, String name, Set<Long> groupIds);

	UserKey getUserKeyByUserId(Long userId);

	UserKey getUserKeyByUserName(String username);

	UserKey getUserKey(Long id);

	void saveUserKey(UserKey userKey);

	/**
	 * 根据用户名批量获得用户
	 * @param userNames 用户
	 * @return
	 */
	List<User> listUserByNames(Collection<String> userNames);

	/**
	 * 检查用户帐号状态，并返回相关的状态码.
	 *
	 * @param userId 用户ID
	 * @return 表示用户状态的整型值：{@link User#SC_OK}、{@link User#SC_DISABLED}、{@link User#SC_EXPIRED}、{@link User#SC_MISSING}、{@link User#SC_DELETED}。
	 */
	int checkUserStatus(Long userId);

}
