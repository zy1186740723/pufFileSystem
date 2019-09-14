package com.gh.filemanagement.repository;

import com.gh.filemanagement.DAO.MessageInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: zhangyan
 * @Date: 2019/9/11 22:46
 * @Version 1.0
 */
public interface MessageInfoRepository extends MongoRepository<MessageInfo,String> {

}
