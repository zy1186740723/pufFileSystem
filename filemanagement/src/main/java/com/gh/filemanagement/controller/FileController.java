package com.gh.filemanagement.controller;

import com.gh.filemanagement.DAO.FileIdMap;
import com.gh.filemanagement.Service.Impl.FileIdServiceImpl;
import com.gh.filemanagement.Service.Impl.UserServiceImpl;
import com.gh.filemanagement.handler.WxOperationException;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 11:06
 * @Version 1.0
 */
@Controller
public class FileController {
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private FileIdServiceImpl fileIdService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 用户上传文件
     * @param request
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    @ResponseBody
    public Object uploadFile(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("file");
        String cookie= request.getHeader("Cookie");
        String realFileName=request.getParameter("fileName");
        //判断文件是否重复上传
        //根据cookie找到openId
        String openId=userService.findOpenIdByUserKey(cookie);
        Query query=new Query();
        query.addCriteria(Criteria.where("realFileName").is(realFileName)
                .and("openId").is(openId));
        if (mongoTemplate.findOne(query,FileIdMap.class)!=null){
            throw new WxOperationException();
        }
        else {
            //获得提交的文件名
            String fileName=part.getSubmittedFileName();




            //获取文件的输入流
            InputStream ins=part.getInputStream();

            String contentType=part.getContentType();

            //String index=""+fileName;

            ObjectId gridFSFile=gridFsTemplate.store(ins,fileName,contentType);


            //构建文件索引
            //构建filedMap存储
            FileIdMap fileIdMap=fileIdService.buildFiledMap(openId,fileName,realFileName);
            fileIdService.saveFileID(fileIdMap);

            System.out.println("文件在数据库中的_id:"+gridFSFile.toString());

            return gridFSFile;
        }


    }

    /**
     * 用户下载文件
     * TODO:后期文件分享采取get的方式，加上权限判断确保安全性
     * @param fileId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/downloadFile")
    public void downloadFile(@RequestParam(name = "file_id") String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        System.out.println("开始下载"+fileId+"文件");
        // 查询单个文件
        GridFSFile gfsfile = gridFsTemplate.findOne(query);

        GridFsResource gridFsResource=new GridFsResource(gfsfile);


        if (gfsfile == null) {
            return;
        }
        String fileName = gfsfile.getFilename().replace(",", "");
        //处理中文文件名乱码
       if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
               request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
               || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
           fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } else {
////            //非IE浏览器的处理：
           fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
     //通知浏览器进行文件下载
        //response.setContentType(gfsfile.getContentType());
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        IOUtils.copy(gridFsResource.getInputStream(),response.getOutputStream());

    }

    @GetMapping(value = "/downloadFileList")
    public List<String> downloadFileList (String token){


        return null;
    }


}
