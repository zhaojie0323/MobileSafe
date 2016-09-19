package com.thunder.MobileSafe.activity;

import com.thunder.MobileSafe.R;
import com.thunder.MobileSafe.utils.Md5Util;
import com.thunder.MobileSafe.utils.SpUtil;
import com.thunder.MobileSafe.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private String[] mTitleStrs;
	private GridView gv_home;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
		initData();
		
		System.out.println("HomeActivity");
	}

	private void initData() {
		mTitleStrs = new String[]{"手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
		mDrawableIds = new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,
				R.drawable.home_apps,R.drawable.home_taskmamage,
				R.drawable.home_netmanager,R.drawable.home_trojan,
				R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_setting};
		//九宫格控件设置数据适配器
		gv_home.setAdapter(new MyAdapter());
		//注册九宫格条目点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch(position){
				case 0:
					showDialog();
					break;
				case 8:
					Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	protected void showDialog() {
		// 判断本地是否存有密码
		String pwd=SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PWD, "");
		if(TextUtils.isEmpty(pwd)){
			//1、设置初始密码对话框
			showSettingPwdDialog();
		}else{
			//2、确认密码对话框
			showConfrimPwdDialog();
		}
	}

	/**
	 * 设置初始密码对话框
	 */
	private void showSettingPwdDialog() {
		// 需要自己定义对话框的展示样式，所以需要调用dialog.setView(view)
		//view是由自己编写的xml转换成的view对象
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view=View.inflate(this, R.layout.set_pwd_dialog, null);
		//dialog.setView(view);
		//为了兼容低版本，给对话框设置布局的时候，让其没有内边距
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		//给button注册监听事件
		Button bt_commit = (Button) view.findViewById(R.id.bt_commit);
		Button bt_cancel=(Button) view.findViewById(R.id.bt_cancel);
		bt_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText et_set_pwd=(EditText) view.findViewById(R.id.et_set_pwd);
				EditText et_confirm_pwd=(EditText) view.findViewById(R.id.et_confirm_pwd);
				String set_pwd=et_set_pwd.getText().toString();
				String confirm_pwd=et_confirm_pwd.getText().toString();
				if(!TextUtils.isEmpty(set_pwd)&&!TextUtils.isEmpty(confirm_pwd)){
					if(set_pwd.equals(confirm_pwd)){
						//进入手机应用防盗模块，开启一个新的activity
						Intent intent=new Intent(HomeActivity.this,SetupOverActivity.class);
						startActivity(intent);
						SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PWD, Md5Util.encoder(set_pwd));
						dialog.dismiss();
					}else{
						ToastUtil.show(getApplicationContext(), "密码不一致，请重新输入");
					}
				}else{
					ToastUtil.show(getApplicationContext(), "密码不能为空");
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
	}

	/**
	 * 确认密码对话框
	 */
	private void showConfrimPwdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view=View.inflate(this, R.layout.confirm_pwd_dialog, null);
		//dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		//给button注册监听事件
		Button bt_commit = (Button) view.findViewById(R.id.bt_commit);
		Button bt_cancel=(Button) view.findViewById(R.id.bt_cancel);
		bt_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText et_confirm_pwd=(EditText) view.findViewById(R.id.et_confirm_pwd);
				String confirm_pwd=et_confirm_pwd.getText().toString();
				if(!TextUtils.isEmpty(confirm_pwd)){
					String sp_pwd=SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PWD, "");
					if(sp_pwd.equals(Md5Util.encoder(confirm_pwd))){
						//进入手机应用防盗模块，开启一个新的activity
						Intent intent=new Intent(HomeActivity.this,SetupOverActivity.class);
						startActivity(intent);
						dialog.dismiss();
					}else{
						ToastUtil.show(getApplicationContext(), "密码有误，请重新输入");
					}
				}else{
					ToastUtil.show(getApplicationContext(), "密码不能为空");
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
	}
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=View.inflate(HomeActivity.this, R.layout.girdview_item, null);
			ImageView iv_icon=(ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_title=(TextView) view.findViewById(R.id.tv_title);
			iv_icon.setBackgroundResource(mDrawableIds[position]);
			tv_title.setText(mTitleStrs[position]);
			return view;
		}
		
	}


}
