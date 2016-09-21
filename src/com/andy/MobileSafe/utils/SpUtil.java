package com.andy.MobileSafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
	
	private static SharedPreferences sp;

	//写入boolean值
	/**
	 * @param context 上下文环境
	 * @param key  存储节点名称
	 * @param value 存储节点的值boolean
	 */
	public static void putBoolean(Context context,String key,boolean value){
		if(sp==null){
			sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}
	//读取boolean值
	/**
	 * @param context   上下文环境
	 * @param key       存储节点名称
	 * @param defValue  没有该节点时的默认值boolean
	 * @return          存储节点的值或默认值
	 */
	public static boolean getBoolean(Context context,String key,boolean defValue){
		if(sp==null){
			sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}
	/**
	 * @param context 上下文环境
	 * @param key  存储节点名称
	 * @param value 存储节点的值boolean
	 */
	public static void putString(Context context,String key,String value){
		if(sp==null){
			sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	/**
	 * @param context   上下文环境
	 * @param key       存储节点名称
	 * @param defValue  没有该节点时的默认值String
	 * @return          存储节点的值或默认值
	 */
	public static String getString(Context context,String key,String defValue){
		if(sp==null){
			sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}
	/**
	 * @param context   上下文环境
	 * @param key 要移除节点的节点名称
	 */
	public static void remove(Context context,String key){
		if(sp==null){
			sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		}
		 sp.edit().remove(key).commit();
	}
}
