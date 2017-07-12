package com.trs.om.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.PermissionCheck;
import com.trs.om.bean.ResourceAclCriterion;
import com.trs.om.bean.ResourceGroupAcl;
import com.trs.om.bean.ResourceType;
import com.trs.om.bean.ResourceUserAcl;
import com.trs.om.bean.Role;
import com.trs.om.bean.Session;
import com.trs.om.bean.User;
import com.trs.om.bean.UserCriterion;
import com.trs.om.bean.UserGroup;
import com.trs.om.bean.UserKey;
import com.trs.om.common.PermissionConstants;
import com.trs.om.common.ResourceConstants;
import com.trs.om.common.SystemUtil;
import com.trs.om.dao.LoginUserDao;
import com.trs.om.dao.ResourceDao;
import com.trs.om.dao.UserDao;
import com.trs.om.dao.UserKeyDao;
import com.trs.om.exception.TRSOMException;
import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IRoleManager;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.resource.Resource;
import com.trs.om.security.DigestAuthUtils;
import com.trs.om.security.SecurityConstants;
import com.trs.om.security.UserAddEvent;
import com.trs.om.security.UserDisabledEvent;
import com.trs.om.service.EncryptService;
import com.trs.om.service.PermissionService;
import com.trs.om.service.ResourceAclService;
import com.trs.om.service.ResourceService;
import com.trs.om.service.RoleService;
import com.trs.om.service.UserGroupService;
import com.trs.om.service.UserLogService;
import com.trs.om.service.UserService;
import com.trs.om.util.CsvUtil;
import com.trs.om.util.DateUtils;
import com.trs.om.util.PagedArrayList;
import com.trs.otm.authentication.HttpAuthnUtils;
import com.trs.otm.authentication.MD5;

public class UserServiceImpl implements UserService,ApplicationContextAware {

	// fields ---------------------------------------------------------------------
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	private static final String CONFIG_FILENAME = "user_config.xml";

	private String configCharset = "UTF8";
	private UserDao userDao;
	private LoginUserDao loginUserDao;
	private ISessionManager sessionManager;
	private IRoleManager roleManager;
	private UserLogService userLogService;
	private RoleService roleService;
	private UserGroupService userGroupService;
	private PermissionService permissionService;
	private ResourceAclService resourceAclService;
	private ResourceService resourceService;
	private UserKeyDao userKeyDao;
	private ApplicationContext ctx;
	private String digestRealmName;
	/** 默认的分页大小，默认为10. */
	private int defaultPageSize = 10;
	private ResourceDao resourceDao;
//	private IndexPageDao indexPageDao;
//	private IndexPageAssignmentDao pageAssignmentDao;
	// add by changguanghua,在创建用户同时创建用户首页
//	private IndexPageService indexPageService;
//	private ReportWorkFlowStatusService reportWorkFlowStatusService;
//	private KeyPeopleService keyPeopleService;

	public UserServiceImpl() {
		String key = System.getProperty("trsom.key");
		if (StringUtils.isEmpty(key))
			throw new TRSOMException("注册码未填入key.ini文件中！");
		EncryptService obj;
		try {
			obj = EncryptService.getInstance();
			obj.checkLicence();
		}
		catch (Exception e) {
			throw new TRSOMException("无法获得或者校验注册码：" + e.getMessage(), e);
		}
	}

	// methods ---------------------------------------------------------------------
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx=applicationContext;
	}

//	@Override
//	@Transactional
//	public <T extends Resource> Set<Long> batchGetShareableIds(Long userId, Collection<T> resources) {
//		if (resources == null || resources.isEmpty())
//			return Collections.emptySet();
//		return this.pickOperateableResourceIds(userId, PermissionConstants.SHARE, resources);
//	}

	@Override
	@Transactional
	public <T extends Resource> Set<Long> pickOperateableResourceIds(Long userId, String operate, Collection<T> resources) {
		if (resources == null || resources.isEmpty())
			return new HashSet<Long>();
		Set<Long> ids = new HashSet<Long>();// 全部id集合
		for (Resource r : resources) {
			ids.add(r.getId());
		}
		if (SecurityConstants.SYSTEM_ADMIN_ID.equals(userId)) {
			return ids;
		}
		Resource r = resources.iterator().next();
		ResourceType resourceType = (ResourceType) r.getClass().getAnnotation(ResourceType.class);
		String[] permissionString = new String[] { operate, resourceType.typeString() };
		User user=this.get(userId);
		boolean hasPermission = hasPermission(permissionString, user);
		Set<Long> operateable = new HashSet<Long>();
		if (hasPermission) {
			// 找到自己的事件
			operateable.addAll(resourceDao.pickCreatedResourceIds(user.getCreatorId(), ids, r.getClass()));// this.pickResourceIdsByCreator(user,
																									// resources));
			ids.removeAll(operateable);
			// 别人的事件
			if (!ids.isEmpty()) {
				Set<T> others = new HashSet<T>();
				for (T resource : resources) {
					if (!operateable.contains(resource.getId()))
						others.add(resource);
				}
				Collection<Long> otherOperateable =pickOperatableResouceIds(user, operate,
						resources);// .pickOperatableResouceIds(user, operate,
									// resourceType.typeInt(),ids);//.pickOperatableResouceIds(user,permissionString[0],resourceType,ids);
				operateable.addAll(otherOperateable);
			}
		}
		return operateable;
	}

	@Override
	@Transactional
	public <T extends Resource> boolean hasPermission(Long userId, String operate, T resource) {
		if (SecurityConstants.SYSTEM_ADMIN_ID.equals(userId)) {
			return true;
		}
		boolean mine = resource.getCreatorId().equals(userId);
		ResourceType resourceType = (ResourceType) resource.getClass().getAnnotation(ResourceType.class);
		User user=this.get(userId);
		boolean hasPermission = hasPermission(new String[] { operate, resourceType.typeString() }, user);
		if (hasPermission) {
			if (mine)
				return true;
			return hasPermissionFromAcl(userId, operate, resource);
		}
		return false;
	}
	
	private Set<Long> listGroupIdsForPermission(User user, String operate,Resource resource){
		ResourceType resourceType=(ResourceType) resource.getClass().getAnnotation(ResourceType.class);
		return permissionService.listGroupIdsForPermission(user, new String[]{operate,resourceType.typeString()});
	}

	@Override
	@Transactional
	public boolean hasPermissionFromAcl(Long userId,String operate, Resource resource) {
		if(userId.equals(SecurityConstants.SYSTEM_ADMIN_ID))
			return true;
		ResourceAclCriterion resourceAclCriterion=resourceAclService.getCriterionByOperate(operate);//;new ResourceAclCriterion();
		Set<Long> uids=new HashSet<Long>();
		uids.add(userId);
		resourceAclCriterion.setUserId(uids);
		Set<Long> hids=new HashSet<Long>();
		hids.add(resource.getId());
		resourceAclCriterion.setResourceId(hids);
		Set<Long> gids=listGroupIdsForPermission(this.get(userId),operate,resource);
		List<ResourceUserAcl> userAcls=resourceAclService.listResourceUserAclByCriterion(resourceAclCriterion);
		for(ResourceUserAcl ua:userAcls){
			if(gids.contains(ua.getGroupId()))
				return true;
		}
		if(gids!=null&&gids.size()>0){
			resourceAclCriterion.setGroupId(gids);
			List<ResourceGroupAcl> groupAcls=resourceAclService.listResourceGroupAclByCriterion(resourceAclCriterion);
			if(!groupAcls.isEmpty())
				return true;
		}
		return false;
	}
	
	@Override
	@Transactional
	public Set<UserGroup> listAclableGroups(Long userId,Class clazz) {
		Set<UserGroup> userGroups=new HashSet<UserGroup>();
		ResourceType resourceType=(ResourceType) clazz.getAnnotation(ResourceType.class); 
		if(userId.equals(SecurityConstants.SYSTEM_ADMIN_ID)){//admin可以看到系统中全部用户组
			userGroups.addAll(userGroupService.listAll());
		}else{//非admin的用户在设置ACL时能看到的用户组是那些对其可见的用户组
			//考虑到用户可能属于多个组，需要首先列出用户在哪些组下具有共享资源的权限，只有具有共享资源权限的用户才可以设置共享
			Set<Long> ugids=new HashSet<Long>();
			ugids=permissionService.listGroupIdsForPermission(this.get(userId),new String[]{PermissionConstants.SHARE,resourceType.typeString()});
			for(Long ugid:ugids){
				List<UserGroup> gs=userGroupService.listVisibleGroups(ugid);
				UserGroup userg = gs.get(0);
				if(userg.getId() == 3 || userg.getId()==2 ) gs.remove(0);
				userGroups.addAll(gs);
			}
		}
		return userGroups;
	}
	
	@Transactional
	public PagedArrayList<User> getUsersByPageAndName(int page, String name) {
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setUserName(name);
		return userDao.find(userCriterion, new PageCriterion(defaultPageSize, page));
	}


	@Transactional
	public PagedArrayList<User> getUsersByCriterion(int page, UserCriterion userCriterion) {
		return userDao.find(userCriterion, new PageCriterion(defaultPageSize, page));
	}


	@Override
	@Transactional
	public List<User> listUserByNames(Collection<String> userNames) {
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setUserNames(userNames);
		userCriterion.setCheckAccess(false);
		return userDao.find(userCriterion);
	}

	@Transactional
	public PagedArrayList<User> getUsersByPageAndName(int page, String name, String[] excludes) {
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setUserName(name);
		userCriterion.setExcludes(excludes);
		return userDao.find(userCriterion, new PageCriterion(defaultPageSize, page));
	}

	@Transactional
	public PagedArrayList<User> getUsersByPageAndNameInGroups(String name, String[] excludes, int page,
			Set<Long> groupIds) {
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setUserName(name);
		userCriterion.setExcludes(excludes);
		userCriterion.setGroupIds(groupIds);
		return userDao.find(userCriterion, new PageCriterion(defaultPageSize, page));
	}

	@Transactional
	public PagedArrayList<User> getUsersByPage(int page) {
		return userDao.find(null, new PageCriterion(defaultPageSize, page));
	}

	/**
	 * 查询所有用户.
	 *
	 * @return the all users
	 */
	@Transactional
	public List<User> getAllUsers() {
		return userDao.findAll();
	}

	private boolean deleteUser(User user) {
		// 检查是否为预置用户
		if (user.getUserType() == 0) {
			LOGGER.info("阻止了试图删除预置用户的行为");
			return false;
		}
		/*// 删除该用户相应简报
		Long idL = user.getId();
		List<ReportWorkFlowStatus> list = reportWorkFlowStatusService.list(idL);
		Long[] tempLongs = new Long[list.size()];
		if (list != null) {
			for (int j = 0; j < list.size(); j++) {
				tempLongs[j] = list.get(j).getId();
			}
		}
		reportWorkFlowStatusService.delete(tempLongs);
		// 删除用户的登录记录
		LoginUser loginUser = loginUserDao.getByUserName(user.getUserName());
		if (loginUser != null && loginUser.getId() != null) {
			loginUserDao.delete(loginUser);
		}
		// 删除首页指派关系
		IndexPageAssignment pageAssignment = pageAssignmentDao.getByUserId(user.getId());
		if (null != pageAssignment) {
			pageAssignmentDao.makeTransient(pageAssignment);
		}
		// 删除用户的个性化首页
		IndexPage indexPage = indexPageDao.getByUser(user.getId());
		if (null != indexPage) {
			indexPageDao.delete(indexPage);
		}
		// 删除关于该用户的acl控制
		resourceAclService.clearAclByUser(user.getId());
		// 删除非预置用户
		userDao.makeTransient(user);*/
		return true;
	}

	/**
	 * 删除指定用户.
	 *
	 * @param userName the user name
	 */
	@Transactional
	public boolean deleteUser(String userName) {
		if (userName == null || userName.trim().equals(""))
			return false;
		User user = userDao.getByName(userName);
		return deleteUser(user);
	}

	/**
	 * 根据用户名查询指定用户.
	 *
	 * @param userName the user name
	 *
	 * @return the user
	 */
	@Transactional
	public User getUser(String userName) {
		if (userName == null || userName.trim().equals(""))
			return null;
		return userDao.getByName(userName);
	}

	/**
	 * 更新用户信息.
	 *
	 * @param user the user
	 */
	@Transactional
	public boolean updateUser(User user) {
		userDao.makePersistent(user);
		return true;
	}

	/**
	 * 新建用户.
	 *
	 * @param user the user
	 */
	@Transactional
	public boolean addUser(User user) {
		if (user == null)
			return false;
		if (StringUtils.isEmpty(user.getUserName()))
			return false;
		if (user.getCreationDate() == null)
			return false;
		if (StringUtils.isEmpty(user.getUserPassword()))
			return false;
		if (StringUtils.isEmpty(user.getCreator()))
			return false;
		String password=user.getUserPassword();
		user.setUserPassword(MD5.md5(password));
		user.setDigestPassword(MD5.md5(user.getUserName()+":"+digestRealmName+":"+password));
		userDao.makePersistent(user);
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.trs.om.service.UserService#addUser(com.trs.om.bean.User, java.lang.String[], com.trs.om.bean.UserLog)
	 */
	@Transactional
	public String addUser(User user, Long[] roleIds) {
		try {
			User u = userDao.getByName(user.getUserName());
			if (u != null) {
				return SecurityConstants.USER_EXIST;
			}
			String password=user.getUserPassword();
			user.setUserPassword(MD5.md5(password));
			user.setDigestPassword(MD5.md5(user.getUserName()+":"+digestRealmName+":"+password));
			// 添加账户
			userDao.makePersistent(user);
			// 新建用户时不再设置角色，等到用户分配组以后才分配 @changguanghua 2012-5-11 10:37:25
			// if(roleIds!=null){
			// for(Long role:roleIds){
			// sessionManager.addNewSession(role,user.getId());
			// }
			// }
			// 记录日志
			StringBuilder logBuilder = new StringBuilder();
			logBuilder.append("创建新用户").append(user.getUserName()).append("，并附加角色[");
			if (roleIds != null && roleIds.length > 0) {
				for (int i = 0; i < roleIds.length; i++) {
					Role role = roleManager.getRole(roleIds[i]);
					if (i > 0)
						logBuilder.append(',');
					logBuilder.append(role.getName());
				}
			}
			logBuilder.append(']');
			userLogService.log(logBuilder.toString());
			ctx.publishEvent(new UserAddEvent(user));
			return SecurityConstants.SUCCESS;
		}
		catch (AuthorizationException e) {
			throw new TRSOMException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.trs.om.service.UserService#deleteUsersByIds(java.lang.String[], com.trs.om.bean.UserLog)
	 */
	@Transactional
	public void deleteUsersByIds(String[] ids) {
		if (ids != null) {
			StringBuilder logBuilder = new StringBuilder();
			for (int i = 0; i < ids.length; i++) {
				Long idL = Long.valueOf(ids[i]);
				User user = userDao.findById(idL, false);
				if (user.getUserName().equals("admin") || user.getUserName().equals("anonymous"))
					continue;
				deleteUser(user);
				if (i > 0)
					logBuilder.append(',');
				logBuilder.append(user.getUserName());
			}
			userLogService.log("删除用户" + logBuilder.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.trs.om.service.UserService#get(java.lang.Long)
	 */
	@Transactional
	public User get(Long userId) {
		return userDao.findById(userId, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.trs.om.service.UserService#resetUserPassword(com.trs.om.bean.User, com.trs.om.bean.UserLog)
	 */
	@Transactional
	public void resetUserPassword(User user) {
		User u = userDao.findById(user.getId(), false);
		String password=user.getUserPassword();
		u.setUserPassword(MD5.md5(password));
		u.setDigestPassword(DigestAuthUtils.encodePasswordInA1Format(user.getUserName(),digestRealmName,password));
		userDao.makePersistent(u);
		// 记录日志
		userLogService.log("重置用户" + u.getUserName() + "的密码");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.trs.om.service.UserService#updateUserInfo(com.trs.om.bean.User, com.trs.om.bean.UserLog)
	 */
	@Transactional
	public void updateUserInfo(User user) {
		User u = userDao.findById(user.getId(), false);
		u.setEmail(user.getEmail());
		u.setMobile(user.getMobile());
		u.setUserRemark(user.getUserRemark());
		u.setClientIpRestraint(user.getClientIpRestraint());
		userDao.makePersistent(u);
		// 记录日志
		userLogService.log("修改用户" + u.getUserName() + "的基本信息");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.trs.om.service.UserService#updateUserRoles(java.lang.Long, java.lang.String[], com.trs.om.bean.UserLog)
	 */
	// @SuppressWarnings("unchecked")
	// public void updateUserRoles(Long userId, Long[] roleIds) {
	// try{
	// User user = userDao.findById(userId, false);
	// List sessionList=sessionManager.findSessions(userId);
	// for(int i=0;i<sessionList.size();i++){
	// Session mySession=(Session)sessionList.get(i);
	// Role role=roleManager.getRole(mySession.getRoleId());
	// if("admin".equals(user)&&"系统管理员".equals(role.getName()))
	// continue;
	// sessionManager.deleteSession(mySession.getId());
	// }
	// if((roleIds!=null)&&(!roleIds[0].equals(""))){
	// for(Long role:roleIds){
	// sessionManager.addNewSession(role,userId);
	// }
	// }
	// //记录日志
	// userLogService.log("修改用户"+user.getUserName()+"的角色");
	// }catch(AuthorizationException e){
	// throw new TRSOMException(e);
	// }
	// }

	@Override
	@Transactional
	public void updateUserRoles(Long userId, Map<Long, Set<Long>> rolesInGroup, Long[] groups) {
		User user = this.get(userId);
		if (groups == null)
			return;
		for (Long groupId : groups) {// 删除用户在这个组中原来的角色配置
			try {
				List sessionList = sessionManager.findSessionsByUserAndGroup(userId, groupId);
				for (int i = 0; i < sessionList.size(); i++) {
					Session mySession = (Session) sessionList.get(i);
					Role role = roleManager.getRole(mySession.getRoleId());
					if (SecurityConstants.SYSTEM_DEFAULT_ADMIN.equals(user.getUserName())
							&& "系统管理员".equals(role.getName()))
						continue;
					sessionManager.deleteSession(mySession.getId());
				}
			}
			catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}
		for (Long groupId : rolesInGroup.keySet()) {
			Set<Long> roleIds = rolesInGroup.get(groupId);
			if (roleIds != null && roleIds.size() > 0) {
				try {
					for (Long rid : roleIds) {
						sessionManager.addNewSession(rid, userId, groupId);
					}

				}
				catch (AuthorizationException e) {
					throw new TRSOMException(e);
				}
			}
		}
		// 记录日志
		userLogService.log("修改用户" + user.getUserName() + "的角色");
	}

	// @Override
	// public void removeGroupManager(Long groupId) {
	// Role role=roleService.getRoleByName(RoleService.DEFAULT_GROUP_ADMIN);
	// try {
	// List<Session> session=sessionManager.findSessionsByRoleAndGroup(role.getId(),groupId);
	// if(session!=null&&session.size()>0)
	// for(Session s:session){
	// this.sessionManager.deleteSession(s.getId());
	// }
	// } catch (AuthorizationException e) {
	// e.printStackTrace();
	// }
	// }
	@Transactional
	public List<Long> getGroupIds(Long userId) {
		User user = userDao.findById(userId, false);
		Set<UserGroup> groupSet = user.getUserGroups();
		Iterator<UserGroup> it = groupSet.iterator();
		List<Long> rList = new ArrayList<Long>();
		while (it.hasNext()) {
			UserGroup g = it.next();
			rList.add(g.getId());
		}
		return rList;
	}

	@Transactional
	public void configUserGroups(Long userId, String groupIds) {
		User user = userDao.findById(userId, false);
		String[] ids = groupIds.split(",");
		Set<UserGroup> previousGroups = user.getUserGroups();
		Set<Long> deletedGids = new HashSet<Long>();
		for (UserGroup ug : previousGroups) {
			Long gid = ug.getId();
			boolean deleted = true;
			for (String id : ids) {
				if (id.equals(String.valueOf(gid))) {
					deleted = false;
					break;
				}
			}
			if (deleted)
				deletedGids.add(gid);
		}

		for (Long groupId : deletedGids) {
			// 删除用户在该组下的所有角色赋予
			try {
				List<Session> ss = sessionManager.findSessions(userId, null, groupId);
				for (Session s : ss) {
					sessionManager.deleteSession(s.getId());
				}
			}
			catch (AuthorizationException e) {
				e.printStackTrace();
			}

		}
		// 删除用户在用户组下的访问控制
		if (!deletedGids.isEmpty()) {
			ResourceAclCriterion resourceAclCriterion = new ResourceAclCriterion();
			Set<Long> userIds = new HashSet<Long>();
			userIds.add(userId);
			resourceAclCriterion.setUserId(userIds);
			resourceAclCriterion.setGroupId(deletedGids);
			resourceAclService.batchDeleteUserAcls(resourceAclCriterion);
		}
		user.getUserGroups().clear();
		for (int i = 0; i < ids.length; i++) {
			if (StringUtils.isNotBlank(ids[i])) {
				Long id = Long.valueOf(ids[i]);
				UserGroup ug = userGroupService.get(id);
				user.getUserGroups().add(ug);
			}
		}
		userDao.makePersistent(user);
	}

	@Transactional
	public void disableUsers(String ids) {
		String[] ss = ids.split(",");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] != null && ss[i].trim().length() > 0) {
				Long id = Long.valueOf(ss[i].trim());
				User u = userDao.findById(id, false);
				if ("admin".equals(u.getUserName()))
					continue;
				u.setDisabled(true);
				userDao.makePersistent(u);
				if (i > 0) {
					sb.append(",").append(u.getUserName());
				}
				else {
					sb.append(u.getUserName());
				}
				ctx.publishEvent(new UserDisabledEvent(u));
			}
		}
		userLogService.log("停用用户[" + sb.toString() + "]");
	}

	@Transactional
	public void enableUsers(String ids) {
		String[] ss = ids.split(",");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] != null && ss[i].trim().length() > 0) {
				Long id = Long.valueOf(ss[i].trim());
				User u = userDao.findById(id, false);
				u.setDisabled(false);
				userDao.makePersistent(u);
				if (i > 0) {
					sb.append(",").append(u.getUserName());
				}
				else {
					sb.append(u.getUserName());
				}

			}
		}
		userLogService.log("重新启用用户[" + sb.toString() + "]");
	}

	@Transactional
	public User getWithGroupsUsers(Long userId) {
		User u = userDao.findById(userId, false);
		Set<UserGroup> set = u.getUserGroups();
		Iterator<UserGroup> it = set.iterator();
		while (it.hasNext()) {
			UserGroup gh = it.next();
			gh.getUsers().size();// ×××××调用函数使懒加载
		}
		return u;
	}

	@Transactional
	public User getWithAllGroupsUsers(Long userId) {
		PagedArrayList<UserGroup> groups = userGroupService.listAll(1, 9999);

		Set<UserGroup> set = new HashSet<UserGroup>();
		for (int i = 0; i < groups.getSize(); i++) {
			UserGroup ug = groups.get(i);
			ug.getUsers().size();
			set.add(ug);
		}
		User uu = new User();
		uu.setUserGroups(set);
		return uu;
	}

	@Override
	@Transactional
	public List<User> listUsersInGroup(Long groupId) {
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setUgroupId(groupId);
		return this.listUsers(userCriterion);
	}

	@Override
	@Transactional
	public String generateAppendStringForGroupUsers(Long groupId) {
		List<User> users = this.listUsersInGroup(groupId);
		StringBuilder sb = new StringBuilder();
		for (User user : users) {
			String tag = "u" + user.getId() + "-" + groupId;
			sb.append("<tr class=\"groupUser_" + groupId + "\"><td width=\"40%\" style=\"border-left:0px;\" title=\"")
					.append(user.getUserName()).append("\">").append(user.getUserName()).append("</td>")
					.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"userId\"");
			sb.append("checked=\"checked\"");
			sb.append(" id=\"").append(tag + "_d\" onclick=\"check_d('" + tag + "')\"/></td>");
			sb.append("</tr>");
		}
		return sb.toString();
	}

	@Override
	@Transactional
	public Map<Long, String> batchGetUserNames(Set<Long> userIds) {
		Map<Long, String> id2name = new HashMap<Long, String>();
		if (userIds == null || userIds.isEmpty())
			return id2name;
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setCheckAccess(false);
		userCriterion.setInIds(userIds);
		List<User> users = this.listUsers(userCriterion);

		for (User user : users) {
			id2name.put(user.getId(), user.getUserName());
		}
		for (Long userId : userIds) {
			if (!id2name.containsKey(userId))
				id2name.put(userId, "用户不存在或已删除");
		}
		return id2name;
	}

	@Override
	@Transactional
	public Map<Long, String> batchGetUserNicknames(Set<Long> userIds) {
		Map<Long, String> id2name = new HashMap<Long, String>();
		if (userIds == null || userIds.isEmpty())
			return id2name;
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setCheckAccess(false);
		userCriterion.setInIds(userIds);
		List<User> users = this.listUsers(userCriterion);

		for (User user : users) {
			id2name.put(user.getId(), user.getNickName());
		}
		for (Long userId : userIds) {
			if (!id2name.containsKey(userId))
				id2name.put(userId, "用户不存在或已删除");
		}
		return id2name;
	}
	

	@Override
	@Transactional
	public List<User> listUsers(UserCriterion userCriterion) {
		if (userCriterion.isCheckAccess()) {
			if (!HttpAuthnUtils.isAdmin()) {
				User user = this.get(HttpAuthnUtils.getLoginUserId());
				Set<Long> groupIds = permissionService.listGroupIdsForPermission(user,
						PermissionConstants.VIEW_USER_PERMISSION);
				userCriterion.setGroupIds(groupIds);
			}
		}
		return userDao.find(userCriterion);
	}

	@Transactional
	public PagedArrayList<User> listUsers(String name, Long groupId, Collection<Long> excludeIds, int dbpage,
			int pageSize) {
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setUserName(name);
		userCriterion.setUgroupId(groupId);
		userCriterion.setExcludeIds(excludeIds);
		return userDao.find(userCriterion, new PageCriterion(pageSize, dbpage));
	}

	@Override
	@Transactional
	public Set<User> listUsersInGroupsWithRoles(List<Long> groupIds, List<Long> roleIds) {
		Set<User> users = new HashSet<User>();
		List<Session> sessions = new ArrayList<Session>();
		try {
			sessions = (List<Session>) sessionManager.findSessions(null, roleIds, groupIds);
		}
		catch (AuthorizationException e) {
			e.printStackTrace();
		}
		for (Session s : sessions) {
			Long userId = s.getUserId();
			if (userId != null)
				users.add(this.get(userId));
		}
		return users;
	}

	@Override
	@Transactional
	public Set<User> listUsersInGroupWithoutRole(Long groupId, Long roleId) {
		// 下一步优化此代码，改用dao一次关联检索
		Set<User> inUsers = this.listUsersInGroupWithRole(groupId, roleId);
		List<User> groupUsers = this.listUsersInGroup(groupId);
		Set<User> outUsers = new HashSet<User>();
		if (groupUsers != null && !groupUsers.isEmpty()) {
			groupUsers.removeAll(inUsers);
		}
		outUsers.addAll(groupUsers);
		return outUsers;
	}

	@Override
	@Transactional
	public Set<User> listUsersInGroupWithRole(Long groupId, Long roleId) {
		Set<User> users = new HashSet<User>();
		List<Session> sessions = new ArrayList<Session>();
		try {
			sessions = (List<Session>) sessionManager.findSessions(null, roleId, groupId);
		}
		catch (AuthorizationException e) {
			e.printStackTrace();
		}
		for (Session s : sessions) {
			Long userId = s.getUserId();
			if (userId != null)
				users.add(this.get(userId));
		}
		return users;
	}

	@Override
	@Transactional
	public Set<User> listUsersInGroupWithPermission(Long groupId, String[] permissionString) {
		Set<User> users = new HashSet<User>();
		Set<Long> roleIds = permissionService.listRoleIdsForPermission(permissionString);
		for (Long roleId : roleIds) {
			try {
				List<Session> sessions = sessionManager.findSessions(null, roleId, groupId);
				for (Session s : sessions) {
					users.add(this.get(s.getUserId()));
				}
			}
			catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}
		return users;
	}

	@Override
	@Transactional
	public Set<User> listUsersInGroupWithoutPermission(Long groupId, String[] permissionString) {
		Set<User> users = new HashSet<User>();
		UserGroup userGroup = this.userGroupService.get(groupId);
		if (userGroup != null) {
			List<User> groupUsers = this.listUsersInGroup(groupId);
			if (!groupUsers.isEmpty()) {
				Set<User> inUsers = this.listUsersInGroupWithPermission(groupId, permissionString);
				groupUsers.removeAll(inUsers);
				users.addAll(groupUsers);
			}
		}
		return users;
	}

	@Override
	@Transactional
	public boolean hasPermission(String[] permissionString, User user) {
		return this.hasPermission(permissionString,user,null);
	}

	@Override
	@Transactional
	public boolean hasPermission(String[] permissionString, User user, Long groupId) {
		if (SecurityConstants.SYSTEM_DEFAULT_ADMIN.equals(user.getUserName()))
			return true;
		if (groupId != null) {
			UserGroup userGroup = userGroupService.get(groupId);
			if (userGroup != null) {
				if (userGroupService.isGroupDisabled(userGroup))
					return false;
			}
			else {
				return false;
			}
		}
		return userDao.hasPermission(permissionString, user, groupId);
	}

	@Override
	@Transactional
	public boolean hasDataPermission(Long userId, Long groupId) {
		User user = this.get(userId);
		if (user != null)
			return userDao.hasPermission(PermissionConstants.DATA_PERMISSION, user, groupId);
		else
			return false;
	}

	@Transactional
	public boolean changeDueTime(Long userId, Date dueTime) {
		User u = userDao.findById(userId, false);
		u.setDueTime(dueTime);
		userDao.makePersistent(u);
		return true;
	}

	// 批量修改到期时间
	@Transactional
	public boolean changeEpireTimes(String ids, Date dueTime) {
		String[] groupuserIds = ids.split(",");
		for (int i = 0; i < groupuserIds.length; i++) {
			if (groupuserIds[i] != null && groupuserIds[i].trim().length() > 0) {
				Long userId = Long.valueOf(groupuserIds[i].trim());
				this.changeDueTime(userId, dueTime);
			}
			else {
				return false;
			}
		}
		return true;
	}

	// 批量修改组配置
	@Transactional
	public void ChangeGroupConfigs(String ids, String groupIds) {
		String[] groupuserIds = ids.split(",");
		for (int i = 0; i < groupuserIds.length; i++) {
			if (groupuserIds[i] != null && groupuserIds[i].trim().length() > 0) {
				Long userId = Long.valueOf(groupuserIds[i].trim());
				this.configUserGroups(userId, groupIds);
			}
		}
	}

	// 批量修改角色
	// public void ChangeRoleConfigs(String ids, String roleIds) {
	// String[] groupuserIds = ids.split(",");
	// String[] grouproleIds = roleIds.split(",");
	// Long[] rids=new Long[grouproleIds.length];
	// for(int i=0;i<grouproleIds.length;i++){
	// if(StringUtils.isNotBlank(grouproleIds[i]))
	// rids[i]=Long.valueOf(grouproleIds[i]);
	// }
	// for(int i=0;i<groupuserIds.length;i++){
	// if(groupuserIds[i]!=null&&groupuserIds[i].trim().length()>0){
	// Long userId = Long.valueOf(groupuserIds[i].trim());
	// User user = userDao.findById(userId, false);
	// if(user.getUserName().equals("admin")) //admin用户不可配置用户组
	// continue;
	// this.updateUserRoles(userId, rids);
	// }
	// }
	// }

	@Transactional
	public PagedArrayList<User> getUsersByPageAndDate(int page, String date) {
		UserCriterion userCriterion = new UserCriterion();
		String fmt = "yyyy-MM-dd";
		SimpleDateFormat dateFmt = DateUtils.getChineseSimpleDateFormat(fmt);
		try {
			userCriterion.setDueTime(dateFmt.parse(date));
		}
		catch (ParseException e) {
			throw new TRSOMException("日期" + date + "不符合格式串" + fmt, e);
		}
		return userDao.find(userCriterion, new PageCriterion(defaultPageSize, page));
	}

	@Transactional
	public void resetUserRetryCount(String ids) {
		String[] ss = ids.split(",");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] != null && ss[i].trim().length() > 0) {
				Long id = Long.valueOf(ss[i].trim());
				User u = userDao.findById(id, false);
				u.setRetryCount(0);
				userDao.makePersistent(u);
				if (i > 0) {
					sb.append(",").append(u.getUserName());
				}
				else {
					sb.append(u.getUserName());
				}

			}
		}
		userLogService.log("重置用户登录次数[" + sb.toString() + "]");
	}

	@Transactional
	public PagedArrayList<User> listUsers(UserCriterion userCriterion, PageCriterion pageCriterion) {
		if (userCriterion.isCheckAccess()) {
			if (!HttpAuthnUtils.isAdmin()) {
				User user = this.get(HttpAuthnUtils.getLoginUserId());
				Set<Long> groupIds = permissionService.listGroupIdsForPermission(user,
						PermissionConstants.VIEW_USER_PERMISSION);
				userCriterion.setGroupIds(groupIds);
			}
		}
		return userDao.find(userCriterion, pageCriterion);
	}

	// accessors ---------------------------------------------------------------------
	/* begin adding by changguanghua */
//	public void setIndexPageService(IndexPageService indexPageService) {
//		this.indexPageService = indexPageService;
//	}
//
//	public IndexPageService getIndexPageService() {
//		return indexPageService;
//	}

	/* finish adding by changguanghua */
	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public LoginUserDao getLoginUserDao() {
		return loginUserDao;
	}

	public void setLoginUserDao(LoginUserDao loginUserDao) {
		this.loginUserDao = loginUserDao;
	}

//	public IndexPageDao getIndexPageDao() {
//		return indexPageDao;
//	}
//
//	public void setIndexPageDao(IndexPageDao indexPageDao) {
//		this.indexPageDao = indexPageDao;
//	}
//
//	public IndexPageAssignmentDao getPageAssignmentDao() {
//		return pageAssignmentDao;
//	}
//
//	public void setPageAssignmentDao(IndexPageAssignmentDao pageAssignmentDao) {
//		this.pageAssignmentDao = pageAssignmentDao;
//	}

	public UserLogService getUserLogService() {
		return userLogService;
	}

	public void setUserLogService(UserLogService userLogService) {
		this.userLogService = userLogService;
	}

	public int getDefaultPageSize() {
		return defaultPageSize;
	}

	public void setDefaultPageSize(int defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}

	@Transactional
	public void exportUsers(String ids, int type, File configRoot) {
		List<User> users = new ArrayList<User>();
		if (StringUtils.isEmpty(ids)) {
			users = userDao.findAll();
		}
		else {
			String[] ss = ids.split(",");
			for (int i = 0; i < ss.length; i++) {
				if (ss[i] != null && ss[i].trim().length() > 0) {
					Long id = Long.valueOf(ss[i].trim());
					User user = userDao.findById(id, false);
					users.add(user);
				}
			}
		}
		StringBuffer sb = new StringBuffer();
		Set<UserGroup> relatedGroups = new HashSet<UserGroup>();
		Set<Session> relatedSesions = new HashSet<Session>();
		Set<Role> relatedRoles = new HashSet<Role>();
		// sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><user_config>");//user_config
		sb.append("<users>");// users
		for (User user : users) {
			sb.append("<user userName=\"").append(user.getUserName()).append("\"");
			sb.append(" userPassword=\"").append(user.getUserPassword()).append("\"");
			sb.append(" userType=\"").append(user.getUserType()).append("\"");
			sb.append(" creator=\"").append(user.getCreator()).append("\"");
			sb.append(" creationDate=\"").append(user.getCreationDate()).append("\"");
			sb.append(" disabled=\"").append(user.getDisabled()).append("\"");
			sb.append(" retryCount=\"").append(user.getRetryCount()).append("\">");
			if (!StringUtils.isEmpty(user.getNickName()))
				sb.append("<nickName><![CDATA[").append(user.getNickName()).append("]]></nickName>");
			if (!StringUtils.isEmpty(user.getUserRemark()))
				sb.append("<userRemark><![CDATA[").append(user.getUserRemark()).append("]]></userRemark>");
			if (!StringUtils.isEmpty(user.getEmail()))
				sb.append("<email>").append(user.getEmail()).append("</email>");
			if (!StringUtils.isEmpty(user.getMobile()))
				sb.append("<mobile>").append(user.getMobile()).append("</mobile>");
			if (user.getDueTime() != null)
				sb.append("<dueTime>").append(user.getDueTime()).append("</dueTime>");
			if (type == 1 || type == 3) {// 包含用户组信息
				if (user.getUserGroups() != null && !user.getUserGroups().isEmpty()) {
					relatedGroups.addAll(user.getUserGroups());
					sb.append("<usergroup><![CDATA[");
					StringBuilder sg = new StringBuilder();
					for (UserGroup ug : user.getUserGroups()) {
						sg.append(ug.getGroupName()).append(",");
					}
					sg.deleteCharAt(sg.length() - 1);
					sb.append(sg).append("]]></usergroup>");// usergroup
				}
			}
			if (type == 2 || type == 3) {// 如果需要导出用户角色信息
				try {
					List<Session> sessionList = sessionManager.findSessionsByUser(user.getId());
					if (sessionList != null && !sessionList.isEmpty()) {
						relatedSesions.addAll(sessionList);
						sb.append("<session><![CDATA[");
						StringBuilder sr = new StringBuilder();
						if (sessionList != null && !sessionList.isEmpty()) {
							for (Session session : sessionList) {
								Role role = roleService.getRole(session.getRoleId());
								sr.append(role.getName()).append(",");
								relatedRoles.add(role);// 获得每个Role的数据
							}
							sr.deleteCharAt(sr.length() - 1);
						}
						sb.append(sr).append("]]></session>");// session
					}
				}
				catch (AuthorizationException e) {
					throw new TRSOMException(e.getMessage());
				}
			}
			sb.append("</user>");// user
		}
		sb.append("</users>");// users
		StringBuilder gb = new StringBuilder();
		if (type == 1 || type == 3) {// 包含用户组信息
			if (relatedGroups != null && !relatedGroups.isEmpty()) {
				gb.append("<groups>");
				for (UserGroup group : relatedGroups) {
					gb.append("<group groupName=\"").append(StringEscapeUtils.escapeXml(group.getGroupName()))
							.append("\"");
					gb.append(" createDate=\"").append(group.getCreateDate()).append("\"");
					gb.append(" disabled=\"").append(group.isDisabled()).append("\"");
					gb.append("/>");// group
				}
				gb.append("</groups>");// groups
			}
		}
		StringBuilder rb = new StringBuilder();
		if (type == 2 || type == 3) {
			if (relatedRoles != null && !relatedRoles.isEmpty()) {
				rb.append("<roles>");
				for (Role role : relatedRoles) {
					rb.append("<role name=\"").append(StringEscapeUtils.escapeXml(role.getName())).append("\"");
					;
					rb.append(" desc=\"").append(StringEscapeUtils.escapeXml(role.getDesc())).append("\"");
					;
					rb.append("/>");// role
				}
				rb.append("</roles>");// roles
			}
		}
		BufferedWriter userWriter = null;
		try {
			userWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(configRoot,
					CONFIG_FILENAME)), configCharset));
			userWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><user_config source=\"30\">");
			userWriter.append(rb).append(gb).append(sb);
			userWriter.append("</user_config>");// user_config
		}
		catch (UnsupportedEncodingException e) {
			throw new TRSOMException(e);
		}
		catch (FileNotFoundException e) {
			throw new TRSOMException(e);
		}
		catch (IOException e) {
			throw new TRSOMException(e);
		}
		finally {
			if (null != userWriter)
				try {
					userWriter.close();
				}
				catch (IOException e) {
					LOGGER.error("无法关闭输出流", e);
				}
		}
		for (User user : users) {
			userLogService.log("导出用户[" + user.getUserName().toString() + "]数据");
		}
	}

	@Override
	@Transactional
	public String importUsersFromCsv(File configRoot) {
		Set<String> userNames=new HashSet<String>();
		List<String> dupUserNames=new ArrayList<String>();
		List<String> existUserNames=new ArrayList<String>();
		List<String> groupNames=new ArrayList<String>();
		Set<String> gnames=new HashSet<String>();
		List<String> notExistGroupNames=new ArrayList<String>();
		Map<String,UserGroup> gname2g=new HashMap<String,UserGroup>();
		CsvUtil c;
		try {
			c = new CsvUtil(configRoot.getAbsolutePath());
			List<List<String>> csvList=c.readCSVFile();
			for(int i=1;i<csvList.size();i++){//第一行不读
				List<String> rows=csvList.get(i);
				if(!userNames.contains(rows.get(0))){
					userNames.add(rows.get(0));
				}else{
					dupUserNames.add(rows.get(0));
				}
				String gn=rows.get(2);
				if(StringUtils.isNotBlank(gn)){
					groupNames.add(gn);
					String[] gs=gn.split(",");
					for(String g:gs){
						gnames.add(g);
					}
				}
			}
			List<User> users=this.listUserByNames(userNames);
			if(!users.isEmpty()){
				for(User u:users){
					existUserNames.add(u.getUserName());
				}
			}
			if(!gnames.isEmpty()){
				for(String gname:gnames){
					UserGroup ug=userGroupService.getByFullPath(gname);
					if(ug==null)
						notExistGroupNames.add(gname);
					else
						gname2g.put(gname, ug);
				}
			}
			StringBuilder sb=new StringBuilder();
			String existUsers=null;
			String dupUsers=null;
			String notexistGroup=null;
			if(!existUserNames.isEmpty())
				existUsers=ArrayUtils.toString(existUserNames);
			if(!dupUserNames.isEmpty())
				dupUsers=ArrayUtils.toString(dupUserNames);
			if(!notExistGroupNames.isEmpty())
				notexistGroup=ArrayUtils.toString(notExistGroupNames);
			if(StringUtils.isNotBlank(existUsers)||StringUtils.isNotBlank(dupUsers)||StringUtils.isNotBlank(notexistGroup)){
				if(StringUtils.isNotBlank(existUsers)){
					sb.append("以下用户名在系统中已存在:").append(existUsers).append("\r\n");
				}
				if(StringUtils.isNotBlank(dupUsers)){
					sb.append("以下用户名在CSV文件中重复:").append(dupUsers).append("\r\n");
				}
				if(StringUtils.isNotBlank(notexistGroup)){
					sb.append("以下用户组在系统中不存在:").append(notexistGroup);
				}
				return sb.toString();
			}
			for(int i=1;i<csvList.size();i++){
				List<String> row=csvList.get(i);
				User user=new User();
				user.setUserName(row.get(0));
				user.setUserPassword(row.get(1));
				user.setNickName(row.get(3));
				user.setMobile(row.get(4));
				user.setEmail(row.get(5));
				user.setUserRemark(row.get(6));
				user.setCreationDate(new Date());
				user.setCreatorId(HttpAuthnUtils.getLoginUserId());
				user.setCreator(HttpAuthnUtils.getLoginUserName());
				user.setCreateTime(new Date());
				//user.setUserGroups(userGroups)
				//User nuser=this.getUser(row.get(0));
				String gs=row.get(2);
				if(!StringUtils.isEmpty(gs)){
					String [] gns=gs.split(",");
					Set<UserGroup> gids=new HashSet<UserGroup>();
					for(String g:gns){
						gids.add(gname2g.get(g));
					}
					user.setUserGroups(gids);
//					userDao.makePersistent(nuser);
//					String groupIds=ArrayUtils.toString(gids);
//					groupIds=groupIds.substring(1,groupIds.length()-1);
//					this.configUserGroups(nuser.getId(), groupIds);
				}
				this.addUser(user);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}
	@Transactional
	public String importUsers(int type, File configRoot) {
		SAXReader saxReader = new SAXReader();
		List<User> importUsers = new ArrayList<User>();
		List<Role> importRoles = new ArrayList<Role>();
		List<UserGroup> importGroups = new ArrayList<UserGroup>();// 这三个List保存将被插入的数据
		List<User> updateUsers = new ArrayList<User>();
		List<Role> updateRoles = new ArrayList<Role>();
		List<UserGroup> updateGroups = new ArrayList<UserGroup>();// 这三个List保存将被更新的数据
		StringBuilder sb = new StringBuilder();
		// Map<String,String> roleMap=new HashMap<String,String>();
		// Map<String,Long> groupMap=new HashMap<String,Long>();
		Map<String, String> sessionMap = new HashMap<String, String>();
		Map<String, String> userGroupMap = new HashMap<String, String>();
		try {
			Document document = saxReader.read(configRoot);
			Element root = document.getRootElement();
			List<Element> nodes = root.elements();
			if (!nodes.isEmpty()) {
				for (Element node : nodes) {
					if (node.getName().equals("roles")) {// 有Role的配置，先配置Role
						List<Element> roles = node.elements();
						for (Element role : roles) {
							Role r = new Role();
							String roleName = role.attribute("name").getValue();// .elementText("name");
							Role originRole = roleService.getRoleByName(roleName);// 判断是否存在重名
							if (originRole != null) {// 存在重名，进行重名处理
								if (type == 0) {
									sb.append("角色[" + roleName + "]名称已存在;\n");
									continue;
								}
								else if (type == 1) {// 不考虑重名数据，利用原有数据
									// roleMap.put(roleName, originRole.getId());
									continue;
								}
								else if (type == 2) {// 重名数据覆盖原有同名数据
									// originRole.setName(roleName);
									originRole.setDesc(role.attribute("desc").getValue());
									updateRoles.add(originRole);
									continue;
								}
							}
							else {// 不存在重名
								r.setName(roleName);
								r.setDesc(role.attribute("desc").getValue());
								importRoles.add(r);
							}
						}
						// node.detach();//删除该node，不再分析
					}
					else if (node.getName().equals("groups")) {// 有用户组的配置，先配置用户组
						List<Element> groups = node.elements();
						for (Element group : groups) {
							UserGroup ug = new UserGroup();
							String groupName = group.attribute("groupName").getValue();
							UserGroup originGroup = userGroupService.getByName(groupName);
							if (originGroup != null) {
								if (type == 0) {
									sb.append("用户组[" + groupName + "]名称已存在;\n");
									continue;
								}
								else if (type == 1) {// 不考虑重名数据，利用原有数据
									// groupMap.put(groupName,originGroup.getId());
									continue;
								}
								else if (type == 2) {// 重名数据覆盖原有同名数据
									// originGroup.setGroupName(groupName);
									originGroup.setCreateDate(new Date());
									originGroup
											.setDisabled(group.attribute("disabled").getValue().equals("true") ? true
													: false);
									updateGroups.add(originGroup);
									continue;
								}
								sb.append("用户组[" + groupName + "]名称已存在;\n");
								continue;
							}
							else {// 不存在重名
								ug.setGroupName(groupName);
								ug.setCreateDate(new Date());
								ug.setDisabled(group.attribute("disabled").getValue().equals("true") ? true : false);
								importGroups.add(ug);
							}
						}
						// node.detach();//删除该node，不再分析
					}
					else if (node.getName().equals("users")) {
						List<Element> users = node.elements();
						for (Element user : users) {
							User u = new User();
							String userName = user.attribute("userName").getValue();
							User originUser = this.getUser(userName);
							if (originUser != null) {
								if (type == 0) {
									sb.append("用户[" + userName + "]名称已存在\n");
									continue;
								}
								else if (type == 1) {// 不考虑重名数据，利用原有数据
									// importUsers.add(originUser);//直接不更新该用户数据
									continue;
								}
								else if (type == 2) {// 重名数据覆盖原有同名数据
									originUser.setUserName(userName);
									originUser.setCreationDate(new Date());
									originUser.setUserPassword(user.attribute("userPassword").getValue());
									originUser.setUserType(Integer.valueOf(user.attribute("userType").getValue()));
									originUser.setCreator(user.attribute("creator").getValue());
									originUser.setDisabled(user.attribute("disabled").equals("true") ? true : false);
									originUser.setRetryCount(Integer.parseInt(user.attribute("retryCount").getValue()));
									if (user.element("email") != null)
										originUser.setEmail(user.elementText("email"));
									if (user.element("mobile") != null)
										originUser.setMobile(user.elementText("mobile"));
									if (user.element("nickName") != null)
										originUser.setNickName(user.elementText("nickName"));
									if (user.element("dueTime") != null) {
										SimpleDateFormat fmt = DateUtils.getChineseSimpleDateFormat("yyyy-MM-dd");
										originUser.setDueTime(fmt.parse(user.elementText("dueTime")));
									}
									if (user.element("userRemark") != null)
										originUser.setEmail(user.elementText("userRemark"));
									if (user.element("session") != null) {// 角色配置
										String sessions = user.elementText("session");
										sessionMap.put(userName, sessions);
									}
									if (user.element("usergroup") != null) {// 用户组配置
										String usergroups = user.elementText("usergroup");
										/*
										 * String[] ugg=usergroups.split(","); Set<UserGroup> ug=new
										 * HashSet<UserGroup>(); for(int i=0;i<ugg.length;i++){
										 * if(groupMap.containsKey(ugg[i])){//原有系统的用户组
										 * ug.add(userGroupService.get(groupMap.get(ugg[i]))); }else{ UserGroup
										 * g=userGroupService.getByName(ugg[i]); ug.add(g); userGroupMap.put(userName,
										 * usergroups); } }
										 */
										userGroupMap.put(userName, usergroups);
										// originUser.setUserGroups(ug);
									}
									updateUsers.add(originUser);
									continue;
								}
							}
							else {// 不存在重名
								u.setUserName(userName);
								u.setCreationDate(new Date());
								u.setUserPassword(user.attribute("userPassword").getValue());
								u.setUserType(Integer.valueOf(user.attribute("userType").getValue()));
								u.setCreator(user.attribute("creator").getValue());
								u.setDisabled(user.attribute("disabled").equals("true") ? true : false);
								u.setRetryCount(Integer.parseInt(user.attribute("retryCount").getValue()));
								if (user.element("email") != null)
									u.setEmail(user.elementText("email"));
								if (user.element("mobile") != null)
									u.setMobile(user.elementText("mobile"));
								if (user.element("nickName") != null)
									u.setNickName(user.elementText("nickName"));
								if (user.element("dueTime") != null) {
									SimpleDateFormat fmt = DateUtils.getChineseSimpleDateFormat("yyyy-MM-dd");
									u.setDueTime(fmt.parse(user.elementText("dueTime")));
								}
								if (user.element("userRemark") != null)
									u.setEmail(user.elementText("userRemark"));
								if (user.element("session") != null) {// 角色配置
									String sessions = user.elementText("session");
									sessionMap.put(userName, sessions);
								}
								if (user.element("usergroup") != null) {// 用户组配置
									String usergroups = user.elementText("usergroup");
									userGroupMap.put(userName, usergroups);
									/*
									 * String[] ugg=usergroups.split(","); Set<UserGroup> ug=new HashSet<UserGroup>();
									 * for(int i=0;i<ugg.length;i++){ UserGroup g=userGroupService.getByName(ugg[i]);
									 * ug.add(g); } u.setUserGroups(ug);
									 */
								}
								importUsers.add(u);
							}
							// node.detach();//删除该node，不再分析
						}
					}

				}
			}
			else {
				sb.append("没有可导入的用户数据");
			}
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		if (sb.length() == 0) {// 检查无误后批量存入数据库中
			if (type == 2) {// 只有覆盖导入时才可能存在更新数据
				if (!updateRoles.isEmpty()) {
					for (Role r : updateRoles) {
						roleService.updateRole(r);
					}
				}
				if (!updateGroups.isEmpty()) {
					for (UserGroup ug : updateGroups) {
						userGroupService.updateUserGroup(ug);
					}
				}

			}
			if (!importRoles.isEmpty()) {
				for (Role r : importRoles) {
					String roleName = r.getName();
					roleService.addRole(r);
					// Role rnew=roleService.getRoleByName(roleName);
					// roleMap.put(roleName, rnew.getId());//方便生成usergroup对应。
				}
			}
			if (!importGroups.isEmpty()) {
				for (UserGroup ug : importGroups) {
					userGroupService.addUserGroup(ug);
				}
			}
			if (!updateUsers.isEmpty()) {// 更新用户的用户组和角色均有可能是更新的或者新增的，故只能在此四个数据存好之后进行
				for (User user : updateUsers) {
					String userName = user.getUserName();
					if (sessionMap.containsKey(userName)) {// 更新数据中包含该用户的角色信息
						String session = sessionMap.get(userName);
						String[] ss = session.split(",");
						Long[] roleIds = new Long[ss.length];
						for (int i = 0; i < ss.length; i++) {
							// if(roleMap.containsKey(ss[i])){
							// roles[i]=roleMap.get(ss[i]);
							// }else{
							roleIds[i] = roleService.getRoleByName(ss[i]).getId();
							// }
						}
						// this.updateUserRoles(user.getId(), roleIds);
					}
					if (userGroupMap.containsKey(userName)) {// 如果包含用户组数据则更新已经存入的数据
						String usergroups = userGroupMap.get(userName);
						String[] ugName = usergroups.split(",");
						Set<UserGroup> ugSet = new HashSet<UserGroup>();
						for (int i = 0; i < ugName.length; i++) {
							ugSet.add(userGroupService.getByName(ugName[i]));
							// if(groupMap.containsKey(ugg[i])){//原有系统的用户组
							// ug.add(userGroupService.get(groupMap.get(ugg[i])));
							// }else{
							// UserGroup g=userGroupService.getByName(ugg[i]);
							// ug.add(g);
							// userGroupMap.put(userName, usergroups);
							// }
						}
						user.setUserGroups(ugSet);
						this.updateUser(user);
					}
					this.updateUser(user);
				}
			}
			if (!importUsers.isEmpty()) {
				for (User user : importUsers) {
					String userName = user.getUserName();
					if (sessionMap.containsKey(userName)) {
						String session = sessionMap.get(userName);
						String[] ss = session.split(",");
						Long[] roles = new Long[ss.length];
						for (int i = 0; i < ss.length; i++) {
							// roles[i]=roleMap.get(ss[i]);
							roles[i] = roleService.getRoleByName(ss[i]).getId();
						}
						this.addUser(user, roles);
					}
					else {
						this.addUser(user);
					}
					if (userGroupMap.containsKey(userName)) {// 如果包含用户组数据则更新已经存入的数据
						String usergroups = userGroupMap.get(userName);
						String[] ugName = usergroups.split(",");
						Set<UserGroup> ugSet = new HashSet<UserGroup>();
						for (int i = 0; i < ugName.length; i++) {
							ugSet.add(userGroupService.getByName(ugName[i]));
							// if(groupMap.containsKey(ugg[i])){//原有系统的用户组
							// ug.add(userGroupService.get(groupMap.get(ugg[i])));
							// }else{
							// UserGroup g=userGroupService.getByName(ugg[i]);
							// ug.add(g);
							// userGroupMap.put(userName, usergroups);
							// }
						}
						user.setUserGroups(ugSet);
						this.updateUser(user);
					}
				}
			}
		}
		else {
			userLogService.log("导入用户数据因出现下列重名情况而终止：" + sb.toString().replaceAll("\n", ","));
		}
		return sb.toString();
	}

	public String validatorUserInfo(User user) {
		if (user == null) {
			return "用户校验失败。";
		}
		// 校验用户名称
		if (StringUtils.isBlank(user.getUserName())) {
			return "请填入用户名称！";
		}
		int minLength = Integer.valueOf(SystemUtil.getProperty("security.usernameMinLength"));
		if (user.getUserName().length() < minLength || user.getUserName().length() > 32) {
			return "用户名称至少" + minLength + "个字符,最多32个字符";
		}
		// 校验密码
		if (StringUtils.isBlank(user.getUserPassword())) {
			return "请填入用户密码！";
		}
		if (user.getUserPassword().length() < 8 || user.getUserPassword().length() > 32) {
			return "用户密码至少8个字符,最多32个字符！";
		}
		// 校验用户备注
		if (StringUtils.isNotBlank(user.getUserRemark()) && user.getUserRemark().length() > 255) {
			return "您输入的用户备注已超过255个字符！";
		}
		//校验是否填写邮箱
		if (!StringUtils.isNotBlank(user.getEmail())) {
			return "请填入邮箱！";
		}
		// 校验用户邮箱
		if (StringUtils.isNotBlank(user.getEmail())) {
			if (user.getEmail().length() > 255) {
				return "邮箱地址不能超过255个字符！";
			}
			if (!checkEmail(user.getEmail())) {
				return "邮箱地址格式不正确！";
			}
		}
		// 校验手机
		if (StringUtils.isNotBlank(user.getMobile()) && !checkPhone(user.getMobile())) {
			return "手机号码格式不正确！";
		}

		return SecurityConstants.SUCCESS;
	}

	// =====================判断邮件email是否正确格式
	public boolean checkEmail(String email) {

		Pattern pattern = Pattern.compile("^(\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)?$");
		Matcher matcher = pattern.matcher(email);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	// =====================判断手机号phone是否正确格式
	public boolean checkPhone(String phone) {
		Pattern pattern = Pattern.compile("^(1[3|5|8][0-9])\\d{8}(,(1[3|5|8][0-9])\\d{8})*$");
		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	public String validatorUserBaseInfo(User user) {
		if (user == null) {
			return "用户校验失败。";
		}
		// 校验用户备注
		if (StringUtils.isNotBlank(user.getUserRemark()) && user.getUserRemark().length() > 255) {
			return "您输入的用户备注已超过255个字符！";
		}
		// 校验用户邮箱
		if (StringUtils.isNotBlank(user.getEmail())) {
			if (user.getEmail().length() > 255) {
				return "邮箱地址不能超过255个字符！";
			}
			if (!checkEmail(user.getEmail())) {
				return "邮箱地址格式不正确！";
			}
		}
		// 校验手机
		if (StringUtils.isNotBlank(user.getMobile()) && !checkPhone(user.getMobile())) {
			return "手机号码格式不正确！";
		}
		return SecurityConstants.SUCCESS;
	}

	public String validatorUserPasswordInfo(User user) {
		if (user == null) {
			return "用户校验失败。";
		}
		// 校验密码
		if (StringUtils.isBlank(user.getUserPassword())) {
			return "请填入用户密码！";
		}
		if (user.getUserPassword().length() < 8 || user.getUserPassword().length() > 32) {
			return "用户密码至少8个字符,最多32个字符！";
		}
		return SecurityConstants.SUCCESS;
	}

	@Transactional
	public void secondDeleteUsers(Long[] userIds) {
		// Set<Long> ids=new HashSet<Long>();
		for (Long id : userIds) {
			if (userIds != null) {
				StringBuilder logBuilder = new StringBuilder();
				for (int i = 0; i < userIds.length; i++) {
					Long idL = Long.valueOf(userIds[i]);
					User user = userDao.findById(idL, false);
					if (user.getUserName().equals("admin") || user.getUserName().equals("anonymous"))
						continue;
					deleteUser(user);
					if (i > 0)
						logBuilder.append(',');
					logBuilder.append(user.getUserName());
				}
				userLogService.log("删除用户" + logBuilder.toString());
			}
			// ids.add(id);
		}
		// Long userId=HttpAuthnUtils.getLoginUserId();
		// resourceService.secondDeleteResources(this.get(userId), ids, ResourceService.TYPE_USER);
	}

	@Override
	@Transactional
	public int checkUserStatus(Long userId) {
		User user=userDao.findById(userId, false);
		if(user==null) return User.SC_MISSING;
		if(user.getDeleteTimes()>0) return User.SC_DELETED;
		if(Boolean.TRUE.equals(user.getDisabled())) return User.SC_DISABLED;
		if(user.getDueTime()!=null&&user.getDueTime().before(DateUtils.trimDate(new Date(), Calendar.DATE)))  return User.SC_EXPIRED;
		return User.SC_OK;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public UserGroupService getUserGroupService() {
		return userGroupService;
	}

	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	public RoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

//	public ReportWorkFlowStatusService getReportWorkFlowStatusService() {
//		return reportWorkFlowStatusService;
//	}
//
//	public void setReportWorkFlowStatusService(ReportWorkFlowStatusService reportWorkFlowStatusService) {
//		this.reportWorkFlowStatusService = reportWorkFlowStatusService;
//	}

	public void setResourceAclService(ResourceAclService resourceAclService) {
		this.resourceAclService = resourceAclService;
	}

	public ResourceAclService getResourceAclService() {
		return resourceAclService;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@Override
	@Transactional
	public PagedArrayList<User> getUsersByPageAndName(int page, String name, Set<Long> groupIds) {
		UserCriterion userCriterion = new UserCriterion();
		userCriterion.setUserName(name);
		userCriterion.setGroupIds(groupIds);
		userCriterion.setExcludes(new String[]{"admin"});
		return userDao.find(userCriterion, new PageCriterion(defaultPageSize, page));
	}

	@Transactional
	public UserKey getUserKey(Long id) {
		return userKeyDao.findById(id, false);
	}

	@Transactional
	public UserKey getUserKeyByUserId(Long userId) {
		return userKeyDao.findByUserId(userId);
	}

	@Transactional
	public UserKey getUserKeyByUserName(String username) {
		return userKeyDao.findByUserName(username);
	}

	@Transactional
	public void saveUserKey(UserKey userKey) {
		userKeyDao.makePersistent(userKey);

	}

	public void setUserKeyDao(UserKeyDao userKeyDao) {
		this.userKeyDao = userKeyDao;
	}

//	public KeyPeopleService getKeyPeopleService() {
//		return keyPeopleService;
//	}
//
//	public void setKeyPeopleService(KeyPeopleService keyPeopleService) {
//		this.keyPeopleService = keyPeopleService;
//	}

	public String getDigestRealmName() {
		return digestRealmName;
	}

	public void setDigestRealmName(String digestRealmName) {
		this.digestRealmName = digestRealmName;
	}

	public ResourceDao getResourceDao() {
		return resourceDao;
	}

	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	@Override
	@Transactional
	public String generateAppendStringForResourceUserAclByGroup(
			Long resourceId, Long groupId, Integer resourceType) {
		//列出组下所有用户
		Set<User> users=new HashSet<User>();
		users.addAll(listUsersInGroup(groupId));
		StringBuilder sb=new StringBuilder();
		//列出组下所有用户的acl
		if(resourceId!=null){
			List<ResourceUserAcl> acls=resourceAclService.listResourceUserAclByResourceAndGroup(resourceId, groupId,resourceType);
			Map<Long,PermissionCheck> userToAcl=new HashMap<Long,PermissionCheck>();
			for(ResourceUserAcl acl:acls){
				PermissionCheck pc=new PermissionCheck();
				pc.setView(acl.isView());
				pc.setDelete(acl.isDelete());
				pc.setEdit(acl.isEdit());
				pc.setShare(acl.isShare());
				pc.setAssign(acl.isAssign());
				pc.setConfig(acl.isConfig());
				pc.setCopy(acl.isCopy());
				userToAcl.put(acl.getUserId(), pc);
			}
			//	String[] acls=userAcl.split(",");
			for(User user:users){
				boolean view=false;
				boolean edit=false;
				boolean delete=false;
				boolean share=false;
				boolean assign=false;
				boolean config=false;
				boolean copy=false;
				if(userToAcl.containsKey(user.getId())){
					view=userToAcl.get(user.getId()).isView();
					edit=userToAcl.get(user.getId()).isEdit();
					delete=userToAcl.get(user.getId()).isDelete();
					share=userToAcl.get(user.getId()).isShare();
					assign=userToAcl.get(user.getId()).isAssign();
					config=userToAcl.get(user.getId()).isConfig();
					copy=userToAcl.get(user.getId()).isCopy();
				}
				String tag="u"+user.getId()+"-"+groupId;
				sb.append("<tr class=\"trtdbg groupUser_"+groupId+"\"><td width=\"*\" style=\"border-left:0px;\"  class=\"txtin\" title=\"").append(user.getUserName()).append("\">").append(user.getUserName()).append("</td>");
				//view
				sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\"");
				if(view)
					sb.append("checked=\"checked\"");
				sb.append(" id=\"").append(tag+"_v\"/></td>");
				//edit
				sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\" ");
				if(edit)
					sb.append("checked=\"checked\"");
				sb.append(" id=\"").append(tag+"_e\" onclick=\"check_e('"+tag+"')\"/></td>");
				
				if(resourceType==ResourceConstants.TYPE_INDEXPAGE){
					//delete
					sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\" ");
					if(delete)
						sb.append("checked=\"checked\"");
					sb.append(" id=\"").append(tag+"_d\" onclick=\"check_d('"+tag+"')\"/></td>");
					//share
					sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\" ");
					if(share)
						sb.append("checked=\"checked\"");
					sb.append(" id=\"").append(tag+"_s\" onclick=\"check_s('"+tag+"')\"/></td>");
					sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\"");
					if(assign)
						sb.append("checked=\"checked\"");
					sb.append(" id=\"").append(tag+"_c\" onclick=\"check_c('"+tag+"')\"/></td>")
					.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\"");
					if(config)
						sb.append("checked=\"checked\"");
					sb.append(" id=\"").append(tag+"_a\" onclick=\"check_a('"+tag+"')\" /></td>");
					sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\"");
					if(copy)
						sb.append("checked=\"checked\"");
					sb.append(" id=\"").append(tag+"_p\" onclick=\"check_p('"+tag+"')\" /></td>");
				}
				sb.append("</tr>");
			}
		}else{
			for(User user:users){
				boolean view=false;
				boolean edit=false;
				boolean delete=false;
				boolean share=false;
				boolean assign=false;
				boolean config=false;
				boolean copy=false;
				String tag="u"+user.getId()+"-"+groupId;
				sb.append("<tr class=\"trtdbg groupUser_"+groupId+"\"><td width=\"*\" style=\"border-left:0px;\" class=\"txtin\" title=\"").append(user.getUserName()).append("\">").append(user.getUserName()).append("</td>");
				//view
				sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\"");
				if(view)
					sb.append("checked=\"checked\"");
				sb.append(" id=\"").append(tag+"_v\"/></td>");
				//edit
				sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\" ");
				if(edit)
					sb.append("checked=\"checked\"");
				sb.append(" id=\"").append(tag+"_e\" onclick=\"check_e('"+tag+"')\"/></td>");
				//delete
				sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\" ");
				if(delete)
					sb.append("checked=\"checked\"");
				sb.append(" id=\"").append(tag+"_d\" onclick=\"check_d('"+tag+"')\"/></td>");
				//share
				sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\" ");
				if(share)
					sb.append("checked=\"checked\"");
				sb.append(" id=\"").append(tag+"_s\" onclick=\"check_s('"+tag+"')\"/></td>");
				if(resourceType==ResourceConstants.TYPE_INDEXPAGE){
					sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\"");
					if(assign)
						sb.append("checked=\"checked\"");
					sb.append(" id=\"").append(tag+"_c\" onclick=\"check_c('"+tag+"')\"/></td>")
					.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\"");
					if(config)
						sb.append("checked=\"checked\"");
					sb.append(" id=\"").append(tag+"_a\" onclick=\"check_a('"+tag+"')\" /></td>");
					sb.append("<td width=\"10%\" align=\"center\"><input type=\"checkbox\" name=\"acl\"");
					if(copy)
						sb.append("checked=\"checked\"");
					sb.append(" id=\"").append(tag+"_p\" onclick=\"check_p('"+tag+"')\" /></td>");
				}
				sb.append("</tr>");
			}
		}
		return sb.toString();
	}

	@Override
	@Transactional
	public <T extends Resource> Map<Long, PermissionCheck> batchCheckPermission(Long userId,
			Collection<T> resources) {
		if(resources==null||resources.isEmpty())
			return new HashMap<Long,PermissionCheck>();

		Resource r=resources.iterator().next();
		ResourceType resourceType=(ResourceType) r.getClass().getAnnotation(ResourceType.class);

		// 对admin用户做特殊处理：admin用户具有全部权限
		if(userId.equals(SecurityConstants.SYSTEM_ADMIN_ID)){
			Map<Long,PermissionCheck> pcs=new HashMap<Long,PermissionCheck>();
			PermissionCheck pc=new PermissionCheck();
			pc.setView(true);
			pc.setDelete(true);
			pc.setEdit(true);
			pc.setShare(true);
			pc.setAssign(true);
			pc.setConfig(true);
			pc.setCopy(true);
			for(Resource resource:resources){
				pcs.put(resource.getId(), pc);
			}
			return pcs;
		}
		User user=this.get(userId);
		Map<Long, PermissionCheck> idPermissions=new HashMap<Long, PermissionCheck>();
		// 对资源的创建者做特殊处理：创建者只需要判断是否具有操作权限，不判断数据权限
		String [] editPermission=new String[]{PermissionConstants.EDIT,resourceType.typeString()};
		String [] deletePermission=new String[]{PermissionConstants.DELETE,resourceType.typeString()};
		String [] sharePermission=new String[]{PermissionConstants.SHARE,resourceType.typeString()};
		String [] copyPermission=PermissionConstants.COPY_INDEXPAGE_PERMISSION;
		String [] assignPermission=PermissionConstants.ASSIGN_INDEXPAGE_PERMISSION;
		String [] configPermission=PermissionConstants.CONFIG_INDEXPAGE_PERMISSION;
		boolean hasEditPermission=hasPermission(editPermission, user);
		boolean hasDeletePermission=hasPermission(deletePermission, user);
		boolean hasSharePermission=hasPermission(sharePermission, user);
		boolean hasCopyPermission=false,hasAssignPermission=false,hasConfigPermission=false;
		if(resourceType.typeInt()==ResourceConstants.TYPE_INDEXPAGE){
			hasCopyPermission=hasPermission(copyPermission, user);
			hasAssignPermission=hasPermission(assignPermission, user);
			hasConfigPermission=hasPermission(configPermission, user);
		}
		List<Long> resourceIds=new ArrayList<Long>(); // 保存非当前用户创建的资源ID
		for(Resource resource:resources){
			if(user.getId().equals(resource.getCreatorId())){
				PermissionCheck pc=new PermissionCheck();
				pc.setDelete(hasDeletePermission);
				pc.setEdit(hasEditPermission);
				pc.setShare(hasSharePermission);
				pc.setCopy(hasCopyPermission);
				pc.setAssign(hasAssignPermission);
				pc.setConfig(hasConfigPermission);
				idPermissions.put(resource.getId(), pc);
			}else{
				resourceIds.add(resource.getId());
			}
		}

		// 检索ResourceUserAcl表
		ResourceAclCriterion resourceAclCriterion=new ResourceAclCriterion();
		resourceAclCriterion.setResourceId(resourceIds);
		Set<Long> userIds=new HashSet<Long>();
		userIds.add(user.getId());
		resourceAclCriterion.setUserId(userIds);
		resourceAclCriterion.setResourceType(resourceType.typeInt());
		List<ResourceUserAcl> ruas=resourceAclService.listResourceUserAclByCriterion(resourceAclCriterion);
		List<ResourceGroupAcl> rgas=resourceAclService.listResourceGroupAclByCriterion(resourceAclCriterion);
		for(ResourceUserAcl rua:ruas){
			if(idPermissions.get(rua.getResourceId())!=null){
				boolean isEdit=idPermissions.get(rua.getResourceId()).isEdit();
				boolean isDelete=idPermissions.get(rua.getResourceId()).isDelete();
				boolean isShare=idPermissions.get(rua.getResourceId()).isShare();
				if(isEdit&&isDelete&&isShare)
					continue;
				if(!isEdit&&rua.isEdit())
					idPermissions.get(rua.getResourceId()).setEdit(true);
				if(!isDelete&&rua.isDelete())
					idPermissions.get(rua.getResourceId()).setDelete(true);
				if(!isShare&&rua.isShare())
					idPermissions.get(rua.getResourceId()).setShare(true);
			}else{
				PermissionCheck pc=new PermissionCheck();
				pc.setEdit(rua.isEdit());
				pc.setDelete(rua.isDelete());
				pc.setShare(rua.isShare());
				idPermissions.put(rua.getResourceId(), pc);
			}
		}

		Set<Long> editGroups=permissionService.listGroupIdsForPermission(user, editPermission);
		Set<Long> deleteGroups=permissionService.listGroupIdsForPermission(user, deletePermission);
		Set<Long> shareGroups=permissionService.listGroupIdsForPermission(user, sharePermission);

		for(ResourceGroupAcl rga:rgas){
			Long groupId=rga.getGroupId();
			boolean editable=editGroups.contains(groupId);
			boolean deleteable=deleteGroups.contains(groupId);
			boolean shareable=shareGroups.contains(groupId);
			if(editable||deleteable||shareable){
				if(idPermissions.get(rga.getResourceId())!=null){
					boolean isEdit=idPermissions.get(rga.getResourceId()).isEdit();
					boolean isDelete=idPermissions.get(rga.getResourceId()).isDelete();
					boolean isShare=idPermissions.get(rga.getResourceId()).isShare();
					if(isEdit&&isDelete&&isShare)
						continue;
					if(editable&&!isEdit&&rga.isEdit())
						idPermissions.get(rga.getResourceId()).setEdit(true);
					if(deleteable&&!isDelete&&rga.isDelete())
						idPermissions.get(rga.getResourceId()).setDelete(true);
					if(shareable&&!isShare&&rga.isShare())
						idPermissions.get(rga.getResourceId()).setShare(true);
				}else{
					PermissionCheck pc=new PermissionCheck();
					if(editable)
						pc.setEdit(rga.isEdit());
					if(deleteable)
						pc.setDelete(rga.isDelete());
					if(shareable)
						pc.setShare(rga.isShare());
					idPermissions.put(rga.getResourceId(), pc);
				}
			}
		}
		if(resourceType.typeInt()==ResourceConstants.TYPE_INDEXPAGE){
			for(ResourceUserAcl rua:ruas){
				if(idPermissions.get(rua.getResourceId())!=null){
					boolean isAssign=idPermissions.get(rua.getResourceId()).isAssign();
					boolean isConfig=idPermissions.get(rua.getResourceId()).isConfig();
					boolean isCopy=idPermissions.get(rua.getResourceId()).isCopy();
					if(isAssign&&isConfig&&isCopy)
						continue;
					if(!isAssign&&rua.isAssign())
						idPermissions.get(rua.getResourceId()).setAssign(true);
					if(!isConfig&&rua.isConfig())
						idPermissions.get(rua.getResourceId()).setConfig(true);
					if(!isCopy&&rua.isCopy())
						idPermissions.get(rua.getResourceId()).setCopy(true);
				}else{
					PermissionCheck pc=new PermissionCheck();
					pc.setAssign(rua.isAssign());
					pc.setConfig(rua.isConfig());
					pc.setCopy(rua.isCopy());
					idPermissions.put(rua.getResourceId(), pc);
				}
			}
			Set<Long> assignGroups=permissionService.listGroupIdsForPermission(user, assignPermission);
			Set<Long> configGroups=permissionService.listGroupIdsForPermission(user, configPermission);
			Set<Long> copyGroups=permissionService.listGroupIdsForPermission(user, copyPermission);
			for(ResourceGroupAcl rga:rgas){
				Long groupId=rga.getGroupId();
				boolean assignable=assignGroups.contains(groupId);
				boolean configable=configGroups.contains(groupId);
				boolean copyable=copyGroups.contains(groupId);
				if(assignable||configable||copyable){
					if(idPermissions.get(rga.getResourceId())!=null){
						boolean isAssign=idPermissions.get(rga.getResourceId()).isAssign();
						boolean isConfig=idPermissions.get(rga.getResourceId()).isConfig();
						boolean isCopy=idPermissions.get(rga.getResourceId()).isCopy();
						if(isAssign&&isConfig&&isCopy)
							continue;
						if(assignable&&!isAssign&&rga.isAssign())
							idPermissions.get(rga.getResourceId()).setAssign(true);
						if(configable&&!isConfig&&rga.isConfig())
							idPermissions.get(rga.getResourceId()).setConfig(true);
						if(copyable&&!isCopy&&rga.isCopy())
							idPermissions.get(rga.getResourceId()).setCopy(true);
					}else{
						PermissionCheck pc=new PermissionCheck();
						if(assignable)
							pc.setAssign(rga.isAssign());
						if(configable)
							pc.setConfig(rga.isConfig());
						if(copyable)
							pc.setConfig(rga.isCopy());
						idPermissions.put(rga.getResourceId(), pc);
					}
				}
			}
		}
		return idPermissions;

	}

	@Transactional
	private <T extends Resource> Collection<Long> pickOperatableResouceIds(User user, String operate,
			Collection<T> resources) {
		if(resources==null||resources.isEmpty())
			return new HashSet<Long>();
		List<Long> resourceIds=new ArrayList<Long>();
		Resource r=resources.iterator().next();
		for(Resource resource:resources){
			resourceIds.add(resource.getId());
		}
		ResourceType resourceType=(ResourceType) r.getClass().getAnnotation(ResourceType.class);
		Set<Long> result=new HashSet<Long>();
		if(user.getId().equals(SecurityConstants.SYSTEM_ADMIN_ID)){//admin用户具有全部权限
			return resourceIds;
		}
		Set<Long> gids=listGroupIdsForPermission(user,operate,r);
		//判断用户是否具有该权限
		//获得用户自己创建的资源,这两步暂时在外围做，本方法传入的resourceIds是非本人创建的
		//对于非用户本人创建的资源，检索ResourceUserAcl表
		ResourceAclCriterion resourceAclCriterion=resourceAclService.getCriterionByOperate(operate);
		resourceAclCriterion.setResourceId(resourceIds);
		resourceAclCriterion.setResourceType(resourceType.typeInt());
		List<ResourceUserAcl> ruas=resourceAclService.listResourceUserAclByCriterion(resourceAclCriterion);
		resourceAclCriterion.setGroupId(gids);
		List<ResourceGroupAcl> rgas=resourceAclService.listResourceGroupAclByCriterion(resourceAclCriterion);
		for(ResourceUserAcl rua:ruas){
			result.add(rua.getResourceId());
		}
		for(ResourceGroupAcl rga:rgas){
			result.add(rga.getResourceId());
		}
		return result;
	}

}
