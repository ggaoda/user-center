package com.gundam.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * @author Gundam
 */

@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = 511761797612846612L;

    private String userAccount;
    private String userPassword;


}
