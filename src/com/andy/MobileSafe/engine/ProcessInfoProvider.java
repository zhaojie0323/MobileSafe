package com.andy.MobileSafe.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.andy.MobileSafe.R;
import com.andy.MobileSafe.db.domain.ProcessInfo;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
public class ProcessInfoProvider {
	/**
	 * 获取当前正在运行进程个数
	 * @param context    上下文环境
	 * @return 返回当前正在运行进程个数
	 */
	public static int getProcessCount(Context context){
		//1、获取ActivityManager对象
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//2、获取当前正在运行的进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		//3、返回集合总数
		return runningAppProcesses.size();
	}

	/**
	 * 获取可用的内存大小
	 * @param context   上下文环境
	 * @return   返回可用的内存大小  bytes
	 */
	public static long getAvailableSpace(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	/**
	 * 获取总内存大小
	 * @param context   上下文环境
	 * @return   返回总内存大小（bytes），返回0说明异常
	 */
	public static long getTotalSpace(Context context){
		//以下方法需要API16以上
/*		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		return outInfo.totalMem;*/
		//内存大小写入文件中，读取proc/meminfo,读取第一行，获取数字字符，转换成bytes返回
		FileReader fileReader = null;
		BufferedReader reader = null;
		try {
			fileReader =new FileReader("/proc/meminfo");
			reader  = new BufferedReader(fileReader);
			String lineOne = reader.readLine();
			char[] charArray = lineOne.toCharArray();
			StringBuilder builder = new StringBuilder();
			for (char c : charArray) {
				if(c >= '0' && c <= '9'){
					builder.append(c);
				}
			}
			return Long.parseLong(builder.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				reader.close();
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * @param context   上下文环境
	 * @return  返回当前手机正在运行进程相关信息的集合
	 */
	public static List<ProcessInfo> getProcessInfo(Context context){
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		//获取进程相关信息
		//1、获取ActivityManager对象和PackageManager对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		//2、获取正在运行进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//3、循环遍历上述集合，获取进程相关信息（名称、图标、包名、使用内存大小、是否为系统进程）
		for (RunningAppProcessInfo info : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			//4、获取进程名称==进程包名
			processInfo.setPackageName(info.processName);
			//5、获取进程占用内存大小
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			//6、返回数组中索引为0的对象，为当前进程的内存信息对象
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			//7、获取当前进程占用内存大小
			long totalPrivateDirty = memoryInfo.getTotalPrivateDirty()*1024;
			processInfo.setMemSize(totalPrivateDirty);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.getPackageName(), 0);
				//8、获取应用名称
				processInfo.setLable(applicationInfo.loadLabel(pm).toString());
				//9、获取应用图标
				processInfo.setIcon(applicationInfo.loadIcon(pm));
				//10、判断是否为系统进程
				if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
					processInfo.setSystem(true);
				}else{
					processInfo.setSystem(false);
				}
			} catch (NameNotFoundException e) {
				//异常处理
				processInfo.setLable(info.processName);
				processInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				processInfo.setSystem(true);
				e.printStackTrace();
			}
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}

	/**
	 * 杀死进程
	 * @param context  上下文环境
	 * @param packageName   进程包名
	 */
	public static void killProcess(Context context,ProcessInfo processInfo){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(processInfo.getPackageName());
	}
}
