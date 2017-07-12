package com.trs.om.bean;

/**
 * 用户组间可见性
 * @author chang
 * 2012-4-23 14:41:21
 * */
public class GroupVisibility {
	private Long id;
	private Long fromId;
	private Long toId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFromId() {
		return fromId;
	}
	public void setFromId(Long fromId) {
		this.fromId = fromId;
	}
	public Long getToId() {
		return toId;
	}
	public void setToId(Long toId) {
		this.toId = toId;
	}
	
}
