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
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.RoundProgressDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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
    private HttpUtil http;
    private RoundProgressDialog progressDialog;

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
                    if (msg.arg1==RequestCode.REGISTER){
                        ResultModel model = (ResultModel) ParserUtil.jsonToObject(msg.obj.toString(), ResultModel.class);
                        if (model.getResult().equals("1")){
                            ToastUtil.showBottomLong(mContext,"注册成功");
                            mContext.finish();
                        }else{
                            ToastUtil.showBottomLong(mContext,model.getErrorMsg());
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
        mTitle.setText("注册");
        mSure.setOnClickListener(this);
        if (http == null){
            http = new HttpUtil(handler);
        }
    }

    private void register(String name,String psw){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("注册中...");
                progressDialog.show();
            }
            RequestParams params = http.getParams(WebUrlConfig.getRegister());
            params.addBodyParameter("name",name);
            params.addBodyParameter("password",MyApplication.md5(psw));
            http.sendPost(RequestCode.REGISTER, params);
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
