package com.synway.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.MyTrafficStyle;
import com.synway.map.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author XuKaiyin
 * @class com.synway
 * @name 地图模块管理
 * @describe
 * @time 2018/12/17 13:58
 */
public class MapManager {
    private MapView         mMapView         = null;
    private AMap            mAMap            = null;
    private AMapOptions     mAMapOptions     = null;
    private MyLocationStyle mMyLocationStyle = new MyLocationStyle();

    public MapManager(MapView mapView, AMapOptions aMapOptions, String mapPath) {
        if (!TextUtils.isEmpty(mapPath)) {
            // 指定离线地图路径
            MapsInitializer.sdcardDir = mapPath;
        }
        mMapView = mapView;
        mAMapOptions = aMapOptions;
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        Log.d("MapManager","new MapManager");
    }

    /**
     * 创建显示地图
     * @param bundle
     */
    public void createMap(Bundle bundle) {
        if (mMapView == null) {
            return;
        }
        mMapView.onCreate(bundle);
        hideLogo();
        Log.d("MapManager","createMap");
    }

    /** AMap_setting start */

    /**
     * 设置地图模式，默认普通地图模式 1
     * @param type
     */
    public void setMapType(int type) {
        if (mAMap == null) {
            return;
        }
        switch (type) {
            case 1:
                mAMap.setMapType(AMap.MAP_TYPE_NORMAL);// 设置普通地图模式
                break;
            case 2:
                mAMap.setMapType(AMap.MAP_TYPE_NAVI);// 设置导航地图模式
                break;
            case 3:
                mAMap.setMapType(AMap.MAP_TYPE_NIGHT);// 设置夜景地图模式
                break;
            case 4:
                mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置卫星地图模式
                break;
            case 5:
                mAMap.setMapType(AMap.MAP_TYPE_BUS);// 设置公交地图模式
                break;
            default:
                break;
        }
        Log.d("MapManager","setMapType");
    }

    /**
     * 设置是否打开定位图层
     * 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
     * @param enabled
     */
    public void setMyLocationEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.setMyLocationEnabled(enabled);
        Log.d("MapManager","setMyLocationEnabled");
    }

    /**
     * 设置定位图层（myLocationOverlay）的样式
     * @param model
     */
    public void setMapModel(int model) {
        if (mAMap == null || mMyLocationStyle == null) {
            return;
        }
        switch (model) {
            case 1://连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
                mMyLocationStyle.myLocationType(MyLocationStyle
                        .LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                break;
            case 2://连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
                mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                break;
            case 3://连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
                mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
                break;
            case 4://连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
                mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
                break;
            default:
                break;
        }
        mAMap.setMyLocationStyle(mMyLocationStyle);
        Log.d("MapManager","setMyLocationStyle");
    }

    /**
     * 设置是否打开交通路况图层
     * @param enabled 默认是false
     */
    public void setTrafficEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.setTrafficEnabled(enabled);
    }

    /**
     * 自定义交通路况图层的样式（各种路况对应的颜色）
     * @param style 路况拥堵情况对应的颜色, 只支持六位
     *              默认颜色分布为：
     *              畅通： 0xff00a209
     *              缓慢： 0xffff7508
     *              拥堵： 0xffea0312
     *              严重拥堵： 0xff92000a
     */
    public void setMyTrafficStyle(MyTrafficStyle style) {
        if (mAMap == null) {
            return;
        }
        mAMap.setMyTrafficStyle(style);
    }

    /**
     * 设置是否显示3D建筑物，默认显示
     * @param enabled
     */
    public void showBuildings(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.showBuildings(enabled);
        Log.d("MapManager","showBuildings");
    }

    /**
     * 设置是否显示室内地图，默认不显示
     * @param enabled
     */
    public void showIndoorMap(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.showIndoorMap(enabled);
        Log.d("MapManager","showIndoorMap");
    }

    /**
     * 设置是否显示底图文字标注，默认显示
     * @param enabled
     */
    public void showMapText(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.showMapText(enabled);
        Log.d("MapManager","showMapText");
    }

    /**
     * 设置地图显示范围，无论如何操作地图，显示区域都不能超过该矩形区域
     * @param southwest 西南角坐标
     * @param northeast 东北角坐标
     */
    public void setMapStatusLimits(LatLng southwest, LatLng northeast) {
        if (mAMap == null) {
            return;
        }
        // 通过过指定的两个经纬度坐标（左下、右上）构建的一个矩形区域
        LatLngBounds latLngBounds = new LatLngBounds(southwest, northeast);
        mAMap.setMapStatusLimits(latLngBounds);
        Log.d("MapManager","setMapStatusLimits");
    }

    /** AMap_setting end */


    /** AMapOptions（必须在createMap()方法之前才会有效）  start */

    /**
     * 设置地图初始化时的地图位置，不设置，默认地图中心点为北京天安门, 缩放级别 默认 10
     * @param latLng 中心点
     */
    public void newLatLng(LatLng latLng) {
        if (mAMapOptions == null) {
            return;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(10).build();
        mAMapOptions.camera(cameraPosition);
        Log.d("MapManager","newLatLng(LatLng latLng)");
    }

    /**
     * 设置地图初始化时的地图位置，不设置，默认地图中心点为北京天安门, 缩放级别 默认 10
     * @param latLng    中心点
     * @param zoomLevel 缩放级别 （3-19）
     */
    public void newLatLng(LatLng latLng, int zoomLevel) {
        if (mAMapOptions == null) {
            return;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(zoomLevel).build();
        mAMapOptions.camera(cameraPosition);
        Log.d("MapManager","newLatLng(LatLng latLng, int zoomLevel)");
    }

    /**
     * 设置指南针是否可用，默认true
     * @param enabled
     */
    public void compassEnabled(boolean enabled) {
        if (mAMapOptions == null) {
            return;
        }
        mAMapOptions.compassEnabled(enabled);
        Log.d("MapManager","compassEnabled");
    }

    /**
     * 设置地图是否可以通过手势进行旋转, 默认为true
     * @param enabled
     */
    public void rotateGesturesEnabled(boolean enabled) {
        if (mAMapOptions == null) {
            return;
        }
        mAMapOptions.rotateGesturesEnabled(enabled);
        Log.d("MapManager","rotateGesturesEnabled");
    }

    /**
     * 设置地图是否显示比例尺，默认为false
     * @param enabled
     */
    public void scaleControlsEnabled(boolean enabled) {
        if (mAMapOptions == null) {
            return;
        }
        mAMapOptions.scaleControlsEnabled(enabled);
        Log.d("MapManager","scaleControlsEnabled");
    }

    /**
     * 设置地图是否可以通过手势滑动, 默认为true
     * @param enabled
     */
    public void scrollGesturesEnabled(boolean enabled) {
        if (mAMapOptions == null) {
            return;
        }
        mAMapOptions.scrollGesturesEnabled(enabled);
        Log.d("MapManager","scrollGesturesEnabled");
    }

    /**
     * 设置地图是否可以通过手势倾斜(3D效果), 默认为true
     * @param enabled
     */
    public void tiltGesturesEnabled(boolean enabled) {
        if (mAMapOptions == null) {
            return;
        }
        mAMapOptions.tiltGesturesEnabled(enabled);
        Log.d("MapManager","tiltGesturesEnabled");
    }

    /**
     * 设置地图是否允许缩放,  默认为true
     * @param enabled
     */
    public void zoomControlsEnabled(boolean enabled) {
        if (mAMapOptions == null) {
            return;
        }
        mAMapOptions.zoomControlsEnabled(enabled);
        Log.d("MapManager","zoomControlsEnabled");
    }

    /**
     * 设置地图是否可以通过手势进行缩放, 默认为true
     * @param enabled
     */
    public void zoomGesturesEnabled(boolean enabled) {
        if (mAMapOptions == null) {
            return;
        }
        mAMapOptions.zoomGesturesEnabled(enabled);
        Log.d("MapManager","zoomGesturesEnabled");
    }

    /** AMapOptions  end */

    /** UiSettings start */

    /**
     * 隐藏Logo,默认不隐藏
     */
    public void hideLogo() {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setLogoBottomMargin(-50); // 隐藏高德地图logo
    }

    /**
     * 设置所有手势是否可用，默认true
     * @param enabled
     */
    public void setAllGesturesEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setAllGesturesEnabled(enabled);
    }

    /**
     * 设置指南针是否可见，默认true
     * @param enabled
     */
    public void setCompassEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setCompassEnabled(enabled);
    }

    /**
     * 设置是否以地图中心点缩放，默认true
     * @param isGestureScaleByMapCenter
     */
    public void setGestureScaleByMapCenter(boolean isGestureScaleByMapCenter) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setGestureScaleByMapCenter(isGestureScaleByMapCenter);
    }

    /**
     * 设置室内地图楼层切换控件是否可见，默认true
     * @param isIndoorSwitchEnabled
     */
    public void setIndoorSwitchEnabled(boolean isIndoorSwitchEnabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setIndoorSwitchEnabled(isIndoorSwitchEnabled);
    }

    /**
     * 设置定位按钮是否可见，默认false,不可见
     * @param enabled
     */
    public void showLocationButton(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setMyLocationButtonEnabled(enabled);
    }

    /**
     * 设置旋转手势是否可用，默认true
     * @param enabled
     */
    public void setRotateGesturesEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setRotateGesturesEnabled(enabled);
    }

    /**
     * 设置比例尺控件是否可见，默认true
     * @param enabled
     */
    public void setScaleControlsEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setScaleControlsEnabled(enabled);
    }

    /**
     * 设置拖拽手势是否可用，默认true
     * @param enabled
     */
    public void setScrollGesturesEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setScrollGesturesEnabled(enabled);
    }

    /**
     * 设置倾斜手势是否可用，默认true
     * @param enabled
     */
    public void setTiltGesturesEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setTiltGesturesEnabled(enabled);
    }

    /**
     * 设置缩放按钮是否可见，默认true
     * @param enabled
     */
    public void setZoomControlsEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setZoomControlsEnabled(enabled);
    }

    /**
     * 设置双指缩放手势是否可用，默认true
     * @param enabled
     */
    public void setZoomGesturesEnabled(boolean enabled) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setZoomGesturesEnabled(enabled);
    }

    /**
     * 设置缩放按钮的位置，默认右下2
     * @param position 右边界中部: 1; 右下: 2
     */
    public void setZoomPosition(int position) {
        if (mAMap == null) {
            return;
        }
        mAMap.getUiSettings().setZoomPosition(position);
    }

    /** UiSettings end */

    /** CameraUpdateFactory start */
    /**
     * 设置地图缩放级别
     * @param zoomLevel 缩放级别（3-19)
     * @param animated  是否动画
     */
    public void setZoomLevel(int zoomLevel, boolean animated) {
        if (mAMap == null) {
            return;
        }
        if (animated) {
            // 以动画方式按照传入的CameraUpdate参数更新地图状态，默认动画耗时250毫秒
            mAMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        } else {
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        }
    }

    /**
     * 改变地图的中心点
     * @param latLng   （纬度，经度) 中心点
     * @param animated 是否动画
     */
    public void setLatLng(LatLng latLng, boolean animated) {
        if (mAMap == null) {
            return;
        }
        if (animated) {
            mAMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
        } else {
            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        }
    }

    /**
     * 设置地图中心点以及缩放级别
     * @param latLng    （纬度，经度) 中心点
     * @param zoomLevel 缩放级别（3-19)
     * @param animated  是否动画
     */
    public void setLatLngZoom(LatLng latLng, int zoomLevel, boolean animated) {
        if (mAMap == null) {
            return;
        }
        if (animated) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        } else {
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
    }

    /**
     * 设置地图的旋转角度
     * @param bearing  地图旋转角度。以角度为单位，正北方向为0度，逆时针范围从0度到360度
     * @param animated 是否动画
     */
    public void changeBearing(float bearing, boolean animated) {
        if (mAMap == null) {
            return;
        }
        if (animated) {
            mAMap.animateCamera(CameraUpdateFactory.changeBearing(bearing));
        } else {
            mAMap.moveCamera(CameraUpdateFactory.changeBearing(bearing));
        }
    }

    /**
     * 设置地图倾斜度
     * @param tilt     地图倾斜度。以角度为单位，范围（0,60）
     * @param animated 是否动画
     */
    public void changeTilt(float tilt, boolean animated) {
        if (mAMap == null) {
            return;
        }
        if (animated) {
            mAMap.animateCamera(CameraUpdateFactory.changeTilt(tilt));
        } else {
            mAMap.moveCamera(CameraUpdateFactory.changeTilt(tilt));
        }
    }

    /** CameraUpdateFactory end */

    /** 基本功能方法 start */

    /**
     * 从地图上删除所有的overlay（marker，circle，polyline 等对象）
     */
    public void clear() {
        if (mAMap == null) {
            return;
        }
        mAMap.clear();
    }

    /**
     * 从地图上删除所有的覆盖物（marker，circle，polyline 等对象），但myLocationOverlay（内置定位覆盖物）除外
     * @param isKeepMyLocationOverlay
     */
    public void clear(boolean isKeepMyLocationOverlay) {
        if (mAMap == null) {
            return;
        }
        mAMap.clear(isKeepMyLocationOverlay);
    }

    /**
     * 触发地图立即刷新
     */
    public void runOnDrawFrame() {
        if (mAMap == null) {
            return;
        }
        mAMap.runOnDrawFrame();
    }


    /**
     * 重新加载地图引擎，即调用此接口时会重新加载底图数据，覆盖物不受影响。
     */
    public void reloadMap() {
        if (mAMap == null) {
            return;
        }
        mAMap.reloadMap();
    }

    /**
     * 删除地图缓存
     */
    public void removecache() {
        if (mAMap == null) {
            return;
        }
        mAMap.removecache();
    }

    /**
     * 地图截图
     * @param path 存放路径
     */
    public void screenShotMap(final String path) {
        if (mAMap == null || path == null) {
            return;
        }
        mAMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {

            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int status) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (null == bitmap) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(
                            path + "map_"
                                    + sdf.format(new Date()) + ".png");
                    boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    try {
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuffer buffer = new StringBuffer();
                    if (b)
                        buffer.append("截屏成功 ");
                    else {
                        buffer.append("截屏失败 ");
                    }
                    if (status != 0)
                        buffer.append("地图渲染完成，截屏无网格");
                    else {
                        buffer.append("地图未渲染完成，截屏有网格");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 得到Amap
     * @return AMap
     */
    public AMap getAMap() {
        return mAMap;
    }

    /**
     * 获取当前缩放级别下，地图上1像素点对应的长度，单位米
     * @return Float
     */
    public Float getScalePerPixel() {
        if(mAMap == null) {
            return null;
        }
        return mAMap.getScalePerPixel();
    }

    /**
     * 获取屏幕中心点的坐标
     * @return LatLng 中心点
     */
    public LatLng getMapCenterPoint() {
        if(mAMap == null || mMapView == null) {
            return null;
        }
        int left = mMapView.getLeft();
        int top = mMapView.getTop();
        int right = mMapView.getRight();
        int bottom = mMapView.getBottom();
        // 获得屏幕点击的位置
        int x = (int) (mMapView.getX() + (right - left) / 2);
        int y = (int) (mMapView.getY() + (bottom - top) / 2);
        Projection projection = mAMap.getProjection();
        LatLng pt = projection.fromScreenLocation(new Point(x, y));
        return pt;
    }
    /**
     * 设置地图是否可见
     *
     */
    public void isVisibleMap(boolean isEnable){
        if(mMapView!=null){
            if(isEnable){
                mMapView.setVisibility(View.VISIBLE);
            }else {
                mMapView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 基本功能方法 end
     */

    public void onDestroy() {
        if (mMapView == null) {
            return;
        }
        mMapView.onDestroy();
        Log.d("MapManager","onDestroy");
    }

    public void onResume() {
        if (mMapView == null) {
            return;
        }
        mMapView.onResume();
        Log.d("MapManager","onResume");
    }

    public void onPause() {
        if (mMapView == null) {
            return;
        }
        mMapView.onPause();
        Log.d("MapManager","onPause");
    }

    public void onSaveInstanceState(Bundle outState) {
        if (mMapView == null) {
            return;
        }
        mMapView.onSaveInstanceState(outState);
        Log.d("MapManager","onSaveInstanceState");
    }


}
