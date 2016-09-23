package com.andy.MobileSafe.service;

import com.andy.MobileSafe.utils.ToastUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AddressService extends Service {
	private static final String TAG = "AddressService";
	private TelephonyManager mTM;
	private PhoneStateListener mPhoneStateListener;
	
	@Override
	public void onCreate() {
		Log.d(TAG,"AddressService onCreate");
		// 第一次开启服务后，就需要去管理Toast显示
		//电话状态监听(服务开启的时候需要监听，服务关闭的时候不需要监听)
		//1、获取电话管理者对象
		 mTM= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		 mPhoneStateListener=new MyPhoneStateListener();
		 mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch(state){
			case TelephonyManager.CALL_STATE_IDLE:
				//空闲状态，没有任何活动（移除Toast）
				Log.d(TAG,"CALL_STATE_IDLE");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//响铃状态（展示Toast）
				showToast();
				Log.d(TAG,"CALL_STATE_RINGING");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//拨打或者通话状态
				Log.d(TAG,"CALL_STATE_OFFHOOK");
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

		private void showToast() {
			// TODO Auto-generated method stub
			ToastUtil.show(getApplicationContext(), "来电");
		}
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		// 取消对电话状态的监听
		if(mPhoneStateListener != null){
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);		
		}
		super.onDestroy();
	}

}
