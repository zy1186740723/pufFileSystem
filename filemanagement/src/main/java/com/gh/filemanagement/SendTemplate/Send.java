package com.gh.filemanagement.SendTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: zhangyan
 * @Date: 2019/9/5 10:23
 * @Version 1.0
 */
public class Send {
    private static final String TEMPLATE_ID="cKU4fUyfpmYD34Elv57TeQXkbC54ni1LEqvE3ah3mSA";
    private static final String TEMPLATE_ID2="YG3TNozUx_CcFio6076L36FRLZVgjgxzNg0P7NeY8QE";
    public static void main(String[] args) {
        //推送实体类
        wxsmallTemplate tem=new wxsmallTemplate();
        //设置模板id
        tem.setTemplateId(TEMPLATE_ID);

        tem.setToUser("o4JE448JvlDDuJh7X_KAZjzxTGl8");

        tem.setForm_id("1cf9fcc21ea5469280af56f63bde3447");

        //点开模板路由的地址
        tem.setPage("pages/service/service3/service3");

        List<wxsmallTemplateParam> paras=new ArrayList<>();
        paras.add(new wxsmallTemplateParam("keyword1","同意","#DC143C"));
        //时间按照格式转换
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date temp = new Date();
        String str = "";
        str = sdf.format(temp);

        paras.add(new wxsmallTemplateParam("keyword2",str));

        tem.setTemplateParamList(paras);

        //获取token的凭证
        Token token=CommonUtils.getToken();

        String accessToken=token.getAccessToken();

        boolean result=CommonUtils.sendTemplateMsg(accessToken,tem);

        if (result) {
            System.err.println("推送成功");
        } else {
            System.err.println("推送失败");
        }


    }

    public static void sendTemplate(String openId,String formId,String filename){
        wxsmallTemplate tem=new wxsmallTemplate();
        //设置模板id
        tem.setTemplateId(TEMPLATE_ID);

        tem.setToUser(openId);

        tem.setForm_id(formId);

        //点开模板路由的地址
        tem.setPage("pages/service/service8/service8?filename="+filename);

        List<wxsmallTemplateParam> paras=new ArrayList<>();
        paras.add(new wxsmallTemplateParam("keyword1","同意","#DC143C"));


        paras.add(new wxsmallTemplateParam("keyword2",CommonUtils.buildTime()));

        tem.setTemplateParamList(paras);

        //获取token的凭证
        Token token=CommonUtils.getToken();

        String accessToken=token.getAccessToken();

        boolean result=CommonUtils.sendTemplateMsg(accessToken,tem);

        if (result) {
            System.err.println("推送成功");
        } else {
            System.err.println("推送失败");
        }

    }

    public static void sendTemplate2(String openId,String formId,String textInput){
        wxsmallTemplate tem=new wxsmallTemplate();
        //设置模板id
        tem.setTemplateId(TEMPLATE_ID2);

        tem.setToUser(openId);

        tem.setForm_id(formId);

        //点开模板路由的地址
        tem.setPage("pages/service/service1");

        List<wxsmallTemplateParam> paras=new ArrayList<>();
        paras.add(new wxsmallTemplateParam("keyword1","同意","#DC143C"));


        paras.add(new wxsmallTemplateParam("keyword2",CommonUtils.buildTime()));

        paras.add(new wxsmallTemplateParam("keyword3",textInput));


        tem.setTemplateParamList(paras);

        //获取token的凭证
        Token token=CommonUtils.getToken();

        String accessToken=token.getAccessToken();

        boolean result=CommonUtils.sendTemplateMsg(accessToken,tem);

        if (result) {
            System.err.println("推送成功");
        } else {
            System.err.println("推送失败");
        }

    }

}
