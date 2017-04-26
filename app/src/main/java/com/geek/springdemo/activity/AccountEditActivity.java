package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
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

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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
    private HttpUtil http;
    private RoundProgressDialog progressDialog;
    private String accountID="";//账单id
    private String note = "";//描述内容

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
                    if (msg.arg1 == RequestCode.UPDATEACCOUNTNOTE){
                        ResultModel model = (ResultModel) ParserUtil.jsonToObject(msg.obj.toString(),ResultModel.class);
                        if (model.getResult().equals("1")){
                            ToastUtil.showBottomShort(mContext,"修改成功");
                            Intent intent = new Intent();
                            intent.putExtra("note",mNote.getText().toString());
                            setResult(101,intent);
                            mContext.finish();
                        }else{
                            ToastUtil.showBottomShort(mContext,model.getErrorMsg());
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

    /**
     * 初始化数据
     */
    private void initData(){
        mBack.setOnClickListener(this);
        mTitle.setText("编辑");
        mSave.setOnClickListener(this);
        if (http == null){
            http = new HttpUtil(handler);
        }
        accountID = getIntent().getStringExtra("accountID");
        note = getIntent().getStringExtra("note");
        mNote.setText(note);
    }

    /**
     * 修改
     */
    private void updateAccountNote(String accountID, String userID, String note){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
            http.sendGet(RequestCode.UPDATEACCOUNTNOTE,WebUrlConfig.updateAccountNote(accountID, userID, note));
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
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
            updateAccountNote(accountID,MyApplication.userModel.getUserID(),mNote.getText().toString());
        }
    }
}
