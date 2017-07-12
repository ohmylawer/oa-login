package com.trs.otm.authorization;

import java.util.Properties;

import com.trs.om.rbac.dao.IDAOAccessor;
import com.trs.om.rbac.dao.IDAOService;

public class DAOService implements IDAOService {

	private IDAOAccessor permissionAccessor;
	private IDAOAccessor privilegeAccessor;
	private IDAOAccessor roleAccessor;
	private IDAOAccessor sessionAccessor;

	public IDAOAccessor getAccessor(String accessorName) {
		if(permissionAccessor!=null&&permissionAccessor.getAccessorName().equals(accessorName))
			return permissionAccessor;
		else if(privilegeAccessor!=null&&privilegeAccessor.getAccessorName().equals(accessorName))
			return privilegeAccessor;
		else if(roleAccessor!=null&&roleAccessor.getAccessorName().equals(accessorName))
			return roleAccessor;
		else if(sessionAccessor!=null&&sessionAccessor.getAccessorName().equals(accessorName))
			return sessionAccessor;
		else
			return null;
	}

	public void setPermissionAccessor(IDAOAccessor permissionAccessor) {
		this.permissionAccessor = permissionAccessor;
	}

	public void setPrivilegeAccessor(IDAOAccessor privilegeAccessor) {
		this.privilegeAccessor = privilegeAccessor;
	}

	public void setRoleAccessor(IDAOAccessor roleAccessor) {
		this.roleAccessor = roleAccessor;
	}

	public void setSessionAccessor(IDAOAccessor sessionAccessor) {
		this.sessionAccessor = sessionAccessor;
	}

	public void start(Properties properties) {
		

	}

	public void stop() {
		

	}

}
