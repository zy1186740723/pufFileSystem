package com.gh.filemanagement.controller;

import com.gh.filemanagement.DAO.UserRequest;
import com.gh.filemanagement.SendTemplate.Send;
import com.gh.filemanagement.Service.Impl.SupervisionServiceImpl;
import com.gh.filemanagement.Service.Impl.UserServiceImpl;
import com.gh.filemanagement.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SupervisionController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private SupervisionServiceImpl supervisionService;

    //接受用户的formID和token，存入数据库用于审核
    @RequestMapping("/submit")
    public String sotreFormId(@RequestParam(value = "Cookie",required = false) String token,
                              @RequestParam(value = "filename",required = false)String filename,
                              @RequestParam(value = "formId",required = false)String formId,
                              @RequestParam(value = "fileId",required = false)String fileId){
        System.out.println(token+filename+formId+fileId);
        String openId=userService.findOpenIdByUserKey(token);
        String username=userService.
                findUserInfoByOpenId(openId)
                .getUserName();

        //将写入filename的实体中,注册的过程
        //todo:加入id，对共享行为进行标识或者过期时间
        supervisionService.register(filename,username,formId,fileId);

        return "success";
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
        //文件用户的openId
        String fileOwnerOpenId=userService.findOpenIdByUserKey(token);
        System.out.println("openId:"+openId);
        System.out.println("filename:"+filename);

        //根据filename与filename获取formId
        String formId=supervisionService.findFormIdByfilenameAndUserNameAndOpenId(filename, userName,fileOwnerOpenId);

        System.out.println("formId"+formId);
        Send.sendTemplate(openId,formId,filename);



    }
}
