/**
 *
 */
package com.trs.om.rbac.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IAuthorization;
import com.trs.om.rbac.IAuthorizationServer;
import com.trs.om.rbac.IPermissionManager;
import com.trs.om.rbac.IPrivilegeManager;
import com.trs.om.rbac.IRoleManager;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.bean.Permission;
import com.trs.om.bean.Privilege;
import com.trs.om.bean.Session;

/**
 * @author Administrator
 *
 */
public class AuthorizationServer implements IAuthorizationServer {
	/**
	 *
	 */
	private final static Logger logger = Logger.getLogger(AuthorizationServer.class);

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
	 * @param managerServiceClass
	 * @throws AuthorizationException
	 */
	public void start(String managerServiceClass,String daoServiceClass,Properties properties) throws AuthorizationException{

	}

	/**
	 *
	 */
	public void stop(){

	}

	@Transactional
	public int canDoAsPrivilege(Long userId, String application, String object,
			String operation) throws AuthorizationException {
		if ( application == null || object == null || operation == null ){
			throw new IllegalArgumentException("application|object|operation is null");
		}
		Permission permission = permissionManager.getPermission(application,object,operation);
		if ( null == permission ) return IAuthorization.OPERATION_DENIED;
		List sessions = sessionManager.findSessionsByUser(userId);
		for ( int i = 0 ; i < sessions.size() ; i++ ){
			Privilege previlige = privilegeManager.getPrivilege(permission.getId(),((Session)sessions.get(i)).getRoleId());
			if ( previlige != null ){
				return IAuthorization.OPERATION_ALLOWED;
			}
		}
		return IAuthorization.OPERATION_DENIED;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationServer#canDoAsPrevilige(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public int canDoAsPrivilege(Long userId, String application, String object,
			String operation, String otherPermissions) throws AuthorizationException {
		return canDoAsPrivilege(userId,application,object,operation);
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationServer#canDoAsPrevilige(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public int canDoAsPrivilege(Long userId, String application, String permission) throws AuthorizationException {
		if ( permission == null ){
			throw new IllegalArgumentException("application|object|operation is null");
		}
		//
		int index = permission.indexOf(":");
		String object = null,operation = null;
		if ( index > 0 ){
			object = permission.substring(0, index);
			operation = permission.substring(index+1,permission.length());
		}
		//
		return canDoAsPrivilege(userId,application,object,operation);
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationServer#getOperations(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public List getOperations(Long userId, String application,
			String object) throws AuthorizationException {
		if ( application == null || object == null ){
			throw new IllegalArgumentException("application|object is null");
		}
		//
		List operations = new ArrayList();
		List sessions = sessionManager.findSessionsByUser(userId);
		for ( int i = 0 ; i < sessions.size() ; i++ ){
			List previliges = privilegeManager.findPrivileges(null,((Session)sessions.get(i)).getRoleId());
			for ( int j = 0 ; j < previliges.size() ; j++ ){
				Permission permission = permissionManager.getPermission(((Privilege)previliges.get(j)).getPermissionId());
				if ( permission == null ) continue;
				if ( application.equals(permission.getApplication()) && object.equals(permission.getObject()) ){
					operations.add(permission.getOperation());
				}
			}
		}
		//
		return operations;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationServer#getPermissions(java.lang.String, java.lang.String)
	 */
	@Transactional
	public List getPermissions(Long userId, String application) throws AuthorizationException {
		if ( application == null  ){
			throw new IllegalArgumentException("application is null");
		}
		//
		List permissions = new ArrayList();
		List sessions = sessionManager.findSessionsByUser(userId);
		for ( int i = 0 ; i < sessions.size() ; i++ ){
			List previliges = privilegeManager.findPrivileges(null,((Session)sessions.get(i)).getRoleId());
			for ( int j = 0 ; j < previliges.size() ; j++ ){
				Permission permission = permissionManager.getPermission(((Privilege)previliges.get(j)).getPermissionId());
				if ( permission == null ) continue;
				if ( application.equals(permission.getApplication()) ){
					permissions.add(permission);
				}
			}
		}
		//
		return permissions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationServer#registerPermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public int registerPermission(String application, String object,
			String operation) throws AuthorizationException {
		Permission permission = permissionManager.getPermission(application, object, operation);
		if ( null != permission ){
			permission.setApplication(application);
			permission.setObject(object);
			permission.setOperation(operation);
			permissionManager.updatePermission(permission);
		}else{
			permissionManager.addNewPermission(application, object, operation);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationServer#unregisterPermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public int unregisterPermission(String application, String object,
			String operation) throws AuthorizationException {
		permissionManager.deletePermission(application,object,operation);
		return 0;
	}

	/**
	 *
	 */
	@Transactional
	public int registerPrevilige(Long roleId, String application,
			String object, String operation) throws AuthorizationException {
		Permission permission = permissionManager.getPermission(application,object,operation);
		if ( null == permission ) {
			permission = permissionManager.addNewPermission(application, object, operation);
			privilegeManager.addNewPrivilege(roleId, permission.getId());
		}
		return 0;
	}

	/**
	 *
	 */
	@Transactional
	public int unregisterPrevilige(Long roleId, String application,
			String object, String operation) throws AuthorizationException {
		Permission permission = permissionManager.getPermission(application,object,operation);
		if ( null != permission ) {
			Privilege previlige = privilegeManager.getPrivilege(permission.getId(), roleId);
			privilegeManager.deletePrivilege(previlige.getId());
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationServer#getRoles(java.lang.String)
	 */
	@Transactional
	public List getRoles(Long userId) throws AuthorizationException {
		List sessions = sessionManager.findSessionsByUser(userId);
		List roles = new ArrayList();
		for ( int i = 0 ; i < sessions.size() ; i++ ){
			roles.add(((Session)sessions.get(i)).getRoleId());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationServer#registerSession(java.lang.String, java.lang.String)
	 */
//	public int registerSession(Long user, Long role)
//			throws AuthorizationException {
//		Session session = sessionManager.getSession(user,role);
//		if ( session == null ){
//			sessionManager.addNewSession(role, user);
//		}
//		return 0;
//	}
	/**
	 *
	 */
	@Transactional
	public int unregisterSession(Long userId, Long roleId)
			throws AuthorizationException {
		Session session = sessionManager.getSession(userId,roleId,null);
		if ( session != null ){
			sessionManager.deleteSession(session.getId());
		}
		return 0;
	}

	/**
	 *
	 */
	public IPermissionManager getPermissionManager() {
		return this.permissionManager;
	}

	/**
	 *
	 */
	public IPrivilegeManager getPrivilegeManager() {
		return this.privilegeManager;
	}

	/**
	 *
	 */
	public IRoleManager getRoleManager() {
		return this.roleManager;
	}
	/**
	 *
	 */
	public ISessionManager getSessionManager() {
		return this.sessionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public void setPrivilegeManager(IPrivilegeManager privilegeManager) {
		this.privilegeManager = privilegeManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

}
