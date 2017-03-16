package com.geek.springdemo.activity;

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
import com.geek.springdemo.adapter.MainLeftAdapter;
import com.geek.springdemo.http.HttpUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private MainActivity mContext;//本类
    @ViewInject(R.id.mDrawerLayout)
    private DrawerLayout mDrawerLayout;// 抽屉布局
    @ViewInject(R.id.menu)
    private LinearLayout mMenuLeft;//左边菜单
    @ViewInject(R.id.listView)
    private ListView mLvLeft;//左侧listView
    @ViewInject(R.id.main_listView)
    private ListView mLvMain;//主体listView
    private HttpUtil http;

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

        initLeftData();
    }

    /**
     * 初始化主体数据
     */
    private void initMainData(){

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
    }
}
