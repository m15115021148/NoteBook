package com.geek.springdemo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.rxjava.ProgressSubscriber;
import com.geek.springdemo.rxjava.RetrofitUtil;
import com.geek.springdemo.rxjava.SubscriberOnNextListener;
import com.geek.springdemo.util.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private RegisterActivity mContext;
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.sure)
    private TextView mSure;//
    @ViewInject(R.id.name)
    private EditText mName;
    @ViewInject(R.id.psw)
    private EditText mPsw;
    @ViewInject(R.id.pswSure)
    private EditText mPswSure;

    private SubscriberOnNextListener mListener = new SubscriberOnNextListener<ResultModel>() {
        @Override
        public void onNext(ResultModel model, int requestCode) {
            if (requestCode == RequestCode.REGISTER){
                if (model.getResult().equals("1")){
                    ToastUtil.showBottomLong(mContext,"注册成功");
                    mContext.finish();
                }else{
                    ToastUtil.showBottomLong(mContext,model.getErrorMsg());
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof SocketTimeoutException) {
                ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
            } else if (e instanceof ConnectException) {
                ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
            } else {
                ToastUtil.showBottomLong(mContext, "onError:" + e.getMessage());
            }
        }
    };

    protected void initData(){
        mContext = this;
        mBack.setOnClickListener(this);
        mTitle.setText("注册");
        mSure.setOnClickListener(this);

    }

    /**
     * 注册
     * @param name
     * @param psw
     */
    private void register(String name,String psw){
        if (MyApplication.getNetObject().isNetConnected()) {
            RetrofitUtil.getInstance().register(name,MyApplication.md5(psw),
                    new ProgressSubscriber<ResultModel>(mListener,mContext,RequestCode.REGISTER));
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
        if (v == mSure){
            if (TextUtils.isEmpty(mName.getText().toString())){
                ToastUtil.showBottomLong(mContext,"用户名不能为空");
                return;
            }
            if (TextUtils.isEmpty(mPswSure.getText().toString())||TextUtils.isEmpty(mPsw.getText().toString())){
                ToastUtil.showBottomLong(mContext,"密码不能为空");
                return;
            }
            if (!mPsw.getText().toString().equals(mPswSure.getText().toString())){
                ToastUtil.showBottomLong(mContext,"两次密码不一致");
                return;
            }
            register(mName.getText().toString(),mPsw.getText().toString());
        }
    }
}
