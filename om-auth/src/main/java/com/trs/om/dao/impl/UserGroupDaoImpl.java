package com.trs.om.dao.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.User;
import com.trs.om.bean.UserGroup;
import com.trs.om.bean.UserGroupCriterion;
import com.trs.om.common.RestrictionsUtils;
import com.trs.om.dao.UserGroupDao;
import com.trs.om.util.PagedArrayList;

public class UserGroupDaoImpl extends GenericHibernateDAO<UserGroup, Long> implements UserGroupDao {

	public PagedArrayList<UserGroup> listAll(int dbpage,int limit) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(UserGroup.class);
		return findByPage(c, new PageCriterion(limit, dbpage), Order.desc("createDate"));
	}
	public PagedArrayList<UserGroup> listAll(String searchUserGroupName,int dbpage,int limit) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(UserGroup.class);
		if(!StringUtils.isBlank(searchUserGroupName))
			c.add(RestrictionsUtils.like("groupName", searchUserGroupName,MatchMode.ANYWHERE));
		return findByPage(c, new PageCriterion(limit, dbpage), Order.desc("createDate"));
	}
	
	@Override
	public List<UserGroup> listMainGroups() {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(UserGroup.class);
		c.add(Restrictions.or(Restrictions.eq("parentId", 0L), Restrictions.isNull("parentId")));
	//	c.add(Restrictions.eq("disabled", false));
		c.addOrder(Order.asc("position"));//按position升序排列
		return c.list();
	}
	
	@Override
	public boolean isNameExistedInSublings(UserGroup userGroup) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(UserGroup.class);
		Long parentId=userGroup.getParentId();
		Long id=userGroup.getId();
		if(parentId==null)
			c.add(Restrictions.eq("parentId", 0L));
		else
			c.add(Restrictions.eq("parentId", parentId));
		c.add(Restrictions.eq("groupName", userGroup.getGroupName()));
		if(id!=null)//不是新建组，需要排除当前组
			c.add(Restrictions.not(Restrictions.eq("id", id)));
		return c.list().size()>0;
	}

	
	@Override
	public List<UserGroup> listSubGroups(Long id){
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(UserGroup.class);
		if(id==null)
			c.add(Restrictions.or(Restrictions.isNull("parentId"), Restrictions.eq("parentId", 0L)));
		else
			c.add(Restrictions.eq("parentId", id));
		//c.add(Restrictions.eq("disabled", false));
		c.addOrder(Order.asc("position"));//按position升序排列
		return c.list();
	}
	/* (non-Javadoc)
	 * @see com.trs.om.dao.UserGroupDao#listByUserId(int, int, java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public PagedArrayList<UserGroup> listByUserId(int dbpage,int limit,Long userId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(UserGroup.class);
		Criteria cc = c.createAlias("users","u");
		if(userId!=null&&userId.intValue()!=0){
			cc.add(Restrictions.eq("u.id", userId));
		}
		Integer count=(Integer) c.setProjection(Projections.countDistinct("id")).uniqueResult();
		if(count==0){
			return new PagedArrayList<UserGroup>();
		}
		int totalPages=(count+limit-1)/limit;
		int pageNumber;
		if(dbpage<1){
			pageNumber=1;
		}else if(dbpage>totalPages){
			pageNumber=totalPages;
		}else{
			pageNumber=dbpage;
		}
		int offset=(pageNumber-1)*limit;

		ProjectionList pList=Projections.projectionList();
		pList.add(Projections.distinct(Projections.property("id")));
		pList.add(Property.forName("groupName"));
		c.setProjection(pList).setFirstResult(offset).setMaxResults(limit);
		//c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		c.addOrder(Order.desc("createDate"));

		List list=c.list();
		PagedArrayList<UserGroup> returnList = new PagedArrayList<UserGroup>();
		for(int i=0;i<list.size();i++){
			Object[] oo = (Object[])list.get(i);
			UserGroup ug = new UserGroup();
			ug.setId((Long)oo[0]);
			ug.setGroupName((String)oo[1]);
			returnList.getPageData().add(ug);
		}
		return returnList;
	}

	public UserGroup getByName(String groupName){
		List<UserGroup> list=findByCriteria(Restrictions.eq("groupName", groupName));
		UserGroup group=null;
		if(!list.isEmpty())
			group=list.get(0);
		return group;
	}
	
	@Override
	public UserGroup getByNameAndParentId(String groupName, Long parentId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c=session.createCriteria(UserGroup.class);
		c.add(Restrictions.eq("groupName", groupName));
		if(parentId==null)
			c.add(Restrictions.or(Restrictions.eq("parentId", 0),Restrictions.isNull("parentId")));
		else
			c.add(Restrictions.eq("parentId", parentId));
		List<UserGroup> list=(List<UserGroup>)c.list();
		UserGroup group=null;
		if(!list.isEmpty())
			group=list.get(0);
		return group;
	}
	
	public PagedArrayList<User> getUsersByUserGroup(int page, String groupName) {
		Session session=getSessionFactory().getCurrentSession();
		DetachedCriteria dc=DetachedCriteria.forClass(UserGroup.class,"g")
			.add(Restrictions.eq("groupName", groupName))
			.createAlias("users", "gu")
			.setProjection(Projections.property("gu.id"));
		Criteria criteria=session.createCriteria(User.class)
			.add(Subqueries.propertyIn("id", dc));
		return findByPageEx(criteria, new PageCriterion(10, page), Order.asc("userName"));
	}
	
	public List<User> getUsersByUserGroup(String groupName) {
		Session session=getSessionFactory().getCurrentSession();
		DetachedCriteria dc=DetachedCriteria.forClass(UserGroup.class,"g")
			.add(Restrictions.eq("groupName", groupName))
			.createAlias("users", "gu")
			.setProjection(Projections.property("gu.id"));
		Criteria criteria=session.createCriteria(User.class)
			.add(Subqueries.propertyIn("id", dc));
		return findAllEx(criteria, Order.asc("userName"));
	}
	
	@Override
	public List<UserGroup> listGroups(UserGroupCriterion userGroupCriterion) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = createCriteria(session, userGroupCriterion);

		if(StringUtils.isNotBlank(userGroupCriterion.getOrders())){
			Order[] orders=parseOrders(userGroupCriterion.getOrders());
			if(orders!=null){
				for(Order order:orders){
					c.addOrder(order);
				}
			}
		}else{
			c.addOrder(Order.asc("position"));//按position升序排列
		}

		return c.list();
	}
	
	@Override
	public List<UserGroup> listGroups(Collection<Long> groupIds) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(UserGroup.class);
		if(groupIds!=null&&!groupIds.isEmpty())
			c.add(Restrictions.in("id", groupIds));
		return (List<UserGroup>)c.list();
	}
	
	private Criteria createCriteria(Session session,
			UserGroupCriterion userGroupCriterion) {
		Criteria c = session.createCriteria(UserGroup.class);
		if(userGroupCriterion.getDisabled()!=null){
			c.add(Restrictions.eq("disabled", userGroupCriterion.getDisabled().booleanValue()));
		}
		//String createUserName=userGroupCriterion.getCreateUserName();
		String groupName=userGroupCriterion.getGroupName();
		Long parentId=userGroupCriterion.getParentId();
		if(parentId!=null&&parentId==0){
			c.add(Restrictions.isNull("parentId"));
		}else if(parentId!=null){
			c.add(Restrictions.eq("parentId",parentId));
		}
//		if(StringUtils.isNotBlank(createUserName)){
//			c.add(Restrictions.eq("createUserName", createUserName));
//		}
		if(StringUtils.isNotBlank(groupName)){
			c.add(RestrictionsUtils.like("groupName", groupName,MatchMode.ANYWHERE));
		}
		if(null!=userGroupCriterion.getCreateDate()){
			c.add(Restrictions.eq("createDate", userGroupCriterion.getCreateDate()));
		}else{
			if(null!=userGroupCriterion.getGtCreateDate()){
				c.add(Restrictions.gt("createDate", userGroupCriterion.getGtCreateDate()));
			}else if(null!=userGroupCriterion.getGeCreateDate()){
				c.add(Restrictions.ge("createDate", userGroupCriterion.getGeCreateDate()));
			}
			if(null!=userGroupCriterion.getLtCreateDate()){
				c.add(Restrictions.lt("createDate", userGroupCriterion.getLtCreateDate()));
			}else if(null!=userGroupCriterion.getLeCreateDate()){
				c.add(Restrictions.le("createDate", userGroupCriterion.getLeCreateDate()));
			}
		}
		if(null!=userGroupCriterion.getPosition()){
			c.add(Restrictions.eq("position", userGroupCriterion.getPosition()));
		}else{
			if(null!=userGroupCriterion.getGtPosition()){
				c.add(Restrictions.gt("position", userGroupCriterion.getGtPosition()));
			}else if(null!=userGroupCriterion.getGePosition()){
				c.add(Restrictions.ge("position", userGroupCriterion.getGePosition()));
			}
			if(null!=userGroupCriterion.getLtPosition()){
				c.add(Restrictions.lt("position", userGroupCriterion.getLtPosition()));
			}else if(null!=userGroupCriterion.getLePosition()){
				c.add(Restrictions.le("position", userGroupCriterion.getLePosition()));
			}
		}


		return c;
	}
	@Override
	public List<UserGroup> listGroupsByRole(Long roleId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(UserGroup.class,"g");
		DetachedCriteria dc=DetachedCriteria.forClass(UserGroup.class, "g1")
			.createAlias("g1.roles", "g1rs")
			.add(Restrictions.eqProperty("g1.id", "g.id"))
			.add(Restrictions.eq("g1rs.id", roleId))
			.setProjection(Projections.id());
		criteria.add(Subqueries.exists(dc));
		return (List<UserGroup>)criteria.list();
	}
}
