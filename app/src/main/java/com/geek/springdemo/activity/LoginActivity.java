package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.model.UserModel;
import com.geek.springdemo.rxjava.ProgressSubscriber;
import com.geek.springdemo.rxjava.RetrofitUtil;
import com.geek.springdemo.rxjava.SubscriberOnNextListener;
import com.geek.springdemo.util.PreferencesUtil;
import com.geek.springdemo.util.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private LoginActivity mContext;
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.more)
    private LinearLayout mRegister;
    @ViewInject(R.id.content)
    private TextView content;
    @ViewInject(R.id.sure)
    private TextView mSure;//登录
    @ViewInject(R.id.name)
    private EditText mName;
    @ViewInject(R.id.psw)
    private EditText mPsw;
    private String phone,psw;//手机 密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initData();
    }

    private SubscriberOnNextListener mListener = new SubscriberOnNextListener<Object>() {

        @Override
        public void onNext(Object msg, int requestCode) {
            if (requestCode==101){
                MyApplication.userModel = (UserModel)msg;
                if (MyApplication.userModel.getResult().equals("1")){
                    Intent intent = new Intent(mContext,MainActivity.class);
                    startActivity(intent);
                    PreferencesUtil.isFristLogin(mContext,"first",false);
                    PreferencesUtil.setDataModel(mContext,"userModel",MyApplication.userModel);
                    PreferencesUtil.setStringData(mContext,"phone",phone);
                    PreferencesUtil.setStringData(mContext,"psw",psw);
                    MyApplication.userModel = PreferencesUtil.getDataModel(mContext,"userModel");
                    mContext.finish();
                }else{
                    ToastUtil.showBottomLong(mContext,MyApplication.userModel.getErrorMsg());
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof SocketTimeoutException) {
                ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
            } else if (e instanceof ConnectException) {
                ToastUtil.showBottomLong(mContext,RequestCode.ERRORINFO);
            } else {
                ToastUtil.showBottomLong(mContext, "onError:"+ e.getMessage());
            }
        }
    };

    private void initData(){
        mBack.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mRegister.setVisibility(View.VISIBLE);
        mTitle.setText("登录");
        content.setText("注册");
        mSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            onBackPressed();
            mContext.finish();
        }
        if (v == mRegister){
            Intent intent = new Intent(mContext,RegisterActivity.class);
            startActivity(intent);
        }
        if (v == mSure){
            if (TextUtils.isEmpty(mName.getText().toString())){
                ToastUtil.showBottomLong(mContext,"用户名不能为空");
                return;
            }
            if (TextUtils.isEmpty(mPsw.getText().toString())){
                ToastUtil.showBottomLong(mContext,"密码不能为空");
                return;
            }
            phone = mName.getText().toString();
            psw = mPsw.getText().toString();

            RetrofitUtil.getInstance().login(
                    mName.getText().toString(),
                    MyApplication.md5(psw),
                    new ProgressSubscriber<UserModel>(
                            mListener,mContext,"登陆中...",101
                    )
            );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //退出所有的activity
        Intent intent = new Intent();
        intent.setAction(BaseActivity.TAG_ESC_ACTIVITY);
        sendBroadcast(intent);
        finish();
    }
}
