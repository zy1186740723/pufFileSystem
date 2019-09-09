package com.gh.filemanagement.Service;

import com.gh.filemanagement.DAO.UserInfo;


/**
 * @Author: zhangyan
 * @Date: 2019/8/29 15:45
 * @Version 1.0
 */
public interface UserService {
    UserInfo findUserInfoByOpenId(String openId);

    //根据cookie寻找openId
    String findOpenIdByUserKey(String cookie);

    //根据token获取sessionKey
    String findSessionKeyByToken(String cookie);

    String findOpenIdByUserName(String userName);
}
