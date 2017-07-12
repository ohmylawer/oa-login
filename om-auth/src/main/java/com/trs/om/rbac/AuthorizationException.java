/**
 * 
 */
package com.trs.om.rbac;

/**
 * 授权认证的异常
 * 
 * @author Administrator
 *
 */
public class AuthorizationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String errorDesc;
	
	/**
	 * 
	 * @param errorDesc
	 */
	public AuthorizationException(String errorDesc){
		this.errorDesc = errorDesc;
	}

	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}
}
