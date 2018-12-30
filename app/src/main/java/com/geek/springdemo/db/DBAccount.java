package com.geek.springdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geek.springdemo.model.AccountsModel;

/**
 * @author Administrator
 *
 */
public class DBAccount {
	private DBHelper dbHelper;
	private SQLiteDatabase db;

	public DBAccount(Context context) {
		dbHelper = DBHelper.getNewInstanceDBHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * 查询数据
	 * @return 返回一个Cursor
	 */
	public Cursor queryDBCollectData(){
		Cursor cursor = db.query(DBHelper.ACCOUNT, null, null, null, null, null, null);
		return cursor;
	}
	
	/**
	 *	插入数据
	 */
	public void insert(AccountsModel.DataBean model){
		ContentValues values = new ContentValues();
		values.put("type", model.getType());
		values.put("money", model.getMoney());
		values.put("kind", model.getKind());
		values.put("note", model.getNote());
		values.put("time", model.getTime());
		values.put("lat",model.getLat());
		values.put("lng",model.getLng());
		values.put("address",model.getAddress());
		db.insert(DBHelper.ACCOUNT, null, values);
		Log.i("result", "插入成功");
	}

	/**
	 * 删除表
	 */
	public void delTab(){
		db.execSQL("delete from " + DBHelper.ACCOUNT + ";");
	}

	/**
	 * 删除一条记录
	 * @param id
	 */
	public void delOneAccount(int id){
		String sql = "delete from " + DBHelper.ACCOUNT + " where id="+id+ ";";
		Log.e("result","sql:"+sql);
		db.execSQL(sql);
	}
}
