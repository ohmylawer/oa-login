/**
 * 
 */
package com.trs.om.rbac.dao;

/**
 * @author Administrator
 *
 */
public class AuthorizationDAOException extends Exception {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String errorCode;
	
	/**
	 * 
	 */
	private String errorDesc;
	/**
	 * 
	 * @param errorCode
	 * @param errorDesc
	 */
	public AuthorizationDAOException(String errorCode,String errorDesc){
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}
	/**
	 * 
	 * @param
	 */
	public AuthorizationDAOException(String errorDesc) {
		this.errorCode = "";
		this.errorDesc = errorDesc;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}
	/**
	 * @param errorDesc the errorDesc to set
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
}
