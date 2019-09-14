package com.gh.filemanagement.DAO;

import lombok.Data;

/**
 * @Author: zhangyan
 * @Date: 2019/9/5 14:16
 * @Version 1.0
 */
@Data
public class UserRequest {
    private String userName;

    private String formId;

    private String requestTime;

    //用于防止用户重名的问题
    private String openId;

}
