package com.trs.om.auth.api;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.common.ObjectContainer;

@Path("auth")
@Produces("application/*")
public class AuthResource {
	private static final Logger logger = Logger.getLogger(AuthResource.class);
	
	private ISessionManager sessionManager = (ISessionManager) ObjectContainer.getBean("sessionManager");

	@GET
	@Path("/add")
	public String addBatchNewSession(@QueryParam("roleId") int roleId){
		
			Long role_id = (long) roleId;
			try {
				sessionManager.addBatchNewSession(role_id);
			} catch (AuthorizationException e) {
				e.printStackTrace();
				logger.debug("添加失败，"+e.getMessage());
			}
			
		return  "添加成功";
	}
	

}
