package com.gh.filemanagement.Service.Impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Author: zhangyan
 * @Date: 2019/9/5 14:02
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SupervisionServiceImplTest {

    @Autowired
    private SupervisionServiceImpl supervisionService;
    @Test
    public void register() {
        //supervisionService.register("23456","hahaha456h2","13esff234");
    }

    @Test
    public void findFormIdByfilenameAndUserNameTest(){
        String filename="23456";
        String username="龙队给我球🏀cc";

        //Assert.assertEquals("the formId is a mock one",supervisionService.findFormIdByfilenameAndUserName(filename,username));
    }
}