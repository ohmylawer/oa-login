package com.trs.om.dao;

import com.trs.om.bean.UserLog;
import com.trs.om.util.PagedArrayList;

public interface UserLogDao extends GenericDAO<UserLog, Long> {
	PagedArrayList<UserLog> list(String userName,String userAct,String ip,String sort,int page,int limit);
	long countTotalLogs();
}
