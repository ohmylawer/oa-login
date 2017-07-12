/**
 * 
 */
package com.trs.om.rbac.dao.hb3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.trs.om.bean.AuthorizationObject;
import com.trs.om.rbac.dao.AuthorizationDAOException;
import com.trs.om.rbac.dao.PagedList;
import com.trs.om.rbac.dao.SearchFilter;

/**
 * @author Administrator
 *
 */
public abstract class BaseDAOAccessor implements IHb3DAOAccessor {
	/**
	 * 
	 */
	private final static Logger logger = Logger.getLogger(BaseDAOAccessor.class); 
	
	/**
	 * 
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * 
	 */
	public BaseDAOAccessor(){
	}
	/* (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#delete(java.lang.String)
	 */
	public void delete(Long objectId) throws AuthorizationDAOException {
		//
		AuthorizationObject object = this.getObject(objectId);
		if (object == null) return;
		//
		delete(object);
	}
	
	/* (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#delete(com.trs.idm.rbac.bo.AuthorizationObject)
	 */
	public void delete(AuthorizationObject object)
			throws AuthorizationDAOException {
		Session session = sessionFactory.getCurrentSession();
		session.delete(object);
	}

	/**
	 * 
	 * @return
	 */
	protected abstract Class getObjectClass();
	
	/**
	 * 
	 * @return
	 */
	protected String getObjectIdName(){
		return "id";
	}
	
	/* (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#findObject(java.lang.String, java.lang.String)
	 */
	public AuthorizationObject findObject(String fieldName, String objectName)
			throws AuthorizationDAOException {
		List objects = new ArrayList();
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(getObjectClass()).setCacheable(true);
		criteria.add(Restrictions.eq(fieldName, objectName));
		objects = criteria.list();
		return objects.size() > 0  ? (AuthorizationObject)objects.get(0) : null;
	}

	/* (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#findObjects(java.util.Map)
	 */
	public List findObjects(Map parameters) throws AuthorizationDAOException {
		List objects = new ArrayList();
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(getObjectClass()).setCacheable(true);
		for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext(); ){
			String fieldName = (String)iterator.next();
			criteria.add(Restrictions.eq(fieldName, parameters.get(fieldName)));
		}
		objects = criteria.list();
		return objects;
	}
	
	public List findObjectsWithin(Map parameters) throws AuthorizationDAOException {
		List objects = new ArrayList();
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(getObjectClass()).setCacheable(true);
		for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext(); ){
			String fieldName = (String)iterator.next();
			criteria.add(Restrictions.in(fieldName, (Collection)parameters.get(fieldName)));
		}
		objects = criteria.list();
		return objects;
	}

	/* (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#getObject(java.lang.String)
	 */
	public AuthorizationObject getObject(Long objectId)
			throws AuthorizationDAOException {
		AuthorizationObject object = null;
		//
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(getObjectClass()).setCacheable(true);
		criteria.add(Restrictions.eq(getObjectIdName(), objectId));
		object = (AuthorizationObject)criteria.uniqueResult();
		return object;
	}

	/* (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#insert(com.trs.idm.rbac.bo.AuthorizationObject)
	 */
	public void insert(AuthorizationObject object)
			throws AuthorizationDAOException {
		Session session = sessionFactory.getCurrentSession();
		session.save(object);
	}

	/* (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#listObjects()
	 */
	public List listObjects() throws AuthorizationDAOException {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(getObjectClass()).setCacheable(true);
		return criteria.list();
	}

	/* (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#update(com.trs.idm.rbac.bo.AuthorizationObject)
	 */
	public void update(AuthorizationObject object)
			throws AuthorizationDAOException {
		Session session = sessionFactory.getCurrentSession();
		session.update(object);
	}

	/**
	 * @throws AuthorizationDAOException 
	 * 
	 */
	public void delete(Map parameters) throws AuthorizationDAOException {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(getObjectClass());
		for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext(); ){
			String fieldName = (String)iterator.next();
			criteria.add(Restrictions.eq(fieldName, parameters.get(fieldName)));
		}
	}
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#pagedObjects(com.trs.idm.rbac.dao.SearchFilter)
	 */
	public PagedList pagedObjects(SearchFilter sf) throws AuthorizationDAOException{
        StringBuffer sb = buildSQLWhere(sf);
        Session session = sessionFactory.getCurrentSession();
        //
        int total = getQueryCount(session,sb.toString(),sf);
        //
        if (sf.getOrderBy() != null) {
        	sb.append(" order by ").append(sf.getOrderBy());
        }
        List objects = getPageItems(session,sb.toString(), sf);
        //
        return new PagedList(objects,sf.getStartPos() / sf.getMaxResults(), sf.getMaxResults(), total);
        
	}
	/**
	 * 
	 * @param countQry
	 * @return
	 */
	private int getQueryCount(Session session,String whereExpression,SearchFilter sf) {
        Query countQry = session.createQuery(buildCountSQL(sf, whereExpression));
        bindParameters(countQry, sf);
        List results = countQry.list();
        if (results == null || results.size() == 0) {
            return 0;
        }
        //
        int count = 0;
        for (Iterator iter = results.iterator(); iter.hasNext();) {
        	Object obj = iter.next();
            if (!(obj instanceof Number)) continue;
            count += ((Number) obj).intValue();
        }
        return count;
	}
	/**
	 * 
	 * @param query
	 * @param sf
	 */
    private void bindParameters(Query query, SearchFilter sf) {
        final byte totalConditions = sf.getTotalConditions();
        for (int i = 0; i < totalConditions; i++) {
            Object value = sf.getValue((byte) i);
            if ("like".equals(sf.getRelationOp((byte) i))) {
                query.setParameter(sf.getPropertyName((byte) i), '%' + value.toString() + '%');
            } else if ("in".equals(sf.getRelationOp((byte) i))) {
                if (value.getClass().isArray()) {
                    query.setParameterList(sf.getPropertyName((byte) i), (Object[]) value);
                } else {
                    query.setParameterList(sf.getPropertyName((byte) i), (Collection) value);
                }
            } else if (SearchFilter.BETWEEN.equals(sf.getRelationOp((byte) i))) {
                query.setParameter(sf.getPropertyName((byte) i) + "lo", value);
                query.setParameter(sf.getPropertyName((byte) i) + "hi", sf.getBetweenValue2((byte) i));
            } else {
                query.setParameter(sf.getPropertyName((byte) i), value);
            }
        }
    }
	/**
	 * 
	 * @param query
	 * @param startPos
	 * @param maxResults
	 * @return
	 */
    private List getPageItems(Session session,String whereExpression, SearchFilter sf) {
        Query query = session.createQuery(whereExpression);
        bindParameters(query, sf);
        query.setFirstResult(sf.getStartPos());
        if (sf.getMaxResults() > 0) {
            query.setMaxResults(sf.getMaxResults());
        }
        return query.list();
    }
	/**
	 * 
	 * @param sf
	 * @param sb
	 * @return
	 */
	private String buildCountSQL(SearchFilter sf, String where) {
		StringBuffer sCountExp = new StringBuffer(256);
        sCountExp.append("select count(*) ");
        sCountExp.append(where);
        if (logger.isDebugEnabled()) {
            logger.debug(where + " [count]" + sCountExp);
        }
		return sCountExp.toString();
	}
	
	/**
	 * 
	 * @param sf
	 * @return
	 */
	private StringBuffer buildSQLWhere(SearchFilter sf) {
		StringBuffer sb = new StringBuffer(160);
        sb.append("from ").append(this.getObjectClass().getName());
        sb.append(sf.buildWhere());
		return sb;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#deleteAll()
	 */
	public void deleteAll() throws AuthorizationDAOException{
        Session session = sessionFactory.getCurrentSession();
	    Query deleteSQL = session.createQuery("delete from " + this.getObjectClass().getName());
	    deleteSQL.executeUpdate();
	}
	/*
	 * (non-Javadoc)
	 * @see com.trs.idm.rbac.dao.IDAOAccessor#delete(java.lang.String, java.lang.String)
	 */
	public void delete(String fieldName,String fieldValue) throws AuthorizationDAOException{
        Session session = sessionFactory.getCurrentSession();
        Query deleteSQL = session.createQuery(buildDeleteSQL(fieldName));
        deleteSQL.setString(fieldName, fieldValue);
        deleteSQL.executeUpdate();
	}
	/**
	 * 
	 * @param fieldName
	 * @return
	 */
	String buildDeleteSQL(String fieldName) {
		return "delete from " + this.getObjectClass().getName() + " where " + fieldName + "= :" + fieldName;
	}
	
	@Override
	public List findGroupBySession(String hql){
		Session session= this.sessionFactory.getCurrentSession();
		return session.createQuery(hql).list();
	}
}
