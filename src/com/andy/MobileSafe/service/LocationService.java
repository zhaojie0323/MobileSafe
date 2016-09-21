package com.andy.MobileSafe.service;

import com.andy.MobileSafe.activity.ConstantValue;
import com.andy.MobileSafe.utils.SpUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.gsm.SmsManager;

public class LocationService extends Service {

	@Override
	public IBinder onBind(Intent intent) { 
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//获取手机经纬度信息
		//1、获取位置管理者对象
		LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//2、以最优的方式获取手机经纬度坐标
		Criteria criteria=new Criteria();
		//允许花费
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//指定获取经纬度的经度
		String bestProvider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(bestProvider, 0, 0, new MyLocationListener());
		System.out.println("service start");
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			//发送短信
			SmsManager smsManager=SmsManager.getDefault();
			String contact_phone=SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
			smsManager.sendTextMessage(contact_phone, null, "latitude:"+latitude+","+"longitude:"+longitude, null, null);
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}}

}
