package com.trs.om.bean;

/**
 * 实现RBAC模型的角色授权关系，提供的属性有许可标识和角色标识
 * 
 * @author chang
 *
 */
public class Privilege extends AuthorizationObject{//
	
	private Long id;
	/**
	 * permissionId
	 */
	private Long permissionId;

	/**
	 * roleId
	 */
	private Long roleId;
	/**
	 * 
	 */
	private String flagDenied;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	/**
	 * @return the flagDenied
	 */
	public String getFlagDenied() {
		return flagDenied;
	}
	/**
	 * @param flagDenied the flagDenied to set
	 */
	public void setFlagDenied(String flagDenied) {
		this.flagDenied = flagDenied;
	}
}
