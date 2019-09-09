package com.gh.filemanagement.controller;

import com.alibaba.fastjson.JSONObject;
import com.gh.filemanagement.DAO.FileInfo;
import com.gh.filemanagement.DAO.UserInfo;
import com.gh.filemanagement.Service.Impl.FileIdServiceImpl;
import com.gh.filemanagement.Service.Impl.SupervisionServiceImpl;
import com.gh.filemanagement.Service.SupervisionService;
import com.gh.filemanagement.Service.UserService;
import com.gh.filemanagement.common.RedisOperator;
import com.gh.filemanagement.controller.WXLoginController;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;
import jdk.nashorn.internal.ir.WhileNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhangyan
 * @Date: 2019/8/30 11:10
 * @Version 1.0
 */
@RestController
@Slf4j
public class ControllerTest {
    @Autowired
    private RedisOperator redis;
    @Autowired
    private UserService userService;
    @Autowired
    private FileIdServiceImpl fileIdService;
    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private SupervisionServiceImpl supervisionService;

    /**
     *
     * @param cookie
     * @return
     */
    //获取列表
    @RequestMapping(value = "/downloadFileList2")
    public List<FileInfo> downloadFileList (@RequestParam(value = "Cookie") String cookie){
        System.out.println(cookie);
        //根据cookie找到用户的openId
        String openId=userService.findOpenIdByUserKey(cookie);

        //根据openId，找出数据库中用户的所有图片名称
        List<FileInfo> fileInfoList=fileIdService.findFileList(openId);

        //构成列表返回
        return fileInfoList;
    }

    @RequestMapping(value = "/downloadFile2")
    public void downloadFile(@RequestParam(name = "file_id") String realFileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //对token进行验证
        //已经放入切面中实现
//        String token=request.getHeader("Cookie");
//        String json=redis.get(token);
//        if(json==null){
//            response.setStatus(500);
//        }

            //根据文件名查询在文件的fileName
            String fileName=fileIdService.findFileNameByrealFileName(realFileName);
            //根据fileName 查询文件的ObjectId
            String objectId=fileIdService.findIdByFileName(fileName);
            //根据objectId也就是数据的files_id，对文件进行检索
            Query query = Query.query(Criteria.where("filename").is(fileName));
            System.out.println("开始下载"+realFileName+"文件");
            // 查询单个文件
            GridFSFile gfsfile =  gridFsTemplate.findOne(query);
//
//        List<GridFsResource> list=new ArrayList<>();
//        InputStream inputStream=null;
//        while (gfsfile.iterator().hasNext()){
//            list.add(new GridFsResource(gfsfile.iterator().next()));
//        }
            //InputStream downLoadStream= GridFSBuckets.create(mongoDbFactory.getDb()).openDownloadStream()


            GridFsResource gridFsResource=new GridFsResource(gfsfile,GridFSBuckets.create(mongoDbFactory.getDb()).openDownloadStream(gfsfile.getObjectId()));


            if (gfsfile == null) {
                return;
            }
            fileName = gfsfile.getFilename().replace(",", "");
            //处理中文文件名乱码
            if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
                    request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
                    || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
////            //非IE浏览器的处理：
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            //InputStream inputStream=gridFsResource.getInputStream();

            //ServletOutputStream outputStream = response.getOutputStream();

            //通知浏览器进行文件下载
            //response.setContentType(gfsfile.getContentType());
            response.setHeader("Content-Disposition", "attachment;filename=\"" + realFileName + "\"");
            IOUtils.copy(gridFsResource.getInputStream(),response.getOutputStream());
//        byte[] bs=new byte[1024];
//        while(inputStream.read(bs)>0){
//            outputStream.write(bs);
//        }
//        inputStream.close();;
//        outputStream.close();





    }

    /**
     * 这个不是必须的 因为无法获得点击用户的具体信息
     * @param encryptedData
     * @param iv
     * @return
     */
    @RequestMapping("/shareInfo")
    public String getShareInfo(@RequestParam(value = "encrypteData",required = false) String encryptedData,
                             @RequestParam(value = "iv" ,required = false) String iv){
        //获取sessionKey
        log.info("获取sessionKey");
        ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=attributes.getRequest();
        String token=request.getHeader("Cookie");

        String sessionKey=userService.findSessionKeyByToken(token);
        //{"watermark":{"appid":"wx2f09b53436c31f02","timestamp":1567615682},"openGId":"tG4JE44y16qzHVs8bFze8bhwBm-ps"}
        JSONObject jsonObject=WXLoginController.getUserInfo(encryptedData,sessionKey,iv);
        System.out.println(jsonObject.toJSONString());

        String openGId=jsonObject.getString("openGId");





        String userName=userService.findUserInfoByOpenId(userService.findOpenIdByUserKey(token)).getUserName();

        return userName;
    }
}
