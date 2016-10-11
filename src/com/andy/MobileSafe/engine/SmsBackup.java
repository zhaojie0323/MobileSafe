package com.andy.MobileSafe.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackup {
	private static int index = 0;
	/**
	 * 备份短信
	 * @param context  上下文环境
	 * @param path     备份的路径
	 * @param CallBack
	 */
	public static void backup(Context context,String path,CallBack callBack){
		Cursor cursor = null;
		//1、获取备份短信写入的文件
		FileOutputStream fos = null;
		try {
			File file =new File(path);
			//2、通过内容解析器查询短信数据库
			cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address","date","type","body"},
					null, null, null);
			//3、文件相应的输出流
			fos =new FileOutputStream(file);
			//4、序列话数据库中读取的数据，放置到xml中
			XmlSerializer smsSerializer = Xml.newSerializer();
			//5、给xml做相应的设置
			smsSerializer.setOutput(fos, "utf-8");
			smsSerializer.startDocument("utf-8", true);
			smsSerializer.startTag(null, "smss");
			//6、备份短信总数指定
			//progressDialog.setMax(cursor.getCount());
			if(callBack != null){
				callBack.setMax(cursor.getCount());
			}
			//7、读取数据库中的每一行数据写入到xml中
			while(cursor != null && cursor.moveToNext()){
				smsSerializer.startTag(null, "sms");

				smsSerializer.startTag(null, "address");
				smsSerializer.text(cursor.getString(0));
				smsSerializer.endTag(null, "address");

				smsSerializer.startTag(null, "date");
				smsSerializer.text(cursor.getString(1));
				smsSerializer.endTag(null, "date");

				smsSerializer.startTag(null, "type");
				smsSerializer.text(cursor.getString(2));
				smsSerializer.endTag(null, "type");

				smsSerializer.startTag(null, "body");
				smsSerializer.text(cursor.getString(3));
				smsSerializer.endTag(null, "body");

				smsSerializer.endTag(null, "sms");
				//8、每循环一次就需要让进度条叠加
				index++;
				//为了让用户看到短信备份的过程，每备份完一条短信睡眠200ms
				Thread.sleep(200);
				//progressDialog.setProgress(index);
				if(callBack != null){
					callBack.setProgress(index);
				}
			}
			smsSerializer.endTag(null, "smss");
			smsSerializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor != null && fos != null){
				try {
					cursor.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public interface CallBack{
		public void setMax(int max);
		public void setProgress(int value);
	}
}
