package com.trs.om.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class ICPinfo implements Serializable {
	private Long sid;
	private String icpbaseInfo; // icp基础信息
	private String siteName; // 站点名称
	private String ip; // ip 地址
	private String nationality="中国"; // 站点所在国家
	private Boolean newsSite=false; // 是否是新闻站点
	private Boolean forumSite=false; // 是否是论坛站点
	private Boolean blogSite=false; // 是否是博客站点
	private Boolean videoSite=false; // 是否是视频站点
	private Boolean weiboSite=false; // 是否是微博站点
	private String domeName;// 域名
	private String manager; // 负责人
	private String phone; // 联系电话
	private String teamName; // 单位名称
	private String teamType; // 单位性质
	private String email; // 邮箱地址
	private String address; // 地址
	private UserGroup userGroup; // 为站点指派添加
	private String district;// 国外站点所在地区
	private UserGroup local;// 国内站点所在地区
	private Set<UserGroup> affiliate;// 准属地
	private Date authTime;// 审核时间
	private Date createTime;//创建时间
	private int siteNature; // 站点性质：0尚未定性、1非敌对站点、2敌对站点
	private final int prime = 31;
	private String groupName;// 所属分组

	@Override
	public int hashCode() {
		int result = 1;
		result = prime * result + ((sid == null) ? 0 : sid.hashCode());
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
		ICPinfo other = (ICPinfo) obj;
		if (sid == null) {
			if (other.sid != null)
				return false;
		} else if (!sid.equals(other.sid))
			return false;
		return true;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public Long getSid() {
		return sid;
	}

	public void setSid(Long sid) {
		this.sid = sid;
	}

	public String getIcpbaseInfo() {
		return icpbaseInfo;
	}

	public void setIcpbaseInfo(String icpbaseInfo) {
		this.icpbaseInfo = icpbaseInfo;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ICPinfo() {
		super();
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getDistrict() {
		return district;
	}

	public void setLocal(UserGroup local) {
		this.local = local;
	}

	public UserGroup getLocal() {
		return local;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDomeName() {
		return domeName;
	}

	public void setDomeName(String domeName) {
		this.domeName = domeName;
	}

	public Set<UserGroup> getAffiliate() {
		return affiliate;
	}

	public void setAffiliate(Set<UserGroup> affiliate) {
		this.affiliate = affiliate;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamType() {
		return teamType;
	}

	public void setTeamType(String teamType) {
		this.teamType = teamType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setAuthTime(Date authTime) {
		this.authTime = authTime;
	}

	public Date getAuthTime() {
		return authTime;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Boolean getNewsSite() {
		return newsSite;
	}

	public void setNewsSite(Boolean newsSite) {
		this.newsSite = newsSite;
	}

	public Boolean getForumSite() {
		return forumSite;
	}

	public void setForumSite(Boolean forumSite) {
		this.forumSite = forumSite;
	}

	public Boolean getBlogSite() {
		return blogSite;
	}

	public void setBlogSite(Boolean blogSite) {
		this.blogSite = blogSite;
	}

	public Boolean getVideoSite() {
		return videoSite;
	}

	public void setVideoSite(Boolean videoSite) {
		this.videoSite = videoSite;
	}

	public Boolean getWeiboSite() {
		return weiboSite;
	}

	public void setWeiboSite(Boolean weiboSite) {
		this.weiboSite = weiboSite;
	}

	public int getPrime() {
		return prime;
	}

	public int getSiteNature() {
		return siteNature;
	}

	public void setSiteNature(int siteNature) {
		this.siteNature = siteNature;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
