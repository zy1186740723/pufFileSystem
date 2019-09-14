package com.gh.filemanagement.DAO;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 10:24
 * @Version 1.0
 */
@Data
public class MessageInfo {


    private String openId;

    private String userName;

    private String textInput;

    private List<UserRequest> userRequestList;

    private String timestamp;
}
