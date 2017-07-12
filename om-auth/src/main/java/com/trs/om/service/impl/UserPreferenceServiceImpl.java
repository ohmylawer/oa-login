package com.trs.om.service.impl;

import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.UserPreference;
import com.trs.om.dao.UserPreferenceDao;
import com.trs.om.exception.TRSOMException;
import com.trs.om.service.EncryptService;
import com.trs.om.service.UserPreferenceService;

public class UserPreferenceServiceImpl implements UserPreferenceService {
	private UserPreferenceDao userPreferenceDao;

	public UserPreferenceServiceImpl() {
		EncryptService obj;
		try {
			obj = EncryptService.getInstance();
			obj.checkLicence();
		} catch (Exception e) {
			throw new TRSOMException("无法获得或者校验注册码："+e.getMessage(),e);
		}
	}

	@Transactional
	public Integer getDefaultThemeSpan(Long userId) {
		UserPreference up=userPreferenceDao.getUserPreference(userId);
		if(up==null){//若用户从未设置过自己的偏好，返回默认值
			return 0;
		}
		return up.getDefaultThemeSpan();
	}

	@Transactional
	public void setDefaultThemeSpan(Long userId, Integer timeType) {
		UserPreference up=userPreferenceDao.getUserPreference(userId);
		if(up==null){//若用户从未设置过自己的偏好，则新建一个用户偏好
			up=new UserPreference();
			up.setUserId(userId);
		}
		up.setDefaultThemeSpan(timeType);
		userPreferenceDao.saveOrUpdate(up);
	}

	@Transactional
	public Integer getIndexPageAutoRefreshTimeSpan(Long userId) {
		UserPreference up=userPreferenceDao.getUserPreference(userId);
		if(up==null){//若用户从未设置过自己的偏好，返回默认值
			return 0;
		}
		return up.getIndexPageAutoRefreshTimeSpan();
	}

	@Transactional
	public void setIndexPageAutoRefreshTimeSpan(Long userId,
			Integer indexPageAutoRefreshTimeSpan) {
		UserPreference up=userPreferenceDao.getUserPreference(userId);
		if(up==null){//若用户从未设置过自己的偏好，则新建一个用户偏好
			up=new UserPreference();
			up.setUserId(userId);
		}
		up.setIndexPageAutoRefreshTimeSpan(indexPageAutoRefreshTimeSpan);
		userPreferenceDao.saveOrUpdate(up);
	}

	@Transactional
	public void setDefaultPageLimit(Long userId, Integer defaultPageLimit) {
		UserPreference up=userPreferenceDao.getUserPreference(userId);
		if(up==null){//若用户从未设置过自己的偏好，则新建一个用户偏好
			up=new UserPreference();
			up.setUserId(userId);
		}
		up.setDefaultPageLimit(defaultPageLimit);
		userPreferenceDao.saveOrUpdate(up);
	}

	@Transactional
	public Integer getDefaultPageLimit(Long userId) {
		UserPreference up=userPreferenceDao.getUserPreference(userId);
		if(up==null){//若用户从未设置过自己的偏好，返回默认值
			return 0;
		}
		return up.getDefaultPageLimit();
	}

	@Transactional
	public void setAutoSearchWord(Long userId, Integer autoSearchWord) {
		UserPreference up=userPreferenceDao.getUserPreference(userId);
		if(up==null){//若用户从未设置过自己的偏好，则新建一个用户偏好
			up=new UserPreference();
			up.setUserId(userId);
		}
		up.setAutoSearchWord(autoSearchWord);
		userPreferenceDao.saveOrUpdate(up);
	}

	@Transactional
	public Integer getAutoSearchWord(Long userId) {
		UserPreference up=userPreferenceDao.getUserPreference(userId);
		if(up==null){//若用户从未设置过自己的偏好，返回默认值
			return 0;
		}
		return up.getAutoSearchWord();
	}

	public void setUserPreferenceDao(UserPreferenceDao userPreferenceDao) {
		this.userPreferenceDao = userPreferenceDao;
	}

	public UserPreferenceDao getUserPreferenceDao() {
		return userPreferenceDao;
	}





}
