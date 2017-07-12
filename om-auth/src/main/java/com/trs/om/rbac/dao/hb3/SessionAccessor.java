/**
 * 
 */
package com.trs.om.rbac.dao.hb3;

import com.trs.om.bean.Session;


/**
 * @author Administrator
 *
 */
public class SessionAccessor extends BaseDAOAccessor {

	public SessionAccessor() {
		super();
	}

	/**
	 * 
	 */
	protected Class getObjectClass() {
		return Session.class;
	}

	/**
	 * 
	 */
	public String getAccessorName() {
		return "Session";
	}
}
