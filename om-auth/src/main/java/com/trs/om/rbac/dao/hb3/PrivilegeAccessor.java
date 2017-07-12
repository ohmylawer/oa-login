/**
 * 
 */
package com.trs.om.rbac.dao.hb3;

import com.trs.om.bean.Privilege;

/**
 * @author Administrator
 *
 */
public class PrivilegeAccessor extends BaseDAOAccessor {

	public PrivilegeAccessor() {
		super();
	}
	/**
	 * 
	 */
	protected Class getObjectClass() {
		return Privilege.class;
	}
	/**
	 * 
	 */
	public String getAccessorName() {
		return "Privilege";
	}




}
