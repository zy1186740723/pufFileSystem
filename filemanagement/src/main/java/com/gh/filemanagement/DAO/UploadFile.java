package com.gh.filemanagement.DAO;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 10:24
 * @Version 1.0
 */

public class UploadFile {

    @Id
    private String openId;

    private String path;
}
