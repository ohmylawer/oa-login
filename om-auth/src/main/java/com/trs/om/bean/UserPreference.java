package com.trs.om.bean;

/**
 * 用户偏好
 * @author changguanghua
 * */

public class UserPreference {
	private Long id;
	/**
	 * 用户id
	 * */
	private Long userId;
	/**
	 * 默认专题显示时间范围，0为1天内，1为1周内，2为1月内，null为全部
	 * */
	private Integer defaultThemeSpan;
	/**
	 * 默认每页显示的条目数
	 * */
	private Integer defaultPageLimit;
	/**
	 * 首页是否开启自动刷新
	 * */
	private Integer indexPageAutoRefreshTimeSpan;
	/**
	 * 是否开启热搜词自动维护功能，默认开启值为非0,目前设为1，当为0时关闭（即采用手工维护方式）
	 */
	private Integer autoSearchWord=1;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getDefaultThemeSpan() {
		return defaultThemeSpan;
	}
	public void setDefaultThemeSpan(Integer defaultThemeSpan) {
		this.defaultThemeSpan = defaultThemeSpan;
	}
	public Integer getDefaultPageLimit() {
		return defaultPageLimit;
	}
	public void setDefaultPageLimit(Integer defaultPageLimit) {
		this.defaultPageLimit = defaultPageLimit;
	}
	public Integer getIndexPageAutoRefreshTimeSpan() {
		return indexPageAutoRefreshTimeSpan;
	}
	public void setIndexPageAutoRefreshTimeSpan(Integer indexPageAutoRefreshTimeSpan) {
		this.indexPageAutoRefreshTimeSpan = indexPageAutoRefreshTimeSpan;
	}
	public Integer getAutoSearchWord() {
		return autoSearchWord;
	}
	public void setAutoSearchWord(Integer autoSearchWord) {
		this.autoSearchWord = autoSearchWord;
	}

}
