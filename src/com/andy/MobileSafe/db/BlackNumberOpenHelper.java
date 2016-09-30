package com.andy.MobileSafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberOpenHelper extends SQLiteOpenHelper {
	private static final String TABLE_NAME = "BlackNumber";
	private static final String CREATE_TABLE = "create table BlackNumber(_id integer primary key autoincrement,phone varchar(20),mode varchar(5))";

	public BlackNumberOpenHelper(Context context) {
		super(context, TABLE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
