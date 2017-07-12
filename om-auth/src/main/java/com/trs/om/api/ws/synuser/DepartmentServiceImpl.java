package com.trs.om.api.ws.synuser;

import java.util.Date;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import com.trs.om.bean.UserGroup;
import com.trs.om.service.UserGroupService;

@WebService(endpointInterface="com.trs.om.api.ws.synuser.DepartmentService")
@SOAPBinding(style=Style.RPC)
public class DepartmentServiceImpl implements DepartmentService {
	private GroupOrgService groupOrgService;
	private UserGroupService userGroupService;

	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	public void setGroupOrgService(GroupOrgService groupOrgService) {
		this.groupOrgService = groupOrgService;
	}

	@Override
	@Transactional
	public boolean updateTheOrg(String org) {
		boolean flag=false;
		try {
			JSONObject jsonobject=new JSONObject(org);
			String oid=jsonobject.getString("oid");
			//判断当前组织机构是否已经存在于OM，如果存在则更新该组，如果不存在则插入并同时记录组——组织结构关系表
			if(groupOrgService.query(oid)){
				UserGroup  ug=userGroupService.get(new Long(groupOrgService.queryforgroupid(oid)));
				ug.setGroupName(jsonobject.getString("name"));
				//判断是否为一级组，是：将其父ID设为0 ；否：继续判断其父组是否存在
				if(!jsonobject.getString("fatherId").equals("")){
					String groupid=groupOrgService.queryforgroupid(jsonobject.getString("fatherId"));
					//判断父亲组是否存在，存在则更新，不存在则返回FALSE
					if(groupid!=null&&!groupid.equals("")){
						ug.setParentId(new Long(groupid));
					}
					else{
						return false;
					}
				}else{
					ug.setParentId(new Long(0));
				}
				ug.setDisabled(jsonobject.getString("status").equals("1")?true:false);
				userGroupService.updateUserGroup(ug);
				flag=true;
				return flag;
			}else{
				UserGroup  ug=new UserGroup();
				ug.setGroupName(jsonobject.getString("name"));
				//判断是否为一级组，是：将其父ID设为0 ；否：继续判断其父组是否存在
				if(!jsonobject.getString("fatherId").equals("")){
					String id=groupOrgService.queryforgroupid(jsonobject.getString("fatherId"));
					//判断父亲组是否存在，存在则更新，不存在则返回FALSE
					if(id!=null&&!id.equals("")){
						ug.setParentId(new Long(id));
					}
					else{
						return false;
					}
				}else{
					ug.setParentId(new Long(0));
				}
				ug.setDisabled(jsonobject.getString("status").equals("1")?true:false);
				ug.setCreateDate(new Date());
				String groupid=userGroupService.addUserGroupTY(ug).getId().toString();
				GroupOrg go=new GroupOrg();
				go.setGroupID(groupid);
				go.setOrgID(oid);
				groupOrgService.add(go);
				flag=true;
				return flag;
			}
		} catch (JSONException e) {
			flag=false;
			e.printStackTrace();
		}
		return flag;
	}

}
