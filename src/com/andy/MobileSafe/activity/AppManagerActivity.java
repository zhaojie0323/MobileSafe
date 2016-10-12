package com.andy.MobileSafe.activity;


import java.util.ArrayList;
import java.util.List;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.db.domain.AppInfo;
import com.andy.MobileSafe.engine.AppInfoProvider;
import com.andy.MobileSafe.utils.ToastUtil;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends Activity implements OnClickListener{
	private static final String TAG = "AppManagerActivity";
	private List<AppInfo> mAppInfoList;
	private ListView lv_app_list;
	private TextView tv_des;
	private List<AppInfo> mCustomList;
	private List<AppInfo> mSystemList;
	private AppInfo mAppInfo;
	private PopupWindow mPopupWindow;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			MyAdapter adapter = new MyAdapter();
			lv_app_list.setAdapter(adapter);

			if(tv_des != null && mCustomList != null){
				tv_des.setText("用户应用"+"("+mCustomList.size()+")");
			}
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
	@Override
	protected void onResume() {
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
		super.onResume();
	}

	private void initList() {
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		tv_des = (TextView) findViewById(R.id.tv_des);
		lv_app_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
			//滚动过程中调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//firstVisibleItem 第一个可见条目
				//visibleItemCount 当前一个屏幕的可见条目
				//totalItemCount 条目总数
				if(mCustomList != null && mSystemList != null){
					if(firstVisibleItem >= mCustomList.size()+1){
						//滚动到系统应用
						tv_des.setText("系统应用"+"("+mSystemList.size()+")");
					}else{
						//滚动到用户应用
						tv_des.setText("用户应用"+"("+mCustomList.size()+")");
					}
				}
			}
		});

		lv_app_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0 || position == mCustomList.size()+1){
					//灰色纯文本条目
					return ;
				}else{
					if(position < mCustomList.size()+1){
						 mAppInfo = mCustomList.get(position - 1);
					}else{
						mAppInfo = mSystemList.get(position - mCustomList.size() - 2);
					}
					showPopupWindow(view);
				}
			}
		});
	}

	protected void showPopupWindow(View view) {
		View popupView = View.inflate(this, R.layout.popupwindow_layout, null);
		LinearLayout ll_uninstall = (LinearLayout) popupView.findViewById(R.id.ll_uninstall);
		LinearLayout ll_start = (LinearLayout) popupView.findViewById(R.id.ll_start);
		LinearLayout ll_share = (LinearLayout) popupView.findViewById(R.id.ll_share);
		ll_uninstall.setOnClickListener(this);
		ll_start.setOnClickListener(this);
		ll_share.setOnClickListener(this);
		mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		//2、设置一个透明背景
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		//3、指定窗体位置
		mPopupWindow.showAsDropDown(view, 200,-view.getHeight());
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

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ll_uninstall:
			if(mAppInfo.isSystem()){
				ToastUtil.show(getApplicationContext(), "系统应用不能卸载");
			}else{
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+mAppInfo.getPackageName()));
				startActivity(intent);
			}
			break;
		case R.id.ll_start:
			//通过桌面去启动指定包名应用
			PackageManager pm = getPackageManager();
			//通过Launcher开启指定包名意图，去开启应用
			Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
			if(launchIntentForPackage != null){
				startActivity(launchIntentForPackage);
			}else{
				ToastUtil.show(getApplicationContext(), "此应用不能开启");
			}
			break;
		case R.id.ll_share:
			//同过短信应用，向外发送短信
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用，应用名称为"+mAppInfo.getLabel());
			intent.setType("text/plain");
			startActivity(intent);
			break;
		}
		//点击后窗体消失
		if(mPopupWindow != null){
			mPopupWindow.dismiss();
		}
	}
}
