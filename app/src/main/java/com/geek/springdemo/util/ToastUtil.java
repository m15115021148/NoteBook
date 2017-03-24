package com.geek.springdemo.util;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * com.sitemap.stm.utils
 * 
 * @author chenmeng
 * @Description Toast工具类，分不同量级进行提示
 * @date create at 2016年9月12日 下午4:19:07
 */
public class ToastUtil {

	/**
	 * 长时间显示 位置居中
	 * 
	 * @param context
	 *            所在的activity
	 * @param title
	 *            显示的内容
	 */
	public static void showCenterLong(Context context, String title) {
		Toast toast = Toast.makeText(context, title, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * 显示时间两秒，位置居中
	 * 
	 * @param context
	 *            所在的activity
	 * @param title
	 *            显示的内容
	 */
	public static void showCenterShort(Context context, String title) {
		Toast toast = Toast.makeText(context, title, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * 长时间显示 位置居下
	 * 
	 * @param context
	 * @param title
	 */
	@SuppressWarnings("deprecation")
	public static void showBottomLong(Context context, String title) {
		Toast toast = Toast.makeText(context, title, Toast.LENGTH_LONG);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		toast.setGravity(Gravity.BOTTOM, 0, manager.getDefaultDisplay()
				.getHeight() / 10);
		toast.show();
	}

	/**
	 * 短时间显示 位置居下
	 * 
	 * @param context
	 * @param title
	 */
	@SuppressWarnings("deprecation")
	public static void showBottomShort(Context context, String title) {
		Toast toast = Toast.makeText(context, title, Toast.LENGTH_SHORT);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		toast.setGravity(Gravity.BOTTOM, 0, manager.getDefaultDisplay()
				.getHeight() / 10);
		toast.show();
	}
}
