package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetupOverActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		boolean setup_over=SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if(setup_over){
			//密码设置成功，并且四个导航界面设置完成，停留在设置完成功能列表界面
			setContentView(R.layout.activity_setup_over);
			initUI();
		}else{
			//密码输入成功，四个导航界面没有设置完成，跳转到第一个导航设置界面
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			//开启新的导航设置界面后，关闭功能列表界面
			finish();
		}
	}

	private void initUI() {
		// TODO Auto-generated method stub
		TextView tv_phone=(TextView) findViewById(R.id.tv_phone);
		//设置联系人号码
		String phone=SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
		tv_phone.setText(phone);
		TextView tv_reset_setup=(TextView) findViewById(R.id.tv_reset_setup);
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
	}

}
