package com.thunder.MobileSafe.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.security.auth.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.thunder.MobileSafe.R;
import com.thunder.MobileSafe.utils.SpUtil;
import com.thunder.MobileSafe.utils.StreamUtil;
import com.thunder.MobileSafe.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

    protected static final String tag = "SplashActivity";
	/**
	 * 版本更新状态码
	 */
	protected static final int UPDATE_VERSION = 100;
	/**
	 * 进入主界面状态码
	 */
	protected static final int ENTER_HOME = 101;
	/**
	 * URL错误状态码
	 */
	protected static final int URL_ERROR = 102;
	/**
	 * IO异常状态码
	 */
	protected static final int IO_ERROR = 103;
	/**
	 * JSON异常状态码
	 */
	protected static final int JSON_ERROR = 104;
	private TextView tv_version_name;
	private int mLocalVersionCode;
	private String mVersionDes;	
	private String mDownloadUrl;
	private RelativeLayout rl_root;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_VERSION:
				//弹出对话框，提示用户进行更新
				showUpdateDialog();
				break;
			case ENTER_HOME:
				//进入应用程序主界面，activity跳转
				enterHome();
				break;
			case URL_ERROR:
				ToastUtil.show(SplashActivity.this, "Url异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(SplashActivity.this, "IO异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(SplashActivity.this, "Json异常");
				enterHome();
				break;

			default:
				break;
			}
		};
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化UI
        initUI();
        //初始化数据
        initData();
        //初始化动画
        initAnimation();
       
       
        
    }

    /**
     * 淡入淡出动画
     */
    private void initAnimation() {
		// TODO Auto-generated method stub
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.setAnimation(alphaAnimation);
	}

	/**
     * 弹出更新对话框，提示用户下载更新
     */
    protected void showUpdateDialog() {
    	Builder builder = new AlertDialog.Builder(this);
    	//设置对话框Icon
    	builder.setIcon(R.drawable.ic_launcher);
    	//设置对话框标题
    	builder.setTitle("下载更新");
    	//设置显示内容
    	builder.setMessage(mVersionDes);
    	builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				System.out.println("aaaaaaaaaa");
				// 下载APK  需要下载路径，下载到本地的位置（要判断SD卡是否挂载）
				//[1]判断SD卡是否挂载
				System.out.println(Environment.getExternalStorageState());
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					System.out.println("bbbbbbbbbbb");
					//[2]获取apk存储路径
					String path=Environment.getExternalStorageDirectory()+File.separator+"mobileSafe.apk";
					//[3]发送请求，传递参数（下载地址，下载应用放置位置）
					HttpUtils httpUtils=new HttpUtils();
					httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
						
						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							//下载成功
							System.out.println("下载成功");
							File file=responseInfo.result;
							//提示用户安装
							installAPK(file);
						}
						
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							//下载失败
							System.out.println("下载失败");
						}
						//开始下载
						@Override
						public void onStart() {
							super.onStart();
							System.out.println("开始下载");
							
						}
						//正在下载(下载总大小，当前下载位置，是否正在下载)
						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							super.onLoading(total, current, isUploading);
							System.out.println("total:"+total);
							System.out.println("current:"+current);
							System.out.println("isUploading:"+isUploading);
							
						}
					});
					}
				
			}});
    	builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//取消对话框，进入主界面
				enterHome();
			}
		});
    	//取消事件监听
    	builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				enterHome();
				dialog.dismiss();
			}	
		});
    	builder.show();
	}
    /**
     * 安装APK文件
     * @param file APK文件
     */
    protected void installAPK(File file){
    	Intent intent = new Intent("android.intent.action.VIEW");
    	intent.addCategory("android.intent.category.DEFAULT");
    	intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    	//startActivity(intent);
    	startActivityForResult(intent, 0);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	enterHome();
    }
	/**
     * 进入应用程序主界面
     */
    protected void enterHome() {
    	Intent intent = new Intent(this,HomeActivity.class);
    	startActivity(intent);
    	finish();
	}

	/**
     * 初始化数据方法
     */
    private void initData() {
    	//1、获取版本名称
    	tv_version_name.setText("版本名称："+getVersionName());
    	//检测（本地版本号和服务器版本号比对）是否有更新，如果有则提示用户下载
    	//2、获取本地版本号
    	mLocalVersionCode = getVersionCode();
    	//3、获取服务端版本号（客户端发送请求，服务端响应，json或xml）
    	//json中包含
    	/*
    	 * 更新版本的版本名称
    	 * 新版本的描述信息
    	 * 服务器版本号
    	 * 新版本APK下载地址
    	 */
    	if(SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)){
    		checkVersion();
    	}else{
    		//4秒后再去执行ENTER_HOME所对应的消息事件
    		mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
    	}
    	
	}

	/**
	 * 检测版本号
	 */
	private void checkVersion() {
		new Thread(){
			

			

			public void run(){
				Message msg=Message.obtain();
				try {
					//1、封装Url地址
					URL url = new URL("http://10.0.2.2:8080/update1.json");
					//2、开启一个链接
					HttpURLConnection connection=(HttpURLConnection) url.openConnection();
					//3、设置常见请求参数
					//请求超时
					connection.setConnectTimeout(2000);
					//读取超时
					connection.setReadTimeout(2000);
					//设置请求方法，默认为GET
					connection.setRequestMethod("GET");
					//4、获取响应码，如果是200则请求成功
					if(connection.getResponseCode()==200){
						//5、以流的形式将数据读取下来
						InputStream is=connection.getInputStream();
						//6、将流转换成字符串
						String json=StreamUtil.streamToString(is);
						//7、解析json数据
						//JSONObject jsonObject=new JSONObject(json);
						JSONObject object= new JSONObject(json);
						String versionName = object.getString("versionName");
						mVersionDes = object.getString("versionDes");
						String versionCode = object.getString("versionCode");
						mDownloadUrl = object.getString("downloadUrl");
						System.out.println(versionName);
						System.out.println(mVersionDes);
						System.out.println(versionCode);
						System.out.println(mDownloadUrl);
						//8、版本号比对，如果服务器端的版本号比本地的版本号大，则提示用户下载
						if(mLocalVersionCode<Integer.parseInt(versionCode)){
							//提示用户下载，弹出对话框。子线程中更新UI ，用handle
							msg.what=UPDATE_VERSION;
						}else{
							//进入程序的主界面
							msg.what=ENTER_HOME;
						}
						
					}
				} catch (MalformedURLException e) {
					msg.what=URL_ERROR;
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					msg.what=IO_ERROR;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what=JSON_ERROR;
					e.printStackTrace();
				}finally{
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}

	/**
	 * 获取版本名称:清单文件中
	 * @return 返回null代表异常
	 */
	private String getVersionName() {
		//1、获取包管理者对象
		PackageManager pm = getPackageManager();
		//2、从包管理者对象中，获取指定包名的基本信息（版本名称、版本号），传0代表获取基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			//获取版本名称
			return packageInfo.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取版本号:清单文件中
	 * @return 返回0代表异常
	 */
	private int getVersionCode() {
		//1、获取包管理者对象
		PackageManager pm = getPackageManager();
		//2、从包管理者对象中，获取指定包名的基本信息（版本名称、版本号），传0代表获取基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			//获取版本名称
			return packageInfo.versionCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
     * 初始化UI方法
     */
    
	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
		
	}
}
