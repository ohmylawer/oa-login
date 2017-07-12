package com.trs.om.bean;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * 用户查询条件.
 */
public class UserCriterion extends ResourceCriterion{
	// fields ---------------------------------------------------------------
	/**
	 * 用户名列表，用户精确检索
	 */
	private Collection<String> userNames;
	/** 用户名包含的部分字符串.*/
	private String userName;

	/** 用户所属用户组的ID，null为不限制，小于等于0为检索还没有用户组的用户，
	 * 大于0为检索指定用户组的用户，groupIds、groupId只能设置一个. */
	private Long ugroupId;

	/** 检索所属用户组为以下任一用户组的用户，groupIds、groupId只能设置一个. */
	private Set<Long> groupIds;
	/**
	 * 包含不属于任何组的用户
	 * */
	private Boolean includeNoGroup;
	/** 用户所属的角色ID. */
	private String roleId;

	/** 要排除的用户名列表. */
	private String[] excludes;

	/** 检索登录次数等于这个数的用户. */
	private Integer retryCount;

	/** 检索登录次数大于这个时间，retryCount、gtRetryCount、geRetryCount只能设置一个. */
	private Integer gtRetryCount;

	/** 检索登录次数大于等于这个时间，retryCount、gtRetryCount、geRetryCount只能设置一个. */
	private Integer geRetryCount;

	/** 检索登录次数小于这个时间，retryCount、ltRetryCount、leRetryCount只能设置一个. */
	private Integer ltRetryCount;

	/** 检索登录次数小于等于这个时间，retryCount、ltRetryCount、leRetryCount只能设置一个. */
	private Integer leRetryCount;

	/** 过期时间大于这个时间，gtDueTime只能设置一个. */
	private Date gtDueTime;

	/** 过期时间大于等于这个时间，dueTime、gtDueTime、geDueTime只能设置一个. */
	private Date geDueTime;
   
	/** 过期时间小于这个时间，dueTime、ltDueTime、leDueTime只能设置一个. */
	private Date ltDueTime;

	/** 过期时间小于等于这个时间，dueTime、ltDueTime、leDueTime只能设置一个. */
	private Date leDueTime;

	/** 过期时间等于这个时间. */
	private Date dueTime;

	/** 如果true，则表示检索没有设置过期时间的用户，即永不到期用户.优先于dueTime、gtDueTime、geDueTime、ltDueTime、leDueTime */
	private boolean noDueTime;
	/**
	 * 用户id所在的集合
	 * */
	private Set<Long> inIds;
	/**
	 * 待排除的用户id集合
	 * */
	private Collection<Long> excludeIds;
	/**
	 * 是否检查访问权限.
	 * */
    private boolean checkAccess=false;
	/**
	 * 是否包括全部数据，即包括被假删除的数据
	 */
    private boolean includeAll=false;
    /**
     * 是否显示匿名用户，默认不显示
     */
    private boolean showAnonymous=false;

	/**
	 * 用户的邮箱
	 */
    private String email;

	/**
    * 是否根据创建时间降序排列
    * @return
    */
    private boolean isOredrbyCreationDate=false;
	// accessors ------------------------------------------------------------
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String[] getExcludes() {
		return excludes;
	}

	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public Date getGtDueTime() {
		return gtDueTime;
	}

	public void setGtDueTime(Date gtDueTime) {
		this.gtDueTime = gtDueTime;
	}

	public Date getGeDueTime() {
		return geDueTime;
	}

	public void setGeDueTime(Date geDueTime) {
		this.geDueTime = geDueTime;
	}

	public Date getLtDueTime() {
		return ltDueTime;
	}

	public void setLtDueTime(Date ltDueTime) {
		this.ltDueTime = ltDueTime;
	}

	public Date getLeDueTime() {
		return leDueTime;
	}

	public void setLeDueTime(Date leDueTime) {
		this.leDueTime = leDueTime;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public void setIncludeNoGroup(Boolean includeNoGroup) {
		this.includeNoGroup = includeNoGroup;
	}

	public Boolean getIncludeNoGroup() {
		return includeNoGroup;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Set<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Set<Long> groupIds) {
		this.groupIds = groupIds;
	}

	public Integer getGtRetryCount() {
		return gtRetryCount;
	}

	public void setGtRetryCount(Integer gtRetryCount) {
		this.gtRetryCount = gtRetryCount;
	}

	public Integer getGeRetryCount() {
		return geRetryCount;
	}

	public void setGeRetryCount(Integer geRetryCount) {
		this.geRetryCount = geRetryCount;
	}

	public Integer getLtRetryCount() {
		return ltRetryCount;
	}

	public void setLtRetryCount(Integer ltRetryCount) {
		this.ltRetryCount = ltRetryCount;
	}

	public Integer getLeRetryCount() {
		return leRetryCount;
	}

	public void setLeRetryCount(Integer leRetryCount) {
		this.leRetryCount = leRetryCount;
	}

	public boolean isNoDueTime() {
		return noDueTime;
	}

	public void setNoDueTime(boolean noDueTime) {
		this.noDueTime = noDueTime;
	}

	public Set<Long> getInIds() {
		return inIds;
	}

	public void setInIds(Set<Long> inIds) {
		this.inIds = inIds;
	}

	public Collection<Long> getExcludeIds() {
		return excludeIds;
	}

	public void setExcludeIds(Collection<Long> excludeIds) {
		this.excludeIds = excludeIds;
	}

	public boolean isCheckAccess() {
		return checkAccess;
	}

	public void setCheckAccess(boolean checkAccess) {
		this.checkAccess = checkAccess;
	}

	public Long getUgroupId() {
		return ugroupId;
	}

	public void setUgroupId(Long ugroupId) {
		this.ugroupId = ugroupId;
	}

	public boolean isIncludeAll() {
		return includeAll;
	}

	public void setIncludeAll(boolean includeAll) {
		this.includeAll = includeAll;
	}

	public Collection<String> getUserNames() {
		return userNames;
	}

	public void setUserNames(Collection<String> userNames) {
		this.userNames = userNames;
	}

	public boolean isShowAnonymous() {
		return showAnonymous;
	}

	public void setShowAnonymous(boolean showAnonymous) {
		this.showAnonymous = showAnonymous;
	}

	public boolean isOredrbyCreationDate() {
		return isOredrbyCreationDate;
	}

	public void setOredrbyCreationDate(boolean isOredrbyCreationDate) {
		this.isOredrbyCreationDate = isOredrbyCreationDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
