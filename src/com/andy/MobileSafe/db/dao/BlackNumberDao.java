package com.andy.MobileSafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.andy.MobileSafe.db.BlackNumberOpenHelper;
import com.andy.MobileSafe.db.domain.BlackNumberInfo;

public class BlackNumberDao {
	private static final String TABLE_NAME = "BlackNumber";
	private BlackNumberOpenHelper mBlackNumberOpenHelper;
	//对数据库的处理类使用单例模式
	//1、构造方法私有化
	private BlackNumberDao(Context context){
		mBlackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	//2、声明一个当前类对象
	private static BlackNumberDao mBlackNumberDao= null;
	//3、提供一个静态方法，如果当前类对象为空，创建一个新的
	public static BlackNumberDao getInstance(Context context){
		if(mBlackNumberDao == null){
			mBlackNumberDao = new BlackNumberDao(context);
		}
		return mBlackNumberDao;
	}

	/**
	 * 增加一个条目
	 * @param phone  拦截的电话号码
	 * @param mode   拦截类型（1：短信   2：电话   3：拦截所有（电话+短信))
	 */
	public void insert(String phone,String mode){
		SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert(TABLE_NAME, null, values);

		db.close();
	}

	/**
	 * 删除一个条目
	 * @param phone  删除的电话号码
	 */
	public void delete(String phone){
		SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

		db.delete(TABLE_NAME, "phone = ?", new String[]{phone});

		db.close();
	}

	/**
	 * 根据电话号码，更新拦截模式
	 * @param phone  更新拦截模式的电话号码
	 * @param mode   更新的拦截模式（1：短信   2：电话   3：拦截所有（电话+短信))
	 */
	public void update(String phone,String mode){
		SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update(TABLE_NAME, values, "phone = ?", new String[]{phone});

		db.close();
	}

	/**
	 * 查询数据库中所有的条目
	 * @return  返回数据库中所有的条目的集合
	 */
	public List<BlackNumberInfo> findAll(){
		SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.query(TABLE_NAME, new String[]{"phone","mode" }, null, null, null, null, "_id desc");
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor != null && cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			String phone = cursor.getString(0);
			String mode = cursor.getString(1);
			blackNumberInfo.setPhone(phone);
			blackNumberInfo.setMode(mode);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();

		db.close();
		return blackNumberList;
	}

	/**
	 * 每次查询20条数据
	 * @param index  查询的索引值
	 * @return  返回查询的20条数据的集合
	 */
	public List<BlackNumberInfo> find(int index){
		SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select phone,mode from BlackNumber order by _id desc limit ?,20;", new String[]{index+""});
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor != null && cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			String phone = cursor.getString(0);
			String mode = cursor.getString(1);
			blackNumberInfo.setPhone(phone);
			blackNumberInfo.setMode(mode);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();

		db.close();
		return blackNumberList;
	}

	/**
	 * 用于获取数据库中数据的总条目数
	 * @return  返回数据库中数据的条目数
	 */
	public int getCount(){
		SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
		int count=0;
		Cursor cursor = db.rawQuery("select count(*) from BlackNumber;",null);
		while(cursor !=null && cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		cursor.close();

		db.close();
		return count;
	}
}
