package com.thunder.MobileSafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
	/**
	 * 给指定字符串按照md5算法进行加密
	 * @param pwd  需要进行加密的字符串
	 * @return  加密后的字符串
	 */
	public static String encoder(String pwd) {
		// 密码加盐
		pwd=pwd+"mobilesafe";
		try {
			//1、指定加密算法类型
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//2、将需要加密的字符串转换成byte[]类型的数组，然后进行随机哈希过程
			byte[] bs = digest.digest(pwd.getBytes());
			//3、循环遍历bs，然后让其生成32位字符串
			StringBuffer buffer = new StringBuffer();
			for (byte b : bs) {
				int i=b&0xff;
				//int类型的i需要转换成16进制的字符串
				String hexString = Integer.toHexString(i);
				
				if(hexString.length()<2){
					hexString="0"+hexString;
				}
				//4、字符串拼接
				buffer=buffer.append(hexString);
			}
			return buffer.toString();
			//System.out.println(md5String);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
