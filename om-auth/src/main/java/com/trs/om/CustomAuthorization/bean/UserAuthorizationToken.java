package com.trs.om.CustomAuthorization.bean;

import com.trs.om.bean.User;

import java.util.Date;

/**
 * 用户登录认证token
 * Created by lilei on 2016/9/23.
 */
public class UserAuthorizationToken {

    private Long id;

    /**
     * 跳转网址,
     * 参数用于开发接口时，
     */
    private String redirectUrl;

    /**
     * 用途描述
     */
    private String description;
    /**
     * 所授权用户
     */
    private User user;
    /**
     * 用户信息验证通过后，返回授权请求token
     */
    private String tokenCode;
    /**
     * token的失效时间，进行授权才有效
     */
    private Date tokenInvalidTime;
    /**
     * token状态,0为不可用，1为可用
     */
    private int tokenState=1;

        /**  创建时间  */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Date getTokenInvalidTime() {
        return tokenInvalidTime;
    }

    public void setTokenInvalidTime(Date tokenInvalidTime) {
        this.tokenInvalidTime = tokenInvalidTime;
    }

    public int getTokenState() {
        return tokenState;
    }

    public void setTokenState(int tokenState) {
        this.tokenState = tokenState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
