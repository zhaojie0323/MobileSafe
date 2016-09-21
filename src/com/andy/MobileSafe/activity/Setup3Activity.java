package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.utils.SpUtil;
import com.andy.MobileSafe.utils.ToastUtil;
import com.andy.MobileSafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {
private EditText et_phone_number;
private Button bt_select_number;

@Override
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		initUI();
	}

	private void initUI() {
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		//回显电话号码（从sp中获取）
		et_phone_number.setText(SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, ""));
		bt_select_number = (Button) findViewById(R.id.bt_select_number);
		bt_select_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),ContactListActivity.class);
				startActivityForResult(intent, 0);
			}
		});
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			if(data!=null){
				//1、获取联系人电话
				String phone = data.getStringExtra("phone");
				//2、过滤特殊字符
				phone=phone.replace("-", "").replace(" ","").trim();
				et_phone_number.setText(phone);
				//3、存储联系人到sp中
				SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
			}
			
			super.onActivityResult(requestCode, resultCode, data);
		}


	@Override
	protected void showPrePage() {
		Intent intent=new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		//开启平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.next_out_anim);
	}

	@Override
	protected void showNextPage() {
		String phone=et_phone_number.getText().toString();
		if(!TextUtils.isEmpty(phone)){
			Intent intent = new Intent(this,Setup4Activity.class);
			startActivity(intent);
			finish();
			//如果是手动输入的，将其存在sp中
			SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
			//开启平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.pre_out_anim);
		}else{
			ToastUtil.show(getApplicationContext(), "请输入电话号码");
		}
	}
}
