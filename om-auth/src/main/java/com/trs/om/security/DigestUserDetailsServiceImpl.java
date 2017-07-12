package com.trs.om.security;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.trs.om.bean.User;
import com.trs.om.service.EnvironmentVariableService;
import com.trs.om.service.UserService;

public class DigestUserDetailsServiceImpl implements UserDetailsService {

	// fields ---------------------------------------------------------------
	private static final Logger LOGGER = LoggerFactory.getLogger(DigestUserDetailsServiceImpl.class);

	private UserService userService;

	private EnvironmentVariableService environmentVariableService;

	// methods --------------------------------------------------------------
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		// 获取用户信息
		User user = userService.getUser(username);
		if (null == user)
			throw new UsernameNotFoundException("用户名\"" + username + "\"还未注册");
		// DigestPassword是不允许为空的，但在某些情况下可能为空，见BUG[OM-3113]
		if (StringUtils.isEmpty(user.getDigestPassword())) {
			LOGGER.warn("用户{}的摘要密码字段为空，请重设密码！",username);
			throw new UsernameNotFoundException("密码已过期，请重设密码！");
		}
		boolean accountNonExpired = true;
		if (user.getDueTime() != null)// 如果用户到期时间不是“永不到期”（值为null）
			if (user.getDueTime().before(new Date()))
				accountNonExpired = false;
		boolean enabled = !user.getDisabled(); // enabled==true 当前用户未被停用
		boolean notLocked = true;
		if (StringUtils.equals("on", environmentVariableService.getEnvironmentVariableByName("AUTHN.RETRYLIMIT.ENABLE")
				.getValue())) {
			int retryLimit = Integer.valueOf(environmentVariableService
					.getEnvironmentVariableByName("AUTHN.RETRYLIMIT").getValue());
			notLocked = user.getRetryCount() < retryLimit;
		}
		return new OMUser(user.getId(), username, user.getDigestPassword(), enabled, accountNonExpired,
				true, notLocked, new ArrayList<GrantedAuthority>());
	}

	// accessors ------------------------------------------------------------

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setEnvironmentVariableService(EnvironmentVariableService environmentVariableService) {
		this.environmentVariableService = environmentVariableService;
	}

}
