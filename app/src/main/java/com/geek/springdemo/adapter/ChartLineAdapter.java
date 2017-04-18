package com.geek.springdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.geek.springdemo.R;
import com.geek.springdemo.model.LineModel;
import com.geek.springdemo.view.MyMarkerView;
import com.github.mikephil.charting.animation.AnimationEasing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/4/18.
 */

public class ChartLineAdapter extends BaseAdapter {
    private Context mContext;
    private List<LineModel> mList;
    private Holder holder;
    private Typeface mTf;//字体样式
    private int[] mColor;// 颜色

    public ChartLineAdapter(Context context, List<LineModel> list,int[] color) {
        this.mContext = context;
        this.mList = list;
        this.mColor = color;
        mTf = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chart_line_item, null);
            holder = new Holder();
            holder.mChart = (LineChart) convertView.findViewById(R.id.lineChart);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        LineModel model = mList.get(position);

        initLineChartData(holder.mChart,model.getValue(),model,mColor[position]);

        return convertView;
    }

    private class Holder {
        LineChart mChart;
    }

    /**
     * 初始化线形图的数据
     */
    private void initLineChartData(LineChart mLineChart, List<LineModel.TimeModel> list,LineModel model,int color){
        // apply styling
        mLineChart.setDescription("");
        mLineChart.setDrawGridBackground(false);
        // enable value highlighting
        mLineChart.setHighlightEnabled(true);

        // enable touch gestures
        mLineChart.setTouchEnabled(true);

        // enable scaling and dragging
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view);
        mLineChart.setMarkerView(mv);
        mLineChart.setHighlightIndicatorEnabled(false);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(8f);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5);
        leftAxis.setStartAtZero(true);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(2);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);

        // set data
        mLineChart.setData(setLineData(list,model,color));
        mLineChart.animateX(2000, AnimationEasing.EasingOption.Linear);

        Legend l = mLineChart.getLegend();
//        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setTextColor(Color.BLACK);
        l.setTextSize(10f);
//        l.setForm(Legend.LegendForm.CIRCLE);
//        l.setLabels(Arrays.asList(model.getKind()));
        // do not forget to refresh the chart
        mLineChart.invalidate();

    }

    /**
     *设置线性图的数据
     * @return
     */
    private LineData setLineData(List<LineModel.TimeModel> list,LineModel model,int color) {
        List<String> time = new ArrayList<>();
        ArrayList<Entry> e = new ArrayList<>();
        for (int j= 0; j < list.size(); j++) {
            float val = Float.parseFloat(list.get(j).getMoney());
            e.add(new Entry(val,j));
            time.add(list.get(j).getTime());
        }

        LineDataSet set = new LineDataSet(e, model.getKind());
        set.setLineWidth(2f);
        set.setCircleSize(2f);
        set.setColor(color);
        set.setHighLightColor(color);
        set.setDrawValues(false);

        time.add("");//占位
        LineData cd = new LineData(time,set);
        return cd;
    }
}
