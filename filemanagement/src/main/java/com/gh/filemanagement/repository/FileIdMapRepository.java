package com.gh.filemanagement.repository;

import com.gh.filemanagement.DAO.FileIdMap;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 13:05
 * @Version 1.0
 */
public interface FileIdMapRepository extends MongoRepository<FileIdMap,String> {

}
