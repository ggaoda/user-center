package com.gundam.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gundam.usercenter.common.ErrorCode;
import com.gundam.usercenter.constant.UserConstant;
import com.gundam.usercenter.exception.BusinessException;
import com.gundam.usercenter.model.domain.User;
import com.gundam.usercenter.mapper.UserMapper;
import com.gundam.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gundam.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
* @author Gundam
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-02-28 19:14:05
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    //密码加密盐
    private static final String SALT = "gundam";



    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String vipCode) {

        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空!");
        }

        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度太短!");
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短!");
        }

        if (vipCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "vip编号过长!");
        }



        //账户不能包含特殊字符
        String regExp = "^[\\w_]{6,20}$";
        Matcher matcher = Pattern.compile(regExp).matcher(userAccount);
        if (!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户不能包含特殊字符!");
        }

        //密码和校验密码不相同
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不相同!");
        }


        // 账户不能重复
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户不能重复!");
        }

        // vip编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vipCode", vipCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"vip编号不能重复!");
        }


        //2.加密密码
        String securePassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(securePassword);
        user.setVipCode(vipCode);
        boolean saveResult = this.save(user);

        if (!saveResult){
            return -1;
        }

        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验

        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            // todo 修改为自定义异常
            return null;
        }

        if (userAccount.length() < 4){
            return null;
        }

        if (userPassword.length() < 8){
            return null;
        }



        //账户不能包含特殊字符
        String regExp = "^[\\w_]{6,20}$";
        Matcher matcher = Pattern.compile(regExp).matcher(userAccount);
        if (!matcher.find()){
            return null;
        }




        //2.验证用户是否存在

        String securePassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", securePassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed, userAccount can not match userPassword!");
            return null;
        }


        User safeUser = getSafeUser(user);

        //记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);



        return safeUser;


    }


    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafeUser(User originUser){

        if (originUser == null){
            return null;
        }

        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setGender(originUser.getGender());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setRole(originUser.getRole());
        safeUser.setVipCode(originUser.getVipCode());

        return safeUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


}




