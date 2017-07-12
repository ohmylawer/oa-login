/**
 * 
 */
package com.trs.om.rbac.impl;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IPermissionManager;
import com.trs.om.rbac.IPrivilegeManager;
import com.trs.om.rbac.IRoleManager;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.rbac.dao.IDAOService;

/**
 * @author Administrator
 *
 */
public class ManagerService {
	/**
	 * 
	 */
	private final static Logger logger = Logger.getLogger(ManagerService.class);
	/**
	 * 
	 */
	private IDAOService daoService;
	/**
	 * 
	 */
	private IPermissionManager permissionManager;
	/**
	 * 
	 */
	private IPrivilegeManager privilegeManager;
	/**
	 * 
	 */
	private IRoleManager roleManager;
	/**
	 * 
	 */
	private ISessionManager sessionManager;
	/**
	 * @return the permissionManager
	 */
	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}
	/**
	 * @return the previligeManager
	 */
	public IPrivilegeManager getPrivilegeManager() {
		return privilegeManager;
	}
	/**
	 * @return the roleManager
	 */
	public IRoleManager getRoleManager() {
		return roleManager;
	}
	/**
	 * @return the sessionManager
	 */
	public ISessionManager getSessionManager() {
		return sessionManager;
	}
	/**
	 * @throws AuthorizationException 
	 * 
	 */
	public void start(String daoServiceClass,Properties properties) throws AuthorizationException{
		//
		daoService = initDAOService(daoServiceClass);
		if ( null == daoService ){
			throw new AuthorizationException("Can't initialized dao service.");
		}
		daoService.start(properties);
		//
		this.sessionManager = new SessionManager(daoService);
		this.roleManager = new RoleManager(daoService);
		this.permissionManager = new PermissionManager(daoService);
		this.privilegeManager = new PrivilegeManager(daoService);
	}
	
	/**
	 * 
	 * @param daoServiceClass
	 * @return
	 */
	private IDAOService initDAOService(String daoServiceClass) {
		try {
			return (IDAOService) Class.forName(daoServiceClass).newInstance();
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	/**
	 * 
	 */
	public void stop(){
		
	}
}
