package com.trs.om.bean;

import com.trs.otm.authorization.AuthorizationManager;

/**
 * 数据权限实体。
 * @author wengjing
 *
 */
public class DataPermission extends Permission {
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	//fields	---------------------------------------------------------------------
	/**
	 * 数据权限名称
	 */
	private String name;
	/**
	 * 数据权限所控制的TRSSERVER数据库名称或视图名称
	 */
	private String tableName;
	/**
	 * 数据权限对应的TRSSSERVER检索表达式
	 */
	private String searchExpression;
	/**
	 * 用于记录数据权限相关的备注信息
	 */
	private String remark;

	//methods	---------------------------------------------------------------------
	public DataPermission() {
		super();
		this.setApplication(AuthorizationManager.APPLICATION);
		this.setObject("DataPermission:TRSSERVER");
		this.setOperation("检索");
	}

	//accessors	---------------------------------------------------------------------
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSearchExpression() {
		return searchExpression;
	}
	public void setSearchExpression(String searchExpression) {
		this.searchExpression = searchExpression;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
