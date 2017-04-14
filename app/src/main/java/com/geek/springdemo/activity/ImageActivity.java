package com.geek.springdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.geek.springdemo.R;
import com.geek.springdemo.http.HttpImageUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 图片显示 页面
 */
@ContentView(R.layout.activity_image)
public class ImageActivity extends BaseActivity implements View.OnClickListener {
    private ImageActivity mContext;//本类
    @ViewInject(R.id.img)
    private ImageView img;//图片
    private String path;//路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        path = getIntent().getStringExtra("path");
        HttpImageUtil.loadImage(img, path);
        img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == img) {
            mContext.finish();
        }
    }
}
