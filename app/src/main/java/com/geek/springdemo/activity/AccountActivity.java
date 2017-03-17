package com.geek.springdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.springdemo.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_account)
public class AccountActivity extends BaseActivity implements View.OnClickListener{
    private AccountActivity mContext;
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initData();
    }

    private void initData(){
        mBack.setOnClickListener(this);
        mTitle.setText("记账");
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
    }
}
