package com.trs.om.security;

import org.springframework.context.ApplicationEvent;

import com.trs.om.bean.User;

/**
 * 删除用户的应用事件
 * 
 * @author chang
 *
 */
public class UserDeleteEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1587810983062028599L;
	
	private User user;
	
	public UserDeleteEvent(User user) {
		super(user);
		this.user=user;
	}

	public User getUser() {
		return user;
	}

}
