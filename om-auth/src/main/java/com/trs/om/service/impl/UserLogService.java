package com.trs.om.service.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.LoginUser;
import com.trs.om.bean.UserLog;
import com.trs.om.dao.UserLogDao;
import com.trs.om.exception.TRSOMException;
import com.trs.om.security.CustomWebAuthenticationDetails;
import com.trs.om.service.EncryptService;
import com.trs.om.util.PagedArrayList;
import com.trs.otm.authentication.HttpAuthnUtils;

public class UserLogService implements com.trs.om.service.UserLogService {

	// fields ---------------------------------------------------------------
	private UserLogDao userLogDao;

	public UserLogService() {
		EncryptService obj;
		try {
			obj = EncryptService.getInstance();
			obj.checkLicence();
		} catch (Exception e) {
			throw new TRSOMException("无法获得或者校验注册码："+e.getMessage(),e);
		}
	}

	// methods --------------------------------------------------------------
	@Transactional
	public void log(String userName, String userAct, String ip) {
		if (StringUtils.isBlank(userName))
			return;
		if (StringUtils.isBlank(userAct))
			return;
		if (StringUtils.isBlank(ip))
			return;
		UserLog userLog = new UserLog();
		userLog.setUserName(userName);
		userLog.setUserAct(userAct);
		userLog.setIp(ip);
		userLog.setLogDate(new Date());
		userLogDao.makePersistent(userLog);
	}

	@Transactional
	public void log(LoginUser loginUser, String userAct) {
		UserLog userLog = new UserLog();
		userLog.setIp(loginUser.getIp());
		userLog.setIpLocation(loginUser.getIpLocation());
		userLog.setLogDate(new Date());
		userLog.setUserAct(userAct);
		userLog.setUserName(loginUser.getUser().getUserName());
		userLogDao.makePersistent(userLog);
	}

	@Transactional
	public void log(String userAct) {
		CustomWebAuthenticationDetails details=HttpAuthnUtils.getWebAuthenticationDetails();
		if(null!=details){
			UserLog userLog = new UserLog();
			userLog.setIp(details.getOriginalClientIP());
			userLog.setIpLocation(details.getIpLocation());
			userLog.setLogDate(new Date());
			userLog.setUserAct(userAct);
			userLog.setUserName(HttpAuthnUtils.getLoginUserName());
			userLogDao.makePersistent(userLog);
		}
	}

	@Transactional
	public void log(String userName,String userAct,String ip,String ipLocation){
		UserLog userLog = new UserLog();
		userLog.setIpLocation(ipLocation);
		userLog.setLogDate(new Date());
		userLog.setUserAct(userAct);
		userLog.setUserName(userName);
		userLog.setIp(ip);
		userLogDao.makePersistent(userLog);
	}

	@Transactional
	public PagedArrayList<UserLog> getUserLogsByPage(int page) {
		return userLogDao.list(null, null, null,null, page, 10);
	}

	@Transactional
	public PagedArrayList<UserLog> getUserLogsByPage(int page, String userName,
			String userAct,String ip,String sort) {
		return userLogDao.list(userName, userAct, ip, sort,page, 10);
	}

	@Transactional
	public long countTotalLogs() {
		return userLogDao.countTotalLogs();
	}

	// accessors ------------------------------------------------------------
	public UserLogDao getUserLogDao() {
		return userLogDao;
	}

	public void setUserLogDao(UserLogDao userLogDao) {
		this.userLogDao = userLogDao;
	}
}
