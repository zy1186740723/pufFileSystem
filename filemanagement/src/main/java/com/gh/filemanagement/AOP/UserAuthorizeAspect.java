package com.gh.filemanagement.AOP;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gh.filemanagement.Service.UserService;
import com.gh.filemanagement.common.RedisOperator;
import com.gh.filemanagement.handler.LoginException;
import com.gh.filemanagement.utils.CookieConstant;
import com.gh.filemanagement.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: zhangyan
 * @Date: 2019/8/29 14:16
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class UserAuthorizeAspect {
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private UserService userService;

    @Pointcut("execution(public * com.gh.filemanagement.controller.*.*(..))"+
    "&&!execution(public * com.gh.filemanagement.controller.WXLoginController.*(..))"
            +"&&!execution(public * com.gh.filemanagement.controller.ControllerTest.downloadFile(..))"
            +"&&!execution(public * com.gh.filemanagement.controller.FileController.uploadFile(..))")
    public void verify(){}

    @Before("verify()")
    public void doVerify(){
        System.out.println("doVerify");
        ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=attributes.getRequest();

        //todo:进一步优化，把请求中token的位置确定下来
        String token=request.getParameter("Cookie");
        if(token==null){
            token=request.getHeader("Cookie");
        }



        //验证request header中设置的值token值
        if (StringUtils.isEmpty(token)){
            log.warn("登陆信息有问题，没有token，无法进行相应操作，请登录");
            throw new LoginException();
            //todo:返回前端提示用户登录
        }
        //根据token获取openId todo:根据openId 获取token，比对token是否过期
        String sessionObjectJson=redisOperator.get(token);
        if (StringUtils.isEmpty(sessionObjectJson)){
            log.warn("token过期，需要重新注册");
            //todo:返回前端提示用户登录
            throw new LoginException();
        }

        //校验token对应的openId的真实性
        String actualOpenId=JSONObject.parseObject(sessionObjectJson).getString("openId");
        if (userService.findUserInfoByOpenId(actualOpenId)==null){
            log.warn("openId不存在");
            throw new LoginException();
        }









    }

}
