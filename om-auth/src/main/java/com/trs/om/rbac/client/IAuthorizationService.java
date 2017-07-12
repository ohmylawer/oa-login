/**
 * 
 */
package com.trs.om.rbac.client;

import java.util.List;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IAuthorization;

/**
 * 基于RBAC权限模型的客户端服务接口
 * @author chang
 *
 */
public interface IAuthorizationService {
	/**
	 * 验证权限
	 * @param userId 用户Id
	 * @param application 应用名称
	 * @param object 待验证权限对象
	 * @param operation 待验证权限操作
	 * @return 验证结果，参见{@link IAuthorization}
	 */
	public int canOperate(Long userId,String application,String object,String operation);
	/**
	 * 验证权限
	 * @param userName 用户名
	 * @param application 应用名称
	 * @param object 待验证权限对象
	 * @param operation 待验证权限操作
	 * @return 验证结果，参见{@link IAuthorization}
	 */
	public int canOperate(String userName,String application,String object,String operation);
	/**
	 * 验证权限
	 * @param userId 用户Id
	 * @param application 应用名称
	 * @param permission 待验证权限许可，格式为”待验证权限对象:待验证权限操作"
	 * @return 验证结果，参见{@link IAuthorization}
	 */
	public int canOperate(Long userId,String application,String object,String operation,String otherPermissions);
	/**
	 * 验证权限
	 * @param userId 用户Id
	 * @param application 应用名称
	 * @param permission 待验证权限许可，格式为”待验证权限对象:待验证权限操作"
	 * @return 验证结果，参见{@link IAuthorization}
	 */
	public int canOperate(Long userId,String application,String permission);
	/**
	 * 获取指定用户在指定应用的指定对象上的操作列表
	 * @param userId 用户Id
	 * @param appliation 应用名称
	 * @param object 指定应用的指定对象
	 * @return 操作列表，为String对象，与设置的操作名称一致；
	 * @throws AuthorizationException 抛出获取操作过程中发生的异常
	 */
	public List getOperations(Long userId,String application,String object) throws AuthorizationException;
	/**
	 * 获取角色列表
	 * @param userId 用户Id
	 * @return 角色列表，元素为String类型的角色名称
	 * @throws AuthorizationException  抛出获取角色过程中的异常
	 */
	public List getRoles(Long userId) throws AuthorizationException;
	/**
	 * 获取指定用户在指定应用上的许可列表
	 * @param userId 用户Id
	 * @param application 应用名称
	 * @return 许可列表，为Permission对象；
	 * @throws AuthorizationException 抛出获取许可过程中发生的异常
	 */
	public List getPermissions(Long userId,String application) throws AuthorizationException;
}
