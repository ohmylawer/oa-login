package com.trs.om.bean;

/**
 * 用户的角色授权关系
 * @author chang
 *
 */
public class Session extends AuthorizationObject{// 
	/**
	 * 序号
	 */
	private Long id;
	/**
	 * 角色id
	 */
	private Long roleId;
	/**
	 * 角色赋予给用户所在的组id
	 */
	private Long groupId;

	/**
	 * 用户名称
	 */
	private Long userId;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
}

