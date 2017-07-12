package com.trs.om.service;

import java.util.List;

import com.trs.om.bean.PageCriterion;
import com.trs.om.bean.Possession;
import com.trs.om.bean.User;
import com.trs.om.util.PagedArrayList;


public interface PossessionService {
	boolean deletePossession(Long id);
	Possession getPossession(String possessionName);
	Possession getById(Long id);
	boolean updatePossession(Possession possession);
	boolean addPossession(Possession possession);
	
	/**获得当前用户的属地
	 * @param ids
	 * @param log
	 * void
	 */
	PagedArrayList<Possession> listPossessionsByPage(User user);
	/**获得当前用户的属地
	 * @param ids
	 * @param log
	 * void
	 */
	PagedArrayList<Possession> listPossessions(String possessionName,Long groupId,PageCriterion pageCriterion);
	/**获得当前用户的属地
	 * @param ids
	 * @param log
	 * void
	 */
	List<Possession> listAllPossessions(User user);
	/**获得当前用户的属地
	 * @param groupIds 用户组id列表
	 * void
	 */
	List<Possession> listPossessionsByGroupIds(List<Long> groupIds);
	/**批量删除属地
	 * @param ids
	 * @param log
	 * void
	 */
	void deleteByIds(Long[] ids);
}
