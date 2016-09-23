package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.engine.AddressDao;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QuerryAddressActivity extends Activity {
	private static final String TAG="QuerryAddressActivity";
	private TextView tv_query_address_result;
	private EditText et_phone_number;
	private Button bt_query_phone_address;
	private String mAddress;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			//4、更新UI
			tv_query_address_result.setText(mAddress);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		initUI();
	}

	private void initUI() {
		et_phone_number = (EditText) findViewById(R.id.et_query_phone_number);
		tv_query_address_result = (TextView)findViewById(R.id.tv_query_address_result);
		bt_query_phone_address = (Button) findViewById(R.id.bt_query_phone_address);
		//1、点击查询功能，注册按钮点击事件
		bt_query_phone_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_phone_number.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					//2、耗时操作，开启子线程
					query(phone);
				}else{
					//输入为空，抖动动画
			        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
			        et_phone_number.startAnimation(shake);
			        //手机震动
			        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			        vibrator.vibrate(1000);
			        //vibrator.vibrate(new long[]{1000,2000,1000,5000},-1);
				}
			}
		});
		//5、实时查询（监听输入文本框的变化）
		tv_query_address_result.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String phone = et_phone_number.getText().toString();
				query(phone);
			}
		});
	}
	
	/**
	 * 耗时操作
	 * 查询电话号码的归属地
	 * @param phone  要查询的电话号码
	 */
	private void query(final String phone){
		new Thread(new Runnable() {
			@Override
			public void run() {
				mAddress = AddressDao.getAddress(getApplicationContext(), phone);
				if(mAddress != null){
					//3、消息机制，告诉主线程查询完毕，可以更新UI
					mHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}
}
