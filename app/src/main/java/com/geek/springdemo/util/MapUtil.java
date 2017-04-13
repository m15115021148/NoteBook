package com.geek.springdemo.util;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.geek.springdemo.R;

/**
 * @desc 地图处理 工具类
 * Created by chenmeng on 2017/3/23.
 */

public class MapUtil {
    private Context mContext;
    private MapView mMapView;
    private BaiduMap mBaiduMap;//百度地图对象
    private BaiduMap.OnMarkerClickListener markClick;// 地图标注点击事件
    private int level = 15;//地图显示等级
    private Location location;//定位对象

    public MapUtil(Context context,MapView mMapView){
        this.mContext = context;
        this.mMapView = mMapView;
        this.mBaiduMap = mMapView.getMap();
    }

    /**
     * 隐藏图标
     */
    public void hidezoomView() {
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        //地图上比例尺
        mMapView.showScaleControl(false);
        // 隐藏缩放控件
        mMapView.showZoomControls(false);
//        final int count = mMapView.getChildCount();
//        for (int i = 0; i < count; i++) {
//            View child = mMapView.getChildAt(i);
//            if (child instanceof ZoomControls) {
//                child.setVisibility(View.INVISIBLE);
//            }
//        }
    }

    /**
     * 自定义点标记
     *
     * @param point
     * @param adress
     * @param time
     * @param type   0默认点，1带点击事件
     */
    public void setPoint(LatLng point, String adress, String time, int type, String img, final int i) {
//        mBaiduMap.clear();
        // 创建InfoWindow展示的自定义view,显示详情对话框
        TextView button = setPop(adress);

        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromAssetWithDpi(img);

        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point)
                .icon(bitmap).zIndex(3);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.mapmark_layout, null);
//        TextView location_time = (TextView) contentView.findViewById(R.id.location_time);
//        TextView location_address = (TextView) contentView.findViewById(R.id.location_address);
//        location_address.setText(adress);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.hideInfoWindow();
            }
        });
        // 构建对话框用于显示
        // 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        final InfoWindow mInfoWindow = new InfoWindow(contentView, point, -20);

        markClick = new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
//                mBaiduMap.showInfoWindow(mInfoWindow);
//                Log.i("TAG", "getTrainID" + ltrain.get(i).getTrainID());
                return false;
            }
        };
        if (type == 1) {
//            location_time.setText(time);
//             显示InfoWindow
//            mBaiduMap.showInfoWindow(mInfoWindow);
            mBaiduMap.setOnMarkerClickListener(markClick);
        }
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        // 正常显示
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        if (type == 1) {
            // 定义地图状态
            updateStatus(point, level);
        } else {
            // 定义地图状态
            updateStatus(point, level);
        }

    }

    /**
     * 自定义地图对话框，展示详情
     *
     * @param address
     * @return
     */
    private TextView setPop(String address) {
        TextView button = new TextView(mContext);
        button.setBackgroundColor(Color.parseColor("#FFFFFF"));
        button.setTextSize(14);
        button.setTextColor(Color.parseColor("#333333"));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                200, LinearLayout.LayoutParams.MATCH_PARENT);
        button.setText(address);
        layoutParams.setMargins(50, 0, 50, 0);
        button.setLayoutParams(layoutParams);

        return button;
    }

    /**
     * update地图的状态与变化
     *
     * @param point
     * @param zoom
     */
    public void updateStatus(LatLng point, int zoom) {
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(zoom)
                .build();
        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate =
                MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    /**
     * 增加mark点
     */
    public void setMarkPoint(int res,LatLng point){
        Marker marker = null;
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(res);

        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point)
                .icon(bitmap).zIndex(2);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        // 正常显示
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        updateStatus(point,level);
    }

}
