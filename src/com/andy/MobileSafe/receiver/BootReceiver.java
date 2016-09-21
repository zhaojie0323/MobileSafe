package com.andy.MobileSafe.receiver;

import com.andy.MobileSafe.activity.ConstantValue;
import com.andy.MobileSafe.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
			//1、手机重启后获取sim卡序列号
			TelephonyManager manager = (TelephonyManager) 
					context.getSystemService(Context.TELEPHONY_SERVICE);
			String simSerialNumber = manager.getSimSerialNumber()+"123";
			//2、从sp中获取sim卡序列号
			String sp_sim=SpUtil.getString(context, ConstantValue.SIM_NUM, "");
			//3、sim卡序列号进行比对,如果不相同则发送报警短信
			if(!sp_sim.equals(simSerialNumber)){
				SmsManager sms=SmsManager.getDefault();
				sms.sendTextMessage(SpUtil.getString(context, ConstantValue.CONTACT_PHONE, ""), null, "sim is different", null, null);
			}
	}

}
