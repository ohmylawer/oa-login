/*
 * Title: 	  TRS 角色服务器
 * Copyright: Copyright (c) 2004-2005, TRS信息技术有限公司. All rights reserved.
 * License:   see the license file.
 * Company:   TRS信息技术有限公司(www.trs.com.cn)
 * 
 * Created on 2004-12-7
 */
package com.trs.om.rbac.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 对SQL查询条件的封装. <BR>
 * @author TRS信息技术有限公司
 * @version 1.0
 */
public class SearchFilter {

	/**
	 * 
	 */
    public static final String BETWEEN = "between";

    /**
     * 
     */
    private static final Logger logger = Logger.getLogger(SearchFilter.class);

    /**
     * 
     */
    private List conditions = new ArrayList();

    /**
     * 
     */
    private int startPos = 0;  // firstResult, 默认值0表示从第一条开始.

    /**
     * 
     */
    private int fetchSize = -1;  // jdbc的fetchSize大小, 默认值-1表示不进行设置.

    /**
     * 
     */
    private int maxResults = -1;  // 取记录数时的最大数目上限, 默认值-1表示无限制.
    
    /**
     * SQL语句中order子句中"order by"后面的部分.
     */
    private String orderBy;

    /**
     * 返回查询条件的总个数.
     */
    public byte getTotalConditions() {
        return (byte) conditions.size();
    }

    /**
     * 返回指定项查询条件的查询项, 也就是对应的对象属性(即数据表的字段)名.
     * @param index 指定项的序号
     * @return 对应的对象属性(即数据表的字段)名
     */
    public String getPropertyName(byte index) {
        return getConditionByIndex(index).property;
    }

    /**
     * 返回指定项查询条件的查询值.
     * @param index 指定项的序号
     * @return 对应的查询值
     */
    public Object getValue(byte index) {
        return getConditionByIndex(index).value;
    }
    
    public Object getBetweenValue2(byte index) {
        return getConditionByIndex(index).value2;
    }

    /**
     * 返回指定项查询条件的关系运算符.
     * @param index 指定项的序号
     * @return 对应的关系运算符
     */
    public String getRelationOp(byte index) {
        return getConditionByIndex(index).op;
    }

    /**
     * 添加一个相等的查询条件, 即指定某属性的取值. <BR>
     * 调用该方法等价于调用 <code>addCondition("=", prop, value);</code>
     * @param prop 属性名
     * @param value 取值
     */
    public void addEqCondition(String prop, Object value) {
        addCondition("=", prop, value);
    }

    /**
     * 添加一个查询条件. <BR>
     * 如果value参数为null, 该方法并不会实际添加该条件.
     * @param op 属性和取值的关系运算符. 可以取得值有"=", "like", ">=", "<=", ">", "<", "in". 对于除此以外的值, 该方法并不会实际添加该条件.
     * @param prop 对象的属性名
     * @param value 取值. 如果value参数为null, 该方法并不会实际添加该条件.
     * @see #isValidOp(String) 
     */
    public void addCondition(String op, String prop, Object value) {
        if (value != null) {
            if (isValidOp(op)) {
                conditions.add(new Condition(op, prop, value));
            }
        }
    }
    
    public void addBetweenCondition(String prop, Object value1, Object value2) {
        if (value1 != null && value2 != null) {
            conditions.add(new Condition(BETWEEN, prop, value1, value2));
        }
    }

    /**
     * @return Returns the fetchSize.
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * jdbc取记录数, 默认值-1表示取全部
     * @deprecated 不再对jdbc的fetchsize进行控制
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * 取记录的起始位置.<BR> 
     * @return 取记录的起始位置. 0表示从第一条开始取记录. 该值不会小于0.
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * 设置取记录的起始位置.<BR>
     * 如果参数小于0, 则忽略此设值操作.
     * @param startPos 取记录的起始位置. 0表示从第一条开始. 如果参数小于0, 则忽略此设值操作.
     */
    public void setStartPos(int startPos) {
        if (startPos > 0) {
            this.startPos = startPos;
        }
    }

    /**
     * 获取取记录数的总数, 默认值-1表示无限制.
     * @return Returns the maxResults.
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * 设置取记录数的总数, 默认值-1表示无限制.
     * @param maxResults The maxResults to set.
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * 内部类, 封装一个SQL查询条件.
     * @author TRS信息技术有限公司
     * @version 1.0
     */
    private class Condition {
        /**
         * 该条件中, 属性和取值的关系运算符. 默认为相等, 即"=".
         */
        String op;

        String property;

        Object value;
        
        Object value2;

        /**
         * @param op
         * @param prop
         * @param value
         */
        Condition(String op, String prop, Object value) {
            this.op = op;
            this.property = prop;
            this.value = value;
        }

        Condition(String op, String prop, Object value1, Object value2) {
            this.op = op;
            this.property = prop;
            this.value = value1;
            this.value2 = value2;
        }
    }

    /**
     * 返回指定项的查询条件.
     * @param index 指定项的序号
     * @return 查询条件
     */
    private Condition getConditionByIndex(byte index) {
        if (index < 0 || (index >= conditions.size())) {
            logger.warn("没有该项查询条件!");
            throw new IndexOutOfBoundsException("没有该项查询条件!");
        }
        return (Condition) conditions.get(index);
    }

    /**
     * 判断是否为合法的运算符(查询条件).
     * @param op 给定的运算符
     * @return 是合法的运算符返回true, 否则返回false.
     */
    public static boolean isValidOp(String op) {
        if ("=".equals(op) || "like".equals(op) || ">=".equals(op) || "<=".equals(op)) {
            return true;
        }
        if (">".equals(op) || "<".equals(op)) {
            return true;
        }
        if ("in".equals(op)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the {@link #orderBy}.
     * @return the orderBy.
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Set {@link #orderBy}.
     * @param orderBy The orderBy to set.
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 
     * @return
     */
    public String buildWhere(){
        byte totalConditions = this.getTotalConditions();
        if (totalConditions <= 0) return "";
    	StringBuffer sb = new StringBuffer();
        sb.append(" where ");
        for (int i = 0; i < totalConditions; i++) {
        	sb.append(this.getPropertyName((byte) i)).append(' ');
            sb.append(this.getRelationOp((byte) i)).append(' ');
            if ("in".equals(this.getRelationOp((byte) i))) {
            	sb.append("(:").append(this.getPropertyName((byte) i)).append(')');
            } else if (SearchFilter.BETWEEN.equals(this.getRelationOp((byte) i))) {
            	sb.append(':').append(this.getPropertyName((byte) i)).append("lo");
                sb.append(" and :").append(this.getPropertyName((byte) i)).append("hi");
            } else {
            	sb.append(':').append(this.getPropertyName((byte) i));
            }
            if (i < totalConditions - 1) {
            	sb.append(" and ");
            }
        }
        return sb.toString();
    }
}