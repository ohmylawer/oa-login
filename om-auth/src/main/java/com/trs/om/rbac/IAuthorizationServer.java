/**
 * 
 */
package com.trs.om.rbac;

import java.util.List;
import java.util.Properties;


/**
 * 权限验证服务器，实现权限验证的全部业务逻辑，并提供对外服务的操作接口
 * 
 * @author Administrator
 *
 */
public interface IAuthorizationServer {
	/**
	 * 验证权限
	 * @param user 用户名
	 * @param application 应用名称
	 * @param object 待验证权限对象
	 * @param operation 待验证权限操作
	 * @return 验证结果，参见{@link IAuthorization}
	 * @throws AuthorizationException 抛出权限验证过程中发生的异常
	 */
	public int canDoAsPrivilege(Long userId,String application,String object,String operation) throws AuthorizationException;
	/**
	 * 验证权限，暂时未实现
	 * @param user 用户名
	 * @param application 应用名称
	 * @param object 待验证权限对象
	 * @param operation 待验证权限操作
	 * @param permissions 其他辅助验证权限对象，格式为“待验证权限对象:待验证权限操作;"
	 * @return 验证结果，参见{@link IAuthorization}
	 * @throws AuthorizationException 抛出权限验证过程中发生的异常 
	 */
	public int canDoAsPrivilege(Long userId,String application,String object,String operation,String otherPermissions) throws AuthorizationException;
	/**
	 * 验证权限
	 * @param user 用户名
	 * @param application 应用名称
	 * @param permission 待验证权限许可，格式为”待验证权限对象:待验证权限操作"
	 * @return 验证结果，参见{@link IAuthorization}
	 * @throws AuthorizationException 抛出权限验证过程中发生的异常 
	 */
	public int canDoAsPrivilege(Long userId,String application,String permission) throws AuthorizationException;
	/**
	 * 获取指定用户在指定应用上的许可列表
	 * @param user 用户名
	 * @param application 应用名称
	 * @return 许可列表，为Permission对象；
	 * @throws AuthorizationException 抛出获取许可过程中发生的异常
	 */
	public List getPermissions(Long userId,String application) throws AuthorizationException;
	/**
	 * 获取指定用户在指定应用的指定对象上的操作列表
	 * @param user 用户名
	 * @param appliation 应用名称
	 * @param object 指定应用的指定对象
	 * @return 操作列表，为String对象，与设置的操作名称一致；
	 * @throws AuthorizationException 抛出获取操作过程中发生的异常
	 */
	public List getOperations(Long userId,String application,String object) throws AuthorizationException;
	
	/**
	 * 向授权模块注册应用的许可
	 * @param application 应用名称
	 * @param object 待注册的目标对象
	 * @param operation 待注册的操作
	 * @return 注册的结果
	 * @throws AuthorizationException 抛出注册过程中发生的异常 
	 */
	public int registerPermission(String application,String object,String operation) throws AuthorizationException;
	
	/**
	 * 从授权模块撤销注册的许可
	 * @param application 应用名称
	 * @param object 待撤销的目标对象
	 * @param operation 待撤销的操作
	 * @return 撤销注册的结果
	 * @throws AuthorizationException 抛出注册过程中发生的异常
	 */
	public int unregisterPermission(String application,String object,String operation) throws AuthorizationException;
	/**
	 * 
	 * @param user
	 * @param application
	 * @param object
	 * @param operation
	 * @throws AuthorizationException
	 */
	public int registerPrevilige(Long roleId,String application,String object,String operation)throws AuthorizationException;
	/**
	 * 
	 * @param user
	 * @param application
	 * @param object
	 * @param operation
	 * @throws AuthorizationException
	 */
	public int unregisterPrevilige(Long roleId,String application,String object,String operation)throws AuthorizationException;
	/**
	 * 注册用户和角色的关系
	 * @param user 用户名
	 * @param role 角色名称
	 * @return 注册结果
	 * @throws AuthorizationException 抛出注册过程中发生的异常
	 */
//	public int registerSession(Long userId,Long roleId) throws AuthorizationException;
	/**
	 * 撤销注册的用户和角色关系
	 * @param user 用户名
	 * @param role 角色名称
	 * @return 撤销注册的结果
	 * @throws AuthorizationException 抛出注册过程中发生的异常
	 */
	public int unregisterSession(Long user,Long roleId) throws AuthorizationException;
	/**
	 * 启动认证授权模块
	 * @param managerServiceClass Manager的Service类，默认可以设为null
	 * @param daoServiceClass DAO的Service类，默认可以设为null，则使用Hibernate存储数据
	 * @param properties 外部传入的额外配置属性，默认的配置属性在hibernate.cfg.xml中
	 * @throws AuthorizationException 抛出启动过程中发生的异常
	 */
	public void start(String managerServiceClass,String daoServiceClass,Properties properties) throws AuthorizationException;
	/**
	 * 停止认证授权模块
	 */
	public void stop();
	/**
	 * 获取角色列表
	 * @param userId 用户Id
	 * @return 角色列表，元素为String类型的角色名称
	 * @throws AuthorizationException  抛出获取角色过程中的异常
	 */
	public List getRoles(Long userId) throws AuthorizationException;
	/**
	 * 获取授权模块的许可管理类，供维护权限数据使用
	 * @return 系统当前使用的许可管理类
	 */
	public IPermissionManager getPermissionManager();
	/**
	 * 获取授权模块的角色管理类，供维护权限数据使用
	 * 
	 * @return 系统当前使用的角色管理类
	 */
	public IRoleManager getRoleManager(); 
	/**
	 * 获取授权模块的用户角色映射关系管理类，供维护权限数据使用
	 * @return 系统当前使用的用户<=>角色映射关系管理类
	 */
	public ISessionManager getSessionManager();
	/**
	 * 获取授权模块的角色许可映射关系管理类，供维护权限数据使用
	 * @return 系统当前使用的角色<=>许可映射关系管理类
	 */
	public IPrivilegeManager getPrivilegeManager();
}
