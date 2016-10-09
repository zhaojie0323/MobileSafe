package com.andy.MobileSafe.activity;

import java.util.List;
import com.andy.MobileSafe.R;
import com.andy.MobileSafe.db.dao.BlackNumberDao;
import com.andy.MobileSafe.db.domain.BlackNumberInfo;
import com.andy.MobileSafe.utils.ToastUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class BlackNumberActivity extends Activity {
	private BlackNumberDao mBlackNumberDao;
	private List<BlackNumberInfo> mBlackNumberInfoList;
	private ListView lv_blacknumber;
	private String mode = "1";
	private BlackNumberAdapter mAdapter;
	private boolean mIsload=false;
	private int mCount = 0;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(mAdapter == null){
				mAdapter = new BlackNumberAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			}else{
				mAdapter.notifyDataSetChanged();
			}
		};
	};

	class BlackNumberAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mBlackNumberInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		//对ListView进行优化
		//1、复用convertView
		//2、减少findViewById的次数、将findViewById的过程封装到convertView == null的情景中去，使用ViewHolder
		//3、将ViewHolder定义成静态，不会去创建多个对象
		//4、ListView有多个条目的时候，可以使用分页算法，每一次加载20个条目，逆序返回
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			//复用ViewHolder步骤一
			ViewHolder holder = null;
			if(convertView == null){
				convertView  = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
				//复用ViewHolder步骤三
				holder = new ViewHolder();
				//复用ViewHolder步骤四
				holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
				//复用ViewHolder步骤五
				convertView.setTag(holder);
			}else{
				//复用ViewHolder步骤六
				holder = (ViewHolder) convertView.getTag();
			}

			final String phone = mBlackNumberInfoList.get(position).getPhone();
			holder.tv_phone.setText(phone);
			String mode = mBlackNumberInfoList.get(position).getMode();
			switch(Integer.parseInt(mode)){
			case 1:
				holder.tv_mode.setText("拦截短信");
				break;
			case 2:
				holder.tv_mode.setText("拦截电话");
				break;
			case 3:
				holder.tv_mode.setText("拦截所有");
				break;
			}

			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//删除数据库
					mBlackNumberDao.delete(phone);
					//集合中删除
					mBlackNumberInfoList.remove(position);
					//更新数据适配器
					if(mAdapter != null){
						mAdapter.notifyDataSetChanged();
					}
				}
			});
			return convertView;
		}
	}
	//复用ViewHolder步骤二
	static class ViewHolder{
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);
		initUI();
		initData();
	}

	private void initData() {
		new Thread(){
			public void run() {
				mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
				mBlackNumberInfoList = mBlackNumberDao.find(0);
				mCount = mBlackNumberDao.getCount();
				//告知主线程数据已经准备好，可以使用
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		Button bt_add = (Button) findViewById(R.id.bt_add);
		bt_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		//监听其滚动状态
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			//滚动过程中状态发生改变调用
			//OnScrollListener.SCROLL_STATE_FLING  飞速滚动
			//OnScrollListener.SCROLL_STATE_IDLE   空闲状态
			//OnScrollListener.SCROLL_STATE_TOUCH_SCROLL   拿手触摸着滚动
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(mBlackNumberInfoList != null){
					//条件一：滚动到停止状态
					//条件二：最后一个条目可见（最后一个条目的索引值>=数据适配器中集合的总条目个数-1)
					//mIsload:防止重复加载的变量，如果当前正在加载，mIsload为true，本次加载完毕之后，再将mIsload改为false，然后加载下一页数据
					if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumber.getLastVisiblePosition() >= mBlackNumberInfoList.size()-1
							&& !mIsload){
						//如果条目的总数大于集合的总数时，再去加载数据
						if(mCount > mBlackNumberInfoList.size()){
							new Thread(){
								public void run() {
									mIsload =true;
									//1、获取操作黑名单数据库对象
									mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
									//2、查询部分数据
									List<BlackNumberInfo> moreData = mBlackNumberDao.find(mBlackNumberInfoList.size());
									//3、添加下一页数据
									mBlackNumberInfoList.addAll(moreData);
									//4、告知适配器刷新数据
									mHandler.sendEmptyMessage(0);
									mIsload = false;
								};
							}.start();
						}
					}
				}
			}
			//滚动过程中调用方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_summit = (Button) view.findViewById(R.id.bt_summit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.rb_sms:
					mode = "1";
					break;
				case R.id.rb_phone:
					mode = "2";
					break;
				case R.id.rb_all:
					mode = "3";
					break;
				}
			}
		});
		bt_summit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					//向数据库中插入数据
					mBlackNumberDao.insert(phone, mode);

					//使集合中的数据和数据库中的数据保持一致
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.setMode(mode);
					blackNumberInfo.setPhone(phone);
					mBlackNumberInfoList.add(0, blackNumberInfo);

					//通知数据适配器刷新
					if(mAdapter != null){
						mAdapter.notifyDataSetChanged();
					}

					dialog.dismiss();
				}else{
					ToastUtil.show(getApplicationContext(), "请输入拦截号码");
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
}
