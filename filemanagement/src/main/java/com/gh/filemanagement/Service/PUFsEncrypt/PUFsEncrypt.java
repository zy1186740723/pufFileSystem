package com.gh.filemanagement.Service.PUFsEncrypt;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/** 调用服务端口，进行PUFs加密
 * @Author: zhangyan
 * @Date: 2019/9/14 16:01
 * @Version 1.0
 */
@Service
public class PUFsEncrypt {


    private static final String PUFENCRYPT_API="http://119.23.11.21:7777/api/compress/common";
    private static final String PUFDECRYPT_API="http://119.23.11.21:7777/api/depress/common";
    public static InputStream Pencrypt(InputStream inputStream){
        CloseableHttpClient httpClient= HttpClientBuilder.create().build();
        // 响应模型
        CloseableHttpResponse response = null;
        InputStreamBody inputStreamBody=new InputStreamBody(inputStream,"tempFile");
        HttpPost httpPost=new HttpPost(PUFENCRYPT_API);
        HttpEntity reqEntiy= MultipartEntityBuilder.create()
                .addPart("file1",inputStreamBody).build();
        //将req体设置到请求中去
        httpPost.setEntity(reqEntiy);
        InputStream res=null;
        //发送请求
        try {
            response=httpClient.execute(httpPost);

            HttpEntity responseEntity=response.getEntity();
            if (responseEntity!=null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                //System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                res= responseEntity.getContent();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
    public static InputStream Pdecrypt(InputStream inputStream){
        CloseableHttpClient httpClient= HttpClientBuilder.create().build();
        // 响应模型
        CloseableHttpResponse response = null;
        InputStreamBody inputStreamBody=new InputStreamBody(inputStream,"tempFile");
        HttpPost httpPost=new HttpPost(PUFDECRYPT_API);
        HttpEntity reqEntiy= MultipartEntityBuilder.create()
                .addPart("file1",inputStreamBody).build();
        //将req体设置到请求中去
        httpPost.setEntity(reqEntiy);
        InputStream res=null;
        //发送请求
        try {
            response=httpClient.execute(httpPost);

            HttpEntity responseEntity=response.getEntity();
            if (responseEntity!=null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                //内容只能使用一次
                //System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                res= responseEntity.getContent();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

}
