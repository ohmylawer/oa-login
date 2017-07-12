package com.trs.om.dao.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.Permission;
import com.trs.om.bean.Privilege;
import com.trs.om.bean.User;
import com.trs.om.bean.UserCriterion;
import com.trs.om.bean.UserGroup;
import com.trs.om.common.RestrictionsUtils;
import com.trs.om.dao.UserDao;
import com.trs.om.util.PagedArrayList;

public class UserDaoImpl extends GenericHibernateDAO<User, Long> implements UserDao {

	// methods --------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public User getByName(String name) {
		Session session=getSessionFactory().getCurrentSession();
		List list=session.createCriteria(User.class).add(Restrictions.eq("userName", name)).setCacheable(true).list();
		if(list.isEmpty())
			return null;
		else
			return (User) list.get(0);
	}
	private Criteria buildCriteria(Session session,UserCriterion userCriterion){
		Criteria criteria=session.createCriteria(User.class,"u");
		if(null!=userCriterion){
			//增加邮件检索用户
			if(null != userCriterion.getEmail() && !userCriterion.getEmail().isEmpty())
			{
				criteria.add(Restrictions.eq("email", userCriterion.getEmail()));
			}
			if(userCriterion.getUserNames()!=null&&!userCriterion.getUserNames().isEmpty()){
				criteria.add(Restrictions.in("userName", userCriterion.getUserNames()));
			}
			if(userCriterion.getInIds()!=null&&!userCriterion.getInIds().isEmpty()){
				criteria.add(Restrictions.in("id", userCriterion.getInIds()));
			}
			if(StringUtils.isNotBlank(userCriterion.getUserName())){
				criteria.add(RestrictionsUtils.like("userName", userCriterion.getUserName(),MatchMode.ANYWHERE));
			}
			if(null!=userCriterion.getExcludes()&&userCriterion.getExcludes().length>0){
				criteria.add(Restrictions.not(Restrictions.in("userName", userCriterion.getExcludes())));
			}
			if(null!=userCriterion.getUgroupId()){
				if(userCriterion.getUgroupId()<=0l){
					DetachedCriteria dc=DetachedCriteria.forClass(User.class, "u1")
						.createAlias("u1.userGroups", "u1gs")
						.add(Restrictions.eqProperty("u1.id", "u.id"))
						.add(Restrictions.isNull("u1gs.id"))
						.setProjection(Projections.id());
					criteria.add(Subqueries.exists(dc));
				}else{
					DetachedCriteria dc=DetachedCriteria.forClass(User.class, "u1")
						.createAlias("u1.userGroups", "u1gs")
						.add(Restrictions.eqProperty("u1.id", "u.id"))
						.add(Restrictions.eq("u1gs.id", userCriterion.getUgroupId()))
						.setProjection(Projections.id());
					criteria.add(Subqueries.exists(dc));
				}
			}else{

			}
			if(null!=userCriterion.getGroupIds()&&0!=userCriterion.getGroupIds().size()){
				DetachedCriteria dc=DetachedCriteria.forClass(User.class, "u1")
					.createAlias("u1.userGroups", "u1gs")
					.add(Restrictions.eqProperty("u1.id", "u.id"))
					.add(Restrictions.in("u1gs.id", userCriterion.getGroupIds()))
					.setProjection(Projections.id());
				criteria.add(Subqueries.exists(dc));
			}
			if(StringUtils.isNotBlank(userCriterion.getRoleId())){
				DetachedCriteria userNamesInRoleCriteria=DetachedCriteria.forClass(com.trs.om.bean.Session.class)
					.add(Restrictions.eq("role", userCriterion.getRoleId())).setProjection(Projections.property("user"));
				criteria.add(Subqueries.propertyIn("u.userName", userNamesInRoleCriteria));
			}

			if(null!=userCriterion.getRetryCount()){
				criteria.add(Restrictions.eq("retryCount", userCriterion.getRetryCount()));
			}else{
				if(null!=userCriterion.getGtRetryCount()){
					criteria.add(Restrictions.gt("retryCount", userCriterion.getGtRetryCount()));
				}else if(null!=userCriterion.getGeRetryCount()){
					criteria.add(Restrictions.ge("retryCount", userCriterion.getGeRetryCount()));
				}
				if(null!=userCriterion.getLtRetryCount()){
					criteria.add(Restrictions.lt("retryCount", userCriterion.getLtRetryCount()));
				}else if(null!=userCriterion.getLeRetryCount()){
					criteria.add(Restrictions.le("retryCount", userCriterion.getLeRetryCount()));
				}
			}

			if(userCriterion.isNoDueTime()){
				criteria.add(Restrictions.isNull("dueTime"));
			}else if(null!=userCriterion.getDueTime()){
				criteria.add(Restrictions.eq("dueTime", userCriterion.getDueTime()));
			}else{
				if(null!=userCriterion.getGtDueTime()){
					criteria.add(Restrictions.gt("dueTime", userCriterion.getGtDueTime()));
				}else if(null!=userCriterion.getGeDueTime()){
					criteria.add(Restrictions.ge("dueTime", userCriterion.getGeDueTime()));
				}
				if(null!=userCriterion.getLtDueTime()){
					criteria.add(Restrictions.lt("dueTime", userCriterion.getLtDueTime()));
				}else if(null!=userCriterion.getLeDueTime()){
					criteria.add(Restrictions.le("dueTime", userCriterion.getLeDueTime()));
				}
			}
			if(null!=userCriterion.getExcludeIds()&&0!=userCriterion.getExcludeIds().size()){
				criteria.add(Restrictions.not(Restrictions.in("id", userCriterion.getExcludeIds())));
			}
			if(!userCriterion.isShowAnonymous()){
				criteria.add(Restrictions.not(Restrictions.eq("userName", "anonymous")));
			}
			if (!userCriterion.isIncludeAll()){
				if (!userCriterion.isShowDeleted())
					criteria.add(Restrictions.eq("deleteTimes", 0));
				else
					criteria.add(Restrictions.eq("deleteTimes", 1));
			}
			if(userCriterion.isOredrbyCreationDate()){
				criteria.addOrder(Order.desc("creationDate"));
			}
		}
		return criteria;
	}

	public PagedArrayList<User> find(UserCriterion userCriterion,PageCriterion pageCriterion) {
		Session session=getSessionFactory().getCurrentSession();
		return findByPage(buildCriteria(session, userCriterion), pageCriterion);
	}

	public List<User> find(UserCriterion userCriterion) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria criteria=buildCriteria(session, userCriterion);
		criteria.add(Restrictions.eq("disabled",false));
		return criteria.list();
	}

	public List<User> find(Long number,String currentUser) {
		Session session=getSessionFactory().getCurrentSession();
		Query query=session.createQuery("from UserGroup as temp where temp.id=?").setParameter(0, number);
		UserGroup tempGroup=(UserGroup)query.uniqueResult();
		List<User> list=new ArrayList<User>();
		for(User temp:tempGroup.getUsers()){
			if(!temp.getUserName().equals(currentUser)){
				list.add(temp);
			}
		}
		return list;
	}

	@Override
	public boolean hasPermission(String[] permissionString, User user,
			Long groupId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(com.trs.om.bean.Session.class);
		criteria.add(Restrictions.eq("userId", user.getId()));
		if(groupId!=null)
			criteria.add(Restrictions.eq("groupId", groupId));
		DetachedCriteria permissionc=DetachedCriteria.forClass(Permission.class, "p1")
			.add(Restrictions.eq("object", permissionString[1]))
			.add(Restrictions.eq("operation", permissionString[0]))
			.setProjection(Projections.id());
		DetachedCriteria privilegec=DetachedCriteria.forClass(Privilege.class, "p2")
				.add(Subqueries.propertyIn("permissionId", permissionc))
				.setProjection(Projections.property("roleId"));
		criteria.add(Subqueries.propertyIn("roleId", privilegec));
		return ((Number)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue()>0?true:false;
	}
	@Override
	public List<User> batchGetUsers(Set<Long> ids) {
		if(ids==null||ids.isEmpty())
			return new ArrayList<User>();
		Session session=getSessionFactory().getCurrentSession();
		Criteria c=session.createCriteria(User.class);
		c.add(Restrictions.in("id", ids));
		return (List<User> )c.list();
	}
}
