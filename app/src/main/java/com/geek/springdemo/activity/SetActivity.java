package com.geek.springdemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.util.DialogUtil;
import com.geek.springdemo.util.PreferencesUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 设置页面
 */
@ContentView(R.layout.activity_set)
public class SetActivity extends BaseActivity implements View.OnClickListener {
    private SetActivity mContext;//本类
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.version)
    private TextView mVersion;//版本
    @ViewInject(R.id.userExit)
    private TextView mExit;//退出用户
    private PackageManager pm;//获得PackageManager对象
    private View dialog;//dialog

    /**
     * 初始化数据
     */
    protected void initData(){
        mContext = this;
        mBack.setOnClickListener(this);
        mTitle.setText("设置");
        mExit.setOnClickListener(this);
        pm = getPackageManager();
        mVersion.setText("V" + getVersion());
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
        if (v == mExit){
            dialog = DialogUtil.customPromptDialog(mContext, "确定", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferencesUtil.isFristLogin(mContext,"first",true);
                    PreferencesUtil.setDataModel(mContext,"userModel","");
                    PreferencesUtil.setStringData(mContext,"phone","");
                    PreferencesUtil.setStringData(mContext,"psw","");
                    //退出所有的activity
                    Intent intent = new Intent(mContext,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, null);
            TextView txt = (TextView) dialog.findViewById(R.id.dialog_tv_txt);
            txt.setText("确定要退出当前用户吗？");
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
