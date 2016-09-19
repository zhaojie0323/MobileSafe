package com.thunder.MobileSafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector gestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//2、创建手势管理对象，用作管理在onTouchEvent(MotionEvent event)中传递过来的手势
				gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						// 监听手势的移动
						if(e1.getX()-e2.getX()>0){
							//由右向左移动，移动到下一页,调用子类下一页的方法，抽象方法
							showNextPage();

						}
						if(e1.getX()-e2.getX()<0){
							//由左向右移动，移动到上一页，调用子类上一页的方法，抽象方法
							showPrePage();
						}
						
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	//1、监听屏幕上响应的事件类型（按下，移动，抬起）
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 3、通过手势处理类，接收多种类型的事件，用作处理
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	abstract protected void showPrePage();
	abstract protected void showNextPage();
	//上一页点击事件
	public void prePage(View view){
		showPrePage();
	}
	//下一页点击事件
	public void nextPage(View view){
		showNextPage();
	}
}
