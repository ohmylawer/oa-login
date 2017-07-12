package com.trs.om.dao;

import com.trs.om.bean.UserPreference;

/**
 * 用户级别的系统使用偏好DAO
 * @author changguanghua
 * */
public interface UserPreferenceDao {
	/**
	 * 获得用户使用偏好
	 * @param userId 用户id
	 * */
	 UserPreference getUserPreference(Long userId);
	 /**
	 * 保存用户使用偏好
	 * @param userPreference 用户使用偏好
	 * */
	 void saveOrUpdate(UserPreference userPreference);

}
