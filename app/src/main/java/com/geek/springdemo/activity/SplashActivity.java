package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.UserModel;
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.PreferencesUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.ParticleView;
import com.geek.springdemo.view.RoundProgressDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity implements Runnable {
    private SplashActivity mContext;
    private HttpUtil http;
    private RoundProgressDialog progressDialog;
    @ViewInject(R.id.pv_1)
    private ParticleView mPv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HttpUtil.SUCCESS:
                    if (msg.arg1 == RequestCode.LOGIN){
                        MyApplication.userModel = (UserModel) ParserUtil.jsonToObject(msg.obj.toString(), UserModel.class);
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
                    break;
                case HttpUtil.EMPTY:
                    break;
                case HttpUtil.FAILURE:
                    ToastUtil.showBottomLong(mContext, "服务器无法连接，进入本地保存");
                    //预记账 页面
                    MyApplication.userModel = PreferencesUtil.getDataModel(mContext,"userModel");
                    Intent read = new Intent(mContext,ReadyAccountActivity.class);
                    startActivity(read);
                    mContext.finish();
                    break;
                case HttpUtil.LOADING:
                    break;
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
            progressDialog = RoundProgressDialog.createDialog(mContext);
            RequestParams params = http.getParams(WebUrlConfig.getLogin());
            params.addBodyParameter("name",name);
            params.addBodyParameter("password",MyApplication.md5(psw));
            http.sendPost(RequestCode.LOGIN, params);
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
//        handler.postDelayed(this, 3000);
        mPv.postDelayed(this,200);
        if (http == null){
            http = new HttpUtil(handler);
        }
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
            mContext.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(this);
        //强制回收
        System.gc();
    }

    @Override
    public void run() {
        handler.sendEmptyMessage(2);
    }
}
