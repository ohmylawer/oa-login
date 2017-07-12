package com.trs.om.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.trs.om.bean.DataPermission;
import com.trs.om.dao.DataPermissionDao;

/**
 * 接口{@link DataPermissionDao}的实现类。
 * @author wengjing
 *
 */
public class DataPermissionDaoImpl extends GenericHibernateDAO<DataPermission, Long> implements DataPermissionDao {

	//methods	---------------------------------------------------------------------
	public boolean isNameExisting(String name, Long excluedId) {
		Session session=getSessionFactory().getCurrentSession();
		Criteria criteria=session.createCriteria(DataPermission.class)
			.add(Restrictions.eq("name", name));
		if(excluedId!=null)
			criteria.add(Restrictions.not(Restrictions.eq("id", excluedId)));
		criteria.setProjection(Projections.rowCount());
		return ((Integer)criteria.uniqueResult())>0;
	}

	@SuppressWarnings("unchecked")
	public List<DataPermission> find(Long userId,String tableName) {
		Session session=getSessionFactory().getCurrentSession();
		String hql="select dp from DataPermission dp,Privilege pr,Session se where se.userId=:userId and dp.tableName=:tableName and dp.id=pr.permissionId and pr.roleId=se.roleId";
		Query query=session.createQuery(hql).setLong("userId", userId).setString("tableName", tableName).setCacheable(true);
		return query.list();
	}
}
