package com.trs.om.api.ws.synuser;

import com.trs.om.dao.GenericDAO;

public interface GroupOrgDao extends GenericDAO<GroupOrg, Long>{
	/**
	 * 添加组织机构与用户组的关系
	 * @param go
	 * @return
	 */
	public boolean add(GroupOrg go);
	/**
	 *通过组织机构ID查询关系表中是否已存在
	 * @param orgid
	 * @return
	 */
	public boolean query(String orgid);
	/**
	 * 通过组织机构ID查询关系表中是否已存在并返回用户组ID
	 * @param orgid
	 * @return 用户组id
	 */
	public String queryforgroupid(String orgid);
	/**
	 * 添加组织机构与用户组的关系并返回实体
	 * @param go
	 * @return
	 */
	public GroupOrg addForGroupOrg(GroupOrg go);


}
