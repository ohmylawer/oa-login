package com.trs.om.CustomAuthorization.bean;

import com.trs.om.bean.Permission;
import com.trs.om.bean.User;


import java.util.Date;
import java.util.Set;

/**
 * 用户授权token
 * Created by lilei on 2016/9/23.
 */
public class AccessToken {
    private Long id;


    /**
     * 所授权用户
     */
    private User user;

    /**
     * 授权项目
     */
    private Long projectId;



    private String tokenCode;


    /**
     * token状态,0为不可用，1为可用
     */
    private int tokenState=1;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
