package com.thunder.MobileSafe.view;

import com.thunder.MobileSafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.thunder.MobileSafe";
	private CheckBox cb_box;
	private TextView tv_des;
	private String mDeson;
	private String mDesoff;
	private String mDestitle;

	//默认的三种构造方法
/*	public SettingItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}*/
	
	//为了能够使用最后一种构造方法，先修改如下
	
	public SettingItemView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// xml转换成View  将设置界面的一个条目转换成view对象，直接添加到了当前SettingItemView对应的view中
		View.inflate(context, R.layout.setting_item_view, this);
		/*
		 * 上面一行代码等同于下面两行代码
		View view=View.inflate(context, R.layout.setting_item_view, null);
		this.addView(view);*/
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox)findViewById(R.id.cb_box);
		//从AttributeSet attrs对象中获取自定义属性
		initAttrs(attrs);
		//将自定义属性的值复制给组合控件内的控件
		tv_title.setText(mDestitle);
	}
	
	/**
	 * @param attrs 构造方法中维护好的属性的集合
	 * 返回属性集合中自定义属性的值
	 */
	private void initAttrs(AttributeSet attrs) {
/*		System.out.println("attrs.getAttributeCount()"+attrs.getAttributeCount());
		for(int i=0;i<attrs.getAttributeCount();i++){
			System.out.println("name="+attrs.getAttributeName(i));
			System.out.println("value="+attrs.getAttributeValue(i));
		}*/
		//获取自定义属性的值
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
	}

	/**
	 * 判断是否开启的方法
	 * @return 返回当前SettingItemView是否选中状态  true开启  false关闭
	 */
	public boolean isCheck(){
		return cb_box.isChecked();
	}
	
	/**
	 * @param isCheck  
	 */
	public void setCheck(boolean isCheck){
		cb_box.setChecked(isCheck);
		
		if(isCheck){
			tv_des.setText(mDeson);
		}else{
			tv_des.setText(mDesoff);
		}

	}
}
