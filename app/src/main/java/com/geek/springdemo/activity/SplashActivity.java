package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.model.UserModel;
import com.geek.springdemo.rxjava.ProgressSubscriber;
import com.geek.springdemo.rxjava.RetrofitUtil;
import com.geek.springdemo.rxjava.SubscriberOnNextListener;
import com.geek.springdemo.util.PreferencesUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.ParticleView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity implements Runnable {
    private SplashActivity mContext;
    @ViewInject(R.id.pv_1)
    private ParticleView mPv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//影藏系统状态栏
        initData();
    }

    private SubscriberOnNextListener mListener = new SubscriberOnNextListener<UserModel>() {

        @Override
        public void onNext(UserModel model, int requestCode) {
            if (requestCode == RequestCode.LOGIN){
                MyApplication.userModel = model;
                if (MyApplication.userModel.getResult().equals("1")){
                    Intent intent = new Intent(mContext,MainActivity.class);
                    startActivity(intent);
                    PreferencesUtil.setDataModel(mContext,"userModel",MyApplication.userModel);
                    MyApplication.userModel = PreferencesUtil.getDataModel(mContext,"userModel");
                    mContext.finish();
                }else{//重新登录
                    Intent intent = new Intent(mContext,LoginActivity.class);
                    startActivity(intent);
                    mContext.finish();
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof SocketTimeoutException) {
                ToastUtil.showBottomLong(mContext, "服务器无法连接，进入本地保存");
            } else if (e instanceof ConnectException) {
                ToastUtil.showBottomLong(mContext,"服务器无法连接，进入本地保存");
            } else {
                ToastUtil.showBottomLong(mContext, "onError:"+ e.getMessage());
            }
//            ToastUtil.showBottomLong(mContext, "服务器无法连接，进入本地保存");
            //预记账 页面
            MyApplication.userModel = PreferencesUtil.getDataModel(mContext,"userModel");
            Intent read = new Intent(mContext,ReadyAccountActivity.class);
            startActivity(read);
            mContext.finish();
        }
    };


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (PreferencesUtil.getFristLogin(mContext,"first")){//第一次登录
                        Intent intent = new Intent(mContext,LoginActivity.class);
                        startActivity(intent);
                        mContext.finish();
                    }else{
                        String phone = PreferencesUtil.getStringData(mContext,"phone");
                        String psw = PreferencesUtil.getStringData(mContext,"psw");
                        if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(psw)){
                            login(phone,psw);
                        }else{//保存为“” 重新登录
                            Intent intent = new Intent(mContext,LoginActivity.class);
                            startActivity(intent);
                            mContext.finish();
                        }
                    }
                    break;
                case 2:
                    mPv.startAnim();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 登录
     * @param name
     * @param psw
     */
    private void login(String name,String psw){
        if (MyApplication.getNetObject().isNetConnected()) {
            RetrofitUtil.getInstance().login(name,MyApplication.md5(psw),
                    new ProgressSubscriber<UserModel>(mListener,mContext,RequestCode.LOGIN,false));
        } else {//自动登录 没有网络 预记账页面
            MyApplication.userModel = PreferencesUtil.getDataModel(mContext,"userModel");
            Intent intent = new Intent(mContext,ReadyAccountActivity.class);
            startActivity(intent);
            mContext.finish();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mPv.postDelayed(this,200);
        //动画结束回调
        mPv.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                handler.sendEmptyMessage(1);
            }
        });
    }

    /**
     * 改写物理按键——返回的逻辑
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handler.removeCallbacks(this);
            mPv.removeCallbacks(this);
            mContext.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(this);
        mPv.removeCallbacks(this);
        //强制回收
        System.gc();
    }

    @Override
    public void run() {
        handler.sendEmptyMessage(2);
    }
}
