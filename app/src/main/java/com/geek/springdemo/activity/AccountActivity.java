package com.geek.springdemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.geek.springdemo.R;
import com.geek.springdemo.application.MyApplication;
import com.geek.springdemo.config.RequestCode;
import com.geek.springdemo.config.WebUrlConfig;
import com.geek.springdemo.http.HttpUtil;
import com.geek.springdemo.model.AccountsModel;
import com.geek.springdemo.model.KindModel;
import com.geek.springdemo.model.ResultModel;
import com.geek.springdemo.util.DateUtil;
import com.geek.springdemo.util.LocationService;
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
    private String permissionInfo;// 定位权限
    private LocationService locationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initData();
    }

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        locationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
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
        // after andrioid m,must request Permiision on runtime
        getPersimmions();
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
    private void upLoadAccount(String userID,String type,String kind,String money,String note,String time,String lat,String lng,String address){
        if (MyApplication.getNetObject().isNetConnected()) {
            progressDialog = RoundProgressDialog.createDialog(mContext);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage("加载中...");
                progressDialog.show();
            }
            http.sendGet(RequestCode.UPLOADACCOUNT,WebUrlConfig.upLoadAccount(userID, type, kind, money, note, time,lat,lng,address));
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
                model.setLat(String.valueOf(MyApplication.lat));
                model.setLng(String.valueOf(MyApplication.lng));
                model.setAddress(MyApplication.address==null?"":MyApplication.address);
                MyApplication.db.insert(model);
                setResult(101);
                mContext.finish();
            }else{
                upLoadAccount(
                        MyApplication.userModel.getUserID(),
                        String.valueOf(type),kind,mMoney.getText().toString(),
                        mNote.getText().toString(),time,
                        String.valueOf(MyApplication.lat),String.valueOf(MyApplication.lng),MyApplication.address==null?"":MyApplication.address
                );
            }
        }
    }


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 127);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /**
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                MyApplication.lat = location.getLatitude();
                MyApplication.lng = location.getLongitude();
                MyApplication.address = location.getAddrStr();
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
//                    sb.append("\ngps status : ");
//                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
            }
        }

    };
}
