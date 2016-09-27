package com.andy.MobileSafe.activity;

import com.andy.MobileSafe.R;
import com.andy.MobileSafe.utils.SpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ToastLocationActivity extends Activity {
	private static final String TAG = "ToastLocationActivity";
	private Button bt_drag;
	private Button bt_top;
	private Button bt_bottom;
	private int mScreenWidthPixels;
	private int mScreenHeightPixels;
	private long[] mHits = new long[2];//记录点击次数（2 双击）数组
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		initUI();
	}

	private void initUI() {
		bt_drag = (Button) findViewById(R.id.bt_drag);
		bt_top = (Button) findViewById(R.id.bt_top);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);
		//获取屏幕宽高
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidthPixels = dm.widthPixels;
		mScreenHeightPixels = dm.heightPixels;
		Log.d(TAG,"mScreenWidthPixels = "+mScreenWidthPixels+","+"mScreenHeightPixels = "+mScreenHeightPixels);
		int locationX = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, bt_drag.getLeft());
		int locationY = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, bt_drag.getTop());
		//左上角坐标作用在bt_drag上
		//bt_drag在相对布局中，所以其所在位置的规则需要由相对布局提供
		//指定宽高都为wrap_content
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		//指定相对于屏幕的坐标位置
		layoutParams.leftMargin = locationX;
		layoutParams.topMargin = locationY;
		//将以上规则作用在bt_drag上
		bt_drag.setLayoutParams(layoutParams);
		if(layoutParams.topMargin < (mScreenHeightPixels - 50)/2){
			bt_top.setVisibility(View.INVISIBLE);
			bt_bottom.setVisibility(View.VISIBLE);
		}else{
			bt_top.setVisibility(View.VISIBLE);
			bt_bottom.setVisibility(View.INVISIBLE);
		}

		bt_drag.setOnTouchListener(new OnTouchListener() {
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
					//1、计算当前控件在手指移动后的坐标
					int left = bt_drag.getLeft() + offsetX;
					int right = bt_drag.getRight() + offsetX;
					int top = bt_drag.getTop() + offsetY;
					int bottom = bt_drag.getBottom() + offsetY;
					//容错处理（bt_drag不能托拽出屏幕）
					//左边超出屏幕边缘||右边超出屏幕边缘||上边超出屏幕边缘||下边超出屏幕的边缘（指定状态栏为50像素）
					if( left < 0 || right > mScreenWidthPixels ||
							top < 0 || bottom > mScreenHeightPixels - 50){
						return true;
					}
					if(top < (mScreenHeightPixels - 50)/2){
						bt_top.setVisibility(View.INVISIBLE);
						bt_bottom.setVisibility(View.VISIBLE);
					}else{
						bt_top.setVisibility(View.VISIBLE);
						bt_bottom.setVisibility(View.INVISIBLE);
					}
					//2、告知空件，按计算出的坐标进行展示
					bt_drag.layout(left, top, right, bottom);
					//3、重置起始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					//存储移动后的位置
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, bt_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, bt_drag.getTop());
					break;
				}
				//在当前的情况下返回false不响应移动事件，返回true才会去相应事件
				//既要相应点击事件，又要响应托拽事件，返回false
				return false;
			}
		});
		//双击事件
		bt_drag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1,mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if(mHits[mHits.length-1] - mHits[0] < 500){
					int left = mScreenWidthPixels/2 - bt_drag.getWidth()/2;
					int right = mScreenWidthPixels/2 + bt_drag.getWidth()/2;
					int top = mScreenHeightPixels/2 - bt_drag.getHeight()/2;
					int bottom = mScreenHeightPixels/2 + bt_drag.getHeight()/2;
					bt_drag.layout(left, top, right, bottom);
					//存储移动后的位置
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, bt_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, bt_drag.getTop());
				}
			}
		});
	}
}
