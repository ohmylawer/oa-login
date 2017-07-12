package com.trs.om.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.Possession;
import com.trs.om.bean.User;
import com.trs.om.dao.PossessionDao;
import com.trs.om.exception.TRSOMException;
import com.trs.om.service.EncryptService;
import com.trs.om.service.PossessionService;
import com.trs.om.service.UserService;
import com.trs.om.service.UserGroupService;
import com.trs.om.util.PagedArrayList;

public class PossessionServiceImpl implements PossessionService {

	private UserService userService;
	private UserGroupService userGroupService;
	private PossessionDao possessionDao;

	public PossessionServiceImpl() {
		EncryptService obj;
		try {
			obj = EncryptService.getInstance();
			obj.checkLicence();
		} catch (Exception e) {
			throw new TRSOMException("无法获得或者校验注册码："+e.getMessage(),e);
		}
	}

	@Transactional
	public boolean addPossession(Possession possession) {
		possessionDao.makePersistent(possession);
		return true;
	}

	@Transactional
	public boolean deletePossession(Long id) {
		Possession ps=possessionDao.findById(id, false);
		this.possessionDao.makeTransient(ps);
		return true;
	}

	@Transactional
	public void deleteByIds(Long[] ids) {
		for(Long id:ids){
			Possession ps=possessionDao.findById(id, false);
			if(null!=ps) {
				this.possessionDao.makeTransient(ps);
			}
		}
	}

	@Transactional
	public Possession getPossession(String possessionName) {
		return possessionDao.getByName(possessionName);
	}

	@Transactional
	public List<Possession> listAllPossessions(User user) {
		List<Long> ugids=this.userService.getGroupIds(user.getId());
		if(ugids.isEmpty())
			return new ArrayList<Possession>();
		else
			return listPossessionsByGroupIds(ugids);
	}

	@Transactional
	public PagedArrayList<Possession> listPossessionsByPage(User user) {
		return null;
	}

	@Transactional
	public boolean updatePossession(Possession possession) {
		this.possessionDao.makePersistent(possession);
		return true;
	}

	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	public UserGroupService getUserGroupService() {
		return userGroupService;
	}

	@Transactional
	public List<Possession> listPossessionsByGroupIds(List<Long> groupIds) {
		return this.possessionDao.findByGroupIds(groupIds);
	}

	@Transactional
	public PagedArrayList<Possession> listPossessions(String possessionName,
			Long groupId, PageCriterion pageCriterion) {
		return this.possessionDao.find(possessionName,groupId, pageCriterion);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public PossessionDao getPossessionDao() {
		return possessionDao;
	}

	public void setPossessionDao(PossessionDao possessionDao) {
		this.possessionDao = possessionDao;
	}

	@Transactional
	public Possession getById(Long id) {
		return possessionDao.findById(id, false);
	}


}