package com.geek.springdemo.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.geek.springdemo.db.DBAccount;
import com.geek.springdemo.model.UserModel;
import com.geek.springdemo.util.LocationService;
import com.geek.springdemo.util.NetworkUtil;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/3/14.
 */

public class MyApplication extends Application{
    /**application对象*/
    private static MyApplication instance;
    public static NetworkUtil netState;//网络状态
    public static UserModel userModel;//用户实体类
    public static DBAccount db;//数据库管理
    public LocationService locationService;
    public static double lat;// 纬度
    public static double lng;// 经度
    public static String address;//定位地址

    @Override
    public void onCreate() {
        super.onCreate();
        initXutils();
        netState = new NetworkUtil(getApplicationContext());
        if (db == null){
            db = new DBAccount(getApplicationContext());
        }
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        JPushInterface.setDebugMode(true);// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
    }

    /**
     * 初始化xutils框架
     */
    private void initXutils() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }

    public static MyApplication instance() {
        if (instance != null) {
            return instance;
        } else {
            return new MyApplication();
        }
    }

    /**
     * 获取手机网络状态对象
     *
     * @return
     */
    public static NetworkUtil getNetObject() {
        if (netState != null) {
            return netState;
        } else {
            return new NetworkUtil(instance().getApplicationContext());
        }
    }

    /**
     * listview没有数据显示 的控件
     * @param context 本类
     * @param T AbsListView
     * @param txt 内容
     */
    public static void setEmptyShowText(Context context, AbsListView T, String txt){
        TextView emptyView = new TextView(context);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText(txt);
        emptyView.setTextSize(18);
        emptyView.setTextColor(Color.parseColor("#acacac"));
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)T.getParent()).addView(emptyView);
        T.setEmptyView(emptyView);
    }

    /**
     * 描述：MD5加密.
     *(全大写字母)32
     * @param string
     *            要加密的字符串
     * @return String 加密的字符串
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toUpperCase();
    }
}
