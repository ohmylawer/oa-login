package com.trs.om.bean;

import java.util.Set;

import org.apache.commons.lang.StringUtils;


/**
 * 实现RBAC模型的角色对象，提供的属性有角色名称和角色描述
 * 
 */
public class Role extends AuthorizationObject{// 
	/**
	 * 
	 */
	private Long id;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String desc;
	
	private Set<UserGroup> userGroups;
	
	private final int prime = 31;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */

	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Set<UserGroup> getUserGroups() {
		return userGroups;
	}
	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if(!StringUtils.equals(other.getName(), this.getName()))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode())+((StringUtils.isEmpty(name)) ? 0 : name.hashCode());
		return result;
	}
	
}

