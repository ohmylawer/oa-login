package com.trs.om.api.ws.synuser;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface DepartmentService {
	/**
	 * 更新组织机构（创建，修改）
	 * @param org
	 * @return 成功true；失败false;
	 */
	public boolean updateTheOrg(String org);

}
