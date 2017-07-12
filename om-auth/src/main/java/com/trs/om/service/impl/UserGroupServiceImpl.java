package com.trs.om.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.GroupVisibility;
import com.trs.om.bean.ResourceAclCriterion;
import com.trs.om.bean.Role;
import com.trs.om.bean.Session;
import com.trs.om.bean.User;
import com.trs.om.bean.UserCriterion;
import com.trs.om.bean.UserGroup;
import com.trs.om.bean.UserGroupCriterion;
import com.trs.om.common.PermissionConstants;
import com.trs.om.dao.GroupVisibilityDao;
import com.trs.om.dao.UserDao;
import com.trs.om.dao.UserGroupDao;
import com.trs.om.exception.TRSOMException;
import com.trs.om.rbac.AuthorizationException;
import com.trs.om.rbac.IRoleManager;
import com.trs.om.rbac.ISessionManager;
import com.trs.om.security.SecurityConstants;
import com.trs.om.service.EncryptService;
import com.trs.om.service.PermissionService;
import com.trs.om.service.ResourceAclService;
import com.trs.om.service.RoleService;
import com.trs.om.service.UserGroupService;
import com.trs.om.service.UserLogService;
import com.trs.om.util.PagedArrayList;
import com.trs.om.util.StringOption;
import com.trs.otm.authentication.HttpAuthnUtils;

public class UserGroupServiceImpl implements UserGroupService {
	//fields	---------------------------------------------------------------------
	private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupService.class);
	private UserGroupDao userGroupDao;
	private UserLogService userLogService;
	private UserDao userDao;
	private GroupVisibilityDao groupVisibilityDao;
	private IRoleManager roleManager;
	private RoleService roleService;
	private ResourceAclService resourceAclService;
//	private PossessionDao possessionDao;
//	private ICPinfoDao icpInfoDao;
	private ISessionManager sessionManager;
	private PermissionService permissionService;
	private static final String CONFIG_FILENAME="group_config.xml";
	private static final String configCharset="UTF8";

	public UserGroupServiceImpl() {
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
	public void delete(UserGroup userGroup) {
		//删除acl相关配置
		resourceAclService.clearAclByGroup(userGroup.getId());
		//删除group_role表中相关配置
		userGroupDao.makeTransient(userGroup);
	}

	@Transactional
	public UserGroup get(Long id) {
		return userGroupDao.findById(id, false);
	}

	@Transactional
	public PagedArrayList<UserGroup> listAll(int page, int limit) {
		return userGroupDao.listAll(page, limit);
	}

	@Transactional
	public PagedArrayList<UserGroup> listAll(String searchUserGroupName,int page,int limit){
		return userGroupDao.listAll(searchUserGroupName,page,limit);
	}

	@Transactional
	public List<UserGroup> listMainGroups() {
		return userGroupDao.listMainGroups();
	}

	@Transactional
	public List<UserGroup> listSubGroups(Long id) {
		return userGroupDao.listSubGroups(id);
	}

	@Transactional
	public PagedArrayList<UserGroup> listByUserId(int page,int limit,Long userId){
		return userGroupDao.listByUserId(page, limit,userId);
	}

	@Transactional
	public UserGroup getByName(String groupName){
		return this.userGroupDao.getByName(groupName);
	}

	@Override
	@Transactional
	public UserGroup getByFullPath(String path) {
		String[] gpaths=path.split("/");
		Long parentId=0L;
		UserGroup ug=null;
		for(int i=0;i<gpaths.length;i++){
			ug=this.getByNameAndParentId(gpaths[i], parentId);
			if(ug!=null)
				parentId=ug.getId();
			else
				return null;
		}
		return ug;
	}
	@Override
	@Transactional
	public UserGroup getByNameAndParentId(String groupName, Long parentId) {
		return this.userGroupDao.getByNameAndParentId(groupName,parentId);
	}

	@Transactional
	public void deleteGroupsByIds(String[] groupIds){
		if(groupIds!=null&&groupIds.length>0){
			// 删除用户组的属地关联
			ArrayList<Long> longGroupIds=new ArrayList<Long>(groupIds.length);
			Set<Long> gids=new HashSet<Long>();
			//Set<Role> roles=new HashSet<Role>();
			Set<UserGroup> ugroups=new HashSet<UserGroup>();

			for(String groupId:groupIds){
				UserGroup userGroup = this.get(Long.valueOf(groupId));
				if(userGroup!=null)
					ugroups.add(userGroup);
				longGroupIds.add(Long.valueOf(groupId));
				gids.add(Long.valueOf(groupId));
			}
//			for(UserGroup ug:ugroups){
//				roles.addAll(ug.getRoles());
//			}
			//删除相关用户组下所有用户的角色赋予
			List<Long> groups=new ArrayList<Long>();
			groups.addAll(gids);
			List<Session> sessions;
			try {
				sessions = (List<Session> )sessionManager.findSessions(null,null,groups);
				for(Session s:sessions){
					sessionManager.deleteSession(s.getId());
				}
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
			/*List<Possession> possessions=possessionDao.findByGroupIds(longGroupIds);
			for(Possession possession:possessions){
				possession.setUserGroup(null);
				possessionDao.makePersistent(possession);
			}
			// 删除用户组的ICP信息关联
			List<ICPinfo> icpInfos=icpInfoDao.listAllLocal(longGroupIds);
			for(ICPinfo icpInfo:icpInfos){
				icpInfo.setLocal(null);
				icpInfoDao.makePersistent(icpInfo);
			}*/
			Set<Long> userIds=new HashSet<Long>();
			// 删除用户组的用户关联
			UserCriterion userCriterion=new UserCriterion();
			userCriterion.setGroupIds(gids);
			userCriterion.setIncludeAll(true);
			List<User> users=userDao.find(userCriterion);
			for(User user:users){
				userIds.add(user.getId());
				Iterator<UserGroup> it=user.getUserGroups().iterator();
				while(it.hasNext()){
					UserGroup userGroup=it.next();
					if(longGroupIds.indexOf(userGroup.getId())!=-1){
						it.remove();
						if(userGroup.getUsers()!=null)
							userGroup.getUsers().remove(user);
					}
				}
				userDao.makePersistent(user);
			}
			//批量删除相应ACL
			ResourceAclCriterion resourceAclCriterion=new ResourceAclCriterion();
			resourceAclCriterion.setUserId(userIds);
			resourceAclCriterion.setGroupId(gids);
			resourceAclService.batchDeleteUserAcls(resourceAclCriterion);

			for(UserGroup ug:ugroups){
				ug.getRoles().clear();
			}
			// 删除用户组
			StringBuilder logBuilder=new StringBuilder();
			logBuilder.append("删除用户组");
			for(UserGroup userGroup:ugroups){
				logBuilder.append(userGroup.getGroupName()).append(",");
				this.delete(userGroup);
			}
			//记录日志
			if(logBuilder.length()>0) userLogService.log(logBuilder.toString());
		}
	}

	@Override
	@Transactional
	public void deleteGroupsByIdsString(String groupIds) {
		String[] ids=groupIds.split(",");
		List<UserGroup> totals=new ArrayList<UserGroup>();
		for(int i=0;i<ids.length;i++){
			String id=ids[i];
			UserGroup ug=this.get(Long.valueOf(id));
			if(ug.getGroupName().equals(SecurityConstants.SYSTEM_ANONYMOUS_GROUP))
				continue;
			if(!totals.contains(ug))
				totals.add(ug);
			List<UserGroup> children=this.listChildrenInLevelOrder(ug);
			for(UserGroup g:children)
				totals.add(g);
		}
		int total=totals.size();
		String[] result=new String[total];
		for(int i=0;i<total;i++){
			UserGroup ug=totals.get(total-1-i);
			if(ug!=null)
				result[i]=ug.getId().toString();
		}
		this.deleteGroupsByIds(result);
	}

	@Override
	@Transactional
	public void disableGroupsByIdsString(String userGroupIds) {
		String[] ids=userGroupIds.split(",");
		for(int i=0;i<ids.length;i++){
			Long groupid=Long.valueOf(ids[i]);
			//this.disableUsersByGroupId(groupid);
			UserGroup temp=this.get(groupid);
			temp.setDisabled(true);
			this.updateUserGroup(temp);
		}
	}

	@Override
	@Transactional
	public void enableGroupsByIdsString(String userGroupIds) {
		String[] ids=userGroupIds.split(",");
		for(int i=0;i<ids.length;i++){
			Long groupid=Long.valueOf(ids[i]);
			//this.disableUsersByGroupId(groupid);
			UserGroup temp=this.get(groupid);
			temp.setDisabled(false);
			this.updateUserGroup(temp);
		}
	}

	public List<UserGroup> listChildrenInLevelOrder(UserGroup ug) {
		List<UserGroup> totals=new ArrayList<UserGroup>();
		int leftPivot=0;
		List<UserGroup> groups=this.listSubGroups(ug.getId());
		if(groups!=null&&groups.size()>0){
			totals.addAll(groups);
		}
		int rightPivot=totals.size()-1;
		while(leftPivot<rightPivot){
			for(int j=leftPivot;j<rightPivot;j++){
				 UserGroup sug=totals.get(j);
				 groups=this.listSubGroups(sug.getId());
				 if(groups!=null&&groups.size()>0){
					totals.addAll(groups);
				}
			}
			leftPivot=rightPivot;
			rightPivot=totals.size()-1;
		}
		return totals;
	}

	@Transactional
	public void disableUsersByGroupId(Long userGroupId){
		UserGroup ug = userGroupDao.findById(userGroupId, false);  //根据用户组号 查找用户组
		ug.setDisabled(true);
		userGroupDao.makePersistent(ug);
		StringBuffer sb = new StringBuffer();
		UserCriterion userCriterion=new UserCriterion();
		userCriterion.setUgroupId(ug.getId());
		List<User> users = userDao.find(userCriterion);   //获取该组下所有用户
		Iterator<User> it = users.iterator();
		int temp;
		while(it.hasNext()){
			User u = it.next();  //取出其中一个用户
			if(SecurityConstants.SYSTEM_DEFAULT_ADMIN.equals(u.getUserName())) //不停用admin
				continue;
			//if(u.getUserGroups().size()==1){   //如果用户只属于一个用户组停用该用户
			u.setDisabled(true);     //现在的逻辑是只要用户属于这个用户组，立即被停用
			userDao.makePersistent(u);
			if(sb.length()>0){
				sb.append(",");
			}
			sb.append(u.getUserName());
			//	}
		}
		userLogService.log("通过用户组["+ug.getGroupName()+"]停用用户,受影响的用户有["+sb.toString()+"]");
	}

	@Transactional
	public void enableUsersByGroupId(Long userGroupId){
		UserGroup ug = userGroupDao.findById(userGroupId, false);
		StringBuffer sb = new StringBuffer();
		UserCriterion userCriterion=new UserCriterion();
		userCriterion.setUgroupId(ug.getId());
		List<User> users = userDao.find(userCriterion);   //获取该组下所有用户
		Iterator<User> it = users.iterator();
		while(it.hasNext()){
			User u = it.next();
			u.setDisabled(false);
			userDao.makePersistent(u);
			if(sb.length()>0){
				sb.append(",");
			}
			sb.append(u.getUserName());
		}
		userLogService.log("通过用户组["+ug.getGroupName()+"]重新启用用户,受影响的用户有["+sb.toString()+"]");
	}

	@Transactional
	public List<UserGroup> listAll() {
		return userGroupDao.findAll();
	}

	/*public void startUserByGroupIds(String tempidString) {
		String[]  tempIds=tempidString.split("q");
		for(int i=0;i<tempIds.length;i++){
			Long groupid=Long.valueOf(tempIds[i]);
			//this.enableUsersByGroupId(groupid);
			UserGroup temp=this.get(groupid);
			temp.setDisabled(false);
			this.updateUserGroup(temp);
		}
	}

	public void stopUserByGroupIds(String tempidString) {
		String[]  tempIds=tempidString.split("q");
		for(int i=0;i<tempIds.length;i++){
			Long groupid=Long.valueOf(tempIds[i]);
			//this.disableUsersByGroupId(groupid);
			UserGroup temp=this.get(groupid);
			temp.setDisabled(true);
			this.updateUserGroup(temp);
		}
	}*/


	@Override
	@Transactional
	public boolean isNameExistedInSublings(UserGroup userGroup) {
		return userGroupDao.isNameExistedInSublings(userGroup);
	}

	@Override
	@Transactional
	public UserGroup addUserGroup(UserGroup userGroup) {
		UserGroup ug=this.userGroupDao.makePersistent(this.attachAttributeForGroupAdd(userGroup));
		if(!HttpAuthnUtils.isAdmin()){
			User user=userDao.getByName(HttpAuthnUtils.getLoginUserName());
			//当前登陆用户如果不是admin，那么
			//1.将该用户分配到该组下
			user.getUserGroups().add(ug);
			userDao.makePersistent(user);
			//2.同时需要把新建组的管理权限赋给该用户
			try {
				Session session=new Session();
				session.setGroupId(ug.getId());
				session.setRoleId(roleManager.getRoleByName("组管理员").getId());
				session.setUserId(user.getId());
				sessionManager.addNewSession(session);
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}
		return ug;
	}
	@Override
	@Transactional
	public UserGroup addUserGroupTY(UserGroup userGroup) {
		UserGroup ug=this.userGroupDao.makePersistent(this.attachAttributeForGroupAdd(userGroup));
		return ug;
	}

	@Override
	@Transactional
	public UserGroup updateUserGroup(UserGroup userGroup) {
		attachAttributeForGroupUpdate(userGroup);
		return this.userGroupDao.makePersistent(userGroup);
	}

	private UserGroup attachAttributeForGroupUpdate(UserGroup userGroup){
		userGroup.setInnerTag(this.getInnerTag(userGroup));
		//UserGroup parent=this.get(userGroup.getParentId());
		if(userGroup.getParentId()==null||this.get(userGroup.getParentId())==null){
			userGroup.setParentId(0L);
			userGroup.setLevel(1);
		}else{
			userGroup.setLevel(this.getLevel(userGroup));
		}
		return userGroup;
	}

	/**
	 * 为web端传入的userGroup bean维护必要的内部属性，包括position，innerTag，level（这些属性不必从web端传入）
	 * */
	private UserGroup attachAttributeForGroupAdd(UserGroup userGroup){
		userGroup.setPosition(this.getLastPosition(userGroup.getParentId())+1);//添加结点在兄弟结点间的排序
		userGroup.setInnerTag(this.getInnerTag(userGroup));
		//UserGroup parent=this.get(userGroup.getParentId());
		if(userGroup.getParentId()==null||this.get(userGroup.getParentId())==null){
			userGroup.setParentId(0L);
			userGroup.setLevel(1);
		}else{
			userGroup.setLevel(this.getLevel(userGroup));
		}
		return userGroup;
	}

	private int getLastPosition(Long parentId) {
		List<UserGroup> list=this.listSubGroups(parentId);
		if(list.size()>0)
			return list.get(list.size()-1).getPosition();//查该list中最后一个元素，也就是position最大的元素的position值
		else return 0;
	}

	private String getInnerTag(UserGroup userGroup){
		StringBuilder sb=new StringBuilder();
		Long parentId=userGroup.getParentId();
		if(parentId!=null){
			UserGroup parent=this.get(parentId);
			while(parent!=null){
				if(sb.length()>0)
					sb.insert(0, "/");
				sb.insert(0,parent.getId());
				parent=this.get(parent.getParentId());
			}
		}
		return sb.toString();
	}

	private int getLevel(UserGroup userGroup){
		int level=1;
		if(userGroup.getParentId()==null||userGroup.getParentId()==0L)
			return level;
		UserGroup parent=this.get(userGroup.getParentId());
		while(parent!=null){
			level++;
			parent=this.get(parent.getParentId());//find parent
		}
		return level;
	}

	@Override
	@Transactional
	public boolean alignGroup(Long userGroupId, Long refId, String type) {
		UserGroup userGroup=this.userGroupDao.findById(userGroupId, false);
		UserGroup refTheme=this.userGroupDao.findById(refId, false);
		int pos1=userGroup.getPosition();
		int pos2=refTheme.getPosition();
		Long parent1=userGroup.getParentId();
		Long parent2=refTheme.getParentId();
		long par1=parent1==null?0:parent1.longValue();
		long par2=parent2==null?0:parent2.longValue();
		if("before".equals(type)){
			UserGroupCriterion userGroupCriterion=new UserGroupCriterion();
			userGroupCriterion.setParentId(par2);
			if(par1!=par2) {
				//userGroup移走后原来排在它后面的所有兄弟结点position减1
				UserGroupCriterion criterion=new UserGroupCriterion();
				criterion.setParentId(par1);
				criterion.setGtPosition(pos1);
				List<UserGroup> userGrouplist=this.listGroups(criterion);
				for(UserGroup ht:userGrouplist){
					ht.setPosition(ht.getPosition()-1);
					this.userGroupDao.makePersistent(ht);
				}
				//userGroup移动后排在它后面的所有兄弟结点position加1
				userGroupCriterion.setGePosition(pos2);
				List<UserGroup> list=this.listGroups(userGroupCriterion);
				for(UserGroup ht:list){
					ht.setPosition(ht.getPosition()+1);
					this.userGroupDao.makePersistent(ht);
				}
				//userGroup移动后的parentId和位置确定
				userGroup.setParentId(parent2);
				userGroup.setPosition(pos2);
			}else{//兄弟节点间移动只需要改变受影响的结点pos
				if(pos1<pos2){//移动点在参考点之前
					userGroupCriterion.setGtPosition(pos1);
					userGroupCriterion.setLtPosition(pos2);
					List<UserGroup> list=this.listGroups(userGroupCriterion);
					for(UserGroup ht:list){
						ht.setPosition(ht.getPosition()-1);
						this.userGroupDao.makePersistent(ht);
					}
					userGroup.setPosition(pos2-1);
				}else{//移动点在参考点之后
					userGroupCriterion.setGePosition(pos2);
					userGroupCriterion.setLtPosition(pos1);
					List<UserGroup> list=this.listGroups(userGroupCriterion);
					for(UserGroup ht:list){
						ht.setPosition(ht.getPosition()+1);
						this.userGroupDao.makePersistent(ht);
					}
					userGroup.setPosition(pos2);
				}
			}
		}else if("after".equals(type)){
			UserGroupCriterion userGroupCriterion=new UserGroupCriterion();
			userGroupCriterion.setParentId(par2);
			if(par1!=par2) {
				//userGroup移走后原来排在它后面的所有兄弟结点position减1
				UserGroupCriterion criterion=new UserGroupCriterion();
				criterion.setParentId(par1);
				criterion.setGtPosition(pos1);
				List<UserGroup> userGrouplist=this.listGroups(criterion);
				for(UserGroup ht:userGrouplist){
					ht.setPosition(ht.getPosition()-1);
					this.userGroupDao.makePersistent(ht);
				}
				//userGroup移动后排在它后面的所有兄弟结点position加1
				userGroupCriterion.setGtPosition(pos2);
				List<UserGroup> list=this.listGroups(userGroupCriterion);
				for(UserGroup ht:list){
					ht.setPosition(ht.getPosition()+1);
					this.userGroupDao.makePersistent(ht);
				}
				//userGroup移动后的parentId和位置确定
				userGroup.setParentId(parent2);
				userGroup.setPosition(pos2+1);
			}else{//兄弟节点间移动只需要改变受影响的结点pos
				if(pos1<pos2){//移动点在参考点之前
					userGroupCriterion.setGtPosition(pos1);
					userGroupCriterion.setLePosition(pos2);
					List<UserGroup> list=this.listGroups(userGroupCriterion);
					for(UserGroup ht:list){
						ht.setPosition(ht.getPosition()-1);
						this.userGroupDao.makePersistent(ht);
					}
					userGroup.setPosition(pos2);
				}else{//移动点在参考点之后
					userGroupCriterion.setGtPosition(pos2);
					userGroupCriterion.setLtPosition(pos1);
					List<UserGroup> list=this.listGroups(userGroupCriterion);
					for(UserGroup ht:list){
						ht.setPosition(ht.getPosition()+1);
						this.userGroupDao.makePersistent(ht);
					}
					userGroup.setPosition(pos2+1);
				}
			}
		}else{//inside
			userGroup.setPosition(this.getLastPosition(refId)+1);
			userGroup.setParentId(refId);
			UserGroupCriterion userGroupCriterion=new UserGroupCriterion();
			userGroupCriterion.setParentId(par1);
			userGroupCriterion.setGtPosition(pos1);
			List<UserGroup> userGrouplist=this.listGroups(userGroupCriterion);
			for(UserGroup ht:userGrouplist){
				ht.setPosition(ht.getPosition()-1);
				this.userGroupDao.makePersistent(ht);
			}
		}
		this.userGroupDao.makePersistent(userGroup);
		return true;
	}

	@Override
	@Transactional
	public List<UserGroup> listGroups(UserGroupCriterion userGroupCriterion) {
		return this.userGroupDao.listGroups(userGroupCriterion);
	}

	@Override
	@Transactional
	public List<UserGroup> listGroups(Collection<Long> groupIds) {
		return this.userGroupDao.listGroups(groupIds);
	}

	@Override
	@Transactional
	public List<UserGroup> listGroupsByRole(Role role) {
		return this.userGroupDao.listGroupsByRole(role.getId());
	}

	/*@Override
	public void setVisibility(String fromIds, String toIds) {
		String[] froms=fromIds.split(",");
		String[] tos=toIds.split(",");
		Long[] ids=new Long[tos.length];
		for(int i=0;i<tos.length;i++){
			ids[i]=Long.valueOf(tos[i]);
		}
		//kk
		for(String from:froms){
			Long f=Long.valueOf(from);
			groupVisibilityDao.clearByFromId(f);
			for(Long t:ids){
				GroupVisibility gv=new GroupVisibility();
				gv.setFromId(f);
				gv.setToId(t);
				this.groupVisibilityDao.makePersistent(gv);
			}

		}
	}*/


	@Override
	@Transactional
	public void setVisibility(String fromIds, Long toId) {
		UserGroup userGroup=this.get(toId);
		if(userGroup==null)
			return;
		List<UserGroup> preGroups=this.listVisibleGroups(toId);

		Set<Long> afterGroupIds=new HashSet<Long>();//之后的用户组
		if(!StringUtils.isEmpty(fromIds)){
			String[] groups = fromIds.split(",");
			for(String g:groups){
				afterGroupIds.add(Long.valueOf(g));
			}
		}
		List<UserGroup> afterGroups=this.listGroups(afterGroupIds);
		List<Long> adds=new ArrayList<Long>();
		List<Long> removes=new ArrayList<Long>();
		for(UserGroup af:afterGroups){
			if(!preGroups.contains(af))
				adds.add(af.getId());
		}
		for(UserGroup pre:preGroups){
			Long preId=pre.getId();
			if(!afterGroups.contains(pre))
				removes.add(preId);
		}
		groupVisibilityDao.clearByToId(toId);
		for(Long add:adds){//视野中新增的组
			if(add.equals(toId))
				continue;
			GroupVisibility gv=new GroupVisibility();
			gv.setFromId(add);
			gv.setToId(toId);
			this.groupVisibilityDao.makePersistent(gv);
		}
		for(Long remove:removes){//视野中删除的组
			if(remove.equals(toId))
				continue;
			groupVisibilityDao.deleteVisibility(remove,toId);
		}
	}

	@Override
	@Transactional
	public List<UserGroup> listVisibleGroups(Long toId) {
		List<GroupVisibility> gvs=groupVisibilityDao.listByToId(toId);
		List<UserGroup> ugs=new ArrayList<UserGroup>();
		for(GroupVisibility gv:gvs){
			UserGroup ug=this.get(gv.getFromId());
			if(ug!=null)
				ugs.add(ug);
		}
		UserGroup thisGroup=this.get(toId);
		ugs.add(thisGroup);
		return ugs;
	}

	@Override
	@Transactional
	public List<UserGroup> listVisibleToGroups(Long fromId) {
		List<GroupVisibility> gvs=groupVisibilityDao.listByFromId(fromId);
		List<UserGroup> ugs=new ArrayList<UserGroup>();
		for(GroupVisibility gv:gvs){
			UserGroup ug=this.get(gv.getToId());
			if(ug!=null)
				ugs.add(ug);
		}
		return ugs;
	}

	@Override
	@Transactional
	public String getGroupFullName(UserGroup userGroup) {
		String innerTag=userGroup.getInnerTag();
		StringBuilder sb=new StringBuilder();
		if(StringUtils.isNotBlank(innerTag)){
			String[] tags=innerTag.split("/");
			for(int i=0;i<tags.length;i++){
				UserGroup g=this.get(Long.valueOf(tags[i]));
				if(sb.length()>0)
					sb.append(UserGroupService.GROUP_NAME_SEPERATOR);
				if(g!=null)
					sb.append(g.getGroupName());
			}
		}
		if(sb.length()>0)
			sb.append(UserGroupService.GROUP_NAME_SEPERATOR);
		sb.append(userGroup.getGroupName());
		return sb.toString();
	}

	@Override
	@Transactional
	public void cancelVisibility(Long fromId, Long toId) {
		this.groupVisibilityDao.deleteVisibility(fromId,toId);
	}

	@Override
	@Transactional
	public void deleteRolesInGroup(String roleIds, Long userGroupId) {
		if(userGroupId==null)
			return;
		UserGroup userGroup=this.get(userGroupId);
		if(userGroup==null)
			return;
		Set<Role> removedRoles=new HashSet<Role>();
		String[] roleids=roleIds.split(",");
		try{
			for(String roleId:roleids){
				if(StringUtils.isBlank(roleId))
					continue;
				Role role=roleManager.getRole(Long.valueOf(roleId));
				if(role!=null)
					removedRoles.add(role);
			}
		}catch(AuthorizationException e){
			throw new TRSOMException(e.getMessage(),e);
		}
		this.removeGroupRoles(userGroup, removedRoles);
	}

	@Override
	@Transactional
	public void configRoles(String roleIds, Long userGroupId) {
		UserGroup userGroup=this.get(userGroupId);
		if(userGroup==null)
			return;
		String[] roleids=roleIds.split(",");
		Set<Role> beforeRoles=userGroup.getRoles();//当前用户组之前可用的角色
		List<Role> afterRoles=new ArrayList<Role>();//当前用户组可用的角色
		//Set<Role> removedRoles=new HashSet<Role>();//在之前的角色配置中现在需要被删除的角色
		try{
			for(String roleId:roleids){
				if(StringUtils.isBlank(roleId))
					continue;
				Role role=roleManager.getRole(Long.valueOf(roleId));
				if(role!=null)
					afterRoles.add(role);
			}
		}catch(AuthorizationException e){
			throw new TRSOMException(e.getMessage(),e);
		}
		this.configGroupRoles(userGroup,beforeRoles, afterRoles);
	}

	/**
	 * 配置用户组的可用角色
	 * @param userGroup 待配置的用户组
	 * @param beforeRoles 用户组之前的角色配置
	 * @param afterRoles 用户组配置后的角色配置
	 * */
	private void configGroupRoles(UserGroup userGroup,Collection<Role> beforeRoles,Collection<Role> afterRoles){
		Set<Role> removedRoles=new HashSet<Role>();//在之前的角色配置中现在需要被删除的角色
		for(Role r:beforeRoles){
			if(!afterRoles.contains(r)){
				removedRoles.add(r);
			}
		}
		//step1 删除需要被删除的角色
		removeGroupRoles(userGroup,removedRoles);
		//step2 在当前用户组上添加需要添加的角色
		userGroup.getRoles().clear();
		for(Role role:afterRoles){
			role.getUserGroups().add(userGroup);
			userGroup.getRoles().add(role);
		}
		userGroupDao.makePersistent(userGroup);
	}

	@Override
	@Transactional
	public void removeGroupRoles(UserGroup userGroup,Collection<Role> removedRoles){
		List<UserGroup> operateGroups=this.listChildrenInLevelOrder(userGroup);
		operateGroups.add(userGroup);
		for(Role r:removedRoles){
			//从一个用户组中移除某个角色，那么它的所有下级用户组中都将清除之。
			List<UserGroup> ugs=this.listGroupsByRole(r);//r.getUserGroups();
			List<Long> groups=new ArrayList<Long>();
			for(UserGroup ug:operateGroups){
				if(ugs.contains(ug)){
					groups.add(ug.getId());
					r.getUserGroups().remove(ug);
					ug.getRoles().remove(r);
					userGroupDao.makePersistent(ug);
				}
			}
			List<Long> roles=new ArrayList<Long>();
			roles.add(r.getId());
			//所有下级用户组中涉及到已经赋予角色的用户，全部清除该角色赋予
			List<Session> sessions;
			try {
				sessions = (List<Session> )sessionManager.findSessions(null,roles,groups);
				for(Session s:sessions){
					sessionManager.deleteSession(s.getId());
				}
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	@Transactional
	public void configRoleToGroups(Long roleId, String groupIds) {
		Role role=null;
		try{
			role=roleManager.getRole(roleId);
			if(role==null)
				return;
		}catch(AuthorizationException e){
			throw new TRSOMException(e.getMessage(),e);
		}
		List<UserGroup> ugs=this.listGroupsByRole(role);//角色已经配置到的用户组
		List<UserGroup> permittedGroups=new ArrayList<UserGroup>();
		if(HttpAuthnUtils.isAdmin()){
			permittedGroups=ugs;
		}else{//过滤掉当前用户看不到的用户组配置
			User cu=userDao.findById(HttpAuthnUtils.getLoginUserId(), false);
			Set<Long> ids=permissionService.listGroupIdsForPermission(cu, PermissionConstants.GROUP_ROLE_PERMISSION);

			for(Long id:ids){
				UserGroup ug=this.get(id);
				if(ug!=null&&ugs.contains(ug))
					permittedGroups.add(ug);
			}
		}

		Set<Long> preGroups=new HashSet<Long>();//之前的用户组
		for(UserGroup ug:permittedGroups){
			preGroups.add(ug.getId());
		}

		Set<Long> afterGroups=new HashSet<Long>();//之后的用户组
		if(!StringUtils.isEmpty(groupIds)){
			String[] groups = groupIds.split(",");
			for(String g:groups){
				afterGroups.add(Long.valueOf(g));
			}
		}

		List<UserGroup> adds=new ArrayList<UserGroup>();
		List<UserGroup> removes=new ArrayList<UserGroup>();
		for(Long af:afterGroups){
			if(!preGroups.contains(af))
				adds.add(this.get(af));
		}
		for(Long pre:preGroups){
			if(!afterGroups.contains(pre))
				removes.add(this.get(pre));
		}
		for(UserGroup ug:adds){//角色配置新增的组
			role.getUserGroups().add(ug);
			ug.getRoles().add(role);
			userGroupDao.makePersistent(ug);
		}
		for(UserGroup ug:removes){//角色配置删除的组
			role.getUserGroups().remove(ug);
			ug.getRoles().remove(role);
			userGroupDao.makePersistent(ug);
		}
	}

	@Override
	@Transactional
	public void removeUser(Long userId, Long groupId) {
		User user=userDao.findById(userId, false);
		UserGroup ug=this.get(groupId);
		if(user!=null&&ug!=null){
			Set<UserGroup> ugs=user.getUserGroups();
			if(ugs.contains(ug)){
				ugs.remove(ug);
				user.setUserGroups(ugs);
				this.userDao.makePersistent(user);
			}
		}
		//删除用户在该组下的所有角色赋予
		try {
			List<Session> ss=sessionManager.findSessions(userId, null, groupId);
			for(Session s:ss){
				sessionManager.deleteSession(s.getId());
			}
		} catch (AuthorizationException e) {
			e.printStackTrace();
		}
		//删除用户在用户组下的访问控制
		ResourceAclCriterion resourceAclCriterion=new ResourceAclCriterion();
		Set<Long> userIds=new HashSet<Long>();
		userIds.add(userId);
		Set<Long> groupIds=new HashSet<Long>();
		groupIds.add(groupId);
		resourceAclCriterion.setUserId(userIds);
		resourceAclCriterion.setGroupId(groupIds);
		resourceAclService.batchDeleteUserAcls(resourceAclCriterion);
	}

	@Override
	@Transactional
	public Set<UserGroup> findTopGroups(Set<Long> groupIds) {
		Set<Long> ids=new HashSet<Long>();
		Set<UserGroup> gs=new HashSet<UserGroup>();
		Set<UserGroup> result=new HashSet<UserGroup>();
		//Map<UserGroup,>
		for(Long id:groupIds){
			UserGroup g=this.get(id);
			if(g!=null){
				gs.add(g);
				ids.add(id);
			}
		}
		for(UserGroup g:gs){
			String innerTag=g.getInnerTag();
			if(StringUtils.isBlank(innerTag)){
				result.add(g);
			}else{
				boolean top=true;
				for(Long id:ids){
					if(innerTag.contains(id.toString())){
						top=false;
						break;
					}
				}
				if(top)
					result.add(g);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public void exportGroups(String ids, File configRoot) {
		Set<UserGroup> topGroups=new HashSet<UserGroup>();
		if(StringUtils.isEmpty(ids)){//ids为空导出全部用户组
			topGroups.addAll(this.listMainGroups());
		}else{
			String[] gids=ids.split(",");
			Set<Long> groupIds=new HashSet<Long>();
			for(String gid:gids){
				groupIds.add(Long.valueOf(gid));
			}
			topGroups=findTopGroups(groupIds);
		}
		StringBuffer sb = new StringBuffer();//所有的group的字符串
		for(UserGroup group:topGroups){
			sb.append(getUserGroupXmlData(group));
			List<UserGroup> children=this.listChildrenInLevelOrder(group);
			for(UserGroup ug:children){
				sb.append(getUserGroupXmlData(ug));
			}
		}
		BufferedWriter groupWriter=null;
		try {
			groupWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(configRoot, CONFIG_FILENAME)), configCharset));
			groupWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><group_config source=\"40\">");
			groupWriter.append(sb);
			groupWriter.append("</group_config>");//group_config
		} catch (UnsupportedEncodingException e) {
			throw new TRSOMException(e);
		} catch (FileNotFoundException e) {
			throw new TRSOMException(e);
		} catch (IOException e) {
			throw new TRSOMException(e);
		} finally{
			if(null!=groupWriter)
				try {
					groupWriter.close();
				} catch (IOException e) {
					LOGGER.error("无法关闭输出流", e);
				}
		}
	}

	private String getUserGroupXmlData(UserGroup userGroup){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sb = new StringBuffer();
		sb.append("<group id=\"").append(userGroup.getId()).append("\" groupName=\"").append(userGroup.getGroupName()).append("\"");
		sb.append(" parentId=\"").append(userGroup.getParentId()).append("\"");
		sb.append(" position=\"").append(userGroup.getPosition()).append("\"");
		sb.append(" createDate=\"").append(sdf.format(userGroup.getCreateDate())).append("\"");
		sb.append(" disabled=\"").append(userGroup.isDisabled()).append("\"/>");
		return sb.toString();
	}

	@Override
	@Transactional
	public String importGroups(int type, File configRoot) {
		SAXReader saxReader=new SAXReader();
		List<UserGroup> importGroups=new ArrayList<UserGroup>();//这个List保存将被插入的数据
		List<UserGroup> updateGroups=new ArrayList<UserGroup>();//这个List保存将被更新的数据
		Map<Long,Long> groupMap=new HashMap<Long,Long>();//用于将所有的xml文档中的group的id对应成导入后系统中真实的id
		groupMap.put(0L, 0L);
		StringBuilder sb=new StringBuilder();
		try {
			Document document=saxReader.read(configRoot);
			Element root=document.getRootElement();
			List<Element> nodes=root.elements();
			if(!nodes.isEmpty()){
				for(Element group:nodes){
					 if(group.getName().equals("group")){//有用户组的配置，先配置用户组
						UserGroup ug=new UserGroup();

						Long groupId=Long.valueOf(group.attribute("id").getValue());
						ug.setId(groupId);
						String groupName=group.attribute("groupName").getValue();
						Long parentId=Long.valueOf(group.attribute("parentId").getValue());
						ug.setGroupName(groupName);
						ug.setCreateDate(new Date());
						ug.setDisabled(group.attribute("disabled").getValue().equals("true")?true:false);
						ug.setParentId(parentId);
						if(groupMap.containsKey(parentId)){//groupMap中包含该parentId，说明id为parentId的节点是重名节点，此时需要判断当前节点是否重名，否则不用
							Long newParentId=groupMap.get(parentId);//找到其parentId在系统中对应的真实parentId
							UserGroup originGroup=this.getByNameAndParentId(groupName, newParentId);
							if(originGroup!=null){//当前节点重名
								groupMap.put(groupId,originGroup.getId());
								if(type==0){
									sb.append("用户组["+groupName+"]名称已存在;\n");
									continue;
								}else if(type==1){//不考虑重名数据，利用原有数据
									continue;
								}else if(type==2){//重名数据覆盖原有同名数据
									originGroup.setCreateDate(new Date());
									originGroup.setDisabled(group.attribute("disabled").getValue().equals("true")?true:false);
									updateGroups.add(originGroup);
									continue;
								}
								sb.append("用户组["+groupName+"]名称已存在;\n");
								continue;
							}else{//当前节点不存在重名
								importGroups.add(ug);
							}
						}else{//groupMap中不包含该parentId，说明id为parentId的节点是不重名节点，此时不需要判断当前节点是否重名，直接将其按照xml文档中数据插入
							importGroups.add(ug);
						}

					}
				}
			}else{
				sb.append("没有可导入的用户组数据");
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		if(sb.length()==0){//检查无误后批量存入数据库中
			if(type==2){//只有覆盖导入时才可能存在更新数据
				if(!updateGroups.isEmpty()){
					for(UserGroup ug:updateGroups){
						this.updateUserGroup(ug);
					}
				}

			}
			if(!importGroups.isEmpty()){
				for(UserGroup ug:importGroups){
					Long previousId=ug.getId();
					ug.setId(null);
					ug.setParentId(groupMap.get(ug.getParentId()));
					UserGroup userGroup=this.addUserGroup(ug);
					groupMap.put(previousId, userGroup.getId());
				}
			}
		}else{
			userLogService.log("导入用户组数据因出现下列重名情况而终止："+sb.toString().replaceAll("\n", ","));
		}
		return sb.toString();
	}


	@Override
	@Transactional
	public boolean isGroupDisabled(UserGroup userGroup) {
		if(userGroup==null)
			return false;
		if(userGroup.isDisabled())
			return true;
		UserGroup parent=this.get(userGroup.getParentId());
		while(parent!=null){
			if(parent.isDisabled())
				return true;
			parent=this.get(parent.getParentId());
		}
		return false;
	}

	@Override
	@Transactional
	public boolean isUpperGroupDisabled(UserGroup userGroup) {
		if(userGroup==null)
			return false;
		UserGroup parent=this.get(userGroup.getParentId());
		while(parent!=null){
			if(parent.isDisabled())
				return true;
			parent=this.get(parent.getParentId());
		}
		return false;
	}

	@Transactional
	public PagedArrayList<User> getUsersByUserGroup(int page, String groupName) {
		return userGroupDao.getUsersByUserGroup(page, groupName);
	}

	@Transactional
	public List<User> getUsersByUserGroup(String groupName) {
		return userGroupDao.getUsersByUserGroup(groupName);
	}
	@Override
	@Transactional
	public String getUserGroupJson(Long selectedNodeId) {
		StringBuilder builder=new StringBuilder();
		List<UserGroup> list=listMainGroups();
		List<UserGroup> pathList=new ArrayList<UserGroup>();//当前被选中结点的父节点链
		UserGroup curNode=null;
		if(selectedNodeId!=null){
			curNode=get(selectedNodeId);//当前被选中结点
			while(curNode!=null){
				pathList.add(0,curNode);//pathList中插入第一个位置
				curNode=get(curNode.getParentId());
			}
		}
		appendUserGroupListJson(builder, list, pathList, 0);
		return builder.toString();
	}
	
	private void appendUserGroupListJson(StringBuilder builder,List<UserGroup> nodeList,List<UserGroup> pathToSelectedNodeList,int level) {
		builder.append("[");
		UserGroup inPathNode=null;
		if(level<pathToSelectedNodeList.size())
			inPathNode=pathToSelectedNodeList.get(level);
		for(int i=0;i<nodeList.size();i++){
			UserGroup node=nodeList.get(i);
			if(i>0)
				builder.append(",");
			builder.append("{");
			//builder.append("\"attributes\":{\"id\":\"node_").append(node.getId()).append("\"},");
			builder.append("\"attributes\":{\"id\":\"node_").append(node.getId().toString()).append("\",");
			builder.append("\"title\":\"").append(StringEscapeUtils.escapeJavaScript(node.getGroupName())).append("\",");
			builder.append("\"status\":\"").append(node.isDisabled()?"停用":"启用").append("\",");
			builder.append("\"level\":\"").append(level).append("\"},");
			builder.append("\"data\":\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(StringOption.stringIntercept(node.getGroupName(), 12, "...")))).append("\",");
			if(node.equals(inPathNode)){
				builder.append("\"state\":\"open\",");
				builder.append("\"children\":");
				appendUserGroupListJson(builder, listSubGroups(node.getId()), pathToSelectedNodeList, level+1);
			}else{
				builder.append("\"state\":\"closed\"");
			}
			builder.append("}");
		}
		builder.append("]");

	}

	@Override
	@Transactional
	public String getUserGroupFullJson() {
		StringBuilder builder=new StringBuilder();
		List<UserGroup> nodeList=listMainGroups();
		appendUserGroupListJson(builder, nodeList,0);
		return builder.toString();
	}

	private void appendUserGroupListJson(StringBuilder builder,List<UserGroup> nodeList,int level) {
		builder.append("[");
		for(int i=0;i<nodeList.size();i++){
			UserGroup node=nodeList.get(i);
			if(i>0)
				builder.append(",");
			builder.append("{");
			//builder.append("\"attributes\":{\"id\":\"node_").append(node.getId()).append("\"},");
			builder.append("\"attributes\":{\"id\":\"node_").append(node.getId().toString()).append("\",");
			builder.append("\"title\":\"").append(StringEscapeUtils.escapeJavaScript(node.getGroupName())).append("\",");
			builder.append("\"status\":\"").append(node.isDisabled()?"停用":"启用").append("\",");
			builder.append("\"level\":\"").append(level+1).append("\"},");
			builder.append("\"data\":\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(StringOption.stringIntercept(node.getGroupName(), 12, "...")))).append("\",");
			builder.append("\"state\":\"open\",");
			builder.append("\"children\":");
			List<UserGroup> subList=listSubGroups(node.getId());
			if(subList!=null&&subList.size()>0){
				appendUserGroupListJson(builder, subList,level+1);
			}else{
				builder.append("[]");
			}
			builder.append("}");
		}
		builder.append("]");
	}

	@Override
	public String getTopGroupJson(Set<UserGroup> topGroups) {
		StringBuilder builder=new StringBuilder();
		builder.append("[");
		boolean firstOver=false;
		 for(UserGroup ug:topGroups){
			 if(firstOver)
				 builder.append(",");
			builder.append("{");
			builder.append("\"attributes\":{\"id\":\"node_").append(ug.getId().toString()).append("\",");
			builder.append("\"title\":\"").append(StringEscapeUtils.escapeJavaScript(ug.getGroupName())).append("\",");
			builder.append("\"status\":\"").append(ug.isDisabled()?"停用":"启用").append("\",");
			builder.append("\"level\":\"0\"},");
			builder.append("\"data\":\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(StringOption.stringIntercept(ug.getGroupName(), 12, "...")))).append("\",");
			builder.append("\"state\":\"closed\"}");
			if(!firstOver)
				firstOver=true;
		}
		builder.append("]");
		return builder.toString();
	}
	//accessors	---------------------------------------------------------------------
	public UserGroupDao getUserGroupDao() {
		return userGroupDao;
	}

	public void setUserGroupDao(UserGroupDao userGroupDao) {
		this.userGroupDao = userGroupDao;
	}

	public UserLogService getUserLogService() {
		return userLogService;
	}

	public void setUserLogService(UserLogService userLogService) {
		this.userLogService = userLogService;
	}
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public GroupVisibilityDao getGroupVisibilityDao() {
		return groupVisibilityDao;
	}

	public void setGroupVisibilityDao(GroupVisibilityDao groupVisibilityDao) {
		this.groupVisibilityDao = groupVisibilityDao;
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public RoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public ResourceAclService getResourceAclService() {
		return resourceAclService;
	}

	public void setResourceAclService(ResourceAclService resourceAclService) {
		this.resourceAclService = resourceAclService;
	}

	/*public void setPossessionDao(PossessionDao possessionDao) {
		this.possessionDao = possessionDao;
	}

	public void setIcpInfoDao(ICPinfoDao icpInfoDao) {
		this.icpInfoDao = icpInfoDao;
	}*/

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	@Override
	@Transactional
	public User selectUser(Long userId, Long groupId) {
		// TODO Auto-generated method stub
		User user=userDao.findById(userId, false);
		return user;
	}
}
