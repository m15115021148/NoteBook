package com.geek.springdemo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

/**
 * com.sitemap.stm.utils
 * 
 * @author chenmeng
 * @Description Preferences文件存储对象工具类
 * @date create at 2016年9月12日 下午4:40:32
 */
public class PreferencesUtil {
	/** log标志 */
	private static final String TAG = "tag";
	/** 是否第一次登陆preferneces标志 */
	private static final String SP_LOGIN_PRIVATE = "sp_login_private";
	/** 保存实体类中preferneces标志 */
	private static final String SP_MODEL_PRIVATE = "sp_model_private";

	/**
	 * 是否第一个登录 判断导航页面
	 * 
	 * @param context
	 *            本类
	 * @param key
	 *            保存的标志
	 * @param isFrist
	 *            true为是第一次登录
	 * @see #getFristLogin(Context, String) 配套使用
	 * @return
	 */
	public static void isFristLogin(Context context, String key, boolean isFrist) {
		SharedPreferences sp = context.getSharedPreferences(SP_LOGIN_PRIVATE,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, isFrist).apply();
	}

	/**
	 * 获取第一个登录保存的内容
	 * 
	 * @param context
	 *            本类
	 * @param key
	 *            保存的标志
	 * @return
	 */
	public static boolean getFristLogin(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SP_LOGIN_PRIVATE,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, true);
	}

	/**
	 * 将一个实体类保存起来（可做自动登录用保存用户名和密码）
	 * 数据量小
	 * 
	 * @see #getDataModel(Context, String) 配套使用
	 * @param context
	 *            本类
	 * @param key
	 *            保存的标志
	 * @param object
	 *            实体类 必须实例化 否则抛出异常 要实现#Serializable
	 */
	public static boolean setDataModel(Context context, String key,
			Object object) {
		SharedPreferences sp = context.getSharedPreferences(SP_MODEL_PRIVATE,
				Context.MODE_PRIVATE);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(baos);
			out.writeObject(object);
			String objectVal = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			Editor editor = sp.edit();
			editor.putString(key, objectVal);
			return editor.commit();
		} catch (IOException e) {
			Log.i(TAG, "保存实体类异常了");
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				Log.i(TAG, "保存实体类异常了");
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 获取保存的实体类 获取之前须保存了这个实体类
	 * 
	 * @see #setDataModel(Context, String, Object) 
	 * @param context
	 *            本类
	 * @param key
	 *            保存的标志
	 * @return 返回这个实体类
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getDataModel(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SP_MODEL_PRIVATE,
				Context.MODE_PRIVATE);
		String objectVal = sp.getString(key, null);
		if (objectVal == null) {
			Log.i(TAG, "获取保存的实体类为空");
			return null;
		}
		byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (StreamCorruptedException e) {
			Log.i(TAG, "获取保存的实体类为空");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(TAG, "获取保存的实体类为空");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.i(TAG, "获取保存的实体类为空");
			e.printStackTrace();
		} finally {
			try {
				if (bais != null) {
					bais.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				Log.i(TAG, "获取保存的实体类为空");
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 清除保存的实体类
	 * 
	 * @param context
	 *            本类
	 * @param key
	 *            保存的标志
	 * @return
	 */
	public static boolean deleteDataModel(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SP_MODEL_PRIVATE,
				Context.MODE_PRIVATE);
		return sp.edit().putString(key,"").commit();
	}

	/**
	 * 清除保存的实体类
	 *
	 * @param context
	 *            本类
	 * @param key
	 *            保存的标志
	 * @return
	 */
	public static boolean deleteDataFrist(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SP_LOGIN_PRIVATE,
				Context.MODE_PRIVATE);
		return sp.edit().putBoolean(key,false).commit();
	}

	/**
	 * 保存一个string类型
	 * @param context
	 * @param key
	 */
	public static void setStringData(Context context, String key,String values){
		SharedPreferences sp = context.getSharedPreferences(SP_LOGIN_PRIVATE,
				Context.MODE_PRIVATE);
		sp.edit().putString(key,values).apply();
	}

	/**
	 * 得到保存的字符串
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getStringData(Context context, String key){
		SharedPreferences sp = context.getSharedPreferences(SP_LOGIN_PRIVATE,
				Context.MODE_PRIVATE);
		return sp.getString(key,"");
	}

}
