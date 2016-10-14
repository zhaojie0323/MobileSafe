package com.andy.MobileSafe.service;

import com.andy.MobileSafe.engine.ProcessInfoProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockScreenService extends Service {
	private static final String TAG = "LockScreenService";
	private InnerReceiver innerReceiver;
	@Override
	public void onCreate() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, filter);
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		if(innerReceiver != null){
			unregisterReceiver(innerReceiver);
		}
		super.onDestroy();
	}
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG,"Lock Screen");
			ProcessInfoProvider.killAll(context);
		}
	}
}
