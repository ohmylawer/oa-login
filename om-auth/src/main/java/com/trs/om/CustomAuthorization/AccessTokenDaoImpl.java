package com.trs.om.CustomAuthorization;

import com.trs.om.CustomAuthorization.bean.AccessToken;
import com.trs.om.bean.*;
import com.trs.om.common.RestrictionsUtils;
import com.trs.om.dao.ICPinfoDao;
import com.trs.om.dao.UserGroupDao;
import com.trs.om.dao.impl.GenericHibernateDAO;
import com.trs.om.generic.GenericQBCDAOImpl;
import com.trs.om.generic.QBCCriterion;
import com.trs.om.util.PagedArrayList;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("accessTokenDao")
public class AccessTokenDaoImpl extends GenericHibernateDAO<AccessToken, Long> implements AccessTokenDao {



    @Override
    public List<AccessToken> findAll(QBCCriterion qbcCriterion) {
         List<Criterion>  list =   qbcCriterion.getWhere();
        return this.findByCriteria(list.toArray(new Criterion[list.size()]));
    }


}
