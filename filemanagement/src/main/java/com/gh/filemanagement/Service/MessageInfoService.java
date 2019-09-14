package com.gh.filemanagement.Service;

import com.gh.filemanagement.DAO.MessageInfo;

/**
 * @Author: zhangyan
 * @Date: 2019/9/11 22:48
 * @Version 1.0
 */
public interface MessageInfoService {
    void register(String openId,String username,String formId,String timestamp);

    MessageInfo findByOpenIdAndTimestamp(String openId,String timestamp);
}
