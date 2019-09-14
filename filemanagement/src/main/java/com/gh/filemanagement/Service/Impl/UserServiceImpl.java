package com.gh.filemanagement.Service.Impl;

import com.gh.filemanagement.DAO.UserInfo;

import com.gh.filemanagement.Service.UserService;
import com.gh.filemanagement.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 15:33
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    public void  save(UserInfo userInfo){

        String openId=userInfo.getOpenId();
        String userName=userInfo.getUserName();


        if (findOpenIdByUserName(userName)!=null){
            //找出现在有用户的openId
            String opnIdInSql=findOpenIdByUserName(userName);
            UserInfo userInfoInSql=findUserInfoByOpenId(opnIdInSql);
            if (opnIdInSql.equals(openId) ){
                userInfoRepository.delete(userInfoInSql);
                userInfoRepository.save(userInfo);
            }
            else {
                userInfo.setUserName(userName+"#");
                userInfoRepository.save(userInfo);
            }
        }
        else {userInfoRepository.save(userInfo);}

    }

    @Override
    public UserInfo findUserInfoByOpenId(String openId) {
        UserInfo userInfo=userInfoRepository.findByOpenId(openId);
        return userInfo;
    }

    @Override
    public String findOpenIdByUserKey(String cookie) {
        return userInfoRepository.findByUserKey(cookie).getOpenId();

    }

    @Override
    public String findSessionKeyByToken(String cookie) {
        UserInfo userInfo=userInfoRepository.findByUserKey(cookie);
        return userInfo.getSessionKey();
    }

    @Override
    public String findOpenIdByUserName(String userName) {
        if (userInfoRepository.findByUserName(userName)==null){
            return null;
        }
        else {
            return userInfoRepository.findByUserName(userName).getOpenId();
        }

    }

    @Override
    public String findOpenIdByUserNameAndCreateTime(String openId, String createTime) {
        return userInfoRepository.findByOpenIdAndAndCreateTime(openId,createTime).getOpenId();
    }
}
