/**
 * 
 */
package com.trs.om.rbac;

/**
 * 权限认证公用接口，主要提供一些常量定义
 * 
 * @author Administrator
 *
 */
public interface IAuthorization {
	/**
	 * 权限验证成功
	 */
	public final int OPERATION_ALLOWED = 100;
	/**
	 * 基于继承的父类权限的权限验证成功
	 */
	public final int OPERATION_ALLOWED_INHERIT = 101;
	/**
	 * 权限验证失败
	 */
	public final int OPERATION_DENIED = 200;
}
