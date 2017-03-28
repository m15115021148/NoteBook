package com.geek.springdemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.RoundProgressDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 历史数据 详情页面
 */
@ContentView(R.layout.activity_history_detail)
public class HistoryDetailActivity extends BaseActivity implements View.OnClickListener {
    private HistoryDetailActivity mContext;//本类
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    private RoundProgressDialog progressDialog;
    private HttpUtil http;
    private int type;//类别
    private String kind,startTime,endTime;//类型 开始时间 结束时间

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
                    if (msg.arg1 == RequestCode.GETACCOUNTLIST){

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
        mTitle.setText("数据详情");
        if (http == null){
            http = new HttpUtil(handler);
        }
        type = getIntent().getIntExtra("type",0);
        kind = getIntent().getStringExtra("kind");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        getAccountListData(
                MyApplication.userModel.getUserID(),
                String.valueOf(type).equals("2")?"":String.valueOf(type),
                kind.equals("全部")?"":kind,
                startTime,endTime,"");
    }

    /**
     * 得到账单列表
     */
    private void getAccountListData(String userID,String type,String kind,String startTime,String endTime,String page){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
            http.sendGet(RequestCode.GETACCOUNTLIST, WebUrlConfig.getAccountsList(userID, type, kind, startTime, endTime, page));
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
    }
}
