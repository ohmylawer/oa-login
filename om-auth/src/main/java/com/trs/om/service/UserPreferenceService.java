package com.trs.om.service;
/**
 * 用户级别的系统使用偏好服务
 * @author changguanghua
 * */
public interface UserPreferenceService {
	/**
	 * 设置用户默认显示的专题追踪的时间范围
	 * @param userId 用户id
	 * @param timeType 时间范围类型
	 * */
	void setDefaultThemeSpan(Long userId,Integer timeType);
	/**
	 * 获得默认显示的专题追踪的时间范围
	 * @param timeType 时间范围类型
	 * */
	Integer getDefaultThemeSpan(Long userId);
	/**
	 * 设置用户首页刷新时间
	 * @param userId 用户id
	 * @param indexPageAutoRefreshTimeSpan 时间范围类型
	 * */
	void setIndexPageAutoRefreshTimeSpan(Long userId,Integer indexPageAutoRefreshTimeSpan);
	/**
	 * 获得用户首页刷新时间
	 * @param indexPageAutoRefreshTimeSpan 时间范围类型
	 * */
	Integer getIndexPageAutoRefreshTimeSpan(Long userId);
	/**
	 * 设置默认每页显示数量
	 * @param userId
	 * @param defaultPageLimit
	 */
	void setDefaultPageLimit(Long userId,Integer defaultPageLimit);
	/**
	 * 获取用户每页显示数量
	 * @param userId
	 * @return
	 */
	Integer getDefaultPageLimit(Long userId);
	/**
	 * 设置是否启用自动热搜词维护
	 * @param userId
	 * @param autoSearchWord
	 */
	void setAutoSearchWord(Long userId,Integer autoSearchWord);
	/**
	 * 获取用户设置的热搜词维护方式
	 * @param userId
	 * @return
	 */
	Integer getAutoSearchWord(Long userId);
}
