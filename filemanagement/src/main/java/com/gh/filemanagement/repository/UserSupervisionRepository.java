package com.gh.filemanagement.repository;

import com.gh.filemanagement.DAO.UserSupervision;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: zhangyan
 * @Date: 2019/9/5 1:01
 * @Version 1.0
 */
public interface UserSupervisionRepository extends MongoRepository<UserSupervision,String> {

}
