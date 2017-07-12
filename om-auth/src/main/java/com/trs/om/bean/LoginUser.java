package com.trs.om.bean;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户的登录信息
 * @author jwcn
 *
 */
public class LoginUser implements Serializable {
	
	private static final long serialVersionUID = -4186456424551999894L;
	/**
	 * 登录的唯一标识
	 */
	private Long id;
	/**
	 * 登录的用户
	 */
	private User user;
	/**
	 * 登录的IP
	 */
	private String ip;
	/**
	 * IP对应的区域位置
	 */
	private String ipLocation;
	/**
	 * 登录时间
	 */
	private Date loginTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
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
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
}
