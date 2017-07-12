package com.trs.om.dao;

import com.trs.om.bean.LoginUser;

/**
 * 实体类{@link LoginUser}的DAO接口定义
 * @author wj
 *
 */
public interface LoginUserDao {
	void save(LoginUser loginUser);
	LoginUser get(Long id);
	LoginUser getByUserId(Long userId);
	LoginUser getByUserName(String userName);
	void delete(LoginUser loginUser);
}
