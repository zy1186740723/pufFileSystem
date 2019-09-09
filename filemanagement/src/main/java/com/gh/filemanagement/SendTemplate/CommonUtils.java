package com.gh.filemanagement.SendTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.gh.filemanagement.common.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: zhangyan
 * @Date: 2019/9/5 10:57
 * @Version 1.0
 */
@Slf4j
public class CommonUtils {

    private static final String token_url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    private static final String appID="wx2f09b53436c31f02";

    private static final String secret="c2fbe39e875f32a0dd3714a16b128dfe";

    public static Token getToken(){
        Token token = null;
        String requestUrl = token_url.replace("APPID", appID).replace("APPSECRET", secret);
        //发送get请求
        String res=HttpClientUtil.doGet(requestUrl);
        JSONObject jsonObject= JSON.parseObject(res);

        if (null != jsonObject) {
            try {
                token = new Token();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getIntValue("expires_in"));
            } catch (JSONException e) {
                token = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getIntValue("errcode"),
                        jsonObject.getString("errmsg"));
            }
        }
        return token;
    }

    public static boolean sendTemplateMsg(String token,
                                          wxsmallTemplate template) {

        boolean flag = false;

        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", token);

        //net.sf.json.JSONObject jsonResult = httpsRequest(requestUrl, "POST", template.toJSON());
        String res=HttpClientUtil.doPostJson(requestUrl,template.toJSON());
        JSONObject jsonResult=JSONObject.parseObject(res);
        System.err.println(template.toJSON());
        if (jsonResult != null) {
            Integer errorCode = jsonResult.getIntValue("errcode");
            String errorMessage = jsonResult.getString("errmsg");
            if (errorCode == 0) {
                flag = true;
            } else {
                System.out.println("模板消息发送失败:" + errorCode + "," + errorMessage);
                flag = false;
            }
        }
        return flag;
    }

    public static String buildTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date temp = new Date();
        String str = "";
        str = sdf.format(temp);
        return str;
    }
}
