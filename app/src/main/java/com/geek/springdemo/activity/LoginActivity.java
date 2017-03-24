package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.model.UserModel;
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.PreferencesUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.RoundProgressDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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
    private HttpUtil http;
    private RoundProgressDialog progressDialog;
    private String phone,psw;//手机 密码

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
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();// 关闭进度条
            }
            switch (msg.what){
                case HttpUtil.SUCCESS:
                    if (msg.arg1== RequestCode.LOGIN){
                        MyApplication.userModel = (UserModel) ParserUtil.jsonToObject(msg.obj.toString(), UserModel.class);
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
                    break;
                case HttpUtil.EMPTY:
                    break;
                case HttpUtil.FAILURE:
                    ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
                    break;
                case HttpUtil.LOADING:
                    break;
                default:
                    break;
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
        if (http == null){
            http = new HttpUtil(handler);
        }
    }

    /**
     * 登录
     * @param name
     * @param psw
     */
    private void login(String name,String psw){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("登录中...");
                progressDialog.show();
            }
            RequestParams params = http.getParams(WebUrlConfig.getLogin());
            params.addBodyParameter("name",name);
            params.addBodyParameter("password",MyApplication.md5(psw));
            http.sendPost(RequestCode.LOGIN, params);
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
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
            login(mName.getText().toString(),mPsw.getText().toString());
        }
    }
}
