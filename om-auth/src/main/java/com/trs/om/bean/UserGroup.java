package com.trs.om.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class UserGroup implements Serializable {
	private Long id;
	private String groupName;
	/**
	 * 用户组的全名
	 * */
	private String fullName;
	private Date createDate;
	private boolean  disabled;//用户组是否被停用  1可用   0不可用
	/**
	 * 父组Id
	 */
	private Long parentId;
	/**
	 * 内部标识
	 * */
	private String innerTag;
	/**
	 * 兄弟结点间位置排序
	 */
	private Integer position;
	private Set<User> users;
	private Set<Possession> possessions;//用户组的属地
	private Set<Role> roles;
	private final int prime = 31;
	/**
	 *用户组级别，最高级组level=1，下级组level依次递增 
	 * */
	private Integer level;
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getInnerTag() {
		return innerTag;
	}
	public void setInnerTag(String innerTag) {
		this.innerTag = innerTag;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public void setPossessions(Set<Possession> possessions) {
		this.possessions = possessions;
	}
	public Set<Possession> getPossessions() {
		return possessions;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserGroup other = (UserGroup) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
}
