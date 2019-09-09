package com.gh.filemanagement.Service.Impl;

import com.gh.filemanagement.DAO.FileIdMap;
import com.gh.filemanagement.DAO.UserRequest;
import com.gh.filemanagement.DAO.UserSupervision;
import com.gh.filemanagement.SendTemplate.CommonUtils;
import com.gh.filemanagement.Service.SupervisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/9/5 0:59
 * @Version 1.0
 */
@Service
public class SupervisionServiceImpl implements SupervisionService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private FileIdServiceImpl fileIdService;


    @Override
    public List<String> findUserListByToken(String token) {

        return null;
    }

    @Override
    public void register(String filename, String username,String formId,String fileId) {
        //先找到符合条件的fieldMap
        System.out.println("rf:"+filename);
        System.out.println("fileId:"+fileId);
        Query query=new Query();
        query.addCriteria(Criteria.where("realFileName").is(filename).and("fileName").is(fileId));
        FileIdMap fileIdMap=mongoTemplate.findOne(query, FileIdMap.class);
        //UserSupervision userSupervision=fileIdMap.getUserSupervision();
        //然后处理userRequest的逻辑,生成request
        UserRequest userRequest=new UserRequest();
        userRequest.setUserName(username);
        userRequest.setFormId(formId);
        userRequest.setRequestTime(CommonUtils.buildTime());
        System.out.println(fileIdMap.toString());

        UserSupervision userSupervision1=new UserSupervision();

        if (fileIdMap.getUserRequestList()==null){
            //userSupervision.setFilename(filename);
            List<UserRequest> userList=new ArrayList<>();
            //构建request
            userList.add(userRequest);
            fileIdMap.setUserRequestList(userList);
        }
        else {
            List<UserRequest> userList=fileIdMap.getUserRequestList();
            userList.add(userRequest);
            fileIdMap.setUserRequestList(userList);
        }
        mongoTemplate.remove(query,FileIdMap.class);
        mongoTemplate.save(fileIdMap);

    }

    @Override
    public List<UserRequest> findUserRequestListByFilename(String filename,String openId) {

        return findUserSuervisionByRealFileNameAndOpenId(filename,openId);


    }

    @Override
    public String findFormIdByfilenameAndUserNameAndOpenId(String filename, String userName,String openId) {
        String res="";

        List<UserRequest> list=findUserSuervisionByRealFileNameAndOpenId(filename,openId);


        for (UserRequest userRequest : list) {
            if (userRequest.getUserName().equals(userName)){
                res=userRequest.getFormId();
            }
        }
        return res;
    }

    @Override
    public List<UserRequest> findUserSuervisionByRealFileNameAndOpenId(String filename, String openId) {
        Query query=new Query();
        query.addCriteria(Criteria.where("realFileName").is(filename).and("openId").is(openId));
        FileIdMap fileIdMap=mongoTemplate.findOne(query,FileIdMap.class);
        return fileIdMap.getUserRequestList();
    }
}
