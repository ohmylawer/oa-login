package com.trs.om.dao;

import java.util.List;
import java.util.Set;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.User;
import com.trs.om.bean.UserCriterion;
import com.trs.om.util.PagedArrayList;

public interface UserDao extends GenericDAO<User, Long> {
	/**
	 * 根据用户名查询指定用户对象。
	 * @param name
	 * @return
	 */
	User getByName(String name);

	/**
	 * 检索满足条件的用户信息列表，并以分页形式返回。
	 *
	 * @param userCriterion 检索条件的包装对象。
	 * @param pageCriterion 分页条件的包装对象。
	 * @return 用户信息的分页列表。
	 */
	PagedArrayList<User> find(UserCriterion userCriterion,PageCriterion pageCriterion);
	/**
	 * 检索满足条件的用户信息列表，并返回所有结果。
	 *
	 * @param userCriterion 检索条件的包装对象。
	 * @return 用户信息列表。
	 */
	List<User> find(UserCriterion userCriterion);
	/**
	 * 根据用户组号查询用户组  同时除去当前用户
	 */
	List<User> find(Long number,String currentUser);

	boolean hasPermission(String[] permissionString, User user, Long groupId);

	List<User> batchGetUsers(Set<Long> ids);
}
