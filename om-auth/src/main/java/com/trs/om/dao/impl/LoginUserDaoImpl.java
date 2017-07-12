package com.trs.om.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.trs.om.bean.LoginUser;
import com.trs.om.dao.LoginUserDao;

public class LoginUserDaoImpl implements LoginUserDao {

	private SessionFactory sessionFactory;
	public void delete(LoginUser loginUser) {
		Session session=sessionFactory.getCurrentSession();
		Query q = session.createQuery("delete from LoginUser t where t.id=?");
		if(loginUser!=null)
		{
			q.setLong(0, loginUser.getId());
			q.executeUpdate();
		}
//		if(this.get(loginUser.getId())!=null){
//			session.delete(loginUser);
//			session.flush();
//		}
	}

	public LoginUser get(Long id) {
		Session session=sessionFactory.getCurrentSession();
		return (LoginUser) session.get(LoginUser.class, id);
	}

	public LoginUser getByUserId(Long userId) {
		Session session=sessionFactory.getCurrentSession();
		List list= session.createQuery("from LoginUser lu where lu.user.id=:userId").setLong("userId", userId).setCacheable(true).list();
		if(list.isEmpty())
			return null;
		else
			return (LoginUser) list.get(0);
	}

	public LoginUser getByUserName(String userName) {
		Session session=sessionFactory.getCurrentSession();
		List list= session.createQuery("from LoginUser lu where lu.user.userName=:userName").setString("userName", userName).setCacheable(true).list();
		if(list.isEmpty())
			return null;
		else
			return (LoginUser) list.get(0);
	}

	public void save(LoginUser loginUser) {
		Session session=sessionFactory.getCurrentSession();
		session.save(loginUser);
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
