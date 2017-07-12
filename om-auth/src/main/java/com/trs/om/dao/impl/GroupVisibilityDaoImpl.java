package com.trs.om.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.trs.om.bean.GroupVisibility;
import com.trs.om.dao.GroupVisibilityDao;

public class GroupVisibilityDaoImpl extends GenericHibernateDAO<GroupVisibility, Long>
	implements GroupVisibilityDao{

	@Override
	public void clearByFromId(Long fromId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(GroupVisibility.class);
		c.add(Restrictions.eq("fromId", fromId));
		List<GroupVisibility> gvs=(List<GroupVisibility>)c.list();
		for(GroupVisibility gv:gvs)
			this.makeTransient(gv);
	}
	
	@Override
	public void clearByToId(Long toId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(GroupVisibility.class);
		c.add(Restrictions.eq("toId", toId));
		List<GroupVisibility> gvs=(List<GroupVisibility>)c.list();
		for(GroupVisibility gv:gvs)
			this.makeTransient(gv);
	}
	
	@Override
	public List<GroupVisibility> listByFromId(Long fromId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(GroupVisibility.class);
		c.add(Restrictions.eq("fromId", fromId));
		return (List<GroupVisibility>)c.list();
	}

	@Override
	public List<GroupVisibility> listByToId(Long toId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(GroupVisibility.class);
		c.add(Restrictions.eq("toId", toId));
		return (List<GroupVisibility>)c.list();
	}

	@Override
	public void deleteVisibility(Long fromId, Long toId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(GroupVisibility.class);
		c.add(Restrictions.eq("fromId", fromId));
		c.add(Restrictions.eq("toId", toId));
		List<GroupVisibility> gvs=(List<GroupVisibility>)c.list();
		for(GroupVisibility gv:gvs)
			this.makeTransient(gv);
	}
}
