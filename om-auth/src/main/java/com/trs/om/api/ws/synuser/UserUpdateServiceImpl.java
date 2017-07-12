package com.trs.om.api.ws.synuser;

import java.sql.Timestamp;
import java.util.Date;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.User;
import com.trs.om.service.UserGroupService;
import com.trs.otm.authentication.MD5;

@WebService(endpointInterface="com.trs.om.api.ws.synuser.UserUpdateService")
@SOAPBinding(style=Style.RPC)
public class UserUpdateServiceImpl implements UserUpdateService {
	private GroupOrgService groupOrgService;
	private UserGroupService userGroupService;
	private UserSynService userSynService;
	private String digestRealmName;

	public String getDigestRealmName() {
		return digestRealmName;
	}

	public void setDigestRealmName(String digestRealmName) {
		this.digestRealmName = digestRealmName;
	}

	public void setUserSynService(UserSynService userSynService) {
		this.userSynService = userSynService;
	}

	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	public void setGroupOrgService(GroupOrgService groupOrgService) {
		this.groupOrgService = groupOrgService;
	}

	@Override
	@Transactional
	public boolean updateUser(String user) {
		boolean flag=false;
		try {
			JSONArray array = new JSONArray(user);
			for(int i=0;i<array.length();i++){
				JSONObject jsonobject=new JSONObject(array.getString(i));
				//String oid=jsonobject.getString("orgId");
				//判断用户组是否已经存在；存在则更新用户，不存在则直接返回fale
				//if(groupOrgService.query(oid)){//如果没有传用户组信息，表示该用户没有用户组
					String loginName=jsonobject.has("loginName")?jsonobject.getString("loginName"):"";
					//判断当前用户是否已经存在，如果不存在则插入并插入用户——组关系；如果存在则直接更新用户信息，不更新用户——组关系；
					if(StringUtils.isBlank(loginName))continue;
					if(loginName.equals("admin")||loginName.equals("anonymous"))
						continue;
					if(null==userSynService.getUser(loginName)){
						User u=new User();
						u.setUserName(jsonobject.getString("loginName"));
						if(jsonobject.has("password")&&StringUtils.isNotBlank(jsonobject.getString("password"))){
							u.setDigestPassword(MD5.md5(jsonobject.getString("loginName")+":"+digestRealmName+":"+jsonobject.getString("password")));
							u.setUserPassword(MD5.md5(jsonobject.getString("password")));
						}else if(jsonobject.has("md5")&&StringUtils.isNotBlank(jsonobject.getString("md5"))){
							u.setUserPassword(jsonobject.getString("md5"));
						}
						if(jsonobject.has("name"))
							u.setNickName(jsonobject.getString("name"));
						if(jsonobject.has("remark"))
							u.setUserRemark(jsonobject.getString("remark"));
						if(jsonobject.has("mobile"))
							u.setMobile(jsonobject.getString("mobile"));
						if(jsonobject.has("email"))
							u.setEmail(jsonobject.getString("email"));
						u.setDisabled(jsonobject.getString("status").equals("1")?true:false);
						u.setCreator("第三方推送");
						u.setCreationDate(new Timestamp(new Date().getTime()));
						u.setUserType(User.DEFAULT_USER_TYPE);
						//u.setDisabled(false);
						/*UserGroup ug=userGroupService.get(new Long(groupOrgService.queryforgroupid(jsonobject.getString("orgId"))));
						Set<UserGroup> gids=new HashSet<UserGroup>();
						gids.add(ug);
						u.setUserGroups(gids);*/
						userSynService.addUser(u);
						flag=true;
					}else{
						User u1=userSynService.getUser(loginName);
						u1.setUserName(jsonobject.getString("loginName"));
						u1.setUserPassword(MD5.md5(jsonobject.getString("password")));
						u1.setNickName(jsonobject.getString("name"));
						u1.setDisabled(jsonobject.getString("status").equals("1")?true:false);
						u1.setUserRemark(jsonobject.getString("remark"));
						u1.setMobile(jsonobject.getString("mobile"));
						u1.setEmail(jsonobject.getString("email"));
						u1.setDigestPassword(MD5.md5(jsonobject.getString("loginName")+":"+digestRealmName+":"+jsonobject.getString("password")));
						u1.setCreator("第三方推送");
						//u1.setCreationDate(new Timestamp(new Date().getTime()));
						u1.setUserType(User.DEFAULT_USER_TYPE);
						userSynService.updateUser(u1);
						flag=true;
					}

				/*}else{
					return false;
				}*/
			}
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		return flag;
	}

}
