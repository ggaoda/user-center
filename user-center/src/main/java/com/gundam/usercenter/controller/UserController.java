package com.gundam.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gundam.usercenter.common.BaseResponse;
import com.gundam.usercenter.common.ErrorCode;
import com.gundam.usercenter.common.ResultUtil;
import com.gundam.usercenter.constant.UserConstant;
import com.gundam.usercenter.exception.BusinessException;
import com.gundam.usercenter.model.domain.User;
import com.gundam.usercenter.model.domain.request.UserLoginRequest;
import com.gundam.usercenter.model.domain.request.UserRegisterRequest;
import com.gundam.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gundam.usercenter.constant.UserConstant.ADMIN_ROLE;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;

    /**
     * 注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){

        if (userRegisterRequest == null){
//            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String vipCode = userRegisterRequest.getVipCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, vipCode)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);

        return ResultUtil.success(result);

    }


    /**
     * 登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){

        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        User user = userService.userLogin(userAccount, userPassword, request);

        return ResultUtil.success(user);


    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){

        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
       int result = userService.userLogout(request);

        return ResultUtil.success(result);

    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){

        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        Long id = currentUser.getId();

        //todo 校验用户是否合法
        User user = userService.getById(id);

        User safeUser = userService.getSafeUser(user);

        return ResultUtil.success(safeUser);

    }




    /**
     * 查找
     * @param userName
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> usersSearch(@RequestParam(value = "userName", required = false) String userName, HttpServletRequest request){

        if (! isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(userName)){
            queryWrapper.like("username",userName);
        }
        List<User> userList = userService.list(queryWrapper);
        //搜索抹除密码信息
        List<User> list = userList.stream().map(user -> {
            user.setUserPassword(null);
            return userService.getSafeUser(user);
        }).collect(Collectors.toList());

        return ResultUtil.success(list);

    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestParam Long id, HttpServletRequest request){


       if (! isAdmin(request)){
           throw new BusinessException(ErrorCode.NO_AUTH);
       }

        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Boolean b = userService.removeById(id);
        return ResultUtil.success(b);

    }


    /**
     * 判断权限
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){

        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getRole() == ADMIN_ROLE;


    }



}
