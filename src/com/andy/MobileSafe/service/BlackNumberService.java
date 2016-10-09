package com.andy.MobileSafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.andy.MobileSafe.db.dao.BlackNumberDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BlackNumberService extends Service {
	private static final String TAG = "BlackNumberService";
	private InnerSmsReceiver mInnerSmsReceiver;
	private BlackNumberDao mDao;
	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;
	@Override
	public void onCreate() {
		super.onCreate();
		mDao = BlackNumberDao.getInstance(this);

		//拦截短信
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);

		mInnerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(mInnerSmsReceiver, filter);

		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch(state){
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//挂断电话
				endCall(incomingNumber);
				break;
			}
		}
	}

	private void endCall(String phone) {
		int mode = mDao.getMode(phone);
		if(mode == 2 || mode == 3){
			//ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
			//ServiceManager此类对开发者隐藏，所以不能直接调用其方法，所以要用到反射调用
			try {
				//1、获取ServiceManager字节码文件
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				//2、获取方法
				Method method = clazz.getMethod("getService", String.class);
				//3、反射调用方法
				IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
				//4、调用获取aidl文件对象方法
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				//5、调用在aidl文件中隐藏的方法
				iTelephony.endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	class InnerSmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//获取短信内容及发送短信的电话号码，如果此电话号码在黑名单中，并且拦截模式为1或者3，则拦截短信
			//1、获取短信内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//2、循环便利短信
			for(Object object : objects){
				//3、获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				//4、获取短信对象的基本信息
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				Log.d(TAG,"originatingAddress: "+originatingAddress+","+"messageBody: "+messageBody);
				int mode = mDao.getMode(originatingAddress);
				Log.d(TAG,"mode: "+mode);
				if(mode == 1 || mode == 3){
					//拦截短信
					abortBroadcast();
					Log.d(TAG,"拦截短信");
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		if(mInnerSmsReceiver != null){
			unregisterReceiver(mInnerSmsReceiver);
		}
		super.onDestroy();
	}
}
