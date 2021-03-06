package com.geek.springdemo.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.adapter.ChartLineAdapter;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.model.LineModel;
import com.geek.springdemo.model.PieModel;
import com.geek.springdemo.rxjava.ProgressSubscriber;
import com.geek.springdemo.rxjava.RetrofitUtil;
import com.geek.springdemo.rxjava.SubscriberOnNextListener;
import com.geek.springdemo.util.ToastUtil;
import com.github.mikephil.charting.animation.AnimationEasing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计图
 */
@ContentView(R.layout.activity_charts)
public class ChartsActivity extends BaseActivity implements View.OnClickListener, OnChartValueSelectedListener {
    private ChartsActivity mContext;
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    private int type;//类别
    private String kind, startTime, endTime,note;//类型 开始时间 结束时间
    @ViewInject(R.id.pieChart)
    private PieChart mPieChart;//饼形图
    private Typeface mTf;//字体样式
    private String[] mParties = null;
    private int[] sumColor = new int[]{
            Color.rgb(51, 204, 255), Color.rgb(253, 203, 76), Color.rgb(225, 10, 20),
            Color.rgb(220, 160, 40), Color.rgb(54, 107, 221), Color.rgb(195, 73, 17),
            Color.rgb(100, 50, 255), Color.rgb(225, 155, 23), Color.rgb(11, 200, 74),
            Color.rgb(67, 195, 17), Color.rgb(195, 73, 17), Color.rgb(194, 73, 106),
            Color.rgb(12, 33, 199), Color.rgb(200, 100, 50), Color.rgb(123, 232, 106),
    };
    private int[] mColor = null;//颜色
    private double mSum;//总金额
    @ViewInject(R.id.listView)
    private ListView mLineLv;//线形图 listView
    @ViewInject(R.id.layoutAnalyze)
    private RelativeLayout mAnalyze;//分析
    private List<PieModel> mPieList = new ArrayList<>();//饼形图 数据

    /**
     * 初始化数据
     */
    protected void initData() {
        mContext = this;
        mBack.setOnClickListener(this);
        mTitle.setText("统计图");
        mAnalyze.setOnClickListener(this);

        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            mTitle.setText("收入统计图");
        } else if (type == 1) {
            mTitle.setText("支出统计图");
        }
        kind = getIntent().getStringExtra("kind");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        note = getIntent().getStringExtra("note");
        getChart(
                MyApplication.userModel.getUserID(),
                String.valueOf(type).equals("2") ? "" : String.valueOf(type),
                kind.equals("全部") ? "" : kind,
                startTime, endTime,note
        );
    }

    /**
     * 得到统计图 饼形图
     */
    private void getChart(int userID, String type, String kind, String startTime, String endTime,String note) {
        RetrofitUtil.getInstance().getPieData(userID,type,kind,startTime,endTime,note,
                new ProgressSubscriber<List<PieModel>>(new SubscriberOnNextListener<List<PieModel>>() {
                    @Override
                    public void onNext(List<PieModel> pieModels, int requestCode) {
                        if (requestCode == RequestCode.GETCHARTPIE) {
                            mPieList.clear();
                            mPieList = pieModels;
                            initPieChart(mPieList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SocketTimeoutException) {
                            ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
                        } else if (e instanceof ConnectException) {
                            ToastUtil.showBottomLong(mContext,RequestCode.NOLOGIN);
                        } else {
                            ToastUtil.showBottomLong(mContext, "onError:"+ e.getMessage());
                        }
                    }
                },mContext,RequestCode.GETCHARTPIE));
    }

    /**
     * 得到统计图 饼形图
     */
    private void getLineChart(int userID, String type, String kind, String startTime, String endTime,String note) {
        RetrofitUtil.getInstance().getLineData(userID,type,kind,startTime,endTime,note,
                new ProgressSubscriber<List<LineModel>>(new SubscriberOnNextListener<List<LineModel>>() {
                    @Override
                    public void onNext(List<LineModel> lineModels, int requestCode) {
                        // XI线性
                        if (requestCode == RequestCode.GETCHARTLINE) {
                            List<LineModel> list = lineModels;
                            initLineData(list);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SocketTimeoutException) {
                            ToastUtil.showBottomLong(mContext, RequestCode.ERRORINFO);
                        } else if (e instanceof ConnectException) {
                            ToastUtil.showBottomLong(mContext,RequestCode.NOLOGIN);
                        } else {
                            ToastUtil.showBottomLong(mContext, "onError:"+ e.getMessage());
                        }
                    }
                },mContext,RequestCode.GETCHARTLINE));
    }

    /**
     * 初始化饼形图 属性
     */
    private void initPieChart(List<PieModel> list) {
        mParties = new String[list.size()];
        mColor = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
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
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setMaximumFractionDigits(2);
        mPieChart.setCenterText("总金额为：\n" + format.format(mSum) + "\t\t\t");//中间不显示文本
        mPieChart.setCenterTextSize(15f);
        mPieChart.setCenterTextColor(Color.BLACK);

        mPieChart.setDrawSliceText(false);//不显示文字
        mPieChart.setOnChartValueSelectedListener(this);
        //设置数据
        setPieData(list);

        mPieChart.animateXY(1500, 1500, AnimationEasing.EasingOption.EaseOutBack);
        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setFormSize(10f);
        l.setTextSize(8f);
        l.setTextColor(Color.BLACK);
        l.setLabels(mParties);

        mPieChart.invalidate();
    }

    /**
     * 饼形图数据模式
     */
    private void setPieData(List<PieModel> list) {
        String result = "";//百分比
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(2);
        for (int i = 0; i < list.size(); i++) {
            yVals1.add(new Entry((float) ((float) (Double.parseDouble(list.get(i).getMoney())) / mSum * 100), i));
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

    /**
     * 初始化线形图数据
     * @param list
     */
    private void  initLineData(List<LineModel> list){
        ChartLineAdapter adapter = new ChartLineAdapter(mContext,list,mColor);
        mLineLv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack) {
            mContext.finish();
        }
        if (v == mAnalyze) {
            if (mPieList.size() > 0) {
                getLineChart(
                        MyApplication.userModel.getUserID(),
                        String.valueOf(type).equals("2") ? "" : String.valueOf(type),
                        kind.equals("全部") ? "" : kind,
                        startTime, endTime,note
                );
            }
        }
    }

    @Override
    public void onValueSelected(Entry entry, int dataSetIndex, Highlight highlight) {
        if (entry == null) {
            return;
        }
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setMaximumFractionDigits(2);
        ToastUtil.showBottomShort(mContext, mParties[entry.getXIndex()] + "金额为：" + format.format(mSum * entry.getVal() / 100));
        Log.i("result",
                "Value: " + entry.getVal() + ", xIndex: " + entry.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
    }
}
