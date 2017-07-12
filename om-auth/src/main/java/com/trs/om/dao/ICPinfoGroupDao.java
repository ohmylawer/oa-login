package com.trs.om.dao;

import java.util.List;

import com.trs.om.bean.ICPinfoGroup;

public interface ICPinfoGroupDao extends GenericDAO<ICPinfoGroup, Integer> {
	List<ICPinfoGroup> findUsedGroups();
}
