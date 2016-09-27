package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.service.AddressService;
import com.andy.MobileSafe.utils.ServiceUtil;
import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.view.SettingClickView;
import com.andy.MobileSafe.view.SettingItemView;
import com.andy.MobileSafe.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	private static final String TAG = "SettingActivity";
	private String[] mToastStyleDes;
	private int mToastStyle;
	private SettingClickView scv_toast_style;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initUpdate();
		initAddress();
		initToastStyle();
	}

	private void initToastStyle() {
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		scv_toast_style.setTitle("设置归属地显示风格");
		mToastStyleDes = new String[]{"灰色","橙色","黄色","紫色","绿色","蓝色"};
		mToastStyle = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
		Log.d(TAG,"mToastStyle = "+mToastStyle);
		//3、通过索引，获取字符串数组中的文字，显示给描述内容控件
		scv_toast_style.setDescription(mToastStyleDes[mToastStyle]);
		//4、监听点击事件，弹出对话框
		scv_toast_style.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showToastStyleDialog();
			}
		});
	}

	/**
	 * 创建设置颜色的对话框
	 */
	protected void showToastStyleDialog() {
		 Builder builder = new AlertDialog.Builder(this);
		 builder.setIcon(R.drawable.ic_launcher);
		 builder.setTitle("请选择归属地样式");
		 builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//1、记录选中颜色的索引 2、关闭对话框 3、显示选择的颜色
				SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
				dialog.dismiss();
				scv_toast_style.setDescription(mToastStyleDes[which]);
				//显示对话框时选中上次选择的item
				mToastStyle=which;
			}
		});
		 //取消事件
		 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		 builder.show();
	}

	/**
	 * 版本更新开关
	 */
	private void initUpdate() {
		final SettingItemView siv_update=(SettingItemView) findViewById(R.id.siv_update);
		//获取已有的开关状态，用做显示
		boolean open_update = SpUtil.getBoolean(SettingActivity.this, ConstantValue.OPEN_UPDATE, false);
		//是否选中，根据上次的结果决定
		siv_update.setCheck(open_update);
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//如果是选中状态，点击之后变成为未选中状态
				//如果是未选中状态，点击之后变成选中状态
				boolean isCheck=siv_update.isCheck();
				siv_update.setCheck(!isCheck);
				//将选择结果存储到sp中
				SpUtil.putBoolean(SettingActivity.this, ConstantValue.OPEN_UPDATE, !isCheck);
			}
		});
	}

	/**
	 * 是否显示电话号码归属地的方法
	 */
	private void initAddress(){
		final SettingItemView siv_address=(SettingItemView) findViewById(R.id.siv_address);
		//判断AddressService是否正在运行
		boolean isRunning = ServiceUtil.isRunning(this, "com.andy.MobileSafe.service.AddressService");
		//是否选中，根据isRunning结果决定
		siv_address.setCheck(isRunning);
		siv_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//点击过程中，状态（是否开启电话号码归属地）的切换过程
				boolean isCheck=siv_address.isCheck();
				siv_address.setCheck(!isCheck);
				if(!isCheck){
					startService(new Intent(getApplicationContext(),AddressService.class));
				}else{
					stopService(new Intent(getApplicationContext(),AddressService.class));
				}
			}
		});
	}
}
