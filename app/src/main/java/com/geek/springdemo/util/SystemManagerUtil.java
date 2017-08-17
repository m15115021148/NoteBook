package com.geek.springdemo.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.geek.springdemo.R;


/**
 * 类说明
 * 
 * 侵透式状态栏帮助栏
 * 
 * @classname SystemManagerUtil
 * 
 */
public class SystemManagerUtil {

	// 系统侵透式状态栏
	public static void setSystemManager(Activity context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true, context);
		}
		SystemBarTintManager mTintManager = new SystemBarTintManager(context);
		mTintManager.setStatusBarTintEnabled(true);
		mTintManager.setNavigationBarTintEnabled(true);
		// 设置为蓝色
		mTintManager.setTintColor(context.getResources().getColor(
				R.color.themeColor));
	}

	// target是否为21设置状态栏的属性
	@TargetApi(21)
	private static void setTranslucentStatus(boolean on, Activity activity) {
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

}
