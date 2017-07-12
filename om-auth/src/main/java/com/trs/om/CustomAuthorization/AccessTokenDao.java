package com.trs.om.CustomAuthorization;

import com.trs.om.CustomAuthorization.bean.AccessToken;
import com.trs.om.bean.ICPinfo;
import com.trs.om.bean.ICPinfoCriterion;
import com.trs.om.bean.OffsetLimit;
import com.trs.om.dao.GenericDAO;
import com.trs.om.generic.GenericQBCDAO;
import com.trs.om.generic.QBCCriterion;
import com.trs.om.util.PagedArrayList;

import java.util.List;

public interface AccessTokenDao extends GenericDAO<AccessToken, Long> {

    List<AccessToken> findAll(QBCCriterion qbcCriterion);

}
