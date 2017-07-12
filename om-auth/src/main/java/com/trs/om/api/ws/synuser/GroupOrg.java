package com.trs.om.api.ws.synuser;


public class GroupOrg{
	private Integer id;
	/**
	 * 用户组ID
	 */
	private String groupID;
	/**
	 * 组织机构ID
	 */
	private String orgID;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGroupID() {
		return groupID;
	}
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	public String getOrgID() {
		return orgID;
	}
	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}

}
