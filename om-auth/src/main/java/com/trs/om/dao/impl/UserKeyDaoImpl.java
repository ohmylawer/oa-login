package com.trs.om.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.trs.om.bean.User;
import com.trs.om.bean.UserKey;
import com.trs.om.dao.UserKeyDao;

public class UserKeyDaoImpl extends GenericHibernateDAO<UserKey, Long>
		implements UserKeyDao {

	public UserKey findByUserId(Long userId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(UserKey.class);
		criteria.add(Restrictions.eq("userId", userId));
		return (UserKey) criteria.uniqueResult();
	}

	public UserKey findByUserName(String username) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(UserKey.class);
		DetachedCriteria dc=DetachedCriteria.forClass(User.class);
		dc.add(Restrictions.eq("userName", username));
		dc.setProjection(Projections.id());
		criteria.add(Subqueries.propertyIn("userId", dc));
		return (UserKey) criteria.uniqueResult();
	}



}
