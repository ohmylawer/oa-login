package com.trs.om.api.ws.synuser;

import java.security.NoSuchAlgorithmException;

import com.trs.om.bean.User;

public interface UserSynService {
	/**
	 * 根据用户名查询用户是否已经存在
	 * @param userName
	 * @return true 存在 false 不存在
	 */
	public boolean queryUserByusername(String userName);
	/**
	 * 增加用户
	 * @param user
	 * @return
	 */
	public boolean addUser(User user);
	/**
	 * 更新用户
	 * @return
	 */
	public boolean updateUser(User user);
	/**
	 * 根据用户名查询指定用户
	 * @param userName
	 * @return
	 */
	public User getUser(String userName);

}
