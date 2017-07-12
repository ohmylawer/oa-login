package com.trs.om.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.trs.om.bean.UserPreference;
import com.trs.om.dao.UserPreferenceDao;

public class UserPreferenceDaoImpl implements UserPreferenceDao {
	private SessionFactory sessionFactory;


	public UserPreference getUserPreference(Long userId) {
		Session session=sessionFactory.getCurrentSession();
		Criteria c=session.createCriteria(UserPreference.class);
		c.add(Restrictions.eq("userId", userId));
		return (UserPreference) c.uniqueResult();
	}

	public void saveOrUpdate(UserPreference userPreference) {
		Session session=sessionFactory.getCurrentSession();
		session.saveOrUpdate(userPreference);
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
