package com.thunder.MobileSafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.thunder.MobileSafe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListActivity extends Activity {
	private List<HashMap<String,String>> contactList=new ArrayList<HashMap<String,String>>();
	
	private Handler mHandler=new Handler(){

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			 lv_contact.setAdapter(mAdapter);
		};
	};

	private ListView lv_contact;
	private MyAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		initUI();
		//initData();
		readContacts();
	}
	/**
	 *联系人数据适配器
	 */
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contactList.size();
		}

		@Override
		public HashMap<String,String> getItem(int position) {
			// TODO Auto-generated method stub
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view=View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
			TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView)view.findViewById(R.id.tv_phone);
			HashMap<String,String> hashMap=getItem(position);
			//System.out.println("name:"+hashMap.get("name"));
			//System.out.println("phone:"+hashMap.get("phone"));
			tv_name.setText(hashMap.get("name"));
			tv_phone.setText(hashMap.get("phone"));
			return view;
		}
		
	}
	
	public void readContacts(){
		Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		while(cursor.moveToNext()){
			String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phone=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			HashMap<String,String> hashMap=new HashMap<String, String>();
			hashMap.put("name", name);
			hashMap.put("phone", phone);
			//System.out.println("name:"+name);
			//System.out.println("phone:"+phone);
			contactList.add(hashMap);
		}
		 mHandler.sendEmptyMessage(0);
		cursor.close();
	}
	
	/**
	 * 获取联系人数据
	 */
	private void initData() {
		//读取联系人可能是一个耗时的操作，因此要放在子线程中
		new Thread(){
			public void run(){
				//1、获取内容解析器
				 ContentResolver contentResolver = getContentResolver();
				 if(contentResolver!=null){
					 //2、查询数据库联系人
					 Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
							 new String[]{"contact_id"}, null, null, null);
					 contactList.clear();
						 while(cursor.moveToNext()){
							 String id=cursor.getString(0);
							 //System.out.println("id:"+id);
							 //3、根据用户唯一性id值查询data表和mimetype表生成的视图，获取data和mimetype字段
							 Cursor indexCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"), 
									 new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{id}, null);
							 //4、循环遍历每一个联系人的电话号码和姓名
							 HashMap<String,String> hashMap=new HashMap<String, String>();
							 while(indexCursor.moveToNext()){
								 String data = indexCursor.getString(0);
								 String mimetype=indexCursor.getString(1);
								 System.out.println(data);
								 //System.out.println(mimetype);
								 //5、区分类型去给hashMap填充数据 
								 if(mimetype.equals("vnd.android.cursor.item/phone_v2")){
									 if(!TextUtils.isEmpty(data)){
										hashMap.put("phone", data); 
									 }
								 }else if(mimetype.equals("vnd.android.cursor.item/name")){
									 if(!TextUtils.isEmpty(data)){
										hashMap.put("name", data); 
								     }
								 }
							 }
							 indexCursor.close();
							 contactList.add(hashMap);
						 }
						 cursor.close();
				 }
				 //6、消息机制，发送一个空消息，告知主线程可以去使用已经填充好的数据集和
				 mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(mAdapter!=null){
					//1获取点中条目的索引指向集合中的对象
					HashMap<String,String> hashMap=mAdapter.getItem(position);
					String phone=hashMap.get("phone");
					Intent intent=new Intent();
					intent.putExtra("phone", phone);
					setResult(RESULT_OK,intent);
					finish();
				}

			}
		});
	}
}
