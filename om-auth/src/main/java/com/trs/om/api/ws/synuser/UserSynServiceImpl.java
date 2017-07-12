package com.trs.om.api.ws.synuser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.User;
import com.trs.om.dao.UserDao;
import com.trs.om.service.UserGroupService;

public class UserSynServiceImpl implements UserSynService {
	private GroupOrgService groupOrgService;
	private UserGroupService userGroupService;
	private UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setGroupOrgService(GroupOrgService groupOrgService) {
		this.groupOrgService = groupOrgService;
	}


	@Override
	public boolean queryUserByusername(String userName) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	@Override
	@Transactional
	public boolean addUser(User user) {
		if (user == null)
			return false;
		if (StringUtils.isBlank(user.getUserName()))
			return false;
		if (user.getCreationDate() == null)
			return false;
		if (StringUtils.isBlank(user.getUserPassword()))
			return false;
		if (StringUtils.isBlank(user.getCreator()))
			return false;
		userDao.makePersistent(user);
		return true;
	}
	/**
	 * 根据用户名查询指定用户.
	 *
	 * @param userName the user name
	 *
	 * @return the user
	 */
	@Override
	@Transactional
	public User getUser(String userName) {
		if (userName == null || userName.trim().equals(""))
			return null;
		return userDao.getByName(userName);
	}

	@Override
	@Transactional
	public boolean updateUser(User user) {
		userDao.makePersistent(user);
		return true;
	}



}
