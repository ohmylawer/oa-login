package com.trs.om.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.trs.om.bean.ICPinfo;
import com.trs.om.bean.ICPinfoCriterion;
import com.trs.om.bean.OffsetLimit;
import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.UserGroup;
import com.trs.om.common.RestrictionsUtils;
import com.trs.om.dao.ICPinfoDao;
import com.trs.om.dao.UserGroupDao;
import com.trs.om.util.PagedArrayList;



public class ICPinfoDaoImpl extends GenericHibernateDAO<ICPinfo, Long> implements ICPinfoDao {

	private SessionFactory sessionFactory;
	private UserGroupDao userGroupDao;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public PagedArrayList<ICPinfo> findByName(int dbPage, int limit) {
		Session session = this.getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(ICPinfo.class);
		return findByPage(criteria, new PageCriterion(limit, dbPage));
	}

	public PagedArrayList<ICPinfo> findByName(String siteName, int dbPage,
			int limit) {
		Session session = this.getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(ICPinfo.class);
		criteria.add(RestrictionsUtils.like("siteName",siteName,MatchMode.ANYWHERE));
		return findByPage(criteria, new PageCriterion(limit, dbPage));
	}

	   /**
	    * 按照站点类型查询所有站点信息，add by cgh
	    */
	  public PagedArrayList<ICPinfo> list(int type, int dbpage, int limit){
			Session  session=sessionFactory.getCurrentSession();
			Criteria criteria=session.createCriteria(ICPinfo.class);
			switch (type) {
			case 1:
				criteria.add(Restrictions.eq("newsSite", true));
				break;
			case 2:
				criteria.add(Restrictions.eq("forumSite", true));
				break;
			case 3:
				criteria.add(Restrictions.eq("blogSite", true));
				break;
			case 4:
				criteria.add(Restrictions.eq("videoSite", true));
				break;
			case 5:
				criteria.add(Restrictions.eq("weiboSite", true));
				break;
			default:
				break;
			}
			return findByPage(criteria, new OffsetLimit((dbpage-1)*limit, limit));

	  }
	   /**
	    * 按照站点类型查询所有站点信息，添加境外网站敌对性质的查询，add by cgh
	    */
	  public PagedArrayList<ICPinfo> list(int type,String enemy, int dbpage, int limit){
			Session  session=sessionFactory.getCurrentSession();
			Criteria criteria=session.createCriteria(ICPinfo.class);
			switch (type) {
			case 1:
				criteria.add(Restrictions.eq("newsSite", true));
				break;
			case 2:
				criteria.add(Restrictions.eq("forumSite", true));
				break;
			case 3:
				criteria.add(Restrictions.eq("blogSite", true));
				break;
			case 4:
				criteria.add(Restrictions.eq("videoSite", true));
				break;
			case 5:
				criteria.add(Restrictions.eq("weiboSite", true));
				break;
			default:
				break;
			}
			criteria.add(Restrictions.eq("icpbaseInfo", enemy));

			return findByPage(criteria, new OffsetLimit((dbpage-1)*limit, limit));
	  }

   	/**
   	 * 列出某个责任辖区下的所有站点.
   	 *
   	 * @param userGroups the user groups
   	 * @param dbpage the dbpage
   	 * @param limit the limit
   	 * @return the paged array list
   	 */
	public PagedArrayList<ICPinfo> listDesignate(List<Long> userGroups,
			int dbpage, int limit) {
		Session session = sessionFactory.getCurrentSession();

		List<ICPinfo> list = new ArrayList<ICPinfo>();
		if (dbpage < 1) {
			dbpage = 1;
		}
		PagedArrayList<ICPinfo> pageList = new PagedArrayList<ICPinfo>();
		if (userGroups.size() < 1)
			return pageList;
		int start = (dbpage - 1) * limit;
		Criteria criteria = session.createCriteria(ICPinfo.class);
		Set<UserGroup> set = new HashSet<UserGroup>();
		for (int i = 0; i < userGroups.size(); i++) {
			if (userGroups.get(i) > 0) {
				Long id = Long.valueOf(userGroups.get(i));
				UserGroup ug = userGroupDao.findById(id, false);
				set.add(ug);
			}
		}
		criteria.add(Restrictions.in("userGroup", set.toArray()));
		criteria.addOrder(Order.asc("local"));
		// Query
		// query=session.createQuery("from ICPinfo as ss where ss.local in ?").setParameter(0,
		// userGroups.toString());
		int totalCount = criteria.list().size();
		int totalPages = (totalCount + limit - 1) / limit;
		criteria.setFirstResult(start);
		criteria.setMaxResults(limit);
		list = criteria.list();
		/*
		 * pageList.setPageNumber(dbpage); pageList.setPageData(list);
		 * pageList.setPageSize(list.size());
		 * pageList.setTotalPages(totalPages);
		 * pageList.setTotalRecords(totalCount); return pageList;
		 */
		return new PagedArrayList(list, totalCount, 0, list.size());
	}

	/**
	 * 列出某个地区的所有站点
	 */
	public PagedArrayList<ICPinfo> listLocal(List<Long> userGroups, int dbpage,
			int limit) {
		Session session = sessionFactory.getCurrentSession();
		List<ICPinfo> list = new ArrayList<ICPinfo>();
		if (dbpage < 1) {
			dbpage = 1;
		}
		PagedArrayList<ICPinfo> pageList = new PagedArrayList<ICPinfo>();
		if (userGroups.size() < 1)
			return pageList;
		int start = (dbpage - 1) * limit;
		Criteria criteria = session.createCriteria(ICPinfo.class);
		Set<UserGroup> set = new HashSet<UserGroup>();
		for (int i = 0; i < userGroups.size(); i++) {
			if (userGroups.get(i) > 0) {
				Long id = Long.valueOf(userGroups.get(i));
				UserGroup ug = userGroupDao.findById(id, false);
				set.add(ug);
			}
		}
		criteria.add(Restrictions.in("local", set.toArray()));
		criteria.addOrder(Order.asc("local"));
		// Query
		// query=session.createQuery("from ICPinfo as ss where ss.local in ?").setParameter(0,
		// userGroups.toString());
		int totalCount = criteria.list().size();
		int totalPages = (totalCount + limit - 1) / limit;
		criteria.setFirstResult(start);
		criteria.setMaxResults(limit);
		list = criteria.list();
		/*
		 * pageList.setPageNumber(dbpage); pageList.setPageData(list);
		 * pageList.setPageSize(list.size());
		 * pageList.setTotalPages(totalPages);
		 * pageList.setTotalRecords(totalCount); return pageList;
		 */
		return new PagedArrayList<ICPinfo>(list, totalCount, 0, list.size());
	}

	@Override
	public List<ICPinfo> listAllLocal(List<Long> groupIds) {
		if(groupIds==null||groupIds.isEmpty())
			return new ArrayList<ICPinfo>();
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria=session.createCriteria(ICPinfo.class);
		criteria.add(Restrictions.in("local.id", groupIds));
		return criteria.list();
	}

	/**
	 * 列出属于用户所在准属地的所有站点
	 */
	public PagedArrayList<ICPinfo> ListAffiliate(List<Long> userGroups,
			int dbpage, int limit) {
		if (userGroups.size() < 1)
			return new PagedArrayList<ICPinfo>();
		Set<UserGroup> set = new HashSet<UserGroup>();
		for (int i = 0; i < userGroups.size(); i++) {
			if (userGroups.get(i) > 0) {
				Long id = Long.valueOf(userGroups.get(i));
				UserGroup ug = userGroupDao.findById(id, false);
				set.add(ug);
			}
		}
		// 找到icp_affiliate表中userGroups元素对应的icp的sid的set
		// 根据得到的sid，findBySid
		Session session = getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(ICPinfo.class);
		Criteria cc = c.createAlias("affiliate", "af");
		cc.add(Restrictions.in("af.id", userGroups.toArray()));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List list = c.list();
		// List list=cc.setProjection(Projections.countDistinct("sid")).list();
		Integer count = (Integer) list.size();
		if (count == 0)
			return new PagedArrayList<ICPinfo>();
		int totalPages = (int) ((count + limit - 1) / limit);
		int pageNumber;
		if (dbpage < 1)
			pageNumber = 1;
		else if (dbpage > totalPages)
			pageNumber = totalPages;
		else
			pageNumber = dbpage;
		int offset = (pageNumber - 1) * limit;
		/*
		 * ProjectionList pList=Projections.projectionList();
		 * pList.add(Projections.distinct(Projections.property("sid")));
		 * cc.setProjection(pList).setFirstResult(offset).setMaxResults(limit);
		 * list=cc.list();
		 */
		// PagedArrayList<ICPinfo> returnList = new
		// PagedArrayList<ICPinfo>(limit,pageNumber,totalPages,count,null);
		PagedArrayList<ICPinfo> returnList = new PagedArrayList<ICPinfo>(list,
				count, offset, list.size());// new
											// PagedArrayList<ICPinfo>(limit,pageNumber,totalPages,count,list);
		// returnList.setPageData(new ArrayList<ICPinfo>());
		// for(int i = 0;i<list.size();i++){
		// ICPinfo icp = (ICPinfo)list.get(i);
		// returnList.getPageData().add(icp);
		// }
		return returnList;

	}

	private Criteria buildCriteria(Session session,ICPinfoCriterion criterion){
		Criteria criteria = session.createCriteria(ICPinfo.class);
		if(StringUtils.isNotBlank(criterion.likeSiteName)){
			criteria.add(RestrictionsUtils.like("siteName", criterion.likeSiteName,
					MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotBlank(criterion.likeDomeName)) {
			criteria.add(RestrictionsUtils.like("domeName", criterion.likeDomeName,
					MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(criterion.likeAddress)) {
			criteria.add(RestrictionsUtils.like("address", criterion.likeAddress,
					MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(criterion.likeIcpbaseInfo)) {
			criteria.add(RestrictionsUtils.like("icpbaseInfo", criterion.likeIcpbaseInfo,
					MatchMode.ANYWHERE));
		}

		if(StringUtils.isNotBlank(criterion.eqNationality)){
			criteria.add(Restrictions.eq("nationality", criterion.eqNationality));
		}

		if(criterion.eqLocals!=null&&criterion.eqLocals.length>0){
			criteria.add(Restrictions.in("local.id", criterion.eqLocals));
		}

		ArrayList<Criterion> siteTypes=new ArrayList<Criterion>();
		if(criterion.includeNewsSite!=null){
			if(criterion.includeNewsSite.equals(Boolean.TRUE))
				siteTypes.add(Restrictions.eq("newsSite", true));
			else
				criteria.add(Restrictions.eq("newsSite", false));
		}

		if(criterion.includeForumSite!=null){
			if(criterion.includeForumSite.equals(Boolean.TRUE))
				siteTypes.add(Restrictions.eq("forumSite", true));
			else
				criteria.add(Restrictions.eq("forumSite", false));
		}

		if(criterion.includeBlogSite!=null){
			if(criterion.includeBlogSite.equals(Boolean.TRUE))
				siteTypes.add(Restrictions.eq("blogSite", true));
			else
				criteria.add(Restrictions.eq("blogSite", false));
		}

		if(criterion.includeVideoSite!=null){
			if(criterion.includeVideoSite.equals(Boolean.TRUE))
				siteTypes.add(Restrictions.eq("videoSite", true));
			else
				criteria.add(Restrictions.eq("videoSite", false));
		}

		if(criterion.includeWeiboSite!=null){
			if(criterion.includeWeiboSite.equals(Boolean.TRUE))
				siteTypes.add(Restrictions.eq("weiboSite", true));
			else
				criteria.add(Restrictions.eq("weiboSite", false));
		}

		if(siteTypes.size()>0){
			Criterion temp=siteTypes.remove(siteTypes.size()-1);
			while(!siteTypes.isEmpty()){
				temp=Restrictions.or(temp, siteTypes.remove(siteTypes.size()-1));
			}
			criteria.add(temp);
		}

		if(criterion.eqGroupName!=null){
			if(criterion.eqGroupName.isEmpty()){
				criteria.add(Restrictions.isNull("groupName"));
			}else{
				criteria.add(Restrictions.eq("groupName", criterion.eqGroupName));
			}
		}
		Order order = Order.desc("createTime");
		criteria.addOrder(order);
		return criteria;
	}

	public PagedArrayList<ICPinfo> searchIcpinfo(ICPinfoCriterion criterion,OffsetLimit offsetLimit) {
		Session session = this.getSessionFactory().getCurrentSession();
		return findByPage(buildCriteria(session, criterion), offsetLimit);
	}

	public List<ICPinfo> searchAllIcpinfo(ICPinfoCriterion criterion) {
		Session session = this.getSessionFactory().getCurrentSession();
		Criteria criteria = buildCriteria(session, criterion);
		return criteria.list();
	}

	public ICPinfo findByRootDomainName(String root) {
		Session session = this.getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(ICPinfo.class);
		List list=criteria.add(RestrictionsUtils.like("domeName", root, MatchMode.END)).list();
		if(list.size()>0)
			return (ICPinfo)list.get(0);
		else
			return null;
	}

	public void setUserGroupDao(UserGroupDao userGroupDao) {
		this.userGroupDao = userGroupDao;
	}

	private static String NEWS ="新闻";
	private static String FORUM ="论坛";
	private static String BOLG ="博客";
	private static String VIEW ="视频";
	private static String WEIBO ="微博";
	private static String LOCAL ="国内";
	private static String FOREIGN ="国外";

	@Override
	//TODO 用触发器完成该功能，然后去掉改方法
	@Deprecated
	public List<ICPinfo> countAndAddIcpInfos() {
		Session session = this.getSessionFactory().getCurrentSession();
		String sql ="select * from (SELECT sitename,groupname FROM `group_site`)as temp where temp.sitename not in (SELECT sitename FROM `icpinfo`)";
		ScrollableResults rs = session.createSQLQuery(sql).scroll();
		List<ICPinfo> icpinfosAdd = new ArrayList<ICPinfo>();
		if(rs.first()){

			do{
				Object siteNameString =rs.get(0);
				Object groupname = rs.get(1);
				if(null!=siteNameString&&StringUtils.isNotBlank(siteNameString.toString())&&(!siteNameString.toString().equals("null"))&&(!siteNameString.toString().equals("NULL")))
				{
					String groupnameString="";
					if(null!=groupname&&StringUtils.isNotBlank(groupname.toString())&&(!groupname.toString().equals("null"))&&(!groupname.toString().equals("NULL"))){
						groupnameString =groupname.toString();
					}
					ICPinfo icp = new ICPinfo();
					icp.setSiteName(siteNameString.toString());
					if(groupnameString.contains(NEWS)){
						icp.setNewsSite(true);
					}
					if(groupnameString.contains(FORUM)){
						icp.setForumSite(true);
					}
					if(groupnameString.contains(BOLG)){
						icp.setBlogSite(true);
					}
					if(groupnameString.contains(WEIBO)){
						icp.setWeiboSite(true);
					}
					if(groupnameString.contains(VIEW)){
						icp.setVideoSite(true);
					}
					if(groupnameString.contains(LOCAL)){
						icp.setNationality("中国");
					}
					if(groupnameString.contains(FOREIGN)){
						icp.setNationality("境外");
					}
					icpinfosAdd.add(icp);
				}
			}while(rs.next());
			ICPinfo icpinfo2 = null;
			for(int m = 0;m<icpinfosAdd.size();m++){
				icpinfo2 = (ICPinfo)icpinfosAdd.get(m);
				session.save(icpinfo2);
				//将插入的对象立即写入数据库并释放内存
				if(m%10==0){
					session.flush();
					session.clear();
				}
		}
		rs.close();
		}
		return icpinfosAdd;
	}


}
