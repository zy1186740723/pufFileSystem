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
        userInfoRepository.save(userInfo);
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

        return userInfoRepository.findByUserName(userName).getOpenId();
    }
}
