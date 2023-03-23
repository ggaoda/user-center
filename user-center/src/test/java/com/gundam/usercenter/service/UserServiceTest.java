package com.gundam.usercenter.service;
import java.util.Date;

import com.gundam.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;
    @Test
    public void test1(){

        User user = new User();
        user.setUsername("gundam");
        user.setUserAccount("1008611");
        user.setAvatarUrl("http://photo.chaoxing.com/p/153377889_80");
        user.setGender(0);
        user.setUserPassword("abc123");
        user.setPhone("10086");
        user.setEmail("10086@qq.com");


        boolean result = userService.save(user);

        System.out.println(user);
        assertTrue(result);
    }

    @Test
    void userRegister() {

        String userAccount = "gaoda";
        String userPassword = "";
        String checkPassword = "123456";
        String vipCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        Assertions.assertEquals(-1, result);

        userAccount = "fk";
        result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        Assertions.assertEquals(-1, result);

        userAccount = "gaoda";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        Assertions.assertEquals(-1, result);

        userAccount = "gao da";
        result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        Assertions.assertEquals(-1, result);

        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        Assertions.assertEquals(-1, result);


        userAccount = "1008611";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        Assertions.assertEquals(-1, result);

        userAccount = "gaodaa";
        result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        Assertions.assertTrue(result > 0);


    }
}