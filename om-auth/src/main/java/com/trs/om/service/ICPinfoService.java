package com.trs.om.service;

import java.util.List;

import com.trs.om.bean.ICPinfo;
import com.trs.om.bean.ICPinfoCriterion;
import com.trs.om.bean.ICPinfoGroup;
import com.trs.om.bean.OffsetLimit;
import com.trs.om.util.PagedArrayList;

public interface ICPinfoService {

	/**
	 * 添加一个站点信息.
	 *
	 * @param temp  ICPInfo
	 */
	void add(ICPinfo temp);
	/**
	 * 删除一个站点信息
	 */
   void delete(Long id);
    /**
     * 修改一个站点信息
     */
   void update(ICPinfo temp);
     /**
      * 查询单个站点信息（更据名称）
      */
   PagedArrayList<ICPinfo>  findByName(String  siteName,int  dbPage, int limit);

  /**
   * 查询所有站点信息
   */
   PagedArrayList<ICPinfo>  findByName(int  dbPage, int limit);


   /**
    * 按照站点类型查询站点信息
    */
   PagedArrayList<ICPinfo> list(int type, int dbpage, int limit);

   /**
    * 按照站点类型查询站点信息,增加对敌对性质的判定
    */
   PagedArrayList<ICPinfo> list(int type, String enemy,int dbpage, int limit);
   /**
    * 列出某个责任辖区下的所有站点
    */
   PagedArrayList<ICPinfo> listDesignate(List<Long> userGroups, int dbpage, int limit);
   /**
    * 列出某个地区的所有站点
    */
   PagedArrayList<ICPinfo> listLocal(List<Long> userGroups, int dbpage, int limit);
   /**
    * 列出属于某个准属地的所有站点
    */
   PagedArrayList<ICPinfo> ListAffiliate(List<Long> userGroups, int dbpage, int limit);
   /**
    * 列出站点的准属地信息
    */
   List<Long> getAffiliate(Long siteId);
   /**
    * 配置站点的准属地信息
    */
   void configAffiliate(Long userId,String groupIds);
   /**
    * 根据id查找该对象
    */
   ICPinfo  findBySid(Long sid,boolean lock);
   /**
    * 根据网址获取网站ICP信息
    */
   ICPinfo  findByUrl(String url);
   /**
    * 根据相关条件进行搜索
    */
   PagedArrayList<ICPinfo> searchIcpinfo(ICPinfoCriterion criterion,OffsetLimit offsetLimit);

   /**
    * 根据相关条件进行搜索
    *
    * @param criterion the criterion
    * @return the list
    */
   List<ICPinfo> searchIcpinfo(ICPinfoCriterion criterion);

   /**
    * 查询ICP信息数量大于0的分组.
    *
    * @return the list
    */
   List<ICPinfoGroup> listUsedGroup();
   /**
    * 去group_site_channel表里统计站点信息到icpinfo表
    * @return
    */
   //TODO 用触发器完成该功能，然后去掉改方法
	@Deprecated
   List<ICPinfo> countIcpinfos();

   /**
    * 添加、更新网站排名
    * @param siteName
    * @param siteRank
    */
   void updateSiteRank(String siteName, int siteRank);

   /**
    * 批量添加或更新网站排名
    * @param infos
    */
 //TODO 用触发器完成该功能，然后去掉改方法
	@Deprecated
   void batchAddOrUpdate(List<ICPinfo> infos);

   /**
    * 获取网站排名
    * @param icpinfo
    * @return
    */
   int getSiteRank(ICPinfo icpinfo);

   /**
    * 删除网站排名
    * @param siteName
    */
   void deleteSiteRank(String siteName);

}