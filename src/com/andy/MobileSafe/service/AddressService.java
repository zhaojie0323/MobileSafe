package com.andy.MobileSafe.service;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.activity.ConstantValue;
import com.andy.MobileSafe.engine.AddressDao;
import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.utils.ToastUtil;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class AddressService extends Service {
	private static final String TAG = "AddressService";
	private TelephonyManager mTM;
	private PhoneStateListener mPhoneStateListener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams()                                       ;
	private WindowManager mWM;
	private View mToastView;
	private String mAddress;
	private TextView tv_toast;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	
	@Override
	public void onCreate() {
		Log.d(TAG,"AddressService onCreate");
		// 第一次开启服务后，就需要去管理Toast显示
		//电话状态监听(服务开启的时候需要监听，服务关闭的时候不需要监听)
		//1、获取电话管理者对象
		 mTM= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		 mPhoneStateListener=new MyPhoneStateListener();
		 mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		 mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		super.onCreate();
	}
	
	class MyPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch(state){
			case TelephonyManager.CALL_STATE_IDLE:
				//空闲状态，没有任何活动（移除Toast）
				Log.d(TAG,"CALL_STATE_IDLE");
				//空闲状态，移除mToastView
				if(mWM != null && mToastView != null){
					mWM.removeView(mToastView);
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//响铃状态（展示Toast）
				showToast(incomingNumber);
				Log.d(TAG,"CALL_STATE_RINGING");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//拨打或者通话状态
				Log.d(TAG,"CALL_STATE_OFFHOOK");
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private void showToast(String incomingNumber) {
		final WindowManager.LayoutParams parms=mParams;
		parms.height = WindowManager.LayoutParams.WRAP_CONTENT;
		parms.width = WindowManager.LayoutParams.WRAP_CONTENT;
		parms.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		parms.format = PixelFormat.TRANSLUCENT;
		//在响铃的时候显示Toast,和电话类型一致。
		parms.type = WindowManager.LayoutParams.TYPE_PHONE;
		parms.setTitle("Toast");
		//指定Toast所在位置
		parms.gravity = Gravity.LEFT + Gravity.TOP;
		mToastView = View.inflate(getApplicationContext(), R.layout.view_toast, null);
		tv_toast = (TextView) mToastView.findViewById(R.id.tv_toast);
		//从sp中获取颜色索引设置Toast背景颜色
		int[] drawableID = new int[]{R.drawable.call_location_gray,
				R.drawable.call_location_orange,
				R.drawable.call_location_yellow,
				R.drawable.call_location_purple,
				R.drawable.call_location_green,
				R.drawable.call_location_blue};
		int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
		tv_toast.setBackgroundResource(drawableID[toastStyleIndex]);
		//在窗体上挂在一个view(需要权限)
		mWM.addView(mToastView, mParams);
		queryAddress(incomingNumber);
	}
	/**
	 * 查询归属地
	 * @param incomingNumber  查询号码
	 */
	private void queryAddress(final String incomingNumber) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mAddress = AddressDao.getAddress(getApplicationContext(), incomingNumber);
				mHandler.sendEmptyMessage(0);
			}
		}).start();
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
