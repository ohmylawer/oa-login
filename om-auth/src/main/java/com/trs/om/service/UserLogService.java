package com.trs.om.service;

import com.trs.om.bean.LoginUser;
import com.trs.om.bean.UserLog;
import com.trs.om.util.PagedArrayList;

public interface UserLogService {
	void log(String userName, String userAct, String ip);
	void log(LoginUser userInfo, String userAct);
	/**
	 * 记录当前用户的操作日志.
	 *
	 * @param userAct 操作日志.
	 */
	public void log(String userAct);
	/**
	 * 记录用户的操作日志.
	 *
	 * @param userName 用户名。
	 * @param userAct 操作信息。
	 * @param ip 用户的IP地址。
	 * @param ipLocation 用户IP的地理位置。
	 */
	public void log(String userName,String userAct,String ip,String ipLocation);
	PagedArrayList<UserLog> getUserLogsByPage(int page,String userName,String userAct,String ip,String sort);//
	PagedArrayList<UserLog> getUserLogsByPage(int page);
	/**
	 * 获取日志的总记录数.
	 *
	 * @return 日志的总记录数.
	 */
	public long countTotalLogs();
}
