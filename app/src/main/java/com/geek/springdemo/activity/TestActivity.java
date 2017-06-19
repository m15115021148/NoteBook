package com.geek.springdemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.geek.springdemo.R;
import com.geek.springdemo.view.CustomWebView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_test)
public class TestActivity extends BaseActivity implements View.OnClickListener{
    private TestActivity mContext;
    @ViewInject(R.id.webView)
    private CustomWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        webView.loadHtmlString("assets://b.html");
//        webView.loadUrl("https://www.baidu.com/");
        webView.loadUrl("https://hao.360.cn/?a1004");
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 改写物理按键——返回的逻辑
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();// 返回上一页面
                return true;
            } else {
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
