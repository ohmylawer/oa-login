package com.trs.om.dao;

import java.util.List;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.Possession;
import com.trs.om.bean.User;
import com.trs.om.bean.UserCriterion;
import com.trs.om.util.PagedArrayList;

public interface PossessionDao extends GenericDAO<Possession, Long> {
	/**
	 * 根据用户名查询指定属地对象。
	 * @param name
	 * @return
	 */
	Possession getByName(String name);

	/**
	 * 检索满足条件的属地信息列表，并以分页形式返回。
	 *
	 * @param name 属地名称。
	 * @param groupId 用户组号
	 * @param pageCriterion 分页条件的包装对象。
	 * @return 用户信息的分页列表。
	 */
	PagedArrayList<Possession> find(String name,Long groupId,PageCriterion pageCriterion);
	/**
	 * 根据用户组列表查询全部属地
	 */
	List<Possession> findByGroupIds(List<Long> groupIds);
}
