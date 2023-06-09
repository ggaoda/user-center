package com.gundam.usercenter.service;

import com.gundam.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Gundam
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-02-28 19:14:05
*/
public interface UserService extends IService<User> {





    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param vipCode vip编号
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String vipCode);


    /**
     *用户登录
     * @param userAccount 账号
     * @param userPassword 密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafeUser(User originUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

}
