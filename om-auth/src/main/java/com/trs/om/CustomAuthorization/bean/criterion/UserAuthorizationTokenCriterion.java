package com.trs.om.CustomAuthorization.bean.criterion;

import com.trs.om.bean.User;
import com.trs.om.generic.QBCCriterion;

import java.util.Date;

/**
 * 用户登录认证token
 * Created by lilei on 2016/9/23.
 */
public class UserAuthorizationTokenCriterion  extends QBCCriterion{

    private Long id;

    /**
     * 所授权用户
     */
    private User user;

    /**
     * 用户信息验证通过后，返回授权请求token
     */
    private String tokenCode;
    /**
     * 大于失效时间，token才有效
     */
    private Date gtTokenInvalidTime;
    /**
     * token状态,0为不可用，1为可用
     */
    private int tokenState;

        /**  创建时间  */
    private Date createTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getGtTokenInvalidTime() {
        return gtTokenInvalidTime;
    }

    public void setGtTokenInvalidTime(Date gtTokenInvalidTime) {
        this.gtTokenInvalidTime = gtTokenInvalidTime;
    }
}
