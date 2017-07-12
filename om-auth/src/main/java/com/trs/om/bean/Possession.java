package com.trs.om.bean;


public class Possession {
	private Long id;
	private String possessionName;
	private UserGroup userGroup;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setPossessionName(String possessionName) {
		this.possessionName = possessionName;
	}
	public String getPossessionName() {
		return possessionName;
	}
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}
	public UserGroup getUserGroup() {
		return userGroup;
	}

}
