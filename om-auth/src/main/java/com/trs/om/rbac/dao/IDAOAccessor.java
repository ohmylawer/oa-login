/**
 * 
 */
package com.trs.om.rbac.dao;

import java.util.List;
import java.util.Map;

import com.trs.om.rbac.dao.AuthorizationDAOException;
import com.trs.om.rbac.dao.PagedList;
import com.trs.om.rbac.dao.SearchFilter;
import com.trs.om.bean.AuthorizationObject;

/**
 * @author Administrator
 *
 */
public interface IDAOAccessor {
	/**
	 * 
	 * @param object
	 */
	public void insert(AuthorizationObject object) throws AuthorizationDAOException;
	/**
	 * 
	 * @param objectId
	 */
	public void delete(Long objectId) throws AuthorizationDAOException;
	/**
	 * 
	 * @param object
	 */
	public void delete(AuthorizationObject object) throws AuthorizationDAOException;
	/**
	 * 
	 * @param object
	 */
	public void update(AuthorizationObject object) throws AuthorizationDAOException;
	/**
	 * 
	 * @param objectId
	 * @return
	 */
	public AuthorizationObject getObject(Long objectId) throws AuthorizationDAOException;
	
	/**
	 * 
	 * @param objectName
	 * @return
	 */
	public AuthorizationObject findObject(String fieldName,String objectName) throws AuthorizationDAOException;
	
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	public List findObjects(Map parameters) throws AuthorizationDAOException;
	
	public List findObjectsWithin(Map parameters)throws AuthorizationDAOException;
	/**
	 * 
	 * @return
	 */
	public List listObjects() throws AuthorizationDAOException;
	
	/**
	 * 
	 * @return
	 */
	public String getAccessorName();
	/**
	 * 
	 * @param parameters
	 * @throws AuthorizationDAOException 
	 */
	public void delete(Map parameters) throws AuthorizationDAOException;
	/**
	 * 
	 * @param searchFilter
	 * @return
	 * @throws AuthorizationDAOException
	 */
	public PagedList pagedObjects(SearchFilter searchFilter) throws AuthorizationDAOException;
	/**
	 * @throws AuthorizationDAOException 
	 * 
	 */
	public void deleteAll() throws AuthorizationDAOException;
	/**
	 * 
	 * @param string
	 * @param roleId
	 * @throws AuthorizationDAOException 
	 */
	public void delete(String fieldName, String fieldValue) throws AuthorizationDAOException;
	
	List findGroupBySession(String hql);


}
