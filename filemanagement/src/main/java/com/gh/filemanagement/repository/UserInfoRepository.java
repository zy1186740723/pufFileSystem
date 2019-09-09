package com.gh.filemanagement.repository;

import com.gh.filemanagement.DAO.UserInfo;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 15:32
 * @Version 1.0
 */
public interface UserInfoRepository extends JpaRepository<UserInfo,String> {
    UserInfo findByOpenId(String openId);

    UserInfo findByUserKey(String cookie);

    UserInfo findByUserName(String userName);



}
