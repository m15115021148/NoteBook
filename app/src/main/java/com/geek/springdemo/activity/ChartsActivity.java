package com.geek.springdemo.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.ChartModel;
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.RoundProgressDialog;
import com.github.mikephil.charting.animation.AnimationEasing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计图
 */
@ContentView(R.layout.activity_charts)
public class ChartsActivity extends BaseActivity implements View.OnClickListener {
    private ChartsActivity mContext;
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    private int type;//类别
    private String kind,startTime,endTime;//类型 开始时间 结束时间
    private RoundProgressDialog progressDialog;
    private HttpUtil http;
    @ViewInject(R.id.pieChart)
    private PieChart mPieChart;//饼形图
    private Typeface mTf;//字体样式
    private String[] mParties = null;
    private int[] sumColor = new int[]{
            Color.rgb(51, 204, 255), Color.rgb(253, 203, 76), Color.rgb(225, 10, 20),
            Color.rgb(220, 160, 40), Color.rgb(54, 107, 221), Color.rgb(195, 73, 17),
            Color.rgb(100, 50, 255), Color.rgb(225, 155, 23), Color.rgb(11, 200, 74),
            Color.rgb(67, 195, 17), Color.rgb(195, 73, 17), Color.rgb(194, 73, 106),
    };
    private int[] mColor = null;//颜色
    private double mSum ;//总金额

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
                    if (msg.arg1 == RequestCode.GETCHARTPIE){
                        List<ChartModel> list = ParserUtil.jsonToList(msg.obj.toString(), ChartModel.class);
                        initPieChart(list);
                    }
                    break;
                case HttpUtil.EMPTY:
                    if (msg.arg1 == RequestCode.GETCHARTPIE){

                    }
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
        mTitle.setText("统计图");
        if (http == null){
            http = new HttpUtil(handler);
        }
        type = getIntent().getIntExtra("type",0);
        if (type == 0){
            mTitle.setText("收入统计图");
        }else if (type==1){
            mTitle.setText("支出统计图");
        }
        kind = getIntent().getStringExtra("kind");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        getChart(
                MyApplication.userModel.getUserID(),
                String.valueOf(type).equals("2")?"":String.valueOf(type),
                kind.equals("全部")?"":kind,
                startTime,endTime
        );
    }

    /**
     * 得到统计图
     */
    private void getChart(String userID,String type,String kind,String startTime,String endTime){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
            http.sendGet(RequestCode.GETCHARTPIE, WebUrlConfig.getChartPie(userID, type, kind, startTime, endTime));
        } else {
            ToastUtil.showBottomShort(mContext, RequestCode.NOLOGIN);
        }
    }

    /**
     * 初始化饼形图 属性
     */
    private void initPieChart(List<ChartModel> list){
        mParties = new String[list.size()];
        mColor = new int[list.size()];
        for (int i=0;i<list.size();i++){
            mSum = mSum + Double.parseDouble(list.get(i).getMoney());
            mParties[i] = list.get(i).getKind();
            mColor[i] = sumColor[i];
        }

        mPieChart.setUsePercentValues(true);
        mPieChart.setHoleColorTransparent(true);
        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mPieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        mPieChart.setHoleRadius(60f);
//        mPieChart.setHoleColor(line_color[0]);
        mPieChart.setDescription("");
        mPieChart.setDrawCenterText(true);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(true);
        mPieChart.setCenterText("总金额为：\n"+mSum+"\t\t");//中间不显示文本
        mPieChart.setCenterTextSize(15f);
        mPieChart.setCenterTextColor(Color.BLACK);

        mPieChart.setDrawSliceText(false);//不显示文字

        //设置数据
        setPieData(list);

        mPieChart.animateXY(1500, 1500, AnimationEasing.EasingOption.EaseOutBack);
        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setFormSize(10f);
        l.setTextSize(10f);
        l.setTextColor(Color.BLACK);
        l.setLabels(mParties);

        mPieChart.invalidate();
    }

    /**
     * 饼形图数据模式
     */
    private void setPieData(List<ChartModel> list) {
        String result = "";//百分比
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(2);
        for (int i = 0; i < list.size(); i++) {
            yVals1.add(new Entry((float) ((float)(Double.parseDouble(list.get(i).getMoney()))/mSum*100),i));
//            result = result + format.format((float)(Double.parseDouble(list.get(i).getMoney()))/sum) + "  ";
        }
//        tvPie.setText(result);
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<Integer>();


        for (int c : mColor)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setDrawValues(true);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(mTf);
        mPieChart.setData(data);
        // undo all highlights
        mPieChart.highlightValues(null);

    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
    }
}
