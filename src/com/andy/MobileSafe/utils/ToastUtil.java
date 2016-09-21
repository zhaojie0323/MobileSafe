package com.andy.MobileSafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	
	/**
	 * @param context  上下文
	 * @param text		显示的内容
	 */
	public static void show(Context context,String text){
		Toast.makeText(context, text, 0).show();
	}

}
