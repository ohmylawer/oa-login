package com.trs.om.CustomAuthorization.bean.criterion;

import com.trs.om.bean.Permission;
import com.trs.om.bean.User;
import com.trs.om.generic.QBCCriterion;

import java.util.Date;
import java.util.Set;

/**
 * 用户授权token
 * Created by lilei on 2016/9/23.
 */
public class AccessTokenCriterion extends QBCCriterion{
    private Long id;
    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 所授权用户
     */
    private User user;

    private String tokenCode;

    private int tokenState;


    private Date geTokenInvalidTime;


    /**
     * token的失效时间
     */
    private Date tokenInvalidTime;

    /**  授权时间时间  */
    private Date createTime;

    /**
     * 拥有权限集合
     */
    private Set<Permission> permissions;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTokenCode() {
        return tokenCode;
    }

    public void setTokenCode(String tokenCode) {
        this.tokenCode = tokenCode;
    }

    public int getTokenState() {
        return tokenState;
    }

    public void setTokenState(int tokenState) {
        this.tokenState = tokenState;
    }

    public Date getTokenInvalidTime() {
        return tokenInvalidTime;
    }

    public void setTokenInvalidTime(Date tokenInvalidTime) {
        this.tokenInvalidTime = tokenInvalidTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Date getGeTokenInvalidTime() {
        return geTokenInvalidTime;
    }

    public void setGeTokenInvalidTime(Date geTokenInvalidTime) {
        this.geTokenInvalidTime = geTokenInvalidTime;
    }
}
