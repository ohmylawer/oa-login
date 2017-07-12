package com.trs.om.security.ids;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.trs.idm.client.actor.ActorException;
import com.trs.idm.client.actor.SSOUser;
import com.trs.idm.client.actor.StdHttpSessionBasedActor;
import com.trs.om.rbac.IRoleManager;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.bean.Role;
import com.trs.idm.system.ClientConst;
import com.trs.om.bean.User;
import com.trs.om.common.ObjectContainer;
import com.trs.om.service.UserLogService;
import com.trs.om.service.UserService;

/**
 * 与Spring Security协作，实现IDS SSO集成.
 */
public class Actor extends StdHttpSessionBasedActor {

	/** The Constant log. */
	public static final Logger LOGGER=LoggerFactory.getLogger(Actor.class);
	public static final String TRSIDS_SSOUSER_KEY="TRSIDS_SSOUSER_KEY";
	public static final String LOG_USER="system";

	/**
	 * 判断当前Session是否登录.
	 *
	 * @param session the session
	 * @return true, if successful
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#checkLocalLogin(javax.servlet.http.Ht
	 * tpSession)
	 */
	public boolean checkLocalLogin(HttpSession session) throws ActorException {
		SecurityContext securityContext=(SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		return null!=securityContext
			&&null!=securityContext.getAuthentication()
			&&securityContext.getAuthentication().isAuthenticated();
	}

	/**
	 * 加载登录的统一用户到Demo应用的当前会话(Session对象)中, 完成Demo 应用自己的登录逻辑(不需要再次对用户进行认证, 只需要加载).
	 *
	 * @param request the request
	 * @param user the user
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#loadLoginUser(javax.servlet.http.Http
	 * ServletRequest, com.trs.idm.client.actor.SSOUser)
	 */
	public void loadLoginUser(HttpServletRequest request, SSOUser user)
			throws ActorException {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("加载登录的统一用户：{}",user);
		}
		request.setAttribute(TRSIDS_SSOUSER_KEY, user);

	}

	/**
	 * 完成Demo应用自己的退出登录的逻辑.
	 *
	 * @param session the session
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#logout(javax.servlet.http.HttpSession )
	 */
	public void logout(HttpSession session) throws ActorException {
		session.invalidate();
	}

	/**
	 * Demo应用同步增加用户的实现. 因为Demo应用很简单, 不存储用户数据, 所以本实现仅仅返回<code>true</code>,
	 * 表示同步成功即可.
	 *
	 * @param user the user
	 * @param request the request
	 * @return true, if successful
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#addUser(com.trs.idm.client.actor.SSOU ser,
	 * javax.servlet.http.HttpServletRequest)
	 */
	public boolean addUser(SSOUser user, HttpServletRequest request)
			throws ActorException {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("同步添加用户：{}",user);
		}
		UserService us=(UserService)ObjectContainer.getBean("userService");//.getUserService();
		User u=us.getUser(user.getUserName());
		if(u!=null) return updateUser(user, request);
		u=new User();
		String creationDate=user.getProperty(ClientConst.USERPROPS_CREATED_DATE);
		if(StringUtils.isNotBlank(creationDate)) u.setCreationDate(new Date(Long.valueOf(creationDate)));
		else u.setCreationDate(new Date());
		u.setCreator(LOG_USER);
		String actived=user.getProperty(ClientConst.USERPROPS_ACTIVED);
		if(StringUtils.isNotBlank(actived))	u.setDisabled(!Boolean.valueOf(actived));
		u.setEmail(user.getMail());
		String mobile=user.getProperty(ClientConst.USERPROPS_MOBILE);
		u.setMobile(mobile);
		u.setNickName(user.getProperty(ClientConst.USERPROPS_NICKNAME));
		u.setUserName(user.getUserName());
		u.setUserPassword(user.getEncryptedUserPwd());
		u.setUserRemark(user.getProperty(ClientConst.USERPROPS_DESC));
		if(user.getUserName().equals("admin")) u.setUserType(0);
		else u.setUserType(1);
		try {
			boolean success=us.addUser(u);
			if(success){
				UserLogService userLogService=(UserLogService)ObjectContainer.getBean("userLogService");//.getUserLogService();
				userLogService.log(LOG_USER, "创建用户" + u.getUserName(), request.getRemoteAddr());
				//添加到普通用户角色中@changguanghua :不打算在新建用户时立即分配角色
//				ISessionManager sessionManager=ObjectContainer.getSessionManager();
//				IRoleManager roleManager=ObjectContainer.getRoleManager();
//				Role role=roleManager.getRoleByName("普通用户");
//				sessionManager.addNewSession(role.getId(),u.getId());
			}
			return success;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ActorException("无法同步增加用户",e);
		}
	}

	/**
	 * Demo应用禁用用户的实现. 因为Demo应用很简单, 不需要对禁用用户进行 特别处理, 所以本实现仅仅返回<code>true</code>,
	 * 表示禁用成功即可.
	 *
	 * @param user the user
	 * @return true, if successful
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#disableUser(com.trs.idm.client.actor.
	 * SSOUser)
	 */
	public boolean disableUser(SSOUser user) throws ActorException {
		return true;
	}

	/**
	 * Demo应用启用用户的实现. 因为Demo应用很简单, 不需要对启用用户进行 特别处理, 所以本实现仅仅返回<code>true</code>,
	 * 表示启用成功即可.
	 *
	 * @param user the user
	 * @return true, if successful
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#enableUser(com.trs.idm.client.actor.S
	 * SOUser)
	 */
	public boolean enableUser(SSOUser user) throws ActorException {
		return true;
	}

	/**
	 * 从Demo应用的自有登录页面的表单中获取用户名.
	 *
	 * @param request the request
	 * @return the string
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#extractUserName(javax.servlet.http.Ht
	 * tpServletRequest)
	 */
	public String extractUserName(HttpServletRequest request)
			throws ActorException {
		String result = request.getParameter("userName");
		return (result == null) ? "" : result;
	}

	/**
	 * 从Demo应用的自有登录页面的表单中获取密码.
	 *
	 * @param request the request
	 * @return the string
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#extractUserPwd(javax.servlet.http.Htt
	 * pServletRequest)
	 */
	public String extractUserPwd(HttpServletRequest request)
			throws ActorException {
		String result = request.getParameter("password");

		return (result == null) ? "" : result;
	}

	/**
	 * Demo应用同步删除用户的实现. 因为Demo应用很简单, 不存储用户数据, 所以本实现仅仅返回<code>true</code>,
	 * 表示同步成功即可.
	 *
	 * @param user the user
	 * @param request the request
	 * @return true, if successful
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#removeUser(com.trs.idm.client.actor.S
	 * SOUser, javax.servlet.http.HttpServletRequest)
	 */
	public boolean removeUser(SSOUser user, HttpServletRequest request)
			throws ActorException {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("同步删除用户：{}",user);
		}
		if(user.getUserName().equals("admin"))
			return false;
		UserService us=(UserService)ObjectContainer.getBean("UserService");//.getUserService();
		try {
			boolean success=us.deleteUser(user.getUserName());
			if(success){
				UserLogService userLogService=(UserLogService)ObjectContainer.getBean("userLogService");//.getUserLogService();
				userLogService.log(LOG_USER, "删除用户" + user.getUserName(), request.getRemoteAddr());
			}
			return success;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ActorException("无法同步删除用户",e);
		}
	}

	/**
	 * Demo应用同步更新用户的实现. 因为Demo应用很简单, 不存储用户数据, 所以本实现仅仅返回<code>true</code>,
	 * 表示同步成功即可.
	 *
	 * @param user the user
	 * @param request the request
	 * @return true, if successful
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#updateUser(com.trs.idm.client.actor.S
	 * SOUser, javax.servlet.http.HttpServletRequest)
	 */
	public boolean updateUser(SSOUser user, HttpServletRequest request)
			throws ActorException {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("同步更新用户：{}",user);
		}
		UserService us=(UserService)ObjectContainer.getBean("UserService");//.getUserService();
		User u=us.getUser(user.getUserName());
		if(null==u) return false;
		String actived=user.getProperty(ClientConst.USERPROPS_ACTIVED);
		if(StringUtils.isNotBlank(actived))	u.setDisabled(!Boolean.valueOf(actived));
		u.setEmail(user.getMail());
		String mobile=user.getProperty(ClientConst.USERPROPS_MOBILE);
		u.setMobile(mobile);
		u.setNickName(user.getProperty(ClientConst.USERPROPS_NICKNAME));
		u.setUserPassword(user.getEncryptedUserPwd());
		u.setUserRemark(user.getProperty(ClientConst.USERPROPS_DESC));
		boolean success=us.updateUser(u);
		if(success){
			UserLogService userLogService=(UserLogService)ObjectContainer.getBean("userLogService");//.getUserLogService();
			userLogService.log(LOG_USER, "修改用户" + u.getUserName(), request.getRemoteAddr());
		}
		return success;
	}

	/**
	 * Demo应用判断用户是否存在的实现. 因为Demo应用很简单, 不存储用户数 据, 所以本实现仅仅返回<code>true</code>,
	 * 表示用户存在, 使得登录时不必 先调用同步增加用户的方法.
	 *
	 * @param user the user
	 * @return true, if successful
	 * @throws ActorException the actor exception
	 * @see StdHttpSessionBasedActor#userExist(com.trs.idm.client.actor.SS
	 * OUser)
	 */
	public boolean userExist(SSOUser user) throws ActorException {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("判断用户是否存在：{}",user);
		}
		UserService us=(UserService)ObjectContainer.getBean("UserService");//.getUserService();
		try {
			if(us.getUser(user.getUserName())!=null)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ActorException("无法判断用户是否存在",e);
		}
	}

}
