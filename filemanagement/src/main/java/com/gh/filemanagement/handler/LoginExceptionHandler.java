package com.gh.filemanagement.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangyan 笔记 wx.request 用切面回传json数据，可自自定义其中内容
 * 而如果是wx.download等操作，需要在respo中设置statucode
 * 如下不是http请求 （如wx.download），获取不到req和response
 * @Date: 2019/8/29 21:46
 * @Version 1.0
 */
@ControllerAdvice
@Slf4j
public class LoginExceptionHandler {
    //拦截登录异常
    @ExceptionHandler(value = LoginException.class)
    @ResponseBody
    public Object handlerAuthorizeException(){
        log.info("进入登录异常拦截");
        Map<String,Object> map=new HashMap<>();
        map.put("error",0);
        //HttpServletResponse resp = ((ServletWebRequest)RequestContextHolder.getRequestAttributes()).getResponse();
        //{result: 0, skey: "e5550a59-823b-4672-8db3-668c9fbfe34c"}
        JSONObject resultObject=new JSONObject();
        resultObject.put("error",0);

        //resp.setHeader("file","123");
        //通过statusCode来进行跳转，接收到response就进行跳转
        return resultObject;
    }
}
