package com.lwxf.industry4.webapp.facade;

import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 功能：
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018-08-14 9:31
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class DemoControllerApplicationTest {
//	@Autowired
//	private MockMvc mvc;
//	@Test
//	public void testRongRegister() throws Exception{
//		mvc.perform(MockMvcRequestBuilders.get("/api/demos/rong/register").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk());
//	}
public static String AESEncode(String encodeRules,String content){
	try {
		//1.构造密钥生成器，指定为AES算法,不区分大小写
		KeyGenerator keygen= KeyGenerator.getInstance("AES");
		//2.根据ecnodeRules规则初始化密钥生成器
		//生成一个128位的随机源,根据传入的字节数组
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG") ;
		secureRandom.setSeed(encodeRules.getBytes());
		keygen.init(128, secureRandom);
		//keygen.init(128, new SecureRandom(encodeRules.getBytes()));
		//3.产生原始对称密钥
		SecretKey original_key=keygen.generateKey();
		//4.获得原始对称密钥的字节数组
		byte [] raw=original_key.getEncoded();
		//5.根据字节数组生成AES密钥
		SecretKey key=new SecretKeySpec(raw, "AES");
		//6.根据指定算法AES自成密码器
		Cipher cipher=Cipher.getInstance("AES");
		//7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
		cipher.init(Cipher.ENCRYPT_MODE, key);
		//8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
		byte [] byte_encode=content.getBytes("utf-8");
		//9.根据密码器的初始化方式--加密：将数据加密
		byte [] byte_AES=cipher.doFinal(byte_encode);
		//10.将加密后的数据转换为字符串
		//这里用Base64Encoder中会找不到包
		//解决办法：
		//在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
		String AES_encode=new String(new BASE64Encoder().encode(byte_AES));
		//11.将字符串返回
		return AES_encode;
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	} catch (NoSuchPaddingException e) {
		e.printStackTrace();
	} catch (InvalidKeyException e) {
		e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
		e.printStackTrace();
	} catch (BadPaddingException e) {
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}

	//如果有错就返加nulll
	return null;
}

	public static void main(String[] args) {
		DemoControllerApplicationTest se=new DemoControllerApplicationTest();
		//Scanner scanner=new Scanner(System.in);
		/*
		 * 加密
		 */
		String encodeRules="红田集团"+"牛总"+"130000000";
		String content = "2029-06-04 16:54:37";
		System.out.println("根据输入的规则"+encodeRules+"加密后的密文是:"+se.AESEncode(encodeRules, content));


	}




}
