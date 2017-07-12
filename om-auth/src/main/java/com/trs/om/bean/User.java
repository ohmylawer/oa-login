package com.trs.om.bean;

import java.util.Date;
import java.util.Set;

import com.trs.om.common.PermissionConstants;
import com.trs.om.common.ResourceConstants;
import com.trs.om.resource.Resource;

@ResourceType(typeInt=ResourceConstants.TYPE_USER,typeString=PermissionConstants.USER)
public class User extends Resource{

	/** 该常量用于表示用户账号状态：正常. */
	public static final int SC_OK=0;

	/** 该常量用于表示用户账号状态：已过期. */
	public static final int SC_EXPIRED=1;

	/** 该常量用于表示用户账号状态：被停用. */
	public static final int SC_DISABLED=2;

	/** 该常量用于表示用户账号状态：不存在. */
	public static final int SC_MISSING=3;

	/** 该常量用于表示用户账号状态：被删除(假删除). */
	public static final int SC_DELETED=4;

	public static final int DEFAULT_USER_TYPE=1;
	private static final long serialVersionUID = 1487326026455821580L;
	private Long id;
	private String userName;
	private String nickName;
	private int userType;
	private String userRemark;
	private Date creationDate;
	private String userPassword;
	private String digestPassword;
	private String email;
	private String mobile;
	private Set<UserGroup> userGroups;
	private Boolean disabled;
	private Date dueTime;
	private final int prime = 31;
	/** 登录重试次数. */
	private int retryCount=0;

	/** 客户端IP限制. */
	private String clientIpRestraint;

	public Set<UserGroup> getUserGroups() {
		return userGroups;
	}
	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
	public Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	public String getUserRemark() {
		return userRemark;
	}
	public void setUserRemark(String userRemark) {
		this.userRemark = userRemark;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	@Override
	public int hashCode() {
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public Date getDueTime() {
		return dueTime;
	}
	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public String getClientIpRestraint() {
		return clientIpRestraint;
	}

	/**
	 * 设置客户端IP限制.
	 * <p>可以是一个特定的IP，也可以是一个IP范围，如192.168.1.1或者192.168.1.*</p>
	 *
	 * @param clientIpRestraint 客户端IP
	 */
	public void setClientIpRestraint(String clientIpRestraint) {
		this.clientIpRestraint = clientIpRestraint;
	}
	public String getDigestPassword() {
		return digestPassword;
	}
	public void setDigestPassword(String digestPassword) {
		this.digestPassword = digestPassword;
	}

}
