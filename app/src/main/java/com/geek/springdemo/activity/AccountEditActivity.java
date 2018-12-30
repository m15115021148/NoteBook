package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

/**
 * 编辑页面
 */
@ContentView(R.layout.activity_account_edit)
public class AccountEditActivity extends BaseActivity implements View.OnClickListener {
    private AccountEditActivity mContext;//本类
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.note)
    private TextView mNote;//编辑内容
    @ViewInject(R.id.save)
    private TextView mSave;//保存
    private int accountID = 0;//账单id
    private String note = "";//描述内容

    private SubscriberOnNextListener mListener = new SubscriberOnNextListener<ResultModel>() {

        @Override
        public void onNext(ResultModel model, int requestCode) {
            if (requestCode == RequestCode.UPDATEACCOUNTNOTE){
                if (model.getResult() == 1){
                    ToastUtil.showBottomShort(mContext,"修改成功");
                    Intent intent = new Intent();
                    intent.putExtra("note",mNote.getText().toString());
                    setResult(101,intent);
                    mContext.finish();
                }else{
                    ToastUtil.showBottomShort(mContext,model.getErrorMsg());
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof SocketTimeoutException) {
                ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
            } else if (e instanceof ConnectException) {
                ToastUtil.showBottomLong(mContext,RequestCode.NOLOGIN);
            } else {
                ToastUtil.showBottomLong(mContext, "onError:"+ e.getMessage());
            }
        }
    };

    /**
     * 初始化数据
     */
    protected void initData(){
        mContext = this;
        mBack.setOnClickListener(this);
        mTitle.setText("编辑");
        mSave.setOnClickListener(this);
        accountID = getIntent().getIntExtra("accountID",0);
        note = getIntent().getStringExtra("note");
        mNote.setText(note);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
        if (v == mSave){
            if (TextUtils.isEmpty(mNote.getText().toString().trim())){
                ToastUtil.showBottomShort(mContext,"内容不能为空！");
                return;
            }
            RetrofitUtil.getInstance()
                    .updateAccountNote(accountID,MyApplication.userModel.getUserID(),mNote.getText().toString(),
                    new ProgressSubscriber<ResultModel>(mListener,mContext,RequestCode.UPDATEACCOUNTNOTE));
        }
    }
}
