package com.trs.om.bean;

import java.util.Date;

public class UserLog {

	private int id;
	private String userName;
	private String userAct;
	private Date logDate;
	private String ip;
	private String ipLocation;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIpLocation() {
		return ipLocation;
	}

	public void setIpLocation(String ipLocation) {
		this.ipLocation = ipLocation;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAct() {
		return userAct;
	}

	public void setUserAct(String userAct) {
		this.userAct = userAct;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

}
