/**
 *
 */
package com.trs.om.rbac.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IPermissionManager;
import com.trs.om.bean.Permission;
import com.trs.om.bean.Privilege;
import com.trs.om.rbac.dao.AuthorizationDAOException;
import com.trs.om.rbac.dao.IDAOAccessor;
import com.trs.om.rbac.dao.IDAOService;

/**
 * @author Administrator
 *
 */
public class PermissionManager implements IPermissionManager {
	/**
	 *
	 */
	private final static Logger logger = Logger.getLogger(PermissionManager.class);
	/**
	 *
	 */
	private IDAOService daoService;

	/**
	 *
	 * @param daoService
	 */
	public PermissionManager(IDAOService daoService){
		this.daoService = daoService;
	}
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPermissionManager#addNewPermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public Permission addNewPermission(String application,String object,String operation) throws AuthorizationException{
		Permission permission = new Permission();
		//
		permission.setObject(object);
		permission.setOperation(operation);
		permission.setApplication(application);
		//
		try {
			getAccessor().insert(permission);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.addNew.failed");
		}
		return permission;
	}
	/**
	 *
	 * @return
	 */
	private IDAOAccessor getAccessor() {
		return daoService.getAccessor("Permission");
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPermissionManager#deletePermission(java.lang.String)
	 */
	@Transactional
	public void deletePermission(Long permissionId) throws AuthorizationException{
		try {
			getAccessor().delete(permissionId);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.delete.failed");
		}
		try {
			daoService.getAccessor("Privilege").delete("permissionId",permissionId.toString());
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.delete.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPermissionManager#updatePermission(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public void updatePermission(Long permissionId,String application,String object,String operation) throws AuthorizationException{
		Permission permission = getPermission(permissionId);
		if ( permission == null )
			return;
		permission.setApplication(application);
		permission.setObject(object);
		permission.setOperation(operation);
		try {
			getAccessor().update(permission);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.update.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPermissionManager#getPermission(java.lang.String)
	 */
	@Transactional
	public Permission getPermission(Long permissionId) throws AuthorizationException{
		try {
			return (Permission)getAccessor().getObject(permissionId);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.get.failed");
		}
	}
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPermissionManager#getPermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public Permission getPermission(String application, String object,
			String operation) throws AuthorizationException {
		Map parameters = new HashMap();
		//
		parameters.put("application", application);
		parameters.put("object", object);
		parameters.put("operation", operation);
		//
		List permissions = new ArrayList();
		try {
			permissions = getAccessor().findObjects(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.get.failed");
		}
		return ( permissions.size() > 0 ) ? (Permission)permissions.get(0) : null;
	}
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPermissionManager#deletePermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public void deletePermission(String application, String object,
			String operation) throws AuthorizationException {
		Permission permission = this.getPermission(application, object, operation);
		if ( permission != null ){
			try {
				getAccessor().delete(permission);
			} catch (AuthorizationDAOException e) {
				logger.error(e.getErrorDesc());
				throw new AuthorizationException("permission.delete.failed");
			}
			try {
				Map parameters = new HashMap();
				parameters.put("permissionId", permission.getId());
				daoService.getAccessor("Privilege").delete(parameters);
			} catch (AuthorizationDAOException e) {
				logger.error(e.getErrorDesc());
				throw new AuthorizationException("permission.delete.failed");
			}
		}
	}
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPermissionManager#updatePermission(com.trs.om.rbac.bo.Permission)
	 */
	@Transactional
	public void updatePermission(Permission permission) throws AuthorizationException {
		try {
			getAccessor().update(permission);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.update.failed");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IPermissionManager#findPermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public List findPermissions(String application, String object,
			String operation) throws AuthorizationException {
		Map parameters = new HashMap();
		//
		if ( application != null ){
			parameters.put("application", application);
		}
		if ( object != null ){
			parameters.put("object", object);
		}
		if ( operation != null ){
			parameters.put("operation", operation);
		}
		//
		List permissions = new ArrayList();
		try {
			permissions = getAccessor().findObjects(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.get.failed");
		}
		return permissions;
	}

	@Override
	@Transactional
	public List<Permission> findPermissionsByIds(Set<Long> permissionSet) throws AuthorizationException {
		List permissions = null;
		Map parameters = new HashMap();
		if (  null == permissionSet || permissionSet.isEmpty()  ){
			return permissions;
		}
		parameters.put("id",permissionSet);
		try {
			permissions = getAccessor().findObjectsWithin(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("previlige.get.failed");
		}

		return permissions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IPermissionManager#deleteAll()
	 */
	@Transactional
	public void deleteAll() throws AuthorizationException {
		try{
			getAccessor().deleteAll();
		}catch(AuthorizationDAOException ex){
			throw new AuthorizationException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IPermissionManager#findPermissions(java.lang.String, java.lang.String, boolean)
	 */
	@Transactional
	public List findNonPermissions(String application, Long roleId) throws AuthorizationException {
		if ( logger.isDebugEnabled() ){
			logger.debug("findNonPermissions's parameters:"+ application + "," + roleId );
		}
		Map parameters = new HashMap();
		parameters.put("application", application);
		List permissions = new ArrayList();
		try {
			permissions = getAccessor().findObjects(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.get.failed");
		}
		parameters.clear();
		//
		List privileges = new ArrayList();
		try {
			parameters.put("roleId", roleId);
			privileges = daoService.getAccessor("Privilege").findObjects(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("permission.get.failed");
		}


		List outPermissions = new ArrayList();
		Set<Long> inIds=new HashSet<Long>();//所有的permission的id
		for ( int j = 0 ; j < privileges.size() ; j++ ){
			Long id=((Privilege)privileges.get(j)).getPermissionId();
			inIds.add(id);
		}

		for ( int i = 0 ; i < permissions.size(); i++ ){
			Permission permission=(Permission)permissions.get(i);
			Long permissionId = permission.getId();
			if(!inIds.contains(permissionId))
				outPermissions.add(permission);
		}
		return outPermissions;
	}
}
