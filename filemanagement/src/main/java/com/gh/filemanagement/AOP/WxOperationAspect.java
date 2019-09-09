package com.gh.filemanagement.AOP;

import com.gh.filemanagement.common.RedisOperator;
import com.gh.filemanagement.handler.WxOperationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: zhangyan
 * @Date: 2019/9/8 22:34
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class WxOperationAspect {
    @Autowired
    private RedisOperator redis;

    @Pointcut("execution(public * com.gh.filemanagement.controller.ControllerTest.downloadFile(..))"
    +"|| execution(public * com.gh.filemanagement.controller.FileController.uploadFile(..))")
    public void verify(){}

    @Before("verify()")
    public void doVerify(){
        System.out.println("doVerify");
        ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=attributes.getRequest();
        String token=request.getHeader("Cookie");
        String json=redis.get(token);
        if (json==null){
            throw new WxOperationException();
        }

    }
}
