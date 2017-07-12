package com.trs.om.bean;

import java.util.Date;

/**
 * 封装检索用户组结点的相关参数。
 * @author changguanghua
 * 2012-5-2 13:58:16
 */
public class UserGroupCriterion {
	// fields ---------------------------------------------------------------
	private Boolean disabled;
	/**
	 * 创建人
	 */
	private String createUserName;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 专题名
	 */
	private String groupName;
	/**
	 * 父专题Id
	 */
	private Long parentId;
	/**
	 * 兄弟结点间位置排序
	 */
	private Integer position;//兄弟结点间位置排序

	private Integer gtPosition;
	private Integer gePosition;
	private Integer ltPosition;
	private Integer lePosition;

	/** 创建时间大于这个时间，dueTime、gtDueTime、geDueTime只能设置一个. */
	private Date gtCreateDate;

	/** 创建时间大于等于这个时间，dueTime、gtDueTime、geDueTime只能设置一个. */
	private Date geCreateDate;

	/** 创建时间小于这个时间，dueTime、ltDueTime、leDueTime只能设置一个. */
	private Date ltCreateDate;

	/** 创建时间小于等于这个时间，dueTime、ltDueTime、leDueTime只能设置一个. */
	private Date leCreateDate;

	/** 排序方式. */
	private String orders;


	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
	public Integer getGtPosition() {
		return gtPosition;
	}

	public void setGtPosition(Integer gtPosition) {
		this.gtPosition = gtPosition;
	}

	public Integer getGePosition() {
		return gePosition;
	}

	public void setGePosition(Integer gePosition) {
		this.gePosition = gePosition;
	}

	public Integer getLtPosition() {
		return ltPosition;
	}

	public void setLtPosition(Integer ltPosition) {
		this.ltPosition = ltPosition;
	}

	public Integer getLePosition() {
		return lePosition;
	}

	public void setLePosition(Integer lePosition) {
		this.lePosition = lePosition;
	}
	public Date getGtCreateDate() {
		return gtCreateDate;
	}

	public void setGtCreateDate(Date gtCreateDate) {
		this.gtCreateDate = gtCreateDate;
	}

	public Date getGeCreateDate() {
		return geCreateDate;
	}

	public void setGeCreateDate(Date geCreateDate) {
		this.geCreateDate = geCreateDate;
	}

	public Date getLtCreateDate() {
		return ltCreateDate;
	}

	public void setLtCreateDate(Date ltCreateDate) {
		this.ltCreateDate = ltCreateDate;
	}

	public Date getLeCreateDate() {
		return leCreateDate;
	}

	public void setLeCreateDate(Date leCreateDate) {
		this.leCreateDate = leCreateDate;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getDisabled() {
		return disabled;
	}

}
