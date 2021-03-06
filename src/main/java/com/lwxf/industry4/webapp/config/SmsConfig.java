package com.lwxf.industry4.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yunpian.sdk.YunpianClient;

import com.lwxf.industry4.webapp.baseservice.sms.yunpian.SmsConstant;
import com.lwxf.commons.security.DesTool;
import com.lwxf.industry4.webapp.baseservice.sms.yunpian.SmsConstant;

/**
 * 功能：手机短消息配置
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018-05-24 10:28:39
 * @version：2018 1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Configuration
public class SmsConfig {
	@Value("${sms.accessKeyId}")
	private String accessKeyId;
	@Value("${sms.accessKeySecret}")
	private String accessKeySecret;

	@Value("${sms.yp.apikey}")
	private String yunpianApiKey;

	@Bean(name = "iAcsClientService")
	public IAcsClient iAcsClient() {
		DesTool desTool = new DesTool();
		accessKeyId=desTool.decrypt(accessKeyId);
		accessKeySecret=desTool.decrypt(accessKeySecret);
		//初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile(SmsConstant.REGION_ID, accessKeyId, accessKeySecret);
		try {
			DefaultProfile.addEndpoint(SmsConstant.ENDPOINT_NAME, SmsConstant.REGION_ID, SmsConstant.PRODUCT, SmsConstant.DOMAIN);
		} catch (ClientException e) {
			e.printStackTrace();
		}
		IAcsClient acsClient = new DefaultAcsClient(profile);
		return  acsClient;
	}

	@Bean(name = "yunpianClient")
	public YunpianClient yunpianClient(){
		DesTool desTool = new DesTool();
		this.yunpianApiKey = desTool.decrypt(this.yunpianApiKey);
		YunpianClient yunpianClient = new YunpianClient(this.yunpianApiKey);
		return yunpianClient;
	}
}
