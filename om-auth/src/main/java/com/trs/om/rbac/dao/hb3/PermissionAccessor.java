/**
 * 
 */
package com.trs.om.rbac.dao.hb3;

import com.trs.om.bean.AuthorizationObject;
import com.trs.om.bean.Permission;
import com.trs.om.rbac.dao.AuthorizationDAOException;

/**
 * @author Administrator
 *
 */
public class PermissionAccessor extends	BaseDAOAccessor {

	/*
	 */
	public PermissionAccessor() {
		super();
	}

	/*
	 */
	protected Class getObjectClass() {
		return Permission.class;
	}

	/**
	 * 
	 */
	public String getAccessorName() {
		return "Permission";
	}


}
