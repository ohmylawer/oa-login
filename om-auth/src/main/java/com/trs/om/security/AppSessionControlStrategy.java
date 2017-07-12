package com.trs.om.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

public class AppSessionControlStrategy extends ConcurrentSessionControlStrategy {

	// fields ---------------------------------------------------------------
	private SessionRegistry sessionRegistry;
	private int maximumAppSessions=-1;
	private boolean exceptionIfMaximumAppExceeded = false;

	// methods --------------------------------------------------------------
	public AppSessionControlStrategy(SessionRegistry sessionRegistry) {
		super(sessionRegistry);
		this.sessionRegistry=sessionRegistry;
	}

	@Override
	public void onAuthentication(Authentication authentication,
			HttpServletRequest request, HttpServletResponse response) {
		// 通过TOKEN方式访问的不进行会话控制
		if(authentication instanceof TokenAuthenticationToken)
			return;
		if(-1!=maximumAppSessions){
			int sessionCount=0;
			SessionInformation leastRecentlyUsed=null;
			boolean alreadyRegistered=false;
			HttpSession session = request.getSession(false);
			synchronized (sessionRegistry) {
				List<Object> allPrincipals=sessionRegistry.getAllPrincipals();
				for(Object principal:allPrincipals){
					List<SessionInformation> sessions=sessionRegistry.getAllSessions(principal, false);
					for(SessionInformation sessInfo:sessions){
						if ((leastRecentlyUsed == null)
			                    || sessInfo.getLastRequest().before(leastRecentlyUsed.getLastRequest())) {
			                leastRecentlyUsed = sessInfo;
			            }
						if(null!=session&&!alreadyRegistered&&session.getId().equals(sessInfo.getSessionId())){
							alreadyRegistered=true;
						}

					}
					sessionCount+=sessions.size();
				}
				if(alreadyRegistered||sessionCount==maximumAppSessions){
					allowableAppSessionsExceeded(sessionCount, leastRecentlyUsed);
				}
			}
		}
		super.onAuthentication(authentication, request, response);
	}

	protected void allowableAppSessionsExceeded(int sessionCount, SessionInformation leastRecentlyUsed) throws SessionAuthenticationException {
        if (exceptionIfMaximumAppExceeded) {
        	throw new SessionAuthenticationException("超过最大的会话数"+sessionCount);
        }

        leastRecentlyUsed.expireNow();
    }


	// accessors ------------------------------------------------------------
	public int getMaximumAppSessions() {
		return maximumAppSessions;
	}

	public void setMaximumAppSessions(int maximumAppSessions) {
		this.maximumAppSessions = maximumAppSessions;
	}

	public boolean isExceptionIfMaximumAppExceeded() {
		return exceptionIfMaximumAppExceeded;
	}

	public void setExceptionIfMaximumAppExceeded(
			boolean exceptionIfMaximumAppExceeded) {
		this.exceptionIfMaximumAppExceeded = exceptionIfMaximumAppExceeded;
	}


}
