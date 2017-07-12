package com.trs.om.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IPrivilegeManager;
import com.trs.om.bean.Privilege;
import com.trs.om.bean.DataPermission;
import com.trs.om.dao.DataPermissionDao;
import com.trs.om.exception.TRSOMException;
import com.trs.om.service.DataPermissionService;
import com.trs.om.service.EncryptService;
import com.trs.om.service.UserService;

/**
 * 接口{@link DataPermissionService}的实现类。
 * @author wengjing
 *
 */
public class DataPermissionServiceImpl implements DataPermissionService {

	//fields	---------------------------------------------------------------------
	private DataPermissionDao dataPermissionDao;
	private IPrivilegeManager privilegeManager;
	private UserService userService;

	public DataPermissionServiceImpl() {
		EncryptService obj;
		try {
			obj = EncryptService.getInstance();
			obj.checkLicence();
		} catch (Exception e) {
			throw new TRSOMException("无法获得或者校验注册码："+e.getMessage(),e);
		}
	}

	//methods	---------------------------------------------------------------------
	@Transactional
	public void addDataPermission(DataPermission dataPermission) {
		//dataPermission.setId(IdGenerator.newId());
		dataPermission.setModule("数据权限");
		dataPermissionDao.makePersistent(dataPermission);

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void deleteDataPermissions(Long[] ids) {
		if(null!=ids&&ids.length>0){
			for(Long id:ids){
				DataPermission permission=dataPermissionDao.findById(id, false);
				if(null!=permission){
					try {
						//删除权限与角色之间的关系
						List<Privilege> privileges=privilegeManager.findPrivileges(id, null);
						for(Privilege privilege:privileges)
							privilegeManager.deletePrivilege(privilege.getId());
					} catch (AuthorizationException e) {
						throw new TRSOMException(e);
					}
					//删除权限
					dataPermissionDao.makeTransient(permission);
				}
			}
		}

	}

	@Transactional
	public boolean isNameExisting(String name, Long excluedId) {
		return dataPermissionDao.isNameExisting(name, excluedId);
	}

	@Transactional
	public List<DataPermission> listAllDataPermissions() {
		return dataPermissionDao.findAll();
	}

	@Transactional
	public void updateDataPermission(DataPermission dataPermission) {
		dataPermissionDao.makePersistent(dataPermission);

	}

	@Transactional
	public List<DataPermission> listDataPermissionsByUserId(Long userId,String tableName) {
		return dataPermissionDao.find(userId,tableName);
	}

	//accessors	---------------------------------------------------------------------
	public DataPermissionDao getDataPermissionDao() {
		return dataPermissionDao;
	}

	public void setDataPermissionDao(DataPermissionDao dataPermissionDao) {
		this.dataPermissionDao = dataPermissionDao;
	}

	public IPrivilegeManager getPrivilegeManager() {
		return privilegeManager;
	}

	public void setPrivilegeManager(IPrivilegeManager privilegeManager) {
		this.privilegeManager = privilegeManager;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
