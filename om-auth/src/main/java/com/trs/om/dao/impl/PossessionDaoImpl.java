package com.trs.om.dao.impl;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.Possession;
import com.trs.om.bean.User;
import com.trs.om.bean.UserGroup;
import com.trs.om.common.RestrictionsUtils;
import com.trs.om.dao.PossessionDao;
import com.trs.om.util.PagedArrayList;

public class PossessionDaoImpl extends GenericHibernateDAO<Possession, Long> implements PossessionDao {
	private SessionFactory sessionFactory;
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public PagedArrayList<Possession> find(String name,Long groupId,
			PageCriterion pageCriterion) {
		Session session=sessionFactory.getCurrentSession();
		Criteria c=session.createCriteria(Possession.class);
		if(StringUtils.isNotBlank(name))
			c.add(RestrictionsUtils.like("possessionName",name,MatchMode.ANYWHERE));
		if(groupId!=null&&groupId!=0l){
			c.add(Restrictions.eq("userGroup.id",groupId));
		}
		return this.findByPage(c, pageCriterion);
	}

	public Possession getByName(String name) {
		Session session=sessionFactory.getCurrentSession();
		List list=session.createCriteria(Possession.class).add(Restrictions.eq("possessionName", name)).list();
		if(list.isEmpty())
			return null;
		else
			return (Possession) list.get(0);
	}


	public List<Possession> findByGroupIds(List<Long> groupIds) {
		Session session=sessionFactory.getCurrentSession();
		List list=session.createCriteria(Possession.class).add(Restrictions.in("userGroup.id", groupIds)).list();
		return list;
	}

}
