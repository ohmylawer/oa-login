package com.trs.om.dao;

import java.util.List;

import com.trs.om.bean.GroupVisibility;

public interface GroupVisibilityDao  extends GenericDAO<GroupVisibility, Long> {
	List<GroupVisibility> listByFromId(Long fromId);
	List<GroupVisibility> listByToId(Long toId);
	void clearByFromId(Long f);
	void clearByToId(Long f);
	void deleteVisibility(Long fromId, Long toId);

}
