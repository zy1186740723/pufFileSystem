package com.gh.filemanagement.controller;

import com.gh.filemanagement.DAO.MessageInfo;
import com.gh.filemanagement.DAO.UserRequest;
import com.gh.filemanagement.SendTemplate.Send;
import com.gh.filemanagement.Service.Impl.MessageInfoServiceImpl;
import com.gh.filemanagement.Service.Impl.SupervisionServiceImpl;
import com.gh.filemanagement.Service.Impl.UserServiceImpl;
import com.gh.filemanagement.Service.UserService;
import com.gh.filemanagement.common.RedisOperator;
import com.gh.filemanagement.handler.WxOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/9/5 11:55
 * @Version 1.0
 */
@RestController
@Slf4j
public class SupervisionController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private SupervisionServiceImpl supervisionService;
    @Autowired
    private RedisOperator redis;
    @Autowired
    private MessageInfoServiceImpl messageInfoService;
    @Autowired
    private MongoTemplate mongoTemplate;

    //接受用户的formID和token，存入数据库用于审核
    @RequestMapping("/submit")
    public String sotreFormId(@RequestParam(value = "Cookie",required = false) String token,
                              @RequestParam(value = "filename",required = false)String filename,
                              @RequestParam(value = "formId",required = false)String formId,
                              @RequestParam(value = "fileId",required = false)String fileId){
        //fileId用于隔离不同用户的同名文件
        System.out.println(token+filename+formId+fileId);
        String openId=userService.findOpenIdByUserKey(token);
        String username=userService.
                findUserInfoByOpenId(openId)
                .getUserName();

        //将写入filename的实体中,注册的过程
        //todo:加入id，对共享行为进行标识或者过期时间
        supervisionService.register(filename,username,formId,fileId,openId);

        return "success";
    }

    //存储用户的text消息
    @RequestMapping("/textSubmit")
    public String textSubmit(@RequestParam(value = "Cookie",required = false) String token,
                             @RequestParam(value = "textInput",required = false)String text,
                             @RequestParam(value = "timestamp",required = false)String timestamp){
        log.info("进入消息存储");
        //查找openId
        String openId=userService.findOpenIdByUserKey(token);
        String username=userService.findUserInfoByOpenId(openId).getUserName();
        //判断内容是否重复上传
        Query query=new Query();
        query.addCriteria(Criteria.where("openId").is(openId)
                .and("timestamp").is(text));
        if (mongoTemplate.findOne(query,MessageInfo.class)!=null){
            throw new WxOperationException();
        }
        else {
            messageInfoService.buildMessageInfo(openId,username,text,timestamp);
        }
        return "success";
    }
    //提交申请
    @RequestMapping("/submit2")
    public String sotreFormId2(@RequestParam(value = "Cookie",required = false) String token,
                              @RequestParam(value = "formId",required = false)String formId,
                               @RequestParam(value = "timestamp",required = false)String timestamp){
        //查找提交申请人的openId
        String openId=userService.findOpenIdByUserKey(token);
        //找出提交申请人的姓名
        String username=userService.findUserInfoByOpenId(openId).getUserName();
        System.out.println("token:"+token+"username:"+username+"formId:"+formId+"timestamp"+timestamp);

        //根据openId和timestamp找出text的messageInfo
        //并将请求写入其中
        messageInfoService.register(openId,username,formId,timestamp);

        return "success";
    }

    //审核者查询具体的审核信息，带着filename来查询
    @RequestMapping("/supervision2")
    public List<UserRequest> viewSupervision2(@RequestParam(value = "textInput",required = false) String textInput,
                                             @RequestParam(value = "Cookie",required = false)String token){
        //审核的时候需要审核自己上传的文件，不能查看别人上传的文件，
        //找出自己openid
        String openId=userService.findOpenIdByUserKey(token);
        //String userName=userService.findUserInfoByOpenId(openId).getUserName();
        //根据filename与openid找到返回的用户列表
        return supervisionService.findRequestListByTextInputAndOpenId(textInput,openId);
    }

    //审核者查询具体的审核信息，带着filename来查询
    @RequestMapping("/supervision")
    public List<UserRequest> viewSupervision(@RequestParam(value = "filename",required = false) String filename,
                                             @RequestParam(value = "Cookie",required = false)String token){
        //审核的时候需要审核自己上传的文件，不能查看别人上传的文件，
        //找出自己openid
        String openId=userService.findOpenIdByUserKey(token);
        //String userName=userService.findUserInfoByOpenId(openId).getUserName();
        //根据filename与openid找到返回的用户列表
        return supervisionService.findUserRequestListByFilename(filename,openId);
    }

    //进行模板推送
    @RequestMapping("/sendTemplate")
    public void sendTemplate(@RequestParam(value = "userName",required = false) String userName,
                             @RequestParam(value = "Cookie",required = false)String token,
                             @RequestParam(value = "filename",required = false) String filename){
        //根据用户名获得用户的openid
        String openId=userService.findOpenIdByUserName(userName);

        //TODO:程序bug：如果userName重复，会找不到openId,所以用token






        //文件用户的openId
        String fileOwnerOpenId=userService.findOpenIdByUserKey(token);
        System.out.println("openId:"+openId);
        System.out.println("filename:"+filename);

        //根据filename与filename获取formId
        String formId=supervisionService.findFormIdByfilenameAndUserNameAndOpenId(filename, userName,fileOwnerOpenId);

        System.out.println("formId"+formId);
        Send.sendTemplate(openId,formId,filename);
    }

    //进行模板推送,传输的是文本和用户信息
    @RequestMapping("/sendTemplate2")
    public void sendTemplate2(@RequestParam(value = "userName",required = false) String userName,
                             @RequestParam(value = "Cookie",required = false)String token,
                             @RequestParam(value = "textInput",required = false) String textInput,
                              @RequestParam(value = "timestamp",required = false) String timestamp){
        //根据用户名获得用户的openid
        String openId=userService.findOpenIdByUserName(userName);


        //文件用户的openId
        String fileOwnerOpenId=userService.findOpenIdByUserKey(token);
        System.out.println("openId:"+openId);
        System.out.println("filename:"+textInput);
        System.out.println("timestamp:"+timestamp);

        //根据filename与filename获取formId
        String formId=supervisionService
                .findFormIdByTextInputAndTokenAndUserNameAndUserName(fileOwnerOpenId,timestamp,userName);

        System.out.println("formId"+formId);
        Send.sendTemplate2(openId,formId,textInput);
    }


}
