package com.trs.om.bean;

import java.util.Date;

/**
 * ICP备案信息的分组.
 */
public class ICPinfoGroup {

	private Integer id;

	/** 分组名. */
	private String group;

	/** 该分组中的ICP备案信息数量. */
	private Integer num;

	/** 该分组的创建时间. */
	private Date ctime;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Date getCtime() {
		return ctime;
	}
	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}


}
