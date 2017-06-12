package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geek.springdemo.R;
import com.geek.springdemo.adapter.MainContentAdapter;
import com.geek.springdemo.adapter.MainLeftAdapter;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.http.HttpImageUtil;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.rxjava.ProgressSubscriber;
import com.geek.springdemo.rxjava.RetrofitUtil;
import com.geek.springdemo.rxjava.SubscriberOnNextListener;
import com.geek.springdemo.util.DateUtil;
import com.geek.springdemo.util.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener ,MainContentAdapter.OnCallBack{
    private MainActivity mContext;//本类
    @ViewInject(R.id.mDrawerLayout)
    private DrawerLayout mDrawerLayout;// 抽屉布局
    @ViewInject(R.id.menu)
    private LinearLayout mMenuLeft;//左边菜单
    @ViewInject(R.id.listView)
    private ListView mLvLeft;//左侧listView
    @ViewInject(R.id.main_listView)
    private ListView mLvMain;//主体listView
    @ViewInject(R.id.account)
    private LinearLayout mAccount;//记账
    private List<AccountsModel> mList = new ArrayList<>();//数据
    private String startTime = "";//开始时间
    private String endTime ="";//结束时间
    @ViewInject(R.id.userName)
    private TextView mUserName;//名称
    private long exitTime = 0;//退出的时间
    @ViewInject(R.id.header)
    private ImageView mHeader;//头像
    private int currPos = 0;//当前位置
    private MainContentAdapter mAdapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initData();
    }

    private SubscriberOnNextListener mListener = new SubscriberOnNextListener<List<AccountsModel>>() {

        @Override
        public void onNext(List<AccountsModel> accountsModels, int requestCode) {
            if (requestCode == RequestCode.GETACCOUNTLIST){
                mList.clear();
                mList = accountsModels;
                if (mList.size()>0){
                    initMainData(mList);
                }else{
                    mList.clear();
                    if (mAdapter!=null)
                        mAdapter.notifyDataSetChanged();
                    MyApplication.setEmptyShowText(mContext,mLvMain,"暂无数据");
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
                mList.clear();
                if (mAdapter!=null)
                    mAdapter.notifyDataSetChanged();
                MyApplication.setEmptyShowText(mContext,mLvMain,"暂无数据");
                ToastUtil.showBottomLong(mContext, "onError:"+ e.getMessage());
            }
        }
    };;


    /**
     * 初始化菜单
     */
    private void initData(){
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // 关闭手势滑动
        mDrawerLayout.setFocusableInTouchMode(false);//可以点击返回键
        mMenuLeft.setOnClickListener(this);
        mAccount.setOnClickListener(this);
        mHeader.setOnClickListener(this);

        mUserName.setText(MyApplication.userModel.getName());
        initLeftData();

    }

    @Override
    public void onResume() {
        super.onResume();
        HttpImageUtil.loadRoundImage(mHeader,MyApplication.userModel.getPhoto());
        startTime = DateUtil.getCurrentAgeTime(24*3);
        endTime = DateUtil.getCurrentDate();

        RetrofitUtil.getInstance()
                .getAccountList(
                MyApplication.userModel.getUserID(),"","",startTime,endTime,"",
                new ProgressSubscriber<List<AccountsModel>>(mListener,mContext,RequestCode.GETACCOUNTLIST)
        );

    }

    /**
     * 初始化主体数据
     */
    private void initMainData(final List<AccountsModel> list){
        mAdapter = new MainContentAdapter(mContext,list,this);
        mLvMain.setAdapter(mAdapter);
        mLvMain.setSelection(currPos);
        mLvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currPos = position;
                Intent intent = new Intent(mContext,AccountDetailActivity.class);
                intent.putExtra("AccountsModel",list.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化左侧数据
     */
    private void initLeftData(){
        MainLeftAdapter adapter = new MainLeftAdapter(mContext);
        mLvLeft.setAdapter(adapter);
        mLvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawers();
                switch (position){
                    case 0://历史数据查询
                        Intent intent = new Intent(mContext,HistoryCheckActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                    case 2://预记账
                        Intent ready = new Intent(mContext,ReadyAccountActivity.class);
                        startActivity(ready);
                        break;
                    case 3://设置
                        Intent set = new Intent(mContext,SetActivity.class);
                        startActivity(set);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mMenuLeft){
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
        if (v == mAccount){
            Intent intent = new Intent(mContext,AccountActivity.class);
            intent.putExtra("inputType",2);// 直接记账
            startActivity(intent);
        }
        if (v == mHeader){
            Intent intent = new Intent(mContext,UserActivity.class);
            startActivity(intent);
        }
    }

    /**
     *	退出activity
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //退出所有的activity
                Intent intent = new Intent();
                intent.setAction(BaseActivity.TAG_ESC_ACTIVITY);
                sendBroadcast(intent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLooKDes(int pos) {
    }

}
