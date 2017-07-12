package com.trs.om.CustomAuthorization;

import com.trs.om.CustomAuthorization.bean.AccessToken;
import com.trs.om.CustomAuthorization.bean.UserAuthorizationToken;
import com.trs.om.dao.UserGroupDao;
import com.trs.om.dao.impl.GenericHibernateDAO;
import com.trs.om.generic.GenericQBCService;
import com.trs.om.generic.GenericQBCServiceImpl;
import com.trs.om.generic.QBCCriterion;
import com.trs.om.util.PagedArrayList;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("accessTokenService")
public class AccessTokenServiceImpl implements AccessTokenService{

    private AccessTokenDao accessTokenDao;

    public AccessTokenDao getAccessTokenDao() {
        return accessTokenDao;
    }

    @Resource(name = "accessTokenDao")
    public void setAccessTokenDao(AccessTokenDao accessTokenDao) {
        this.accessTokenDao = accessTokenDao;
    }


    @Override
    @Transactional
    public void saveOrUpdate(AccessToken... entitys) {
        for (AccessToken entity : entitys){
            accessTokenDao.makePersistent(entity);
        }
    }

    @Override
    @Transactional
    public void delete(Long... longs) {

    }

    @Override
    @Transactional
    public void deleteByHql(String hql) {

    }

    @Override
    @Transactional
    public AccessToken findById(Long aLong) {
        return null;
    }

    @Override
    @Transactional
    public List<AccessToken> findAll() {
        return null;
    }

    @Override
    @Transactional
    public List<AccessToken> findAll(QBCCriterion qbcCriterion) {
          return   this.getAccessTokenDao().findAll(qbcCriterion);
    }

    @Override
    @Transactional
    public List<AccessToken> findAllNoInit(QBCCriterion qbcCriterions) {
        return null;
    }


    @Override
    @Transactional
    public PagedArrayList<AccessToken> findPaged(QBCCriterion qbcCriterion) {
        return null;
    }

    @Override
    @Transactional
    public long countRecords(QBCCriterion qbcCriterion) {
        return 0;
    }
}
