package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.service.AddressService;
import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.view.SettingItemView;
import com.andy.MobileSafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initUpdate();
		initAddress();
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
		//获取已有的开关状态，用做显示
		boolean open_address = SpUtil.getBoolean(SettingActivity.this, ConstantValue.OPEN_ADDRESS, false);
		//是否选中，根据上次的结果决定
		siv_address.setCheck(open_address);
		siv_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//如果是选中状态，点击之后变成为未选中状态
				//如果是未选中状态，点击之后变成选中状态
				boolean isCheck=siv_address.isCheck();
				siv_address.setCheck(!isCheck);
				//将选择结果存储到sp中
				SpUtil.putBoolean(SettingActivity.this, ConstantValue.OPEN_ADDRESS, !isCheck);
				if(!isCheck){
					startService(new Intent(getApplicationContext(),AddressService.class));
				}else{
					stopService(new Intent(getApplicationContext(),AddressService.class));
				}
			}
		});
	}
}
