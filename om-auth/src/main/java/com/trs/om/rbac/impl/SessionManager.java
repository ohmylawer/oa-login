/**
 *
 */
package com.trs.om.rbac.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.bean.Session;
import com.trs.om.rbac.dao.AuthorizationDAOException;
import com.trs.om.rbac.dao.IDAOAccessor;
import com.trs.om.rbac.dao.IDAOService;

/**
 * @author Administrator
 *
 */
public class SessionManager implements ISessionManager {
	/**
	 *
	 */
	private final static Logger logger = Logger.getLogger(SessionManager.class);
	/**
	 *
	 */
	private IDAOService daoService;
	/**
	 *
	 * @param daoService
	 */
	public SessionManager(IDAOService daoService){
		this.daoService = daoService;
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.ISessionManager#addNewSession(java.lang.String, java.lang.String)
	 */
//	public Session addNewSession(Long roleId,Long userId) throws AuthorizationException{
//		Session session = this.getSession(userId,roleId);
//		if ( session == null ){
//			session = new Session();
//			session.setRoleId(roleId);
//			session.setUserId(userId);
//			//
//			addNewSession(session);
//		}
//		return session;
//	}

	@Transactional
	public Session addNewSession(Long roleId,Long userId,Long groupId) throws AuthorizationException{
		Session session = this.getSession(userId,roleId,groupId);
		if ( session == null ){
			session = new Session();
			session.setRoleId(roleId);
			session.setUserId(userId);
			session.setGroupId(groupId);
			addNewSession(session);
		}
		return session;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public void addBatchNewSession(Long roleId) throws AuthorizationException {
		String hql = "FROM Session GROUP BY userId,groupId";
		List<Session> sessions = new ArrayList<Session>();
		sessions = getAccessor().findGroupBySession(hql);
		if(sessions.size() > 0){
			for (Session session : sessions) {
				if(session.getRoleId() != null  && session.getRoleId() != roleId){
					Session ses = new Session();
					ses.setUserId(session.getUserId());
					ses.setRoleId(roleId);
					ses.setGroupId(session.getGroupId());
					addNewSession(ses);
				}
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.ISessionManager#addNewSession(com.trs.om.rbac.bo.Session)
	 */
	@Transactional
	public void addNewSession(Session session) throws AuthorizationException{
		try {
			getAccessor().insert(session);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.addNew.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.ISessionManager#deleteSession(java.lang.String)
	 */
	@Transactional
	public void deleteSession(Long sessionId) throws AuthorizationException{
		try {
			getAccessor().delete(sessionId);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.delete.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.ISessionManager#updateSession(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional
	public Session updateSession(Long sessionId,Long userId,Long roleId) throws AuthorizationException{
		Session session = getSession(sessionId);
		if ( null == session ) return null;
		session.setRoleId(roleId);
		session.setUserId(userId);
		//
		updateSession(session);
		return session;
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.ISessionManager#getSession(java.lang.String)
	 */
	@Transactional
	public Session getSession(Long sessionId) throws AuthorizationException{
		try {
			return (Session)getAccessor().getObject(sessionId);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.get.failed");
		}
	}
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.ISessionManager#updateSession(com.trs.om.rbac.bo.Session)
	 */
	@Transactional
	public void updateSession(Session session) throws AuthorizationException{
		try {
			getAccessor().update(session);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.update.failed");
		}
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.ISessionManager#findSessions(java.lang.String)
	 */
	@Transactional
	public List findSessionsByUser(Long userId) throws AuthorizationException {
		return this.findSessions(userId, null, null);
	}

	@Transactional
	public List findSessionsByUserAndGroup(Long userId,Long groupId) throws AuthorizationException {
		return this.findSessions(userId, null, groupId);
	}
	/**
	 *
	 * @return
	 */
	private IDAOAccessor getAccessor() {
		return daoService.getAccessor("Session");
	}

	/* (non-Javadoc)
	 * @see com.trs.om.rbac.impl.ISessionManager#getSession(java.lang.String, java.lang.String)
	 */
/*	public Session getSession(Long userId, Long roleId) throws AuthorizationException {
		Map parameters = new HashMap();
		parameters.put("userId", userId);
		parameters.put("roleId", roleId);
		try {
			List sessions = getAccessor().findObjects(parameters);
			return sessions.size() > 0 ? (Session)sessions.get(0) : null;
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.find.failed");
		}
	}*/
	@Transactional
	public Session getSession(Long userId, Long roleId,Long groupId) throws AuthorizationException {
		Map parameters = new HashMap();
		parameters.put("userId", userId);
		parameters.put("roleId", roleId);
		parameters.put("groupId", groupId);
		try {
			List sessions = getAccessor().findObjects(parameters);
			return sessions.size() > 0 ? (Session)sessions.get(0) : null;
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.find.failed");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.ISessionManager#deleteAll()
	 */
	@Transactional
	public void deleteAll() throws AuthorizationException {
		try {
			this.getAccessor().deleteAll();
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.delete.failed");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.ISessionManager#findSessionsByRole(java.lang.String)
	 */
	@Transactional
	public List findSessionsByRole(Long roleId) throws AuthorizationException {
		return this.findSessions(null, roleId, null);
	}

	@Override
	@Transactional
	public List findSessionsByRoleAndGroup(Long roleId, Long groupId)
			throws AuthorizationException {
		return this.findSessions(null, roleId, groupId);
	}

	/**
	 *
	 * @param Long
	 * @param Long
	 * @throws AuthorizationException
	 */
	@Transactional
	public  List findSessions(Long userId,Long roleId,Long groupId) throws AuthorizationException{
		Map parameters = new HashMap();
		if(userId!=null)
			parameters.put("userId", userId);
		if(roleId!=null)
			parameters.put("roleId", roleId);
		if(groupId!=null)
			parameters.put("groupId", groupId);
		try {
			return getAccessor().findObjects(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.find.failed");
		}
	}

	@Override
	@Transactional
	public List findSessions(List<Long> userIds,List<Long> roleIds,
			List<Long> groupIds) throws AuthorizationException {
		Map parameters = new HashMap();
		if(userIds!=null&&!userIds.isEmpty())
			parameters.put("userId", userIds);
		if(roleIds!=null&&!roleIds.isEmpty())
			parameters.put("roleId", roleIds);
		if(groupIds!=null&&!groupIds.isEmpty())
			parameters.put("groupId", groupIds);
		try {
			return getAccessor().findObjectsWithin(parameters);
		} catch (AuthorizationDAOException e) {
			logger.error(e.getErrorDesc());
			throw new AuthorizationException("session.find.failed");
		}
	}

	


}
