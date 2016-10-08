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
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			mAdapter = new BlackNumberAdapter();
			lv_blacknumber.setAdapter(mAdapter);
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
		//2、减少findViewById的次数、将findViewById的过程封装到convertView == null的情景中去
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
	class ViewHolder{
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
				mBlackNumberInfoList = mBlackNumberDao.findAll();
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
