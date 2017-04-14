package com.geek.springdemo.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.KindModel;
import com.geek.springdemo.util.DateUtil;
import com.geek.springdemo.util.ParserUtil;
import com.geek.springdemo.util.ToastUtil;
import com.geek.springdemo.view.NumericWheelAdapter;
import com.geek.springdemo.view.OnWheelChangedListener;
import com.geek.springdemo.view.RoundProgressDialog;
import com.geek.springdemo.view.WheelTimeView;
import com.geek.springdemo.view.WheelView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 历史数据 查询页面
 */
@ContentView(R.layout.activity_history_check)
public class HistoryCheckActivity extends BaseActivity implements View.OnClickListener {
    private HistoryCheckActivity mContext;//本类
    @ViewInject(R.id.back)
    private LinearLayout mBack;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.income)
    private TextView mIncome;//收入
    @ViewInject(R.id.expend)
    private TextView mExpend;//支出
    @ViewInject(R.id.kind)
    private TextView mKind;//类型
    private int type = 0;// 类别选中的位置
    private int kindSelect = 0;//类型选中的位置
    private List<String> mValues = new ArrayList<>();//类型数据
    private List<KindModel> mKindList = new ArrayList<>();
    private String kind = "";//类型
    private RoundProgressDialog progressDialog;
    private HttpUtil http;
    @ViewInject(R.id.startTime)
    private TextView mStartTime;//开始时间
    @ViewInject(R.id.endTime)
    private TextView mEndTime;//结束时间
    @ViewInject(R.id.sure)
    private TextView mSure;//查询
    private String startTime,endTime;
    private Dialog dialog;//dialog
    private static int START_YEAR = 2000, END_YEAR = 2100;//起始年份，结束年份

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
                        mValues.add(0,"全部");
                        mKind.setText(mValues.get(kindSelect));
                        kind = mValues.get(kindSelect);
                    }
                    break;
                case HttpUtil.EMPTY:
                    if (msg.arg1 == RequestCode.GETKINDS){
                        mValues.clear();
                        mValues.add(0,"全部");
                        mKind.setText(mValues.get(kindSelect));
                        kind = mValues.get(kindSelect);
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
        mTitle.setText("查询");
        mIncome.setOnClickListener(this);
        mExpend.setOnClickListener(this);
        mIncome.setSelected(true);
        type=0;
        mExpend.setSelected(false);
        mKind.setOnClickListener(this);
        mKind.setText("请选择");
        mSure.setOnClickListener(this);
        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);
        startTime = DateUtil.getCurrentAgeTime(24*3);//3t的时间
        endTime = DateUtil.getCurrentDate();
        if (http == null){
            http = new HttpUtil(handler);
        }
        mStartTime.setText(startTime);
        mEndTime.setText(endTime);
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
            http.sendGet(RequestCode.GETKINDS, WebUrlConfig.getKinds());
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
        }
        if (v == mExpend){
            type = 1;
            mIncome.setSelected(false);
            mExpend.setSelected(true);
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
        if (v == mStartTime){
            showDateTimePicker(1);
        }
        if (v == mEndTime){
            showDateTimePicker(2);
        }
        if (v == mSure){
            if (DateUtil.getTwoTimeInterval(endTime,startTime)<0){
                ToastUtil.showBottomShort(mContext,"终止时间不能小于起始时间");
                return;
            }
            if (DateUtil.getTwoTimeInterval(endTime,startTime)>30*24*60*60){
                ToastUtil.showBottomShort(mContext,"时间差最大为30天");
                return;
            }
            Intent intent = new Intent(mContext,HistoryDetailActivity.class);
            intent.putExtra("type",type);
            intent.putExtra("kind",kind);
            intent.putExtra("startTime",startTime);
            intent.putExtra("endTime",endTime);
            startActivity(intent);
        }
    }

    /**
     * @Description:  弹出日期时间选择器
     * @param typeBt 类型
     */
    private void showDateTimePicker(final int typeBt) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
        String[] months_little = { "4", "6", "9", "11" };

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        dialog = new Dialog(this,R.style.TimeDateStyle);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 找到dialog的布局文件
        View view = getLayoutInflater().inflate(R.layout.time_layout, null);

        // 年
        final WheelTimeView wv_year = (WheelTimeView) view.findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wv_year.setCyclic(true);// 可循环滚动
        wv_year.setLabel("年");// 添加文字
        wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

        // 月
        final WheelTimeView wv_month = (WheelTimeView) view.findViewById(R.id.month);
        wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        wv_month.setCyclic(true);
        wv_month.setLabel("月");
        wv_month.setCurrentItem(month);

        // 日
        final WheelTimeView wv_day = (WheelTimeView) view.findViewById(R.id.day);
        wv_day.setCyclic(true);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 30));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumericWheelAdapter(1, 29));
            else
                wv_day.setAdapter(new NumericWheelAdapter(1, 28));
        }
        wv_day.setLabel("日");
        wv_day.setCurrentItem(day - 1);

        // 时
        final WheelTimeView wv_hours = (WheelTimeView) view.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        wv_hours.setCyclic(true);
        wv_hours.setLabel("时");
        wv_hours.setCurrentItem(hour);

        // 分
        final WheelTimeView wv_mins = (WheelTimeView) view.findViewById(R.id.mins);
        wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wv_mins.setCyclic(true);
        wv_mins.setLabel("分");
        wv_mins.setCurrentItem(minute);

        // 添加"年"监听
        OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
            public void onChanged(WheelTimeView wheel, int oldValue, int newValue) {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String
                        .valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(wv_month
                        .getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
                wv_month.setCurrentItem(0);
                wv_day.setCurrentItem(0);
            }
        };
        // 添加"月"监听
        OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
            public void onChanged(WheelTimeView wheel, int oldValue, int newValue) {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
                wv_day.setCurrentItem(0);
            }
        };
        wv_year.addChangingListener(wheelListener_year);
        wv_month.addChangingListener(wheelListener_month);

        // 根据屏幕密度来指定选择器字体的大小
        int textSize = 0;

        textSize = 30;

        wv_day.TEXT_SIZE = textSize;
        wv_hours.TEXT_SIZE = textSize;
        wv_mins.TEXT_SIZE = textSize;
        wv_month.TEXT_SIZE = textSize;
        wv_year.TEXT_SIZE = textSize;

        TextView btn_sure = (TextView) view.findViewById(R.id.btn_datetime_sure);
        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_datetime_cancel);
        // 确定
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 如果是个数,则显示为"02"的样式
                String parten = "00";
                DecimalFormat decimal = new DecimalFormat(parten);
                // 设置日期的显示
                if (typeBt==1){
                    startTime = (wv_year.getCurrentItem() + START_YEAR) + "-"
                            + decimal.format((wv_month.getCurrentItem() + 1)) + "-"
                            + decimal.format((wv_day.getCurrentItem() + 1)) + " "
                            + decimal.format(wv_hours.getCurrentItem()) + ":"
                            + decimal.format(wv_mins.getCurrentItem());
                    mStartTime.setText(startTime);
                }else {
                    endTime = (wv_year.getCurrentItem() + START_YEAR) + "-"
                            + decimal.format((wv_month.getCurrentItem() + 1)) + "-"
                            + decimal.format((wv_day.getCurrentItem() + 1)) + " "
                            + decimal.format(wv_hours.getCurrentItem()) + ":"
                            + decimal.format(wv_mins.getCurrentItem());
                    mEndTime.setText(endTime);
                }

                dialog.dismiss();
            }
        });
        // 取消
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        // 设置dialog的布局,并显示
        dialog.setContentView(view);
        dialog.show();
    }
}
