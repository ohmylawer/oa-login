package com.trs.om.auth.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class AuthApplication extends Application {

	public Set<Class<?>> getClasses() {
		Set<Class<?>> rrcs = new HashSet<Class<?>>();
		rrcs.add(AuthResource.class);
		return rrcs;
	}

}
