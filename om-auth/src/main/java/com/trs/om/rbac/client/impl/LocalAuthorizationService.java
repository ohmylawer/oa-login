package com.trs.om.rbac.client.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.trs.key.OM.KeyUtilBase;
import com.trs.om.exception.TRSOMException;
import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IAuthorizationServer;
import com.trs.om.rbac.client.IAuthorizationService;
import com.trs.om.service.UserService;

/**
 *
 * @author Administrator
 *
 */
public class LocalAuthorizationService implements IAuthorizationService {
	/**
	 *
	 */
	private final static Logger logger = Logger.getLogger(LocalAuthorizationService.class);
	/**
	 *
	 */
	private IAuthorizationServer authorizationServer;
	private UserService userService;
	public LocalAuthorizationService() {
		Properties prop = new Properties();
		String url = this.getClass().getResource("").getPath().replaceAll("%20", " ");
		String path = url.substring(0, url.indexOf("WEB-INF")) + "WEB-INF/system.properties";
		InputStream is;
		try {
			is = new FileInputStream(path);
		} catch (FileNotFoundException e1) {
			throw new TRSOMException("注册码不正确，请确认。");
		}
	    try {
			prop.load(is);
			String key=(String) prop.getProperty("trsom.key");
	         // 加载已加密的外壳类KeyUtilBase
            KeyUtilBase obj;
			try {
				obj = KeyUtilBase.getInstance();
				String result =obj.ValidateOMKey(key);
		        if(result.startsWith("Invalid")){
		        	throw new TRSOMException("注册码不正确，请确认。");
		        }else {
		        	String invalidateYear=result.substring(0,4);
		        	String invalidateMonth=result.substring(4,6);
		        	String invalidateDay=result.substring(6);
		        	String invalDate=invalidateYear+"-"+invalidateMonth+"-"+invalidateDay;
		        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		        	java.util.Date invalidate=dateFormat.parse(invalDate);
		        	if(invalidate.before(new java.util.Date())){
		        		throw new TRSOMException("注册码已到期，请重新申请。");
		        	}
		        }

			} catch (Exception e) {
				throw new TRSOMException("无法启动系统，未找到注册码。");
			}

		} catch (IOException e) {
			throw new TRSOMException("无法启动系统，未找到注册码。");
		}
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	/**
	 *
	 */
	public LocalAuthorizationService(IAuthorizationServer authorizationServer){
		this.authorizationServer = authorizationServer;
	}
	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationService#canOperate(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public int canOperate(Long userId, String application, String object,
			String operation) {
		try {
			return authorizationServer.canDoAsPrivilege(userId, application, object,operation);
		} catch (AuthorizationException e) {
			logger.error(e.getMessage());
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationService#canOperate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public int canOperate(Long userId, String application, String object,
			String operation, String otherPermissions) {
		try {
			return authorizationServer.canDoAsPrivilege(userId, application, object,operation);
		} catch (AuthorizationException e) {
			logger.error(e.getMessage());
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.trs.om.rbac.IAuthorizationService#canOperate(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int canOperate(Long userId, String application, String permission) {
		try {
			return authorizationServer.canDoAsPrivilege(userId, application, permission);
		} catch (AuthorizationException e) {
			logger.error(e.getMessage());
		}
		return -1;
	}
	/**
	 * @return the authorizationServer
	 */
	public IAuthorizationServer getAuthorizationServer() {
		return authorizationServer;
	}
	/**
	 * @param authorizationServer the authorizationServer to set
	 */
	public void setAuthorizationServer(IAuthorizationServer authorizationServer) {
		this.authorizationServer = authorizationServer;
	}
	/**
	 *
	 */
	public List getOperations(Long userId, String application, String object)
			throws AuthorizationException {
		return authorizationServer.getOperations(userId, application, object);
	}
	/**
	 *
	 */
	public List getRoles(Long userId) throws AuthorizationException {
		return authorizationServer.getRoles(userId);
	}
	/**
	 *
	 */
	public List getPermissions(Long userId, String application)
			throws AuthorizationException {
		return authorizationServer.getPermissions(userId, application);
	}
	public int canOperate(String userName, String application, String object,
			String operation) {
		return this.canOperate(this.userService.getUser(userName).getId(), application, object,operation);
	}
}
