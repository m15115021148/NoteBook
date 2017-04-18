package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.adapter.HistoryDetailListAdapter;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.RoundProgressDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史数据 详情页面
 */
@ContentView(R.layout.activity_history_detail)
public class HistoryDetailActivity extends BaseActivity implements View.OnClickListener ,HistoryDetailListAdapter.OnCallBackLook{
    private HistoryDetailActivity mContext;//本类
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    private RoundProgressDialog progressDialog;
    private HttpUtil http;
    private int type;//类别
    private String kind,startTime,endTime;//类型 开始时间 结束时间
    private List<AccountsModel> mList = new ArrayList<>();//数据
    @ViewInject(R.id.listView)
    private ListView mLv;//listView
    private int currPos = 0;//当前位置
    @ViewInject(R.id.more)
    private LinearLayout mChart;//统计图
    @ViewInject(R.id.content)
    private TextView content;//右侧标题

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
                        mList.clear();
                        mList = ParserUtil.jsonToList(msg.obj.toString(),AccountsModel.class);
                        initListData(mList);
                    }
                    break;
                case HttpUtil.EMPTY:
                    if (msg.arg1 == RequestCode.GETACCOUNTLIST){
                        mList.clear();
                        MyApplication.setEmptyShowText(mContext,mLv,"暂无数据");
                    }
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
        mChart.setOnClickListener(this);
        mChart.setVisibility(View.VISIBLE);
        content.setText("统计");
        if (http == null){
            http = new HttpUtil(handler);
        }
        type = getIntent().getIntExtra("type",0);
        if (type == 0){
            mTitle.setText("收入详情");
        }else if (type==1){
            mTitle.setText("支出详情");
        }
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

    /**
     * 数据
     * @param list
     */
    private void initListData(final List<AccountsModel> list){
        HistoryDetailListAdapter adapter = new HistoryDetailListAdapter(mContext,list,this);
        mLv.setAdapter(adapter);
        mLv.setSelection(currPos);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
        if (v == mChart){
            if (mList.size()>0){
                Intent intent = new Intent(mContext,ChartsActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("kind",kind);
                intent.putExtra("startTime",startTime);
                intent.putExtra("endTime",endTime);
                startActivity(intent);
            }else{
                ToastUtil.showBottomShort(mContext,"暂无数据，无法统计");
            }

        }
    }

    @Override
    public void onLookDetail(int pos) {
        currPos = pos;
        Intent intent = new Intent(mContext,AccountDetailActivity.class);
        intent.putExtra("AccountsModel",mList.get(pos));
        startActivity(intent);
    }
}
