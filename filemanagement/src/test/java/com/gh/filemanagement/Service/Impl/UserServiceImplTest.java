package com.gh.filemanagement.Service.Impl;

import com.gh.filemanagement.DAO.UserInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 17:13
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl service;

    @Test
    public void save() {
        UserInfo userInfo=new UserInfo();
        userInfo.setCreateTime(new Date());
        userInfo.setOpenId("123");
        userInfo.setUpdateTime(new Date());
        userInfo.setSessionKey("dsd");
        userInfo.setUserAddress("ddd");
        userInfo.setUserAvatar("dfdfv");
        userInfo.setUserGender("nan");
        userInfo.setUserKey("dddf");
        userInfo.setUserName("jhha");
        service.save(userInfo);
    }

    @Test
    public void findUserInfoByOpenId() {
        String openId="123";
        UserInfo userInfo=service.findUserInfoByOpenId(openId);
        Assert.assertNotNull(userInfo);

    }

    @Test
    public void findSessionKeyByTokenTest(){
        String token="948297d1-d38e-4f22-9f1a-9bccf7737767";
        String res=service.findSessionKeyByToken(token);
        Assert.assertEquals("Ht21TDWq3bk9vP/E3+asyw==",res);
    }

    @Test
    public void findOpenIdByUserNameTest(){
        String userName="龙队给我球\uD83C\uDFC0cc";

        Assert.assertEquals("o4JE448JvlDDuJh7X_KAZjzxTGl8",service.findOpenIdByUserName(userName));
    }
}