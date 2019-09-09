package com.gh.filemanagement.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gh.filemanagement.DAO.UserInfo;
import com.gh.filemanagement.DAO.WXSessionModel;
import com.gh.filemanagement.Service.Impl.UserServiceImpl;
import com.gh.filemanagement.common.HttpClientUtil;
import com.gh.filemanagement.common.IMoocJSONResult;
import com.gh.filemanagement.common.JsonUtils;
import com.gh.filemanagement.common.RedisOperator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.xfire.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.*;

@RestController
public class WXLoginController {
	private static final Logger log= LoggerFactory.getLogger(WXLoginController.class);
	
	@Autowired
	private RedisOperator redis;
	@Autowired
	private UserServiceImpl userService;


	/**
	 * wx.request 的可以用ServletRequestAttributes获取req与response或者参数获取
	 * wx.download直接传入参数获取
	 * @param model1
	 * @param code
	 * @param rawData
	 * @param signature
	 * @param encrypteData
	 * @param iv
	 * @return
	 */
	@PostMapping("/wxLogin")
	@ResponseBody
	public Map<String,Object> wxLogin(Model model1,
								   @RequestParam(value = "code",required = false) String code,
								   @RequestParam(value = "rawData",required = false) String rawData,
								   @RequestParam(value = "signature",required = false) String signature,
								   @RequestParam(value = "encrypteData",required = false) String encrypteData, @RequestParam(value = "iv",required = false) String iv) {
		log.info( "Start get SessionKey" );
		log.info("========================");
		//获取请求中的request
		ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request=attributes.getRequest();

		log.info(request.getHeader("content-type"));
		System.out.println("wxlogin - code: " + code);
		JSONObject rawDataJson=JSONObject.parseObject(rawData);

//		https://api.weixin.qq.com/sns/jscode2session?
//				appid=APPID&
//				secret=SECRET&
//				js_code=JSCODE&
//				grant_type=authorization_code
		
		String url = "https://api.weixin.qq.com/sns/jscode2session";
		Map<String, String> param = new HashMap<>();
		param.put("appid", "wx2f09b53436c31f02");
		param.put("secret", "c2fbe39e875f32a0dd3714a16b128dfe");
		param.put("js_code", code);
		param.put("grant_type", "authorization_code");

		String wxResult = HttpClientUtil.doGet(url, param);
		System.out.println(wxResult);
		WXSessionModel model = JsonUtils.jsonToPojo(wxResult, WXSessionModel.class);
		String openId=model.getOpenid();
		String sessionKey=model.getSession_key();
		//uuid生成唯一key，也就是token
		String skey = UUID.randomUUID().toString();

		//寻找用户是否已经注册过
		UserInfo userInfo=userService.findUserInfoByOpenId(openId);

		if(userInfo==null){
			//第一次登陆，创建新角色
			String nickName = rawDataJson.getString( "nickName" );
			String avatarUrl = rawDataJson.getString( "avatarUrl" );
			String gender  = rawDataJson.getString( "gender" );
			String city = rawDataJson.getString( "city" );
			String country = rawDataJson.getString( "country" );
			String province = rawDataJson.getString( "province" );

			userInfo=new UserInfo();
			userInfo.setCreateTime(new Date());
			userInfo.setSessionKey(sessionKey);
			userInfo.setOpenId(openId);
			userInfo.setUserKey(skey);
			userInfo.setUserAvatar(avatarUrl);
			userInfo.setUserGender(gender);
			userInfo.setUserName(nickName);
			userInfo.setUserAddress(country+" "+province+" "+city);
			userInfo.setUpdateTime(new Date());
			userService.save(userInfo);
		}
		else {
			log.info("用户已经存在，不需要插入");
			userInfo.setUserKey(skey);
			userService.save(userInfo);
		}

		String skey_redis=redis.get(openId);

		if (skey_redis!=null){
            redis.del(skey_redis);
			redis.del(openId);

		}

		JSONObject sessionObject=new JSONObject();
		sessionObject.put("openId",openId);
		sessionObject.put("sessionKey",sessionKey);
		redis.set(skey,sessionObject.toJSONString(),7200);
		redis.set(openId,skey,7200);


		Map<String,Object> map = new HashMap<String, Object>(  );
		//相当于是token，也可以不存，用签名的方式
		map.put("skey",skey);
		map.put("result",0);


		// 存入session到redis,文件夹的树形结构
//		redis.set("user-redis-session:" + model.getOpenid(),
//							model.getSession_key(),
//							1000 * 60 * 30);

		JSONObject userInfoJson=getUserInfo(encrypteData,sessionKey,iv);
		System.out.println("根据解密算法获取的用户信息"+userInfoJson);

		
		return map;
	}

	public static JSONObject getUserInfo(String encryptedData,String sessionKey,String iv){
		// 被加密的数据
		byte[] dataByte = Base64.decode(encryptedData);
		// 加密秘钥
		byte[] keyByte = Base64.decode(sessionKey);
		// 偏移量
		byte[] ivByte = Base64.decode(iv);
		try {
			// 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
			int base = 16;
			if (keyByte.length % base != 0) {
				int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
				byte[] temp = new byte[groups * base];
				Arrays.fill(temp, (byte) 0);
				System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
				keyByte = temp;
			}
			// 初始化
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
			SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
			parameters.init(new IvParameterSpec(ivByte));
			cipher.init( Cipher.DECRYPT_MODE, spec, parameters);// 初始化
			byte[] resultByte = cipher.doFinal(dataByte);
			if (null != resultByte && resultByte.length > 0) {
				String result = new String(resultByte, "UTF-8");
				return JSON.parseObject(result);
			}
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidParameterSpecException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			log.error(e.getMessage(), e);
		} catch (BadPaddingException e) {
			log.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchProviderException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}


}
