package com.trs.om.security;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.trs.om.service.UserLogService;

public class AutoLogoutListener implements HttpSessionListener,ServletContextListener {

	// fields ---------------------------------------------------------------
	private ServletContext servletContext;
	private ApplicationContext applicationContext;
	private UserLogService userLogService;
	private HttpSessionSecurityContextRepository securityContextRepository;

	// methods --------------------------------------------------------------
	public void sessionCreated(HttpSessionEvent event) {
		// do nothing

	}

	private void log(Authentication authentication,String userAct){
		String userName=authentication.getName();
		CustomWebAuthenticationDetails details=(CustomWebAuthenticationDetails) authentication.getDetails();
		userLogService.log(userName, userAct, details.getOriginalClientIP(), details.getIpLocation());
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		if(null==applicationContext){
			applicationContext=WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			userLogService=applicationContext.getBean(UserLogService.class);
			securityContextRepository=applicationContext.getBean(HttpSessionSecurityContextRepository.class);
		}
		if(null!=securityContextRepository){
			SecurityContext securityContext=(SecurityContext) event.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
			if(null!=securityContext
					&&null!=securityContext.getAuthentication()
					&&securityContext.getAuthentication().isAuthenticated()){
				log(securityContext.getAuthentication(), "用户注销");
			}
		}

	}

	public void contextDestroyed(ServletContextEvent event) {
		// do nothing

	}

	public void contextInitialized(ServletContextEvent event) {
		this.servletContext=event.getServletContext();

	}

}
