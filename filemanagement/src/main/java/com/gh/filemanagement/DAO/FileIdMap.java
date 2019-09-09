package com.gh.filemanagement.DAO;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 13:01
 * @Version 1.0
 */
@Data
public class FileIdMap {

    private String openId;

    private String fileName;

    private String fileId;

    //添加一个映射用来查找文件的真实文件名
    private String realFileName;

    private List<UserRequest> userRequestList;
}
