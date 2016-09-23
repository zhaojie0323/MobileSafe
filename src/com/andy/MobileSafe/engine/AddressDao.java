package com.andy.MobileSafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	private static final String TAG="AddressDao";
	//1、指定访问数据库的路径
	private static final String path="data/data/com.andy.MobileSafe/files/callHomeDB.db";
	private static final String MOBILE_TABLE = "mob_location";
	private static final String TEL_TABLE = "tel_location";

	/**
	 * 传递一个电话号码，开启数据库连接，进行访问，返回一个归属地
	 * @param context       上下文环境
	 * @param phoneNunber   被查询的电话号码
	 * @return              返回一个归属地
	 */
	public static String getAddress(Context context,String phoneNunber){
		//正则表达式匹配手机号码
		String regularExpression = "^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(17[0,1,3,5-8])|(18[0-9]))\\d{8}$";
		String address="未查询到归属地";
		//2、开启数据库连接（只读方式打开）
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		if(phoneNunber.matches(regularExpression)){
			phoneNunber = phoneNunber.substring(0, 7);
			Log.d(TAG,"subPhoneNumber is: "+phoneNunber);
			//3、查询数据库
			Cursor cursor = database.query(MOBILE_TABLE, new String[]{"location"}, "_id=?", 
					new String[]{phoneNunber}, null, null, null);
			if(cursor != null ){
				if(cursor.moveToNext()){
					address = cursor.getString(0);
					Log.d(TAG,"address: "+address);				
				}else{
					address = "未查询到归属地";
					Log.d(TAG, "Not query to address");
				}
			}else{
				Log.d(TAG, "cursor is null");
			}
		}
		else{
			int length=phoneNunber.length();
			switch(length){
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "服务电话";
				break;
			case 7:
				address = "本地电话";
				break;
			case 8:
				address = "本地电话";
				break;
			case 11:
				//(3+8)区号+座机号码（外地）
				String phone=phoneNunber.substring(1, 3);
				Log.d(TAG, phone);
				Cursor cursor1 = database.query(TEL_TABLE, new String[]{"location"}, "_id=?", 
						new String[]{phone}, null, null,null);
				if(cursor1 != null){
					if(cursor1.moveToNext()){
						address = cursor1.getString(0);
					}else{
						//(4+7)区号+座机号码（外地）
						phone=phoneNunber.substring(1, 4);
						Log.d(TAG, phone);
						Cursor cursor2 = database.query(TEL_TABLE, new String[]{"location"}, "_id=?", 
								new String[]{phone}, null, null,null);
						if(cursor2 != null){
							if(cursor2.moveToNext()){
								address = cursor2.getString(0);
							}else{
						        address = "未查询到归属地";
							}
					    }
					}
				} 
				break;
			case 12:
				phoneNunber=phoneNunber.substring(1, 4);
				Cursor cursor3 = database.query(TEL_TABLE, new String[]{"location"}, "_id=?", 
						new String[]{phoneNunber}, null, null,null);
				if(cursor3 != null && cursor3.moveToNext()){
					address = cursor3.getString(0);
				}else{
					address = "未查询到归属地";
				}
				break;
			}
		}
		return address;
	}
}
