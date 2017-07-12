package com.trs.om.dao;

import java.util.List;

import com.trs.om.bean.ICPinfo;
import com.trs.om.bean.ICPinfoCriterion;
import com.trs.om.bean.OffsetLimit;
import com.trs.om.util.PagedArrayList;

public interface ICPinfoDao extends GenericDAO<ICPinfo, Long> {

   PagedArrayList<ICPinfo>  findByName(String  siteName,int  dbPage, int limit);

  /**
   * 查询所有站点信息
   */
   PagedArrayList<ICPinfo>  findByName(int  dbPage, int limit);
   /**
    * 按照站点类型查询所有站点信息
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
    * 查询用户组所管辖的所有站点.
    *
    * @param groupIds the user groups
    * @return the list
    */
   List<ICPinfo> listAllLocal(List<Long> groupIds);


   PagedArrayList<ICPinfo> ListAffiliate(List<Long> userGroups, int dbpage,int limit);

   /**
    * 根据相关条件进行搜索.
    *
    * @param criterion the criterion
    * @param offsetLimit the offset limit
    * @return the paged array list
    */
   PagedArrayList<ICPinfo> searchIcpinfo(ICPinfoCriterion criterion,OffsetLimit offsetLimit);

   List<ICPinfo> searchAllIcpinfo(ICPinfoCriterion criterion);
   /**
    * 根据根域名查找ICP信息
    */
   ICPinfo findByRootDomainName(String root);
   //TODO 用触发器完成该功能，然后去掉改方法
	@Deprecated
   List<ICPinfo> countAndAddIcpInfos();

}
