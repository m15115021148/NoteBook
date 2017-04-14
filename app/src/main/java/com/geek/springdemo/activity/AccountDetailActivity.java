package com.geek.springdemo.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.geek.springdemo.R;
import com.geek.springdemo.adapter.ImageAdapter;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.util.MapUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.List;

/**
 * 账单详情页面 加修改
 */
@ContentView(R.layout.activity_account_detail)
public class AccountDetailActivity extends BaseActivity implements View.OnClickListener {
    private AccountDetailActivity mContext;//本类
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.bmapView)
    private MapView mMapView;
    private LatLng latLng;//点
    private MapUtil mapUtil;//地图工具
    private AccountsModel model;//数据
    @ViewInject(R.id.type)
    private TextView mType;//类别
    @ViewInject(R.id.kind)
    private TextView mKind;//类型
    @ViewInject(R.id.money)
    private TextView mMoney;
    @ViewInject(R.id.time)
    private TextView mTime;//时间
    @ViewInject(R.id.note)
    private TextView mNote;//描述
    @ViewInject(R.id.gridView)
    private GridView mGv;//图片适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        mBack.setOnClickListener(this);
        mTitle.setText("账单详情");
        model = (AccountsModel) getIntent().getSerializableExtra("AccountsModel");

        if (model.getType().equals("1")){
            mType.setText("支出");
        }else if (model.getType().equals("0")){
            mType.setText("收入");
        }
        mMoney.setText(Html.fromHtml("金额："+"<font color='#acacac'>"+model.getMoney()+"</font>"));
        mKind.setText(Html.fromHtml("类型："+"<font color='#acacac'>"+model.getKind()+"</font>"));
        mTime.setText(Html.fromHtml("时间："+"<font color='#acacac'>"+model.getTime()+"</font>"));
        mNote.setText(Html.fromHtml("描述："+"<font color='#acacac'>"+model.getNote()+"</font>"));
        String[] split = model.getImg().split(";");
        if (split!=null&&split.length>0){
            initImageData(Arrays.asList(split));
        }

        mapUtil = new MapUtil(mContext,mMapView);
        // 隐藏缩放控件
        mapUtil.hidezoomView();
        latLng = new LatLng(Double.parseDouble(model.getLat()),Double.parseDouble(model.getLng()));
        mapUtil.setMarkPoint(R.drawable.point,latLng);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
    }

    /**
     * 初始化图片数据
     */
    private void initImageData(List<String> list){
        ImageAdapter adapter = new ImageAdapter(mContext,list);
        mGv.setAdapter(adapter);
    }
}
