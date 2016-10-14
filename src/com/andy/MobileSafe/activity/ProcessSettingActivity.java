package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.service.LockScreenService;
import com.andy.MobileSafe.utils.ServiceUtil;
import com.andy.MobileSafe.utils.SpUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		initSystemShow();
		initLockScreenClear();
	}

	private void initLockScreenClear() {
		final CheckBox cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.andy.MobileSafe.service.LockScreenService");
		if(isRunning){
			cb_lock_clear.setText("锁屏清理已开启");
		}else{
			cb_lock_clear.setText("锁屏清理已关闭");
		}
		cb_lock_clear.setChecked(isRunning);

		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cb_lock_clear.setText("锁屏清理已开启");
					startService(new Intent(getApplicationContext(),LockScreenService.class));
				}else{
					cb_lock_clear.setText("锁屏清理已关闭");
					stopService(new Intent(getApplicationContext(),LockScreenService.class));
				}
			}
		});
	}

	private void initSystemShow() {
		final CheckBox cb_showsystem = (CheckBox) findViewById(R.id.cb_showsystem);

		boolean isShowSystem = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false);
		cb_showsystem.setChecked(isShowSystem);
		if(isShowSystem){
			cb_showsystem.setText("显示系统进程");
		}else{
			cb_showsystem.setText("隐藏系统进程");
		}

		cb_showsystem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cb_showsystem.setText("显示系统进程");
				}else{
					cb_showsystem.setText("隐藏系统进程");
				}
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, isChecked);
			}
		});
	}
}
