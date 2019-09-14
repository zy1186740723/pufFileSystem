package com.gh.filemanagement.controller;

import com.gh.filemanagement.DAO.FileIdMap;
import com.gh.filemanagement.DAO.UserRequest;
import com.gh.filemanagement.Service.Impl.FileIdServiceImpl;
import com.gh.filemanagement.Service.Impl.SupervisionServiceImpl;
import com.gh.filemanagement.Service.Impl.UserServiceImpl;
import com.gh.filemanagement.Service.PUFsEncrypt.PUFsEncrypt;
import com.gh.filemanagement.Service.SupervisionService;
import com.gh.filemanagement.handler.WxOperationException;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
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
    @Autowired
    private SupervisionService supervisionService;
    @Autowired
    private MongoDbFactory mongoDbFactory;

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
            //Todo:进行加密操作


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

    //进入文件列表以后，获取文件名，删除指定文件
    @GetMapping(value = "/delete")
    @ResponseBody
    public void delete(@RequestParam(value = "Cookie")String token,
                         @RequestParam(value = "file_id")String realFileName){
        //根据文件名查询在文件的fileName
        String fileName=fileIdService.findFileNameByrealFileName(realFileName);
        Query query = Query.query(Criteria.where("filename").is(fileName));

        System.out.println("开始删除"+realFileName+"文件");

        GridFSFile gfsfile =  gridFsTemplate.findOne(query);

        String f_id=gfsfile.getObjectId().toString();
        System.out.println("f_id:"+f_id);

        //依据objectId，删除文件
        //根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(f_id)));

        fileIdService.delete(realFileName);

    }

    //清空申请信息
    @RequestMapping(value = "/clear")
    @ResponseBody
    public void clear(@RequestParam(value = "filename",required = false) String filename,
                      @RequestParam(value = "Cookie",required = false)String token){
        String openId=userService.findOpenIdByUserKey(token);
        fileIdService.clearUserRequest(filename,openId);

    }

    //PUFs加密上传
    @RequestMapping(value = "/uploadFilePufs",method = RequestMethod.POST)
    @ResponseBody
    public Object uploadFilePufs(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("file");
        String cookie = request.getHeader("Cookie");
        String realFileName = request.getParameter("fileName");
        //判断文件是否重复上传
        //根据cookie找到openId
        String openId = userService.findOpenIdByUserKey(cookie);
        Query query = new Query();
        query.addCriteria(Criteria.where("realFileName").is(realFileName)
                .and("openId").is(openId));
        if (mongoTemplate.findOne(query, FileIdMap.class) != null) {
            throw new WxOperationException();
        } else {
            //获得提交的文件名
            String fileName = part.getSubmittedFileName();


            //获取文件的输入流
            InputStream ins = part.getInputStream();
            //Todo:进行加密操作
            ins=PUFsEncrypt.Pencrypt(ins);


            String contentType = part.getContentType();

            //String index=""+fileName;

            ObjectId gridFSFile = gridFsTemplate.store(ins, fileName, contentType);


            //构建文件索引
            //构建filedMap存储
            FileIdMap fileIdMap = fileIdService.buildFiledMap(openId, fileName, realFileName);
            fileIdService.saveFileID(fileIdMap);

            System.out.println("文件在数据库中的_id:" + gridFSFile.toString());

            return gridFSFile;
        }
    }

    @RequestMapping(value = "/downloadFilePUFs")
    public void downloadFilePUFs(@RequestParam(name = "file_id") String realFileName, HttpServletRequest request, HttpServletResponse response) throws Exception {


        //根据文件名查询在文件的fileName
        String fileName=fileIdService.findFileNameByrealFileName(realFileName);
        //根据fileName 查询文件的ObjectId
        String objectId=fileIdService.findIdByFileName(fileName);
        //根据objectId也就是数据的files_id，对文件进行检索
        Query query = Query.query(Criteria.where("filename").is(fileName));
        System.out.println("开始下载"+realFileName+"文件");
        // 查询单个文件
        GridFSFile gfsfile =  gridFsTemplate.findOne(query);


        //文件下载
        GridFsResource gridFsResource=new GridFsResource(gfsfile, GridFSBuckets.create(mongoDbFactory.getDb()).openDownloadStream(gfsfile.getObjectId()));


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


        //通知浏览器进行文件下载
        //response.setContentType(gfsfile.getContentType());
        response.setHeader("Content-Disposition", "attachment;filename=\"" + realFileName + "\"");
        //TODO;解密操作
        InputStream res=PUFsEncrypt.Pdecrypt(gridFsResource.getInputStream());
        IOUtils.copy(res,response.getOutputStream());

    }

}
