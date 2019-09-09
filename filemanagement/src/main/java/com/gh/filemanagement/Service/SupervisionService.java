package com.gh.filemanagement.Service;

import com.gh.filemanagement.DAO.UserRequest;
import com.gh.filemanagement.DAO.UserSupervision;

import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/9/5 0:54
 * @Version 1.0
 */
public interface SupervisionService {

    List<String> findUserListByToken(String token);

    void register(String filename,String username,String formId,String fileId);

    List<UserRequest> findUserRequestListByFilename(String filename,String openId);

    String findFormIdByfilenameAndUserNameAndOpenId(String filename ,String userName,String openId);

    List<UserRequest> findUserSuervisionByRealFileNameAndOpenId(String fileName,String openId);
}
