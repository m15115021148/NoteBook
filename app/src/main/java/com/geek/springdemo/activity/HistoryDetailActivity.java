package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.adapter.HistoryDetailListAdapter;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.rxjava.ProgressSubscriber;
import com.geek.springdemo.rxjava.RetrofitUtil;
import com.geek.springdemo.rxjava.SubscriberOnNextListener;
import com.geek.springdemo.util.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * 历史数据 详情页面
 */
@ContentView(R.layout.activity_history_detail)
public class HistoryDetailActivity extends BaseActivity implements View.OnClickListener, HistoryDetailListAdapter.OnCallBackLook {
    private HistoryDetailActivity mContext;//本类
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    private int type;//类别
    private String kind, startTime, endTime,note;//类型 开始时间 结束时间 备注
    private List<AccountsModel> mList = new ArrayList<>();//数据
    @ViewInject(R.id.listView)
    private ListView mLv;//listView
    private int currPos = 0;//当前位置
    @ViewInject(R.id.more)
    private LinearLayout mChart;//统计图
    @ViewInject(R.id.content)
    private TextView content;//右侧标题
    private HistoryDetailListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initData();
    }

    private SubscriberOnNextListener mListener = new SubscriberOnNextListener<List<AccountsModel>>() {

        @Override
        public void onNext(List<AccountsModel> list, int requestCode) {
            if (requestCode == RequestCode.GETACCOUNTLIST) {
                mList.clear();
                mList = list;
                if (mList.size() > 0) {
                    mChart.setVisibility(View.VISIBLE);
                    initListData(mList);
                } else {
                    mChart.setVisibility(View.GONE);
                    mList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    MyApplication.setEmptyShowText(mContext, mLv, "暂无数据");
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
            mList.clear();
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
            MyApplication.setEmptyShowText(mContext, mLv, "暂无数据");
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        mBack.setOnClickListener(this);
        mTitle.setText("数据详情");
        mChart.setOnClickListener(this);
        content.setText("统计");

        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            mTitle.setText("收入详情");
        } else if (type == 1) {
            mTitle.setText("支出详情");
        }
        kind = getIntent().getStringExtra("kind");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        note = getIntent().getStringExtra("note");
        getAccountListData(
                MyApplication.userModel.getUserID(),
                String.valueOf(type).equals("2") ? "" : String.valueOf(type),
                kind.equals("全部") ? "" : kind,
                startTime, endTime, note,"");
    }

    /**
     * 得到账单列表
     */
    private void getAccountListData(String userID, String type, String kind, String startTime, String endTime,String note, String page) {
        RetrofitUtil.getInstance()
                .getAccountList(
                        userID,type,kind,startTime,endTime,note,page,
                        new ProgressSubscriber<List<AccountsModel>>(mListener,mContext,RequestCode.GETACCOUNTLIST)
                );
    }

    /**
     * 数据
     *
     * @param list
     */
    private void initListData(final List<AccountsModel> list) {
        mAdapter = new HistoryDetailListAdapter(mContext, list, this);
        mLv.setAdapter(mAdapter);
        mLv.setSelection(currPos);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack) {
            mContext.finish();
        }
        if (v == mChart) {
            if (mList.size() > 0) {
                Intent intent = new Intent(mContext, ChartsActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("kind", kind);
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                intent.putExtra("note",note);
                startActivity(intent);
            } else {
                ToastUtil.showBottomShort(mContext, "暂无数据，无法统计");
            }

        }
    }

    @Override
    public void onLookDetail(int pos) {
        currPos = pos;
        Intent intent = new Intent(mContext, AccountDetailActivity.class);
        intent.putExtra("AccountsModel", mList.get(pos));
        startActivity(intent);
    }
}
