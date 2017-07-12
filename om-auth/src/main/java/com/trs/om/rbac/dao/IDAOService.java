/**
 * 
 */
package com.trs.om.rbac.dao;

import java.util.Properties;

/**
 * @author Administrator
 *
 */
public interface IDAOService {

	/*
	 * 
	 */
	public IDAOAccessor getAccessor(String accessorName);
	/**
	 * 
	 */
	public void stop();
	/**
	 * 
	 * @param properties
	 */
	public void start(Properties properties);
}
