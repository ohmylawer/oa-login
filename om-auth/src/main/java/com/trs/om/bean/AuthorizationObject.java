package com.trs.om.bean;

/**
 * 实现RBAC模型各类对象的基类，提供的属性是：标识
 * 
 * @author Administrator
 *
 */
public class AuthorizationObject {
	/**
	 * 
	 */
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
