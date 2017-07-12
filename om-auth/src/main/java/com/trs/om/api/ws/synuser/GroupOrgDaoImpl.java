package com.trs.om.api.ws.synuser;

import org.hibernate.Query;
import org.hibernate.Session;

import com.trs.om.dao.impl.GenericHibernateDAO;

public class GroupOrgDaoImpl extends GenericHibernateDAO<GroupOrg, Long> implements GroupOrgDao  {
	@Override
	public boolean add(GroupOrg go) {
		boolean flag=false;
		this.makePersistent(go);
		flag=true;
		return flag;
	}

	@Override
	public boolean query(String orgid) {
		boolean flag=false;
		 Session session=getSessionFactory().getCurrentSession();
		 String sql="from GroupOrg go where go.orgID=:orgID";
		 Query query=session.createQuery(sql).setString("orgID", orgid);
		 if(!query.list().isEmpty()){
			 flag=true;
		 }

		return flag;
	}

	@Override
	public String queryforgroupid(String orgid) {
		Session session=getSessionFactory().getCurrentSession();
		 String sql="from GroupOrg go where go.orgID=:orgID";
		 Query query=session.createQuery(sql).setString("orgID", orgid);
		 if(!query.list().isEmpty()){
			 GroupOrg go=(GroupOrg) query.list().get(0);
			return go.getGroupID();
		 }else{
			 return null;
		 }
	}

	@Override
	public GroupOrg addForGroupOrg(GroupOrg go) {
		return makePersistent(go);
	}

}
