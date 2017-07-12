/**
 * 
 */
package com.trs.om.rbac.dao.hb3;

import com.trs.om.bean.Role;



/**
 * @author Administrator
 *
 */
public class RoleAccessor extends BaseDAOAccessor {

	public RoleAccessor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.dao.hb3.BaseDAOAccessor#getObjectClass()
	 */
	protected Class getObjectClass() {
		return Role.class;
	}

	/**
	 * 
	 */
	public String getAccessorName() {
		return "Role";
	}
}
