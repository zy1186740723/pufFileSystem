package com.gh.filemanagement.DAO;


import com.sun.javafx.beans.IDProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 12:44
 * @Version 1.0
 */
@Entity
@Data
public class UserInfo {

    @Id
    private String openId;

    private String sessionKey;

    private String userKey;

    private String userAddress;

    private String userAvatar;

    private String userGender;

    private String userName;

    private Date createTime;

    private Date updateTime;


}
