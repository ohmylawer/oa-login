package com.trs.om.dao;

import java.util.List;

import com.trs.om.bean.DataPermission;

/**
 * {@link DataPermission}实体的DAO接口。
 * @author wengjing
 * @see DataPermission
 */
public interface DataPermissionDao extends GenericDAO<DataPermission, Long> {
	/**
	 * 检查指定的数据权限名称是否已被占用。
	 * @param name 数据权限名称。
	 * @param excluedId 排除的数据权限ID，可以指定也可以不指定。
	 * 如果指定了，则只检查这个数据权限之外的其他数据权限是否使用了这个名称；
	 * 否则检查所有的数据权限。
	 * @return <code>true</code>表示已被占用，<code>false</code>表示未被占用。
	 */
	boolean isNameExisting(String name, Long excluedId);
	/**
	 * 查询用户对指定数据表名的数据权限。
	 * @param userId 用户id。
	 * @param tableName 数据表名。
	 * @return 数据权限列表。
	 */
	List<DataPermission> find(Long userId,String tableName);
//	boolean hasDataPermission(Long userId);
}
