package com.trs.om.security.ids;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.trs.idm.client.actor.SSOUser;

public class IdsPreAuthenticationFilter extends
		AbstractPreAuthenticatedProcessingFilter {

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		SSOUser ssoUser=(SSOUser) request.getAttribute(Actor.TRSIDS_SSOUSER_KEY);
		return ssoUser!=null?ssoUser.getEncryptedUserPwd():null;
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		SSOUser ssoUser=(SSOUser) request.getAttribute(Actor.TRSIDS_SSOUSER_KEY);
		return ssoUser!=null?ssoUser.getUserName():null;
	}

}
