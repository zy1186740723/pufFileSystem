package com.gh.filemanagement.Service.Impl;

import com.gh.filemanagement.DAO.MessageInfo;
import com.gh.filemanagement.DAO.UserRequest;
import com.gh.filemanagement.SendTemplate.CommonUtils;
import com.gh.filemanagement.Service.MessageInfoService;
import com.gh.filemanagement.repository.MessageInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/9/11 22:48
 * @Version 1.0
 */
@Service
public class MessageInfoServiceImpl implements MessageInfoService {
    @Autowired
    private MessageInfoRepository messageInfoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void buildMessageInfo(String openId,String username,String text,String timestamp){

        MessageInfo messageInfo=new MessageInfo();
        List<UserRequest> list=new ArrayList<>();
        messageInfo.setOpenId(openId);
        messageInfo.setTextInput(text);
        messageInfo.setUserName(username);
        messageInfo.setUserRequestList(list);
        messageInfo.setTimestamp(timestamp);
        messageInfoRepository.save(messageInfo);
    }

    @Override
    public void register(String openId, String username, String formId,String timestamp) {
        Query query=new Query();
        query.addCriteria(Criteria.where("timestamp").is(timestamp));

        MessageInfo messageInfo=mongoTemplate.findOne(query,MessageInfo.class);

        UserRequest userRequest=new UserRequest();
        userRequest.setOpenId(openId);
        userRequest.setUserName(username);
        userRequest.setFormId(formId);
        userRequest.setRequestTime(CommonUtils.buildTime());

        if (messageInfo.getUserRequestList()==null){
            List<UserRequest> list=new ArrayList<>();
            list.add(userRequest);
        }
        else {
            List<UserRequest> list=messageInfo.getUserRequestList();
            list.add(userRequest);
            messageInfo.setUserRequestList(list);
        }
        mongoTemplate.remove(query,MessageInfo.class);
        mongoTemplate.save(messageInfo);


    }

    @Override
    public MessageInfo findByOpenIdAndTimestamp(String openId, String timestamp) {
//        Query query=new Query();
//        query.addCriteria(Criteria.where("openId").is(openId)
//        .and("timestamp").is(timestamp));
//
//        return mongoTemplate.findOne(query,MessageInfo.class);
        return null;
    }
}
