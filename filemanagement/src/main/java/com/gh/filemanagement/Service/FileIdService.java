package com.gh.filemanagement.Service;

import com.gh.filemanagement.DAO.FileIdMap;
import com.gh.filemanagement.DAO.FileInfo;

import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 13:04
 * @Version 1.0
 */
public interface FileIdService {

    List<FileInfo> findFileList(String openId);


    FileIdMap buildFiledMap(String openId,String fileName,String realFilename);

    String findFileNameByrealFileName(String realFileName);

    String findIdByFileName(String fileName);

    void delete(String realFileName);

    FileIdMap findByRealFileNameAndOpenId(String realFileName,String openId);
    void clearUserRequest(String realFileName,String openId);
}
