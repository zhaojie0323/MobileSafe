package com.andy.MobileSafe.activity;

import java.util.ArrayList;
import java.util.List;
import com.andy.MobileSafe.R;
import com.andy.MobileSafe.db.domain.ProcessInfo;
import com.andy.MobileSafe.engine.ProcessInfoProvider;
import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.utils.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProcessManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "ProcessManagerActivity";
	private TextView tv_process_count;
	private TextView tv_memery_info;
	private ListView lv_process_list;
	private Button bt_all;
	private Button bt_reverse;
	private Button bt_clear;
	private Button bt_set;
	private List<ProcessInfo> mProcessInfoList;
	private List<ProcessInfo> mSystemList;
	private List<ProcessInfo> mCustomList;
	private TextView tv_des;
	private ProcessInfo mProcessInfo;
	private MyAdapter myAdapter;
	private int mProcessCount;
	private long mAvailableSpace;
	private long mTotalSpace;
	private String mStrTotalSpace;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			myAdapter = new MyAdapter();
			lv_process_list.setAdapter(myAdapter);

			if(tv_des != null && mCustomList != null){
				tv_des.setText("用户进程"+"("+mCustomList.size()+")");
			}
			if(tv_process_count != null){
				tv_process_count.setText("进程总数："+mProcessCount);
			}
			if(tv_memery_info != null){
				tv_memery_info.setText("剩余/总共："+mStrAvailableSpace+"/"+mStrTotalSpace);
			}
		};
	};
	private String mStrAvailableSpace;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		initUI();
		initTitleData();
		initListData();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getData();
		super.onResume();
	}

	private void initListData() {
		//getData();
	}

	private void initTitleData() {
		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		tv_process_count.setText("进程总数："+mProcessCount);

		mAvailableSpace = ProcessInfoProvider.getAvailableSpace(this);
		mStrAvailableSpace = Formatter.formatFileSize(this, mAvailableSpace);
		mTotalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, mTotalSpace);

		tv_memery_info.setText("剩余/总共："+mStrAvailableSpace+"/"+mStrTotalSpace);
	}

	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memery_info = (TextView) findViewById(R.id.tv_memery_info);
		lv_process_list = (ListView) findViewById(R.id.lv_process_list);
		bt_all = (Button) findViewById(R.id.bt_all);
		bt_reverse = (Button) findViewById(R.id.bt_reverse);
		bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_set = (Button) findViewById(R.id.bt_set);
		tv_des = (TextView) findViewById(R.id.tv_des);
		bt_all.setOnClickListener(this);
		bt_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_set.setOnClickListener(this);

		lv_process_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(mCustomList != null && mSystemList != null){
					if(firstVisibleItem <= mCustomList.size()){
						tv_des.setText("用户进程"+"("+mCustomList.size()+")");
					}else{
						tv_des.setText("系统进程"+"("+mSystemList.size()+")");
					}
				}
			}
		});

		lv_process_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0 || position == mCustomList.size() + 1){
					return;
				}else{
					if(position < mCustomList.size() + 1){
						mProcessInfo = mCustomList.get(position - 1);
					}else{
						mProcessInfo = mSystemList.get(position - mCustomList.size() - 2);
					}
					if(mProcessInfo != null){
						//选中条目指向的对象和本应用包名不一致，才需要去设置单选框状态
						if(!mProcessInfo.getPackageName().equals(getPackageName())){
							//状态取反
							mProcessInfo.setCheck(!mProcessInfo.isCheck());
							CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
							cb_box.setChecked(mProcessInfo.isCheck());
						}
					}
				}
			}
		});
	}

	class MyAdapter extends BaseAdapter{
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}
		@Override
		public int getItemViewType(int position) {
			//返回ListView条目的类型，0代表提示（系统进程或用户进程）条目，1代表内容条目
			if(position == 0 || position == mCustomList.size() + 1){
				return 0;
			}else{
				return 1;
			}
		}
		@Override
		public int getCount() {

			if(mCustomList != null && mSystemList != null){
				//根据是否显示系统进程返回不同的结果
				if(SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false)){
					return mCustomList.size() + mSystemList.size() + 2;
				}else{
					return mCustomList.size() + 1;
				}
			}
			return 0;
		}

		@Override
		public ProcessInfo getItem(int position) {
			if(position == 0 || position == mCustomList.size() + 1){
				return null;
			}else{
				if(position < mCustomList.size() + 1){
					//用户进程
					return mCustomList.get(position - 1);
				}else{
					//系统进程
					return mSystemList.get(position - mCustomList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if(type == 0){
				ViewTitleHolder viewTitleHolder = new ViewTitleHolder();
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item_title, null);
					viewTitleHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
					convertView.setTag(viewTitleHolder);
				}else{
					viewTitleHolder = (ViewTitleHolder) convertView.getTag();
				}
				if(position == 0){
					if(mCustomList != null){
						viewTitleHolder.tv_title.setText("用户进程"+"("+mCustomList.size()+")");
					}
				}else{
					if(mSystemList != null){
						viewTitleHolder.tv_title.setText("系统进程"+"("+mSystemList.size()+")");
					}
				}
				return convertView;
			}else{
				ViewHolder viewHolder = new ViewHolder();
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
					viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					viewHolder.tv_process_name = (TextView) convertView.findViewById(R.id.tv_process_name);
					viewHolder.tv_occupy_space = (TextView) convertView.findViewById(R.id.tv_occupy_space);
					viewHolder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder) convertView.getTag();
				}
				viewHolder.iv_icon.setBackgroundDrawable(getItem(position).getIcon());
				viewHolder.tv_process_name.setText(getItem(position).getLable());
				String formatMemSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).getMemSize());
				viewHolder.tv_occupy_space.setText("占用内存：" + formatMemSize);
				//本应用不能被选中，所以隐藏
				if(getItem(position).getPackageName().equals(getPackageName())){
					viewHolder.cb_box.setVisibility(View.GONE);
				}else{
					viewHolder.cb_box.setVisibility(View.VISIBLE);
				}
				viewHolder.cb_box.setChecked(getItem(position).isCheck());
				return convertView;
			}
		}
	}
	static class ViewHolder{
		public ImageView iv_icon;
		public TextView tv_process_name;
		public TextView tv_occupy_space;
		public CheckBox cb_box;
	}
	static class ViewTitleHolder{
		public TextView tv_title;
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bt_all:
			selectAll();
			break;
		case R.id.bt_reverse:
			selectReverse();
			break;
		case R.id.bt_clear:
			clear();
			break;
		case R.id.bt_set:
			setting();
			break;
		}
	}

	private void setting() {
		Intent intent = new Intent(this,ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(myAdapter != null){
			//myAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void clear() {
		//1、获取选中进程
		//2、创建一个需要杀死进程的集合
		List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
		for(ProcessInfo info : mCustomList){
			if(info.getPackageName().equals(getPackageName())){
				continue;
			}
			if(info.isCheck()){
				//4、记录要杀死的用户进程
				killProcessList.add(info);
			}
		}
		for(ProcessInfo info : mSystemList){
			if(info.isCheck()){
				//4、记录要杀死的系统进程
				killProcessList.add(info);
			}
		}
		//5、循环遍历killProcessList，然后移除mCustom和mSystem中的对象
		long totlaReleaseSpace = 0;
		for(ProcessInfo info : killProcessList){
			//6、判断当前进程在哪个集合中，然后从中移除
			if(mCustomList.contains(info)){
				mCustomList.remove(info);
			}
			if(mSystemList.contains(info)){
				mSystemList.remove(info);
			}
			//7、杀死记录在killProcessList中进程（需要权限）
			ProcessInfoProvider.killProcess(getApplicationContext(), info);
			//记录释放空间的大小
			totlaReleaseSpace += info.getMemSize();
		}
		//8、通知数据适配器进行刷新
		if(myAdapter != null){
			myAdapter.notifyDataSetChanged();
		}
		//9、进程总数的更新
		mProcessCount -= killProcessList.size();
		//10、更新可用剩余空间(可用空间大小+释放空间大小)
		mAvailableSpace += totlaReleaseSpace;
		mStrAvailableSpace = Formatter.formatFileSize(getApplicationContext(), mAvailableSpace);
		//11、更新进程总数和剩余空间大小
		tv_process_count.setText("进程总数："+mProcessCount);
		tv_memery_info.setText("剩余/总共："+mStrAvailableSpace+"/"+mStrTotalSpace);
		//12、Toast告知用户
		ToastUtil.show(getApplicationContext(), "杀死了"+killProcessList.size()+"个进程，释放了"
		+Formatter.formatFileSize(getApplicationContext(), totlaReleaseSpace)+"空间");
		//ToastUtil.show(getApplicationContext(), String.format("杀死了%d个进程，释放了%s的空间", killProcessList.size(), totlaReleaseSpace));
	}
	private void selectReverse() {
		//1、将所有集合中对象的isCheck都取反
		for(ProcessInfo info : mCustomList){
			if(info.getPackageName().equals(getPackageName())){
				continue;
			}
			info.setCheck(!info.isCheck());
		}
		for(ProcessInfo info : mSystemList){
			info.setCheck(!info.isCheck());
		}
		//2、通知数据适配器进行刷新
		if(myAdapter != null){
			myAdapter.notifyDataSetChanged();
		}
	}
	private void selectAll() {
		//1、将所有集合中对象的isCheck都设置为true
		for(ProcessInfo info : mCustomList){
			if(info.getPackageName().equals(getPackageName())){
				continue;
			}
			info.setCheck(true);
		}
		for(ProcessInfo info : mSystemList){
			info.setCheck(true);
		}
		//2、通知数据适配器进行刷新
		if(myAdapter != null){
			myAdapter.notifyDataSetChanged();
		}
	}
	private void getData(){
		new Thread(){
			public void run() {
				mSystemList = new ArrayList<ProcessInfo>();
				mCustomList = new ArrayList<ProcessInfo>();
				mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
				for (ProcessInfo info : mProcessInfoList) {
					if(info.isSystem()){
						mSystemList.add(info);
					}else{
						mCustomList.add(info);
					}
				}
				//锁屏清理后，从新获取进程总数和剩余空间大小
				mProcessCount = ProcessInfoProvider.getProcessCount(getApplicationContext());
				mAvailableSpace = ProcessInfoProvider.getAvailableSpace(getApplicationContext());
				mStrAvailableSpace = Formatter.formatFileSize(getApplicationContext(), mAvailableSpace);
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}
