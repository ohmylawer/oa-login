package com.trs.om.CustomAuthorization;

import com.trs.om.CustomAuthorization.bean.AccessToken;
import com.trs.om.CustomAuthorization.bean.UserAuthorizationToken;
import com.trs.om.dao.GenericDAO;
import com.trs.om.dao.impl.GenericHibernateDAO;
import com.trs.om.generic.GenericQBCDAO;
import com.trs.om.generic.QBCCriterion;
import org.hibernate.criterion.Criterion;

import java.util.List;

public interface UserAuthorizationTokenDao extends GenericDAO<UserAuthorizationToken, Long>{


    public List<UserAuthorizationToken> findAll(QBCCriterion qbcCriterion);


}
