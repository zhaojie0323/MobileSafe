package com.andy.MobileSafe.activity;


import java.util.ArrayList;
import java.util.List;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.db.domain.AppInfo;
import com.andy.MobileSafe.engine.AppInfoProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AppManagerActivity extends Activity {
	private static final String TAG = "AppManagerActivity";
	private List<AppInfo> mAppInfoList;
	private ListView lv_app_list;
	private List<AppInfo> mCustomList;
	private List<AppInfo> mSystemList;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			MyAdapter adapter = new MyAdapter();
			lv_app_list.setAdapter(adapter);
		};
	};
	class MyAdapter extends BaseAdapter{
		//获取数据适配器中条目类型的总数，修改成两种（纯文本，文字+图片）
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}
		//指定索引指向的条目类型，条目类型状态码指定，（0（系统复用），1）
		@Override
		public int getItemViewType(int position) {
			if(position == 0 || position == mCustomList.size() + 1){
				//返回0，代表纯文本条目的状态码
				return 0;
			}else{
				//返回1，代表图片+文字的状态码
				return 1;
			}
		}
		@Override
		public int getCount() {
			return mCustomList.size() + mSystemList.size();
		}

		@Override
		public AppInfo getItem(int position) {
			//先展示用户应用，后展示系统应用
			if(position == 0 || position == mCustomList.size()+1){
				//灰色纯文本条目
				return null;
			}else{
				if(position < mCustomList.size()+1){
					return mCustomList.get(position - 1);
				}else{
					return mSystemList.get(position - mCustomList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if(0 == type){
				//展示灰色纯文本条目
				ViewTitleHolder viewTitleHolder = new ViewTitleHolder();
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
					viewTitleHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
					convertView.setTag(viewTitleHolder);
				}else{
					viewTitleHolder = (ViewTitleHolder) convertView.getTag();
				}
				if(position == 0){
					viewTitleHolder.tv_title.setText("用户应用"+"("+mCustomList.size()+")");
				}else{
					viewTitleHolder.tv_title.setText("系统应用"+"("+mSystemList.size()+")");
				}
				return convertView;
			}else{
				//展示图片+文字条目
				ViewHolder viewHolder = new ViewHolder();
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
					viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					viewHolder.tv_package_name = (TextView) convertView.findViewById(R.id.tv_package_name);
					viewHolder.tv_install_path = (TextView) convertView.findViewById(R.id.tv_install_path);
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder) convertView.getTag();
				}
				viewHolder.iv_icon.setBackground(getItem(position).getIcon());
				viewHolder.tv_package_name.setText(getItem(position).getPackageName());
				if(getItem(position).isSdCard()){
					viewHolder.tv_install_path.setText("sd卡应用");
				}else{
					viewHolder.tv_install_path.setText("手机应用");
				}
				return convertView;
			}
		}
	}

	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_package_name;
		TextView tv_install_path;
	}
	static class ViewTitleHolder{
		TextView tv_title;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		initTitle();
		initList();
	}

	private void initList() {
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		new Thread(new Runnable() {
			@Override
			public void run() {
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				mCustomList = new ArrayList<AppInfo>();
				mSystemList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : mAppInfoList) {
					if(appInfo.isSystem()){
						//系统应用
						mSystemList.add(appInfo);
					}else{
						//用户应用
						mCustomList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	private void initTitle() {
		//1、获取磁盘（内存）可用大小
		 String path = Environment.getDataDirectory().getAbsolutePath();
		//2、获取sd卡可用大小
		 String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		//3、获取以上两个路径下文件夹的可用空间
		 String memoryAvailSpace = Formatter.formatFileSize(this, getAvailableSpace(path));
		 String sdMemoryAvailSpace = Formatter.formatFileSize(this, getAvailableSpace(sdPath));

		 TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
		 TextView tv_sd = (TextView) findViewById(R.id.tv_sd);
		 tv_memory.setText("磁盘可用："+memoryAvailSpace);
		 tv_sd.setText("sd卡可用："+sdMemoryAvailSpace);
	}
	private long getAvailableSpace(String path){
		StatFs statFs = new StatFs(path);
		//获取可用区块的个数
		long count = statFs.getAvailableBlocks();
		//获取区块的大小
		long size = statFs.getBlockSize();
		//区块大小*可用区块=可用空间大小
		return size*count;
	}
}
