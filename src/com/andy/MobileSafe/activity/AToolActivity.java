package com.andy.MobileSafe.activity;

import java.io.File;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.engine.SmsBackup;
import com.andy.MobileSafe.engine.SmsBackup.CallBack;
import com.andy.MobileSafe.utils.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AToolActivity extends Activity {
	private TextView tv_query_phone_address;
	private TextView tv_sms_backup;
	private TextView tv_commonnumber_query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		//查询归属地
		initQueryAddress();
		//备份短信
		initSmsBackup();
		//常用号码查询
		initCommonNumber();
	}

	private void initCommonNumber() {
		tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
		tv_commonnumber_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),CommonNumberActivity.class));
			}
		});
	}

	private void initSmsBackup() {
		tv_sms_backup=(TextView)findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSmsBakckupDialog();
			}
		});
	}

	protected void showSmsBakckupDialog() {
		//1、创建一个带进度条的对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		//2、设置icon
		progressDialog.setIcon(R.drawable.ic_launcher);
		//3、设置标题
		progressDialog.setTitle("短信备份");
		//4、设置进度条样式
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//5、显示进度条对话框
		progressDialog.show();
		//6、直接调用备份短信的方法
		new Thread(){
			public void run() {
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"smsbackup.xml";
					//SmsBackup.backup(getApplicationContext(), path, progressDialog);
					SmsBackup.backup(getApplicationContext(), path, new CallBack() {
						@Override
						public void setProgress(int value) {
							progressDialog.setProgress(value);
						}
						@Override
						public void setMax(int max) {
							progressDialog.setMax(max);
						}
					});
					progressDialog.dismiss();
				}else{
					ToastUtil.show(getApplicationContext(), "SD卡不存在");
				}
			};
		}.start();
	}

	private void initQueryAddress() {
		tv_query_phone_address=(TextView)findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),QuerryAddressActivity.class));
			}
		});
	}
}
