package com.gh.filemanagement.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangyan
 * @Date: 2019/9/8 22:44
 * @Version 1.0
 */
@ControllerAdvice
@Slf4j
public class WxOperationExceptionHandler {
    //拦截登录异常
    @ExceptionHandler(value = WxOperationException.class)
    @ResponseBody
    public void handlerWxOperationException(HttpServletRequest request, HttpServletResponse response){
        System.out.println("操作失败");
        response.setStatus(501);
        System.out.println("resp:"+response);
        System.out.println("完成异常拦截，进入重新登录");
    }

    @ExceptionHandler(value = FileExsitingException.class)
    @ResponseBody
    public void handlerFileExsitingException(HttpServletRequest request, HttpServletResponse response){
        System.out.println("操作失败");
        response.setStatus(502);
        System.out.println("resp:"+response);
        System.out.println("文件已经存在");
    }
}
