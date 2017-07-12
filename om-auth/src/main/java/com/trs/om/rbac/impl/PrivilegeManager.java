/**
 *
 */
package com.trs.om.rbac.impl;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IPrivilegeManager;
import com.trs.om.bean.Privilege;
import com.trs.om.rbac.dao.AuthorizationDAOException;
import com.trs.om.rbac.dao.IDAOAccessor;
import com.trs.om.rbac.dao.IDAOService;

/**
 * @author Administrator
 *
 */
public class PrivilegeManager implements IPrivilegeManager {
	/**
	 *
	 */
	private final static Logger logger = Logger.getLogger(PrivilegeManager.class);
	/**
	 *
	 */
	private IDAOService daoService;
	/**
	 *
	 * @param IDAOService
	 */
	public PrivilegeManager(IDAOService daoService){
		this.daoService = daoService;
	}
	/**
	 *
	 * @return
	 */
	private IDAOAccessor getAccessor() {
		return daoService.getAccessor("Privilege");
	}
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPreviligeManager#deletePrevilige(java.lang.String)
	 */
	@Transactional
	public void deletePrivilege(Long previligeId) throws AuthorizationException{
		try {
			getAccessor().delete(previligeId);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("pervilige.delete.failed");
		}
	}
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPreviligeManager#updatePrevilige(com.trs.om.rbac.bo.Previlige)
	 */
	@Transactional
	public void updatePrivilege(Privilege previlige) throws AuthorizationException{
		try {
			getAccessor().update(previlige);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("pervilige.delete.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPreviligeManager#getPrevilige(java.lang.String, java.lang.String)
	 */
	@Transactional
	public Privilege getPrivilege(Long permissionId, Long roleId) throws AuthorizationException {
		Map parameters = new HashMap();
		//
		parameters.put("permissionId", permissionId);
		parameters.put("roleId", roleId);
		//
		List previliges = new ArrayList();
		try {
			previliges = getAccessor().findObjects(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("previlige.get.failed");
		}
		return ( previliges.size() > 0 ) ? (Privilege)previliges.get(0) : null;
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IPreviligeManager#getPreviliges(java.lang.String, java.lang.String)
	 */
	@Transactional
	public List findPrivileges(Long permissionId,Long roleId) throws AuthorizationException {
		Map parameters = new HashMap();
		//
		if ( permissionId != null ){
			parameters.put("permissionId", permissionId);
		}
		if ( roleId != null ){
			parameters.put("roleId", roleId);
		}
		//
		List previliges = null;
		try {
			previliges = getAccessor().findObjects(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("previlige.get.failed");
		}
		return previliges;
	}

	@Transactional
	@Override
	public List findPrivilegesByRoleIds(Set roleIds) throws AuthorizationException {
		List prviliges = null;
		Map parameters = new HashMap();
		if (  null == roleIds || roleIds.isEmpty()  ){
			return prviliges;
		}
		parameters.put("roleId",roleIds);
		try {
			prviliges = getAccessor().findObjectsWithin(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("previlige.get.failed");
		}

		return prviliges;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IPrivilegeManager#addNewPrivilege(java.lang.String, java.lang.String)
	 */
	@Transactional
	public Privilege addNewPrivilege(Long roleId, Long permissionId)
			throws AuthorizationException {
		Privilege previlige = this.getPrivilege(permissionId,roleId);
		if ( previlige == null ) {
			previlige = new Privilege();
			previlige.setRoleId(roleId);
			previlige.setPermissionId(permissionId);
			try {
				getAccessor().insert(previlige);
			} catch (AuthorizationDAOException e) {
				logger.error(e.getErrorDesc());
				throw new AuthorizationException("pervilige.add.failed");
			}
		}
		return previlige;
	}
	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IPrivilegeManager#deleteAll()
	 */
	@Transactional
	public void deleteAll() throws AuthorizationException {
		try {
			this.getAccessor().deleteAll();
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("pervilige.delete.failed");
		}
	}


}
