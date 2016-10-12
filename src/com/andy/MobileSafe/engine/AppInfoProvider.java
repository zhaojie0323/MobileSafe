package com.andy.MobileSafe.engine;

import java.util.ArrayList;
import java.util.List;
import com.andy.MobileSafe.db.domain.AppInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInfoProvider {
	/**
	 * 获取当前手机所有应用相关信息的集合
	 * @param context    上下文环境
	 * @return    返回当前手机所有应用相关信息（包名、应用名称、图标、是否为系统应用、是否为sd上的应用）的集合
	 */
	public static List<AppInfo> getAppInfoList(Context context){
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		//1、获取包管理对象
		PackageManager pm = context.getPackageManager();
		//2、获取已安装应用集合
		List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
		//3、遍历应用信息的集合
		for (PackageInfo packageInfo : packageInfoList) {
			AppInfo appInfo = new AppInfo();
			//4、获取应用的包名
			appInfo.setPackageName(packageInfo.packageName);
			//5、获取应用名称
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			appInfo.setLabel((String)applicationInfo.loadLabel(pm));
			//6、获取应用图标
			appInfo.setIcon(applicationInfo.loadIcon(pm));
			//7、判断是否为系统应用
			if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
				//系统应用
				appInfo.setSystem(true);
			}else{
				//非系统应用
				appInfo.setSystem(false);
			}
			//8、判断是否为sd卡上安装的应用
			if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				//sd卡上的应用
				appInfo.setSdCard(true);
			}else{
				//非sd卡上的应用
				appInfo.setSdCard(false);
			}
			appInfoList.add(appInfo);
		}
		return appInfoList;
	}
}
