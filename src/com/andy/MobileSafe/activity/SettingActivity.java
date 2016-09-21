package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.view.SettingItemView;
import com.andy.MobileSafe.R;

import android.app.Activity;
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
	}

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
}
