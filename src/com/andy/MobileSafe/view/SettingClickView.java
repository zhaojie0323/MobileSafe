package com.andy.MobileSafe.view;

import com.andy.MobileSafe.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickView extends RelativeLayout {
	private TextView tv_des;
	private TextView tv_title;

	public SettingClickView(Context context) {
		this(context,null);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View.inflate(context, R.layout.setting_click_view, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
	}

	/**
	 * 设置标题
	 * @param title  标题内容
	 */
	public void setTitle(String title){
		tv_title.setText(title);
	}

	/**
	 * 设置描述
	 * @param des  描述内容
	 */
	public void setDescription(String des){
		tv_des.setText(des);
	}
}
