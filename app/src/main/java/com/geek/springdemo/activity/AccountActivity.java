package com.geek.springdemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.model.KindModel;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.util.DateUtil;
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.PreferencesUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.RoundProgressDialog;
import com.geek.springdemo.view.WheelView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ContentView(R.layout.activity_account)
public class AccountActivity extends BaseActivity implements View.OnClickListener{
    private AccountActivity mContext;
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.income)
    private TextView mIncome;//收入
    @ViewInject(R.id.expend)
    private TextView mExpend;//支出
    @ViewInject(R.id.money)
    private EditText mMoney;//金额
    @ViewInject(R.id.kind)
    private TextView mKind;//类型
    @ViewInject(R.id.note)
    private EditText mNote;//描述
    @ViewInject(R.id.more)
    private LinearLayout mSure;//保存
    @ViewInject(R.id.content)
    private TextView content;//内容
    private int type = 0;// 类别选中的位置
    private int kindSelect = 0;//类型选中的位置
    private RoundProgressDialog progressDialog;
    private HttpUtil http;
    private List<KindModel> mKindList = new ArrayList<>();
    private List<String> mValues = new ArrayList<>();//类型数据
    private String kind = "";//类型
    private int inputType = 0;//上级页面类型

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
                    if (msg.arg1 == RequestCode.GETKINDS){
                        mKindList.clear();
                        mKindList = ParserUtil.jsonToList(msg.obj.toString(),KindModel.class);
                        mValues.clear();
                        for (KindModel model:mKindList){
                            mValues.add(model.getKind());
                        }
                        mKind.setText(mValues.get(kindSelect));
                        kind = mValues.get(kindSelect);
                        //保存类型 信息
                        PreferencesUtil.setListData(mContext,"kind",mValues);
                    }
                    if (msg.arg1== RequestCode.UPLOADACCOUNT){
                        ResultModel model = (ResultModel) ParserUtil.jsonToObject(msg.obj.toString(),ResultModel.class);
                        if (model.getResult().equals("1")){
                            ToastUtil.showBottomLong(mContext,"记账成功");
                            setResult(100);
                            mContext.finish();
                        }else{
                            ToastUtil.showBottomLong(mContext,model.getErrorMsg());
                        }
                    }
                    break;
                case HttpUtil.EMPTY:
                    if (msg.arg1 == RequestCode.GETKINDS){
                        mValues = PreferencesUtil.getListData(mContext,"kind");
                        mKind.setText(mValues.get(kindSelect));
                        kind = mValues.get(kindSelect);
                    }
                    break;
                case HttpUtil.FAILURE:
                    ToastUtil.showBottomLong(mContext, "服务器无法连接，使用本地保存");
                    if (msg.arg1 == RequestCode.GETKINDS){
                        mValues = PreferencesUtil.getListData(mContext,"kind");
                        mKind.setText(mValues.get(kindSelect));
                        kind = mValues.get(kindSelect);
                    }
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
        mTitle.setText("记账");
        mIncome.setOnClickListener(this);
        mExpend.setOnClickListener(this);
        mIncome.setSelected(true);
        mExpend.setSelected(false);
        mKind.setOnClickListener(this);
        mSure.setOnClickListener(this);
        mSure.setVisibility(View.VISIBLE);
        content.setText("保存");
        mKind.setText("请选择");
        inputType = getIntent().getIntExtra("inputType",0);
        if (http == null){
            http = new HttpUtil(handler);
        }
        getKinds();
    }

    /**
     * 得到常用类型
     */
    private void getKinds(){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
            http.sendGet(RequestCode.GETKINDS,WebUrlConfig.getKinds());
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
    }

    /**
     * 提交信息
     */
    private void upLoadAccount(String userID,String type,String kind,String money,String note,String time){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
            http.sendGet(RequestCode.UPLOADACCOUNT,WebUrlConfig.upLoadAccount(userID, type, kind, money, note, time));
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
        if (v == mIncome){
            type = 0;
            mIncome.setSelected(true);
            mExpend.setSelected(false);
            mMoney.setTextColor(getResources().getColor(R.color.blue_dan));
        }
        if (v == mExpend){
            type = 1;
            mIncome.setSelected(false);
            mExpend.setSelected(true);
            mMoney.setTextColor(getResources().getColor(R.color.red_txt));
        }
        if (v == mKind){
            View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
            WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
            wv.setOffset(2);
            wv.setItems(mValues);
            wv.setSeletion(kindSelect);
            wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    kindSelect = selectedIndex - 2;
                }
            });

            new AlertDialog.Builder(this)
                    .setTitle("请选择类型")
                    .setView(outerView)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mKind.setText(mValues.get(kindSelect));
                            kind = mValues.get(kindSelect);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消",null)
                    .show();
        }
        if (v == mSure){
            if (TextUtils.isEmpty(mMoney.getText().toString())){
                ToastUtil.showBottomLong(mContext,"金额不能为空");
                return;
            }
            String time = DateUtil.getCurrentDate();//当前时间
            if (inputType==1){//预记账
                AccountsModel model = new AccountsModel();
                model.setType(String.valueOf(type));
                model.setKind(kind);
                model.setMoney(mMoney.getText().toString());
                model.setNote(mNote.getText().toString());
                model.setTime(time);
                MyApplication.db.insert(model);
                setResult(101);
                mContext.finish();
            }else{
                upLoadAccount(MyApplication.userModel.getUserID(),String.valueOf(type),kind,mMoney.getText().toString(),mNote.getText().toString(),time);
            }
        }
    }

}
