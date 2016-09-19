package com.thunder.MobileSafe.activity;

import com.thunder.MobileSafe.R;
import com.thunder.MobileSafe.utils.SpUtil;
import com.thunder.MobileSafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		initUI();
	}
	private void initUI() {
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		//1、是否选中状态的回显
		boolean open_security=SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, false);
		cb_box.setChecked(open_security);
		if(open_security){
			cb_box.setText("防盗功能已开启");
		}else{
			cb_box.setText("防盗功能已关闭");
		}
		//2、监听CheckBox的变化状态
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					cb_box.setText("防盗功能已开启");
				}else{
					cb_box.setText("防盗功能已关闭");
				}
				//3存储CheckBox的选中状态到sp中
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
			}
		});
	}
	@Override
	protected void showPrePage() {
		// TODO Auto-generated method stub
		Intent intent=new Intent(this,Setup3Activity.class);
		startActivity(intent);
		finish();
		//开启平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.next_out_anim);
	}
	@Override
	protected void showNextPage() {
		// TODO Auto-generated method stub
		if(SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, false)){
			Intent intent = new Intent(this,SetupOverActivity.class);
			startActivity(intent);
			finish();
			SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);	
			//开启平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.pre_out_anim);
		}else{
			ToastUtil.show(getApplicationContext(), "请开启防盗功能");
		}
	}
}
