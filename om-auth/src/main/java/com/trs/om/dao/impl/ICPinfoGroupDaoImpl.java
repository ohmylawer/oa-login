package com.trs.om.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.trs.om.bean.ICPinfoGroup;
import com.trs.om.dao.ICPinfoGroupDao;

public class ICPinfoGroupDaoImpl extends
		GenericHibernateDAO<ICPinfoGroup, Integer> implements ICPinfoGroupDao {

	@Override
	public List<ICPinfoGroup> findUsedGroups() {
		Session session=getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(ICPinfoGroup.class);
		criteria.add(Restrictions.gt("num", 0));
		criteria.addOrder(Order.desc("ctime"));
		return criteria.list();
	}

}
