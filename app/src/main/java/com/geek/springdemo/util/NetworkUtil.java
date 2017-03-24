package com.geek.springdemo.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/*
 * 所需权限
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <uses-permission android:name="android.permission.INTERNET"/>
 */

/**
 * com.sitemap.stm.utils
 * 
 * @author chenmeng
 * @Description 网络探测器， 判断网络是否打开、连接、可用，以及当前网络状态。
 * @date create at 2016年8月26日 上午10:43:16
 */
@SuppressLint("DefaultLocale")
public class NetworkUtil {
	private static Context context;
	public static final String IP_DEFAULT = "0.0.0.0";
	private static final String TAG = "netWorkState";

	public NetworkUtil(Context context){
		this.context = context;
	}

	/**
	 * 检测网络是否连接
	 * 
	 * @return
	 */
	public static boolean isNetConnected() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo infos = cm.getActiveNetworkInfo();
			if (infos != null) {
				return infos.isAvailable();
			}
		}
		return false;
	}

	/**
	 * Gps是否打开
	 * 
	 * @param
	 * @return
	 */
	public static boolean isGpsEnabled() {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * 检测wifi是否连接
	 * 是否有网络
	 * 
	 * @return
	 */
	public static boolean isWifiConnected() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null
					&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 检测手机移动网络是否连接
	 * 
	 * @return
	 */
	public static boolean is3gConnected() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null
					&& networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取ip地址
	 * 
	 * @return
	 */
	public static String getIPAddress() {
		try {
			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
					.getNetworkInterfaces();
			while (networkInterfaceEnumeration.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaceEnumeration
						.nextElement();
				Enumeration<InetAddress> inetAddressEnumeration = networkInterface
						.getInetAddresses();
				while (inetAddressEnumeration.hasMoreElements()) {
					InetAddress inetAddress = inetAddressEnumeration
							.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
			return IP_DEFAULT;
		} catch (SocketException e) {
			return IP_DEFAULT;
		}
	}

	/**
	 * 没有网络下 打开手机设置网络
	 */
	public static void setNet() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("没有可用的网络").setMessage("是否对网络进行设置?");

		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				try {
					@SuppressWarnings("deprecation")
					String sdkVersion = android.os.Build.VERSION.SDK;
					if (Integer.valueOf(sdkVersion) > 10) {
						intent = new Intent(
								android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					} else {
						intent = new Intent();
						ComponentName comp = new ComponentName(
								"com.android.settings",
								"com.android.settings.WirelessSettings");
						intent.setComponent(comp);
						intent.setAction("android.intent.action.VIEW");
					}
					context.startActivity(intent);
				} catch (Exception e) {
					Log.w(TAG, "open network settings failed, please check...");
					e.printStackTrace();
				}
			}
		}).setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

	/**
	 * 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络
	 * 
	 * @return
	 */
	public static int getConnectedType() {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = 3;
				Log.i(TAG, "net网络...");
			}else {
				netType = 2;
				Log.i(TAG, "wap网络...");
			}
		}else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;
			Log.i(TAG, "WIFI网络...");
		}
		return netType;
	}
}
