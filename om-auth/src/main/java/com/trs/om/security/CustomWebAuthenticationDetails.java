package com.trs.om.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.trs.om.util.IPUtils;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

	// fields ---------------------------------------------------------------
	private static final long serialVersionUID = -5145971952563699359L;
	private String originalClientIP;
	private String ipLocation;
	private Date loginTime;

	// methods --------------------------------------------------------------
	@Override
	protected void doPopulateAdditionalInformation(HttpServletRequest request) {
		this.originalClientIP=IPUtils.getOriginalClientIP(request);
		this.ipLocation=this.originalClientIP;//new URLProcess().IP2Location(this.originalClientIP);
		this.loginTime=new Date();
	}

	public CustomWebAuthenticationDetails(HttpServletRequest request) {
		super(request);
	}

	// accessors ------------------------------------------------------------
	public String getOriginalClientIP() {
		return originalClientIP;
	}

	public void setOriginalClientIP(String originalClientIP) {
		this.originalClientIP = originalClientIP;
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
