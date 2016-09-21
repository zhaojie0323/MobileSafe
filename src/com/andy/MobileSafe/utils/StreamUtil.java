package com.andy.MobileSafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
	
	/**
	 * 将流转换成字符串
	 * @param is  流对象
	 * @return    流转换成的字符串 返回null代表异常
	 */
	public static String streamToString(InputStream is){
		//1、在读取的过程中，将读取的内容存储至缓存当中，然后一次性转换成字符串
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		byte[] buffer=new byte[1024];
		int temp=-1;
		try {
			while((temp=is.read(buffer))!=-1){
				bos.write(buffer, 0, temp);
			}
			return bos.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				is.close();
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
		
	}

}
