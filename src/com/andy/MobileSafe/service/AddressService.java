package com.andy.MobileSafe.service;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.activity.ConstantValue;
import com.andy.MobileSafe.engine.AddressDao;
import com.andy.MobileSafe.utils.SpUtil;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
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
	private int mScreenWidthPixels;
	private int mScreenHeightPixels;
	private InnerOutCallReceiver mInnerOutCallReceiver;
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
		 //获取屏幕宽高
		 DisplayMetrics dm = new DisplayMetrics();
		 mWM.getDefaultDisplay().getMetrics(dm);
		 mScreenWidthPixels = dm.widthPixels;
		 mScreenHeightPixels = dm.heightPixels;
		 //和以下方式得到的结果一样
		 //int wid = mWM.getDefaultDisplay().getWidth();
		 //int hei = mWM.getDefaultDisplay().getHeight();
		 //Log.d(TAG,"wid = "+wid+","+"hei = "+hei);
		 Log.d(TAG,"mScreenWidthPixels = "+mScreenWidthPixels+","+"mScreenHeightPixels = "+mScreenHeightPixels);

		 //监听播出电话的过滤条件
		 IntentFilter intentFilter = new IntentFilter();
		 intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		 mInnerOutCallReceiver = new InnerOutCallReceiver();
		 //注册广播
		 registerReceiver(mInnerOutCallReceiver, intentFilter);
		super.onCreate();
	}
	
	/**
	 * 监听拨出电话的广播接收器
	 */
	class InnerOutCallReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//接受到拨出电话的广播后，显示自定义的Toast，显示电话号码归属地
			String phone = getResultData();
			Log.d(TAG,"Out call number is "+phone);
			showToast(phone);
		}
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
		//指定Toast所在位置(左上角)
		parms.gravity = Gravity.LEFT + Gravity.TOP;
		mToastView = View.inflate(getApplicationContext(), R.layout.view_toast, null);
		tv_toast = (TextView) mToastView.findViewById(R.id.tv_toast);

		mToastView.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;
			@Override
			//监听按下  移动  抬起事件
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					//手按下时相对于屏幕左上角（原点）的距离
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					//手指移动后相对于屏幕左上角（原点）的距离
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					//相对于按下位置的偏移量
					int offsetX = moveX - startX;
					int offsetY = moveY - startY;
					parms.x = parms.x + offsetX;
					parms.y = parms.y + offsetY;

					//容错处理（tv_toast不能托拽出屏幕）
					if(parms.x < 0){
						parms.x = 0;
					}
					if(parms.x > mScreenWidthPixels - tv_toast.getWidth()){
						parms.x = mScreenWidthPixels - tv_toast.getWidth();
					}
					if(parms.y < 0){
						parms.y = 0;
					}
					if(parms.y > mScreenHeightPixels - tv_toast.getHeight() - 30){
						parms.y = mScreenHeightPixels - tv_toast.getHeight() - 30;
					}

					//告知窗体Toast需要按照手势移动去做更新
					mWM.updateViewLayout(mToastView, parms);
					//3、重置起始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, parms.x);
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, parms.y);
					break;
				}
				//由于只有托拽事件，因此在此返回true
				return true;
			}
		});

		//读取sp中存储的位置x,y坐标
		//parms.x为Toast左上角x轴的坐标，parms.y为Toast左上角y轴的坐标
		parms.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
		parms.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

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
		mWM.addView(mToastView, parms);
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
		if(mInnerOutCallReceiver != null){
			unregisterReceiver(mInnerOutCallReceiver);
		}
		super.onDestroy();
	}

}
