package com.trs.om.bean;

/**
 * ICP备案信息查询条件.
 */
public class ICPinfoCriterion {

	/** 站点名称包含指定字符串. */
	public String likeSiteName;

	/** 域名包含指定字符串. */
	public String likeDomeName;

	/** 联系地址包含指定字符串. */
	public String likeAddress;

	/** 备案号包含指定字符串. */
	public String likeIcpbaseInfo;

	/** 指定站点所在国家. */
	public String eqNationality;

	/** 包含新闻站点. */
	public Boolean includeNewsSite;

	/** 包含论坛站点. */
	public Boolean includeForumSite;

	/** 包含博客站点. */
	public Boolean includeBlogSite;

	/** 包含视频站点. */
	public Boolean includeVideoSite;

	/** 包含微博站点. */
	public Boolean includeWeiboSite;

	/** 指定站点所在地. */
	public Long[] eqLocals;
	public String eqGroupName;
}
