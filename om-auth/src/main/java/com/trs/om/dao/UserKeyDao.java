package com.trs.om.dao;

import com.trs.om.bean.UserKey;

public interface UserKeyDao extends GenericDAO<UserKey, Long> {

	UserKey findByUserId(Long userId);
	UserKey findByUserName(String username);
}
