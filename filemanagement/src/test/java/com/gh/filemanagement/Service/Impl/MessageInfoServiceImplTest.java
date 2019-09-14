package com.gh.filemanagement.Service.Impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Author: zhangyan
 * @Date: 2019/9/11 23:33
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageInfoServiceImplTest {

    @Autowired
    private MessageInfoServiceImpl service;

    @Test
    public void saveTest(){
        //service.buildMessageInfo("213","hahah","我来了");
    }

}