package com.synway.map;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.MovingPointOverlay;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author XuKaiyin
 * @class com.synway.map
 * @name
 * @describe
 * @time 2019/2/18 18:22
 */
public class MapMoveManager {
    private MapView mMapView = null;
    private AMap    mAMap    = null;
    private Context mContext;
    private Marker mMarker = null;
    private MovingPointOverlay mMovingPointOverlay = null;

    public MapMoveManager(MapView mapView, Context context) {
        mContext = context;
        mMapView = mapView;
        mMapView = mapView;
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
    }

    /**
     * 添加滑动数据
     * @param latLngs 滑动点组
     * @param options 滑动起点
     * @param time 设置滑动时间
     * @param isStart 是否开启滚动
     */
    public void addMove(List<LatLng> latLngs, MarkerOptions options, Integer time,
                        Boolean isStart) {
        if(mMovingPointOverlay != null) {
            // 清空滑动
            cleanMove();
        }
        if (latLngs == null || latLngs.size() == 0) {
            Toast.makeText(mContext, "请先设置路线", Toast.LENGTH_LONG).show();
            return;
        }
        if (options == null) {
            Toast.makeText(mContext, "请先设置移动点", Toast.LENGTH_LONG).show();
            return;
        }

        if (time == null || time == 0) {
            Toast.makeText(mContext, "速率不能为0", Toast.LENGTH_LONG).show();
            return;
        }

        // 滑动点
        mMarker = mAMap.addMarker(options);
        // 构建 轨迹的显示区域
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLngs.get(0));
        builder.include(latLngs.get(latLngs.size() - 2));
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
        // 实例 MovingPointOverlay 对象
        mMovingPointOverlay = new MovingPointOverlay(mAMap, mMarker);

        // 取轨迹点的第一个点 作为 平滑移动的启动
        LatLng drivePoint = latLngs.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(latLngs, drivePoint);
        latLngs.set(pair.first, drivePoint);
        List<LatLng> subList = latLngs.subList(pair.first, latLngs.size());


        // 设置轨迹点
        mMovingPointOverlay.setPoints(subList);

        // 根据速率和路程计算移动时间,设置平滑移动的总时间  单位 秒
        mMovingPointOverlay.setTotalDuration(time);

        // 设置  自定义的InfoWindow 适配器
        mAMap.setInfoWindowAdapter(infoWindowAdapter);
        // 设置移动的监听事件  返回 距终点的距离  单位 米
//        mMovingPointOverlay.setMoveListener(new MovingPointOverlay.MoveListener() {
//            @Override
//            public void move(final double distance) {
//            }
//        });

        if(isStart != null && isStart) {
            startMove();
        }
    }

    /**
     *  个性化定制的信息窗口视图的类
     *  如果要定制化渲染这个信息窗口，需要重载getInfoWindow(Marker)方法。
     *  如果只是需要替换信息窗口的内容，则需要重载getInfoContents(Marker)方法。
     */
    AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter(){

        // 个性化Marker的InfoWindow 视图
        // 如果这个方法返回null，则将会使用默认的信息窗口风格，内容将会调用getInfoContents(Marker)方法获取
        @Override
        public View getInfoWindow(Marker marker) {

            return getInfoWindowView(marker);
        }

        // 这个方法只有在getInfoWindow(Marker)返回null 时才会被调用
        // 定制化的view 做这个信息窗口的内容，如果返回null 将以默认内容渲染
        @Override
        public View getInfoContents(Marker marker) {

            return getInfoWindowView(marker);
        }
    };

    LinearLayout infoWindowLayout;
    TextView     title;
    TextView     snippet;

    /**
     * 自定义View并且绑定数据方法
     * @param marker 点击的Marker对象
     * @return  返回自定义窗口的视图
     */
    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(mContext);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(mContext);
            snippet = new TextView(mContext);
            title.setText(TextUtils.isEmpty(mMarker.getTitle()) ? "" : mMarker.getTitle());
            snippet.setText(TextUtils.isEmpty(mMarker.getSnippet()) ? "" : mMarker.getSnippet());
            title.setTextColor(Color.BLACK);
            title.setGravity(Gravity.CENTER);
            snippet.setTextColor(Color.BLACK);
            snippet.setGravity(Gravity.CENTER);
            infoWindowLayout.setBackgroundResource(R.drawable.infowindow_bg);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }

    /**
     * 开启滑动
     */
    public void startMove() {
        // 显示 infowindow
        if(mMovingPointOverlay !=  null && mMarker != null) {
            mMarker.showInfoWindow();
            mMovingPointOverlay.startSmoothMove();
        }
    }

    /**
     * 暂停滑动
     */
    public void stopMove() {
        if(mMovingPointOverlay !=  null) {
            mMovingPointOverlay.stopMove();
        }
    }

    /**
     * 重启滑动
     */
    public void reStartMove() {
        if(mMovingPointOverlay !=  null) {
            mMovingPointOverlay.startSmoothMove();
        }
    }

    /**
     * 清空滑动
     */
    public void cleanMove() {
        stopMove();
        destoryMove();
        mMovingPointOverlay = null;
        mMarker = null;
    }

    /**
     * 销毁滑动
     */
    public void destoryMove() {
        // 销毁平滑移动marker
        if(mMovingPointOverlay != null) {
            mMovingPointOverlay.setMoveListener(null);
            mMovingPointOverlay.destroy();
            mMovingPointOverlay.removeMarker();
        }
    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

//    public int computeTime(Double speed, List<LatLng> points) {
//        // 先计算出总路程
//        Double totalDistance = 0.0D;
//        for(int var9 = 0; var9 < points.size() - 1; ++var9) {
//            double var11 = (double) AMapUtils.calculateLineDistance((LatLng)points.get(var9),
//                    (LatLng) points.get(var9 + 1));
//            totalDistance += var11;
//        }
//        // 总路程/速率 = 时间
//        int time = (int)div(totalDistance, speed, 10);
//        return time;
//    }

}
