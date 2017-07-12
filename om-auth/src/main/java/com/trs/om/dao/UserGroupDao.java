package com.trs.om.dao;

import java.util.Collection;
import java.util.List;

import com.trs.om.bean.User;
import com.trs.om.bean.UserGroup;
import com.trs.om.bean.UserGroupCriterion;
import com.trs.om.util.PagedArrayList;

public interface UserGroupDao extends GenericDAO<UserGroup, Long> {

	PagedArrayList<UserGroup> listAll(int page,int limit);
	PagedArrayList<UserGroup> listAll(String searchUserGroupName,int page,int limit);
	/**查询该用户所在的所有组<br/>
	 * <font color="red"><b>返回对象中只包含id和groupName</b></font>
	 * @param page
	 * @param limit
	 * @param userId
	 * @return
	 */
	PagedArrayList<UserGroup> listByUserId(int page,int limit,Long userId);

	/**
	 * 全匹配
	 * @param groupName
	 * @return
	 * UserGroup
	 */
	UserGroup getByName(String groupName);
	
	UserGroup getByNameAndParentId(String groupName, Long parentId);
	/**根据用户组名查询改组所有用户
	 * @param page
	 * @param groupName
	 * @return
	 */
	PagedArrayList<User> getUsersByUserGroup(int page, String groupName);
	List<User> getUsersByUserGroup(String groupName);
	
	List<UserGroup> listMainGroups();
	List<UserGroup> listSubGroups(Long id);
	List<UserGroup> listGroups(UserGroupCriterion userGroupCriterion);
	
	/**
	 * 判断兄弟结点中是否有重名
	 * */
	boolean isNameExistedInSublings(UserGroup userGroup);
	
	List<UserGroup> listGroupsByRole(Long roleId);
	List<UserGroup> listGroups(Collection<Long> groupIds);

}
