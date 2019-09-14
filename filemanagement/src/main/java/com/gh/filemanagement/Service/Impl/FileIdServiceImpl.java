package com.gh.filemanagement.Service.Impl;

import com.gh.filemanagement.DAO.*;
import com.gh.filemanagement.Service.FileIdService;
import com.gh.filemanagement.repository.FileIdMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 13:05
 * @Version 1.0
 */
@Service
public class FileIdServiceImpl implements FileIdService {

    @Autowired
    private FileIdMapRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Transactional
    public void saveFileID(FileIdMap fileIdMap){
        FileIdMap fileIdMap1=repository.save(fileIdMap);
    }

    public FileIdMap findFileId(String filename,String openId){
        Query query=new Query();
        query.addCriteria(Criteria.where("fileName").is(filename)
                .and("openId").is(openId));
        FileIdMap fileIdMap=mongoTemplate.findOne(query,FileIdMap.class);

        return  fileIdMap;
    }

    @Override
    public List<FileInfo> findFileList(String openId) {
        Query query=new Query();
        query.addCriteria(Criteria.where("openId").is(openId));
        List<FileIdMap> list=mongoTemplate.find(query,FileIdMap.class);

        List<FileInfo> fileInfoList=new ArrayList<>();

        for (FileIdMap fileIdMap : list) {
            FileInfo fileInfo=new FileInfo();
            String res1=fileIdMap.getRealFileName();
            String res2=fileIdMap.getFileName();
            fileInfo.setFileId(res2);
            fileInfo.setRealFileName(res1);
            fileInfoList.add(fileInfo);
        }

        return fileInfoList;
    }

    @Override
    public String findFileNameByrealFileName(String realFileName) {
        Query query=new Query();
        query.addCriteria(Criteria.where("realFileName").is(realFileName));
        FileIdMap fileIdMap=mongoTemplate.findOne(query,FileIdMap.class);

        return fileIdMap.getFileName();
    }

    @Override
    public String findIdByFileName(String fileName) {
        Query query=new Query();
        query.addCriteria(Criteria.where("filename").is(fileName));
        //获取fs.file中的objectId
        String objectId=gridFsTemplate.findOne(query).getObjectId().toString();
        FileIdMap fileIdMap=mongoTemplate.findOne(query,FileIdMap.class);
        return objectId;
    }

    @Override
    public FileIdMap buildFiledMap(String openId, String fileName,String realFileName) {
        List<UserRequest> list=new ArrayList<>();
        FileIdMap fileIdMap=new FileIdMap();
        fileIdMap.setFileId(UUID.randomUUID().toString());
        fileIdMap.setFileName(fileName);
        fileIdMap.setOpenId(openId);
        fileIdMap.setRealFileName(realFileName);
        fileIdMap.setUserRequestList(list);
        return fileIdMap;
    }

    @Override
    public void delete(String realFileName) {
        Query query=new Query();
        query.addCriteria(Criteria.where("realFileName").is(realFileName));
        mongoTemplate.remove(query,FileIdMap.class);
    }

    @Override
    public FileIdMap findByRealFileNameAndOpenId(String realFileName, String openId) {
        Query query=new Query();
        query.addCriteria(Criteria.where("realFileName").is(realFileName)
                .and("openId").is(openId));
        return  mongoTemplate.findOne(query,FileIdMap.class);
    }

    @Override
    public void clearUserRequest(String realFileName,String openId) {
        Query query=new Query();
        query.addCriteria(Criteria.where("realFileName").is(realFileName)
                .and("openId").is(openId));

        FileIdMap fileIdMap=mongoTemplate.findOne(query,FileIdMap.class);

        List<UserRequest> list=new ArrayList<>();
        fileIdMap.setUserRequestList(list);
        mongoTemplate.remove(query,FileIdMap.class);
        mongoTemplate.save(fileIdMap);


    }
}

