package com.geek.springdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.adapter.MainContentAdapter;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.db.DBAccount;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.model.KindModel;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.RoundProgressDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 预记账 页面
 */
@ContentView(R.layout.activity_ready_account)
public class ReadyAccountActivity extends BaseActivity implements View.OnClickListener ,MainContentAdapter.OnCallBack{
    private ReadyAccountActivity mContext;//本类
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.more)
    private LinearLayout mAdd;//添加
    @ViewInject(R.id.content)
    private TextView content;//标题
    private RoundProgressDialog progressDialog;
    private HttpUtil http;
    private List<AccountsModel> mList = new ArrayList<>();//数据
    @ViewInject(R.id.main_listView)
    private ListView mLv;//listView
    private MainContentAdapter adapter;
    private int selPos = 0;//当前位置

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
                    if (msg.arg1== RequestCode.UPLOADACCOUNT){
                        ResultModel model = (ResultModel) ParserUtil.jsonToObject(msg.obj.toString(),ResultModel.class);
                        if (model.getResult().equals("1")){
                            ToastUtil.showBottomLong(mContext,"上传成功");
                            MyApplication.db.delOneAccount(Integer.parseInt(mList.get(selPos).getId()));
                            mList.remove(selPos);
                            adapter.notifyDataSetChanged();
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

    /**
     * 初始化数据
     */
    private void initData(){
        mBack.setOnClickListener(this);
        mTitle.setText("预记账");
        mAdd.setOnClickListener(this);
        mAdd.setVisibility(View.VISIBLE);
        content.setText("添加");
        if (http==null){
            http = new HttpUtil(handler);
        }
        mList.clear();
        Cursor cursor = MyApplication.db.queryDBCollectData();
        while(cursor.moveToNext()){
            AccountsModel model = new AccountsModel();
            model.setId(String.valueOf(cursor.getInt(0)));
            model.setType(String.valueOf(cursor.getInt(1)));
            model.setTime(cursor.getString(2));
            model.setMoney(cursor.getString(3));
            model.setKind(cursor.getString(4));
            model.setNote(cursor.getString(5));
            model.setLat(cursor.getString(6));
            model.setLng(cursor.getString(7));
            model.setAddress(cursor.getString(8));
            mList.add(model);
        }
        if (mList.size()>0&&mList!=null){
            initMainData(mList);
        }else{
            MyApplication.setEmptyShowText(mContext,mLv,"暂无数据");
        }
    }

    /**
     * 提交信息
     */
    private void upLoadAccount(String userID,String type,String kind,String money,String note,String time,String lat,String lng,String address){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
            http.sendGet(RequestCode.UPLOADACCOUNT, WebUrlConfig.upLoadAccount(userID, type, kind, money, note, time,lat,lng,address));
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
    }

    /**
     * 初始化主体数据
     */
    private void initMainData(List<AccountsModel> list){
        adapter = new MainContentAdapter(mContext,list,this);
        mLv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
        if (v == mAdd){
            Intent intent = new Intent(mContext,AccountActivity.class);
            intent.putExtra("inputType",1);// 预记账
            startActivityForResult(intent,101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101){
            mList.clear();
            Cursor cursor = MyApplication.db.queryDBCollectData();
            while(cursor.moveToNext()){
                AccountsModel model = new AccountsModel();
                model.setId(String.valueOf(cursor.getInt(0)));
                model.setType(String.valueOf(cursor.getInt(1)));
                model.setTime(cursor.getString(2));
                model.setMoney(cursor.getString(3));
                model.setKind(cursor.getString(4));
                model.setNote(cursor.getString(5));
                model.setLat(cursor.getString(6));
                model.setLng(cursor.getString(7));
                model.setAddress(cursor.getString(8));
                mList.add(model);
            }
            initMainData(mList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 上传到服务器
     * @param pos
     */
    @Override
    public void onLooKDes(int pos) {
        selPos = pos;
        AccountsModel model = mList.get(pos);
        upLoadAccount(MyApplication.userModel.getUserID(),model.getType(),model.getKind(),model.getMoney(),model.getNote(),model.getTime(),model.getLat(),model.getLng(),model.getAddress());
    }
}
