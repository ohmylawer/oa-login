package com.trs.util;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.trs.om.bean.Permission;
import com.trs.om.bean.Privilege;
import com.trs.om.bean.ResourceGroupAcl;
import com.trs.om.bean.ResourceType;
import com.trs.om.bean.ResourceUserAcl;
import com.trs.om.bean.Session;
import com.trs.om.common.PermissionConstants;
import com.trs.om.security.SecurityConstants;
import com.trs.otm.authentication.HttpAuthnUtils;

/**
 * 通用资源检索工具类
 *
 * @author chang
 * @since 2012-6-5 16:59:13
 * */
public class ResourceSearchUtil {
	private static String[] objects = { PermissionConstants.PROJECT, PermissionConstants.REPORT, PermissionConstants.SECTION,
										"微博人物", "微博事件",
										PermissionConstants.WEIBO_ALARM, PermissionConstants.INDEXPAGE, PermissionConstants.USER,
										PermissionConstants.USERGROUP,PermissionConstants.PEOPLE,PermissionConstants.ALARM_KEYWORD ,
										PermissionConstants.ALARM_KEYSITE};
	private static String operation = PermissionConstants.VIEW;
	private static String resourceCreatorIdProperty = "creatorId";
	private static String resourceIdProperty = "id";

	private static DetachedCriteria returnUserAclDc(Long userId,
			Class clazz) {
		ResourceType resourceType=(ResourceType) clazz.getAnnotation(ResourceType.class);
		DetachedCriteria permissionDc = DetachedCriteria
				.forClass(Permission.class, "p")
				.add(Restrictions.eq("p.object", resourceType.typeString()))
				.add(Restrictions.eq("p.operation", operation))
				.setProjection(Projections.property("p.id"));
		DetachedCriteria privilegeDc = DetachedCriteria
				.forClass(Privilege.class, "p1")
				.add(Subqueries.propertyIn("p1.permissionId", permissionDc))
				.setProjection(Projections.property("p1.roleId"));
		DetachedCriteria sessionDc = DetachedCriteria
				.forClass(Session.class, "s")
				.add(Restrictions.eq("s.userId", userId))
				.add(Subqueries.propertyIn("s.roleId", privilegeDc))
				.setProjection(Projections.property("s.groupId"));
		DetachedCriteria ruaDc = DetachedCriteria.forClass(
				ResourceUserAcl.class, "rua");
		ruaDc.add(Restrictions.eq("view", true))
				.add(Restrictions.eq("rua.resourceType", resourceType.typeInt()))
				.add(Restrictions.eq("rua.userId", userId))
				.add(Subqueries.propertyIn("rua.groupId", sessionDc))
				.setProjection(Projections.property("rua.resourceId"));
		return ruaDc;
	}

	private static DetachedCriteria returnGroupAclDc(Long userId,
			Class clazz) {
		ResourceType resourceType=(ResourceType) clazz.getAnnotation(ResourceType.class);
		DetachedCriteria permissionDc = DetachedCriteria
				.forClass(Permission.class, "p")
				.add(Restrictions.eq("p.object", resourceType.typeString()))
				.add(Restrictions.eq("p.operation", operation))
				.setProjection(Projections.property("p.id"));
		DetachedCriteria privilegeDc = DetachedCriteria
				.forClass(Privilege.class, "p1")
				.add(Subqueries.propertyIn("p1.permissionId", permissionDc))
				.setProjection(Projections.property("p1.roleId"));
		DetachedCriteria sessionDc = DetachedCriteria
				.forClass(Session.class, "s")
				.add(Restrictions.eq("s.userId", userId))
				.add(Subqueries.propertyIn("s.roleId", privilegeDc))
				.setProjection(Projections.property("s.groupId"));
		DetachedCriteria rgaDc = DetachedCriteria.forClass(
				ResourceGroupAcl.class, "rga");
		rgaDc.add(Restrictions.eq("view", true))
				.add(Restrictions.eq("rga.resourceType", resourceType.typeInt()))
				.add(Subqueries.propertyIn("rga.groupId", sessionDc))
				.setProjection(Projections.property("rga.resourceId"));
		return rgaDc;
	}

	private static DetachedCriteria returnGroupAclDc(Long userId,
			Integer resourceType) {
		DetachedCriteria permissionDc = DetachedCriteria
				.forClass(Permission.class, "p")
				.add(Restrictions.eq("p.object", objects[resourceType - 1]))
				.add(Restrictions.eq("p.operation", operation))
				.setProjection(Projections.property("p.id"));
		DetachedCriteria privilegeDc = DetachedCriteria
				.forClass(Privilege.class, "p1")
				.add(Subqueries.propertyIn("p1.permissionId", permissionDc))
				.setProjection(Projections.property("p1.roleId"));
		DetachedCriteria sessionDc = DetachedCriteria
				.forClass(Session.class, "s")
				.add(Restrictions.eq("s.userId", userId))
				.add(Subqueries.propertyIn("s.roleId", privilegeDc))
				.setProjection(Projections.property("s.groupId"));
		DetachedCriteria rgaDc = DetachedCriteria.forClass(
				ResourceGroupAcl.class, "rga");
		rgaDc.add(Restrictions.eq("view", true))
				.add(Restrictions.eq("rga.resourceType", resourceType))
				.add(Subqueries.propertyIn("rga.groupId", sessionDc))
				.setProjection(Projections.property("rga.resourceId"));
		return rgaDc;
	}

	/**
	 * 查询个人创建的资源
	 *
	 * @param criteria
	 *            待拼接的原始Criteria
	 * */
	public static Criteria attachPrivateCriteria(
			Criteria criteria) {
		Long userId = HttpAuthnUtils.getLoginUserId();// 当前登录用户id
		criteria.add(Restrictions.eq(resourceCreatorIdProperty, userId));
		return criteria;
	}

	/**
	 * 查询共享(他人创建)的资源
	 *
	 * @param userId
	 *            用户id
	 * @param resourceType
	 *            资源类型，请使用{@link com.trs.om.service.ResourceAclService
	 *            ResourceAclService} 中相关常量定义
	 * @param criteria
	 *            待拼接的原始Criteria
	 * */
	public static Criteria attachSharedCriteria(Long userId,
			Class clazz, Criteria criteria) {
		if (userId.equals(SecurityConstants.SYSTEM_ADMIN_ID)) {
			criteria.add(Restrictions.ne(resourceCreatorIdProperty, userId));
			return criteria;
		}
		DetachedCriteria ruaDc = returnUserAclDc(userId, clazz);
		DetachedCriteria rgaDc = returnGroupAclDc(userId, clazz);
		criteria.add(Restrictions.or(
				Subqueries.propertyIn(resourceIdProperty, ruaDc),
				Subqueries.propertyIn(resourceIdProperty, rgaDc)));
		return criteria;
	}
	public static DetachedCriteria getDetachedCriteria(Long userId, Class clazz,
			DetachedCriteria criteria){
		if (HttpAuthnUtils.getLoginUserId().equals(SecurityConstants.SYSTEM_ADMIN_ID))
			return criteria;
		DetachedCriteria ruaDc = returnUserAclDc(userId, clazz);
		DetachedCriteria rgaDc = returnGroupAclDc(userId, clazz);
		criteria.add(Restrictions.or(Restrictions.eq(resourceCreatorIdProperty,
				userId)// creatorId
				, Restrictions.or(
						Subqueries.propertyIn(resourceIdProperty, ruaDc),
						Subqueries.propertyIn(resourceIdProperty, rgaDc))));// id
		return criteria;
	}
	/**
	 * 通用系统资源检索dao中给原有代码中criteria检索拼接检索
	 *
	 * @param userId
	 *            用户id
	 * @param resourceType
	 *            资源类型，请使用{@link com.trs.om.service.ResourceAclService
	 *            ResourceAclService} 中相关常量定义
	 * @param criteria
	 *            待拼接的原始Criteria
	 * */
	public static Criteria attachCriteria(Long userId, Class clazz,
			Criteria criteria) {
		if (userId.equals(SecurityConstants.SYSTEM_ADMIN_ID))
			return criteria;
		DetachedCriteria ruaDc = returnUserAclDc(userId, clazz);
		DetachedCriteria rgaDc = returnGroupAclDc(userId, clazz);
		criteria.add(Restrictions.or(Restrictions.eq(resourceCreatorIdProperty,
				userId)// creatorId
				, Restrictions.or(
						Subqueries.propertyIn(resourceIdProperty, ruaDc),
						Subqueries.propertyIn(resourceIdProperty, rgaDc))));// id
		return criteria;
	}

	/**
	 * 通用系统资源检索dao中给原有代码中DetachedCriteria检索拼接检索
	 *
	 * @param userId
	 *            用户id
	 * @param resourceType
	 *            资源类型，请使用{@link com.trs.om.service.ResourceAclService
	 *            ResourceAclService} 中相关常量定义
	 * @param criteria
	 *            待拼接的原始DetachedCriteria
	 * */
	public static DetachedCriteria attachDetachedCriteria(Long userId,
			Class clazz, DetachedCriteria criteria) {
		if (userId.equals(SecurityConstants.SYSTEM_ADMIN_ID))
			return criteria;
		DetachedCriteria ruaDc = returnUserAclDc(userId, clazz);
		DetachedCriteria rgaDc = returnGroupAclDc(userId, clazz);
		criteria.add(Restrictions.or(Restrictions.eq(resourceCreatorIdProperty,
				userId)// creatorId
				, Restrictions.or(
						Subqueries.propertyIn(resourceIdProperty, ruaDc),
						Subqueries.propertyIn(resourceIdProperty, rgaDc))));// id
		return criteria;
	}

//	/**
//	 * 通用系统资源检索dao中给原有代码中criteria检索拼接检索
//	 *
//	 * @param userId
//	 *            用户id
//	 * @param resourceType
//	 *            资源类型，请使用{@link com.trs.om.service.ResourceAclService
//	 *            ResourceAclService} 中相关常量定义
//	 * @param criteria
//	 *            待拼接的原始Criteria
//	 * @param resourceIdProperty
//	 *            资源bean定义中id标识的名称，比如：简报的标识名称是rid，而事件是id
//	 * @param resourceCreatorIdProperty
//	 *            资源bean定义中资源创建者的id标识名称，比如：简报的标识名称是authorId，而事件是creatorId
//	 * */
//	@Deprecated
//	private static Criteria attachCriteria(Long userId, Integer resourceType,
//			Criteria criteria, String resourceIdProperty,
//			String resourceCreatorIdProperty) {
//		if (userId.equals(SecurityConstants.SYSTEM_ADMIN_ID))
//			return criteria;
//		DetachedCriteria ruaDc = returnUserAclDc(userId, resourceType);
//		DetachedCriteria rgaDc = returnGroupAclDc(userId, resourceType);
//		criteria.add(Restrictions.or(Restrictions.eq(resourceCreatorIdProperty,
//				userId)// creatorId
//				, Restrictions.or(
//						Subqueries.propertyIn(resourceIdProperty, ruaDc),
//						Subqueries.propertyIn(resourceIdProperty, rgaDc))));// id
//		return criteria;
//	}
	private static DetachedCriteria returnUserAclDc(Long userId,
			Integer resourceType) {
		DetachedCriteria permissionDc = DetachedCriteria
				.forClass(Permission.class, "p")
				.add(Restrictions.eq("p.object", objects[resourceType - 1]))
				.add(Restrictions.eq("p.operation", operation))
				.setProjection(Projections.property("p.id"));
		DetachedCriteria privilegeDc = DetachedCriteria
				.forClass(Privilege.class, "p1")
				.add(Subqueries.propertyIn("p1.permissionId", permissionDc))
				.setProjection(Projections.property("p1.roleId"));
		DetachedCriteria sessionDc = DetachedCriteria
				.forClass(Session.class, "s")
				.add(Restrictions.eq("s.userId", userId))
				.add(Subqueries.propertyIn("s.roleId", privilegeDc))
				.setProjection(Projections.property("s.groupId"));
		DetachedCriteria ruaDc = DetachedCriteria.forClass(
				ResourceUserAcl.class, "rua");
		ruaDc.add(Restrictions.eq("view", true))
				.add(Restrictions.eq("rua.resourceType", resourceType))
				.add(Restrictions.eq("rua.userId", userId))
				.add(Subqueries.propertyIn("rua.groupId", sessionDc))
				.setProjection(Projections.property("rua.resourceId"));
		return ruaDc;
	}

	/**
	 * 通用系统资源检索dao中给原有代码中criteria检索拼接检索
	 *
	 * @param userId
	 *            用户id
	 * @param resourceType
	 *            资源类型，请使用{@link com.trs.om.service.ResourceAclService
	 *            ResourceAclService} 中相关常量定义
	 * @param criteria
	 *            待拼接的原始Criteria
	 * @param resourceIdProperty
	 *            资源bean定义中id标识的名称，比如：简报的标识名称是rid，而事件是id
	 * @param resourceCreatorIdProperty
	 *            资源bean定义中资源创建者的id标识名称，比如：简报的标识名称是authorId，而事件是creatorId
	 * */
	public static Criteria attachCriteria(Long userId, Integer resourceType,
			Criteria criteria) {// ,String resourceIdProperty,String
								// resourceCreatorIdProperty){
		if (userId.equals(SecurityConstants.SYSTEM_ADMIN_ID))
			return criteria;
		DetachedCriteria ruaDc = returnUserAclDc(userId, resourceType);
		DetachedCriteria rgaDc = returnGroupAclDc(userId, resourceType);
		criteria.add(Restrictions.or(Restrictions.eq(resourceCreatorIdProperty,
				userId)// creatorId
				, Restrictions.or(
						Subqueries.propertyIn(resourceIdProperty, ruaDc),
						Subqueries.propertyIn(resourceIdProperty, rgaDc))));// id
		return criteria;
	}
}
