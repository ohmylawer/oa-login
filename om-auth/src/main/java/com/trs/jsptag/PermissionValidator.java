package com.trs.jsptag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.trs.om.bean.User;
import com.trs.om.common.ObjectContainer;
import com.trs.om.service.UserLogService;
import com.trs.om.service.UserService;
import com.trs.otm.authentication.HttpAuthnUtils;

public class PermissionValidator extends TagSupport {

	private static final long serialVersionUID = -7011275970920409076L;
	private String object;
	private String operate;
	private String log;

	public void setObject(String object) {
		this.object = object;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public void setLog(String log) {
		this.log = log;
	}

	@Override
	public int doStartTag() throws JspException {
		if(!HttpAuthnUtils.isAdmin()){//如果是admin直接展示内容
			UserLogService userLogService=(UserLogService)ObjectContainer.getBean("userLogService");//.getUserLogService();
			User user=HttpAuthnUtils.getLoginUser();
			//用户未登录，不允许访问标签内容
			if(user==null)
				return Tag.SKIP_BODY;
			//用户已登录但不具有权限，同样不允许访问标签内容
			String[] permissionString=new String[2];
			permissionString[1]=object;
			permissionString[0]=operate;
			UserService userService=(UserService)ObjectContainer.getBean("userService");//.getUserService();
			//IAuthorizationService authorizationService=ObjectContainer.getAuthorizationService();
	//		if (authorizationService.canOperate(
	//				currentUser, AuthorizationManager.APPLICATION, object,
	//				operate) != IAuthorization.OPERATION_ALLOWED) {

			if(!userService.hasPermission(permissionString, user)){
				if(log!=null)
					userLogService.log(log);
				return Tag.SKIP_BODY;
			}
		}
		//用户已登录且具有权限，允许访问标签内容
		return Tag.EVAL_BODY_INCLUDE;
	}

}
