package com.trs.om.security;

import org.springframework.context.ApplicationEvent;

import com.trs.om.bean.User;

/**
 * 添加用户的应用事件
 * 
 * @author chang
 *
 */
public class UserAddEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8879046721610035893L;
	
	private User user;
	
	public UserAddEvent(User user) {
		super(user);
		this.user=user;
	}

	public User getUser() {
		return user;
	}

}
