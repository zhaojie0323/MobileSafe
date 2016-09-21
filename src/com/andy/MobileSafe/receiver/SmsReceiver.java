package com.andy.MobileSafe.receiver;

import com.andy.MobileSafe.activity.ConstantValue;
import com.andy.MobileSafe.service.LocationService;
import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.R;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.sax.StartElementListener;
import android.telephony.gsm.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("SMS RECEIVER");
		mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(context,DeviceAdmin.class);
		//1、判断是否开启了防盗保护
		boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
		if(open_security){
			//2、获取短信内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//3、循环遍历短信
			for (Object object : objects) {
				//4获取短信对象
				//SmsMessage smsMessage=SmsMessage.createFromPdu((bype[])object);
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])object);
				//5、获取短信对象的基本信息
				String originatingAddress = smsMessage.getOriginatingAddress();
				String messageBody = smsMessage.getMessageBody();
				System.out.println("messageBody:"+messageBody);
				//6、判断是否有包含播放音乐的关键字
				if(messageBody.contains("#*alarm*#")){
					//7、播放报警音乐
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bjyy);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
				}
				if(messageBody.contains("#*location*#")){
					//8、开启获取经纬度信息的服务
					Intent intent2 = new Intent(context,LocationService.class);
					context.startService(intent2);	
				}
				if(messageBody.contains("#*lockscreen*#")){
					if(mDPM.isAdminActive(mDeviceAdminSample)){
						mDPM.lockNow();
					}
				}
				if(messageBody.contains("#*wipedata*#")){
					if(mDPM.isAdminActive(mDeviceAdminSample)){
						mDPM.wipeData(0);
					}
				}
				
				
			}
			
			
		}
	}

}
