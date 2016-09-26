package com.andy.MobileSafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	/**
	 * 判断服务是否开启
	 * @param context    上下文环境
	 * @param serviceName    服务名称
	 * @return    返回true 服务开启，返回false  服务关闭
	 */
	public static boolean isRunning(Context context,String serviceName){
		//获取ActivityManager对象，通过该对象可以得到当前正在运行的服务
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统中正在运行的服务（maxNum 获取的最大数目）
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			if(serviceName.equals(runningServiceInfo.service.getClassName())){
				return true;
			}
		}
		return false;
	}
}
