package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity {
	private GestureDetector gestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	

	@Override
	protected void showPrePage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void showNextPage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		//开启平移动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.pre_out_anim);
	}
}
