package com.gh.filemanagement.Service.Impl;

import com.gh.filemanagement.DAO.FileIdMap;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author: zhangyan
 * @Date: 2019/8/30 11:37
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FileIdServiceImplTest {
    @Autowired
    private FileIdServiceImpl fileIdService;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;

//    @Test
//    public void findFileList() {
//        String openId="o4JE448JvlDDuJh7X_KAZjzxTGl8";
//
//        List<String> list=fileIdService.findFileList(openId);
//        log.info("文件的数量为："+list.size());
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println( list.get(i));
//        }
//
//    }
//    @Test
//    public void mongoTest(){
//        Query query = Query.query(Criteria.where("filename").is("wx2f09b53436c31f02.o6zAJs8d--yMvV3N5rS-DM9VjwlA.KX6qikVyhtRc667ad2da443b70cae60c9325205ad0dd.png"));
//        GridFSFile gfsfile = gridFsTemplate.findOne(query);
//
//        GridFsResource gridFsResource=new GridFsResource(gfsfile);
//        Assert.assertNotNull(gfsfile);
//    }

//    @Test
////    public void findFileNameByrealFileNameTest() {
////        String res=fileIdService.findFileNameByrealFileName("微信图片_20190811220957.png");
////
////        Assert.assertEquals("wx2f09b53436c31f02.o6zAJs8d--yMvV3N5rS-DM9VjwlA.KX6qikVyhtRc667ad2da443b70cae60c9325205ad0dd.png",res); ;
////    }

    @Test
    public void findIdByFileNameTest(){
        String realfileName="Mobile Cockpit System for Enhanced Electric Bicycle Use.pdf";
        String fileName=fileIdService.findFileNameByrealFileName(realfileName);
        String id=fileIdService.findIdByFileName(fileName);
        Assert.assertEquals("5d6e1af7da5d1f2434cca630",id);

    }
}