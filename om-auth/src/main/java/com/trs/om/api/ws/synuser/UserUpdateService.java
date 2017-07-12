package com.trs.om.api.ws.synuser;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface UserUpdateService {
	/**
	 * 更新用户（创建，修改）
	 * @param user
	 * @return 成功true；失败false;
	 */
	public boolean updateUser(String user);

}
