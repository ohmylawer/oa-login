package com.trs.om.service;

import java.util.List;

import com.trs.om.bean.DataPermission;

/**
 * 数据权限相关的管理维护接口。
 * @author wengjing
 *
 */
public interface DataPermissionService {
	/**
	 * 查询所有的数据权限。
	 * @return 数据权限列表。
	 */
	List<DataPermission> listAllDataPermissions();
	/**
	 * 批量删除数据权限。
	 * @param ids 由数据权限ID组成的数组。
	 */
	void deleteDataPermissions(Long[] ids);
	/**
	 * 检查指定的数据权限名称是否已被占用。
	 * @param name 数据权限名称。
	 * @param excluedId 排除的数据权限ID，可以指定也可以不指定。
	 * 如果指定了，则只检查这个数据权限之外的其他数据权限是否使用了这个名称；
	 * 否则检查所有的数据权限。
	 * @return <code>true</code>表示已被占用，<code>false</code>表示未被占用。
	 */
	boolean isNameExisting(String name,Long excluedId);
	/**
	 * 新建数据权限。
	 * @param dataPermission 数据权限。
	 */
	void addDataPermission(DataPermission dataPermission);
	/**
	 * 修改数据权限。
	 * @param dataPermission 数据权限。
	 */
	void updateDataPermission(DataPermission dataPermission);
	/**
	 * 查询用户对指定数据表名的数据权限。
	 * @param userId 用户id。
	 * @param tableName 数据表名。
	 * TRSRADAR是雷达采集库，TRSOM是舆情分析库，如果为空则表示对雷达采集库和舆情分析库都起作用。
	 * @return 数据权限列表。
	 */
	List<DataPermission> listDataPermissionsByUserId(Long userId,String tableName);
//	/**
//	 * 判断用户是否有数据权限
//	 * @param userId 用户id。
//	 * */
//	boolean hasDataPermission(Long userId);
}
