package com.trs.om.api.ws.synuser;

import org.springframework.transaction.annotation.Transactional;

public class GroupOrgServiceImpl implements GroupOrgService {
	private GroupOrgDao groupOrgDao;

	@Override
	@Transactional
	public boolean add(GroupOrg go) {
		// TODO Auto-generated method stub
		return groupOrgDao.add(go);
	}

	@Override
	@Transactional
	public boolean query(String orgid) {
		// TODO Auto-generated method stub
		return groupOrgDao.query(orgid);
	}

	@Override
	@Transactional
	public String queryforgroupid(String orgid) {
		return groupOrgDao.queryforgroupid(orgid);
	}

	public GroupOrgDao getGroupOrgDao() {
		return groupOrgDao;
	}

	public void setGroupOrgDao(GroupOrgDao groupOrgDao) {
		this.groupOrgDao = groupOrgDao;
	}

	@Override
	@Transactional
	public GroupOrg addForGroupOrg(GroupOrg go) {
		return groupOrgDao.addForGroupOrg(go);
	}

}
