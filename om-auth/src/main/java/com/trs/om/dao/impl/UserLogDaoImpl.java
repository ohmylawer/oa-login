package com.trs.om.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.UserLog;
import com.trs.om.common.RestrictionsUtils;
import com.trs.om.dao.UserLogDao;
import com.trs.om.util.PagedArrayList;

public class UserLogDaoImpl extends GenericHibernateDAO<UserLog, Long> implements UserLogDao {

	private Criteria buildCriteria(Session session, String userName,
			String userAct, String ip) {
		return session.createCriteria(UserLog.class).add(
				RestrictionsUtils.like("userName", userName, MatchMode.ANYWHERE))
				.add(RestrictionsUtils.like("userAct", userAct, MatchMode.ANYWHERE))
				.add(RestrictionsUtils.like("ip", ip, MatchMode.ANYWHERE));
	}

	public PagedArrayList<UserLog> list(String userName, String userAct, String ip, String sort,
			int page, int limit) {
		Session session = getSessionFactory().getCurrentSession();
		return findByPage(buildCriteria(session, userName, userAct, ip),
				new PageCriterion(limit, page),
				parseOrders(sort));
	}

	public long countTotalLogs() {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(UserLog.class)
			.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).longValue();
	}

	
}
