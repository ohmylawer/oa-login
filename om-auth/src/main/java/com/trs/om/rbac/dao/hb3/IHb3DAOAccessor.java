/**
 * 
 */
package com.trs.om.rbac.dao.hb3;

import org.hibernate.SessionFactory;

import com.trs.om.rbac.dao.IDAOAccessor;

/**
 * @author Administrator
 *
 */
public interface IHb3DAOAccessor extends IDAOAccessor {
	/**
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory);
}
