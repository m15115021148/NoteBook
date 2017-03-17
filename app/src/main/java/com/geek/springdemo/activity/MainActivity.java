package com.geek.springdemo.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.geek.springdemo.R;
import com.geek.springdemo.adapter.MainContentAdapter;
import com.geek.springdemo.adapter.MainLeftAdapter;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.AccountsModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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
    private HttpUtil http;
    private List<AccountsModel> mList = new ArrayList<>();//数据


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
            switch (msg.what){
                case HttpUtil.SUCCESS:
                    break;
                case HttpUtil.EMPTY:
                    break;
                case HttpUtil.FAILURE:
                    break;
                case HttpUtil.LOADING:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化菜单
     */
    private void initData(){
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // 关闭手势滑动
        mDrawerLayout.setFocusableInTouchMode(false);//可以点击返回键
        mMenuLeft.setOnClickListener(this);
        mAccount.setOnClickListener(this);

        for (int i=0;i<8;i++){
            AccountsModel model = new AccountsModel();
            model.setType(String.valueOf(i%2));
            model.setTime("2017-3-17 10:22");
            model.setImage("");
            model.setMoney((5+i*2)+"");
            model.setKind("类型"+i);
            model.setNote("的健康减肥的风景"+i);
            mList.add(model);
        }

        initLeftData();
        initMainData(mList);
    }

    /**
     * 初始化主体数据
     */
    private void initMainData(List<AccountsModel> list){
        MainContentAdapter adapter = new MainContentAdapter(mContext,list,this);
        mLvMain.setAdapter(adapter);
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
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
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
            startActivity(intent);
        }
    }

    @Override
    public void onLooKDes(int pos) {

    }
}
