package com.trs.om.CustomAuthorization;

import com.trs.om.CustomAuthorization.bean.AccessToken;
import com.trs.om.CustomAuthorization.bean.UserAuthorizationToken;
import com.trs.om.dao.UserGroupDao;
import com.trs.om.dao.impl.GenericHibernateDAO;
import com.trs.om.generic.GenericQBCDAOImpl;
import com.trs.om.generic.QBCCriterion;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository("userAuthorizationTokenDao")
public class UserAuthorizationTokenDaoImpl extends GenericHibernateDAO<UserAuthorizationToken, Long> implements UserAuthorizationTokenDao {


    @Override
    public List<UserAuthorizationToken> findAll(QBCCriterion qbcCriterion) {
        List<Criterion>  list =   qbcCriterion.getWhere();
        return this.findByCriteria(list.toArray(new Criterion[list.size()]));
    }
}
