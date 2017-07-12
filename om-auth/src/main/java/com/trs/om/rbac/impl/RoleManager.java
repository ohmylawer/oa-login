/**
 *
 */
package com.trs.om.rbac.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IRoleManager;
import com.trs.om.bean.Role;
import com.trs.om.rbac.dao.AuthorizationDAOException;
import com.trs.om.rbac.dao.IDAOAccessor;
import com.trs.om.rbac.dao.IDAOService;
import com.trs.om.rbac.dao.SearchFilter;

/**
 * @author Administrator
 *
 */
public class RoleManager implements IRoleManager {
	/**
	 *
	 */
	private final static Logger logger = Logger.getLogger(RoleManager.class);

	/**
	 *
	 */
	private IDAOService daoService;
	/**
	 *
	 * @param daoService
	 */
	public RoleManager(IDAOService daoService){
		this.daoService = daoService;
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#addNewRole(java.lang.String, java.lang.String)
	 */
	@Transactional
	public Role addNewRole(String roleName,String roleDesc){
		Role role = new Role();
		role.setName(roleName);
		role.setDesc(roleDesc);
		//
		addNewRole(role);
		return role;
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#addNewRole(com.trs.om.rbac.bo.Role)
	 */
	@Transactional
	public void addNewRole(Role role){
		try {
			getAccessor().insert(role);
		} catch (AuthorizationDAOException e) {
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#deleteRole(java.lang.String)
	 */
	@Transactional
	public void deleteRole(Long roleId) throws AuthorizationException{
		// 删除指定ID的角色
		try {
			getAccessor().delete(roleId);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("role.delete.failed");
		}
		//删除与此角色相关的Session，以及与此角色相关的Privilege对象
		try {
			daoService.getAccessor("Session").delete("roleId",roleId.toString());
			//
			daoService.getAccessor("Privilege").delete("roleId",roleId.toString());
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("role.delete.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#updateRole(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public Role updateRole(Long roleId,String roleName,String roleDesc) throws AuthorizationException{
		Role role = getRole(roleId);
		if ( role == null ) return null;
		role.setName(roleName);
		role.setDesc(roleDesc);
		updateRole(role);
		return role;
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#updateRole(com.trs.om.rbac.bo.Role)
	 */
	@Transactional
	public void updateRole(Role role) throws AuthorizationException{
		try {
			getAccessor().update(role);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("role.update.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#getRole(java.lang.String)
	 */
	@Transactional
	public Role getRole(Long roleId) throws AuthorizationException{
		try {
			return (Role)getAccessor().getObject(roleId);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("role.get.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#getRoleByName(java.lang.String)
	 */
	@Transactional
	public Role getRoleByName(String roleName) throws AuthorizationException{
		try {
			return (Role)getAccessor().findObject("name", roleName);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("role.getRoleByName.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#listRoles()
	 */
	@Transactional
	public List listRoles() throws AuthorizationException{
		return listRoles("id asc");
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.IRoleManager#getRolesByUser(java.lang.String)
	 */
	@Transactional
	public List getRolesByUser(Long userId) throws AuthorizationException {
		Map parameters = new HashMap();
		parameters.put("userId", userId.toString());
		try {
			return getAccessor().findObjects(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("role.getRolesByUser.failed");
		}
	}
	/**
	 *
	 * @return
	 */
	private IDAOAccessor getAccessor() {
		return daoService.getAccessor("Role");
	}

	/**
	 * @throws AuthorizationException
	 *
	 */
	@Transactional
	public void deleteAll() throws AuthorizationException {
		try {
			this.getAccessor().deleteAll();
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("role.delete.failed");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IRoleManager#listRoles(java.lang.String)
	 */
	@Transactional
	public List listRoles(String orderBy) throws AuthorizationException {
		try {
			SearchFilter sf = new SearchFilter();
			sf.setOrderBy(orderBy);
			return getAccessor().pagedObjects(sf);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("role.list.failed");
		}
	}

}
