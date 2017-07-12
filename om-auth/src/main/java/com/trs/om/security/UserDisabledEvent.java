package com.trs.om.security;

import org.springframework.context.ApplicationEvent;

import com.trs.om.bean.User;

/**
 * 用户被禁用的应用事件
 * 
 * @author Administrator
 *
 */
public class UserDisabledEvent extends ApplicationEvent {

	private static final long serialVersionUID = -1375336446585170786L;
	
	private User user;
	
	public UserDisabledEvent(User user) {
		super(user);
		this.user=user;
	}

	public User getUser() {
		return user;
	}

}
