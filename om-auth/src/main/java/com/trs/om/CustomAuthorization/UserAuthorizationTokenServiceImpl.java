package com.trs.om.CustomAuthorization;

import com.trs.om.CustomAuthorization.bean.AccessToken;
import com.trs.om.CustomAuthorization.bean.UserAuthorizationToken;
import com.trs.om.bean.OffsetLimit;
import com.trs.om.dao.impl.GenericHibernateDAO;
import com.trs.om.generic.GenericQBCServiceImpl;
import com.trs.om.generic.QBCCriterion;
import com.trs.om.util.PagedArrayList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Service("userAuthorizationTokenService")
public class UserAuthorizationTokenServiceImpl  implements UserAuthorizationTokenService{

    private UserAuthorizationTokenDao userAuthorizationTokenDao;

    public UserAuthorizationTokenDao getUserAuthorizationTokenDao() {
        return userAuthorizationTokenDao;
    }

    @Resource(name="userAuthorizationTokenDao")
    public void setUserAuthorizationTokenDao(UserAuthorizationTokenDao userAuthorizationTokenDao) {
        this.userAuthorizationTokenDao = userAuthorizationTokenDao;
    }


    @Override
    @Transactional
    public void saveOrUpdate(UserAuthorizationToken... entitys) {
        for (UserAuthorizationToken entity : entitys)
        userAuthorizationTokenDao.makePersistent(entity);
    }

    @Override
    @Transactional
    public void delete(Long... longs) {
        for (Long id : longs){
            UserAuthorizationToken userAuthorizationToken = new UserAuthorizationToken();
            userAuthorizationToken.setId(id);
            userAuthorizationTokenDao.makeTransient(userAuthorizationToken );
        }
    }

    @Override
    @Transactional
    public void deleteByHql(String hql) {

    }

    @Override
    @Transactional
    public UserAuthorizationToken findById(Long aLong) {
        return null;
    }

    @Override
    @Transactional
    public List<UserAuthorizationToken> findAll() {
        return null;
    }

    @Override
    @Transactional
    public List<UserAuthorizationToken> findAll(QBCCriterion qbcCriterion) {
        return this.getUserAuthorizationTokenDao().findAll(qbcCriterion);
    }

    @Override
    @Transactional
    public List<UserAuthorizationToken> findAllNoInit(QBCCriterion qbcCriterions) {
        return null;
    }

    @Override
    @Transactional
    public PagedArrayList<UserAuthorizationToken> findPaged(QBCCriterion qbcCriterion) {
        return null;
    }

    @Override
    @Transactional
    public long countRecords(QBCCriterion qbcCriterion) {
        return 0;
    }
}
