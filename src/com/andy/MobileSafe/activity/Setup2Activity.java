package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.utils.ToastUtil;
import com.andy.MobileSafe.view.SettingItemView;
import com.andy.MobileSafe.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_sim_bound;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		initUI();
	}
	
	private void initUI() {
		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
		//1、回显（从sp中读取已有的绑定状态，sp中是否存储的sim卡的序列号）
		String sim_num=SpUtil.getString(this, ConstantValue.SIM_NUM, "");
		//2、判断sim卡序列号是否为空
		if(TextUtils.isEmpty(sim_num)){
			//未绑定sim卡
			siv_sim_bound.setCheck(false);
		}else{
			//已绑定sim卡
			siv_sim_bound.setCheck(true);
		}
		
		siv_sim_bound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//3、获取原有状态
				boolean isCheck=siv_sim_bound.isCheck();
				//4、将原有状态取反
				siv_sim_bound.setCheck(!isCheck);
				if(!isCheck){
					//5在sp中存储sim卡序列号
					TelephonyManager manager = (TelephonyManager) 
							getSystemService(Context.TELEPHONY_SERVICE);
					String simSerialNumber = manager.getSimSerialNumber();
					SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUM, simSerialNumber);
					//System.out.println("simSerialNumber:"+simSerialNumber);
					
				}else{
					//在sp中清除sim卡序列号
					SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUM);
				}
			}
		});
	}

	@Override
	protected void showPrePage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();	
		//开启平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.next_out_anim);
	}

	@Override
	protected void showNextPage() {
		// TODO Auto-generated method stub
		if(TextUtils.isEmpty(SpUtil.getString(this, ConstantValue.SIM_NUM, ""))){
			ToastUtil.show(this, "请绑定sim卡");
		}else{
			Intent intent = new Intent(this,Setup3Activity.class);
			startActivity(intent);
			finish();
			
			//开启平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.pre_out_anim);
		}
	}
}
