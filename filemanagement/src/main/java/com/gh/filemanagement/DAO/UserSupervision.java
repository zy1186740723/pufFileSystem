package com.gh.filemanagement.DAO;

import lombok.Data;

import java.util.List;

/** 将点击过分享卡片的用户的信息
 * 存储到数据库中
 * @Author: zhangyan
 * @Date: 2019/9/5 0:49
 * @Version 1.0
 */
@Data
public class UserSupervision {


    //private String filename;

    private List<UserRequest> userList;




}
