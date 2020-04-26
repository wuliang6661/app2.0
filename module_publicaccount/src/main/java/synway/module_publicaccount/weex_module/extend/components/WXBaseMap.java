package synway.module_publicaccount.weex_module.extend.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Arc;
import com.amap.api.maps.model.ArcOptions;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleHoleOptions;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.Gradient;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MultiPointItem;
import com.amap.api.maps.model.MultiPointOverlay;
import com.amap.api.maps.model.MultiPointOverlayOptions;
import com.amap.api.maps.model.NavigateArrow;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonHoleOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.WeightedLatLng;
import com.amap.api.maps.model.particle.ConstantRotationOverLife;
import com.amap.api.maps.model.particle.CurveSizeOverLife;
import com.amap.api.maps.model.particle.ParticleEmissionModule;
import com.amap.api.maps.model.particle.ParticleOverLifeModule;
import com.amap.api.maps.model.particle.ParticleOverlay;
import com.amap.api.maps.model.particle.ParticleOverlayOptions;
import com.amap.api.maps.model.particle.ParticleOverlayOptionsFactory;
import com.amap.api.maps.model.particle.RandomColorBetWeenTwoConstants;
import com.amap.api.maps.model.particle.RandomVelocityBetweenTwoConstants;
import com.amap.api.maps.model.particle.RectParticleShape;
import com.amap.api.maps.model.particle.SinglePointParticleShape;
import com.squareup.picasso.Picasso;
import com.synway.map.MapDrawManager;
import com.synway.map.MapManager;
import com.synway.map.MapMoveManager;
import com.synway.util.KeyGroundOverlayManager;
import com.synway.util.KeyNavigateManager;
import com.synway.util.KeyTileOverlayManager;
import com.synway.util.NoKeyGroundOverlayManager;
import com.synway.util.NoKeyNavigateManager;
import com.synway.util.NoKeyTileOverlayManager;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
//import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import synway.common.ThreadPool;
import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.R;
import synway.module_publicaccount.weex_module.beans.ArcBean;
import synway.module_publicaccount.weex_module.beans.ArrowBean;
import synway.module_publicaccount.weex_module.beans.CircleBean;
import synway.module_publicaccount.weex_module.beans.GroundOverlayBean;
import synway.module_publicaccount.weex_module.beans.HolePolygonBean;
import synway.module_publicaccount.weex_module.beans.MarkerBean;
import synway.module_publicaccount.weex_module.beans.MoveBean;
import synway.module_publicaccount.weex_module.beans.MultiPointBean;
import synway.module_publicaccount.weex_module.beans.ParticleOverlayBean;
import synway.module_publicaccount.weex_module.beans.PolygonBean;
import synway.module_publicaccount.weex_module.beans.PolylineBean;
import synway.module_publicaccount.weex_module.beans.TextBean;
import synway.module_publicaccount.weex_module.beans.TileOverlayBean;

import static com.amap.api.maps.model.PolylineOptions.LineJoinType.LineJoinBevel;
import static com.amap.api.maps.model.PolylineOptions.LineJoinType.LineJoinMiter;
import static com.amap.api.maps.model.PolylineOptions.LineJoinType.LineJoinRound;

/**
 * Created by huangxi
 * DATE :2018/12/28
 * Description ：高德地图属性设置
 */
public class WXBaseMap extends WXComponent<MapView> {
    public Context mContext;
    public MapView mMapView;
    public AMapOptions mAMapOptions = new AMapOptions();
    public  MapManager     mMapManager;
    public  MapDrawManager mMapDrawManager;
    public  MapMoveManager mMapMoveManager;
    private WXSDKInstance  mInstance;

//        public WXBaseMap(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
//            super(instance, dom, parent);
//            mInstance = instance;
//        }
    public WXBaseMap(WXSDKInstance instance, WXVContainer parent, BasicComponentData
            basicComponentData) {
        super(instance, parent, basicComponentData);
        mInstance = instance;
    }

    @Override
    protected MapView initComponentHostView(@NonNull Context context) {
        mContext = context;
        mMapView = new MapView(context, mAMapOptions);
        String path = BaseUtil.OFF_LINE_MAP_PATH;
        mMapManager = new MapManager(mMapView, mAMapOptions, path);
        mMapDrawManager = new MapDrawManager(mMapView, context);
        mMapMoveManager = new MapMoveManager(mMapView, context);
        return mMapView;
    }

    @Override
    public void bindData(WXComponent component) {
        super.bindData(component);
        if (mInstance != null) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Map<String, Object> params = new HashMap<>();
                    mInstance.fireGlobalEventCallback("bindData", params);
                }
            }, 500);
        }
    }

    @Override
    public void destroy() {
        if (mInstance != null) {
            Map<String, Object> params = new HashMap<>();
            mInstance.fireGlobalEventCallback("onWxDestroy", params);
        }
        super.destroy();
    }

    /**
     * 设置地图初始化时的地图位置，不设置，默认地图中心点为北京天安门, 缩放级别 默认 10
     * @param latitude  纬度
     * @param longitude 经度
     * @param zoomLevel 缩放级别 （3-19）
     */
    @JSMethod
    public void setCenterZoom(String latitude, String longitude, int zoomLevel) {
        if (mMapManager != null) {
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(longitude);
            LatLng latLng = new LatLng(lat, lon);
            mMapManager.newLatLng(latLng, zoomLevel);
            Log.d("WXBaseMap", "setCenter");
        }

    }

    /**
     * 设置地图初始化时的地图位置，不设置，默认地图中心点为北京天安门, 缩放级别 默认 10
     * @param latitude  纬度
     * @param longitude 经度
     */
    @JSMethod
    public void setCenter(String latitude, String longitude) {
        if (mMapManager != null) {
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(longitude);
            LatLng latLng = new LatLng(lat, lon);
            mMapManager.newLatLng(latLng);
        }
    }

    // 设置指南针是否可用，默认true
    @WXComponentProp(name = "compassEnabled")
    public void compassEnabled(String compassEnabled) {
        if (mMapManager != null) {
            if (compassEnabled.equals("true")) {
                mMapManager.compassEnabled(true);
            } else {
                mMapManager.compassEnabled(false);
            }
        }
    }

    // 设置地图是否可以通过手势进行旋转, 默认为true
    @WXComponentProp(name = "rotateGesturesEnabled")
    public void rotateGesturesEnabled(String rotateGesturesEnabled) {
        if (mMapManager != null) {
            if (rotateGesturesEnabled.equals("true")) {
                mMapManager.rotateGesturesEnabled(true);
            } else {
                mMapManager.rotateGesturesEnabled(false);
            }
        }
    }

    // 设置地图是否显示比例尺，默认为false
    @WXComponentProp(name = "scaleControlsEnabled")
    public void scaleControlsEnabled(String scaleControlsEnabled) {
        if (mMapManager != null) {
            if (scaleControlsEnabled.equals("true")) {
                mMapManager.scaleControlsEnabled(true);
            } else {
                mMapManager.scaleControlsEnabled(false);
            }
        }
    }

    // 设置地图是否可以通过手势滑动, 默认为true
    @WXComponentProp(name = "scaleControlsEnabled")
    public void scrollGesturesEnabled(String scrollGesturesEnabled) {
        if (mMapManager != null) {
            if (scrollGesturesEnabled.equals("true")) {
                mMapManager.scrollGesturesEnabled(true);
            } else {
                mMapManager.scrollGesturesEnabled(false);
            }
        }
    }

    // 设置地图是否可以通过手势倾斜(3D效果), 默认为true
    @WXComponentProp(name = "tiltGesturesEnabled")
    public void tiltGesturesEnabled(String tiltGesturesEnabled) {
        if (mMapManager != null) {
            if (tiltGesturesEnabled.equals("true")) {
                mMapManager.tiltGesturesEnabled(true);
            } else {
                mMapManager.tiltGesturesEnabled(false);
            }
        }
    }

    // 设置地图是否允许缩放,  默认为true
    @WXComponentProp(name = "zoomControlsEnabled")
    public void zoomControlsEnabled(String zoomControlsEnabled) {
        if (mMapManager != null) {
            if (zoomControlsEnabled.equals("true")) {
                mMapManager.zoomControlsEnabled(true);
            } else {
                mMapManager.zoomControlsEnabled(false);
            }
        }
    }

    // 设置地图是否可以通过手势进行缩放, 默认为true
    @WXComponentProp(name = "zoomGesturesEnabled")
    public void zoomGesturesEnabled(String zoomGesturesEnabled) {
        if (mMapManager != null) {
            if (zoomGesturesEnabled.equals("true")) {
                mMapManager.zoomGesturesEnabled(true);
            } else {
                mMapManager.zoomGesturesEnabled(false);
            }
        }
    }

    // 创建map
    @JSMethod
    public void createMap() {
        if (mMapManager != null) {
            Log.d("WXBaseMap", "createMap");
            mMapManager.createMap(Bundle.EMPTY);
        }
    }

    // ResumeMap
    @JSMethod
    public void onResumeMap() {
        if (mMapManager != null) {
            mMapManager.onResume();
        }
    }

    // PauseMap
    @JSMethod
    public void onPauseMap() {
        if (mMapManager != null) {
            mMapManager.onPause();
        }
    }

    // DestroyMap
    @JSMethod
    public void onDestroyMap() {
        if (mMapManager != null) {
            mMapManager.onDestroy();
        }
    }
    //-------------------------------------------------------------------------------------

    /**
     * 设置地图模式，默认普通地图模式
     * @param type 1：普通，2：导航，3：夜景，4：卫星，5：公交
     */
    @JSMethod
    public void setMapType(int type) {
        if (mMapManager != null) {
            Log.d("WXBaseMap", "setMapType");
            mMapManager.setMapType(type);
        }
    }

    /**
     * 设置是否打开定位图层按钮,默认false,false 将无法定位
     */
    @JSMethod
    public void setMyLocationEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setMyLocationEnabled(enable);
        }
    }

    /**
     * 设置是否显示3D建筑物，默认显示true
     */
    @JSMethod
    public void showBuildings(boolean enable) {
        if (mMapManager != null) {
            mMapManager.showBuildings(enable);
        }
    }

    /**
     * 设置是否显示室内地图，默认不显示false,感觉没啥卵用
     */
    @JSMethod
    public void showIndoorMap(boolean enable) {
        if (mMapManager != null) {
            mMapManager.showIndoorMap(enable);
        }
    }

    /**
     * 设置是否显示底图文字标注，默认显示true
     */
    @JSMethod
    public void showMapText(boolean enable) {
        if (mMapManager != null) {
            mMapManager.showMapText(enable);
        }
    }

    /**
     * 设置所有手势是否可用，默认true
     */
    @JSMethod
    public void setAllGesturesEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setAllGesturesEnabled(enable);
        }
    }

    /**
     * 设置指南针是否可见，默认true
     */
    @JSMethod
    public void setCompassEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setCompassEnabled(enable);
        }
    }

    /**
     * 设置是否以地图中心点缩放，默认true
     */
    @JSMethod
    public void setGestureScaleByMapCenter(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setGestureScaleByMapCenter(enable);
        }
    }

    /**
     * 设置室内地图楼层切换控件是否可见，默认true
     */
    @JSMethod
    public void setIndoorSwitchEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setIndoorSwitchEnabled(enable);
        }
    }

    /**
     * 设置定位按钮是否可见，默认false
     */
    @JSMethod
    public void showLocationButton(boolean enable) {
        if (mMapManager != null) {
            mMapManager.showLocationButton(enable);
        }
    }

    /**
     * 设置旋转手势是否可用，默认true
     */
    @JSMethod
    public void setRotateGesturesEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setRotateGesturesEnabled(enable);
        }
    }

    /**
     * 设置比例尺控件是否可见，默认true
     */
    @JSMethod
    public void setScaleControlsEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setScaleControlsEnabled(enable);
        }
    }

    /**
     * 设置拖拽手势是否可用，默认true
     */
    @JSMethod
    public void setScrollGesturesEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setScrollGesturesEnabled(enable);
        }
    }

    /**
     * 设置倾斜手势是否可用，默认true
     */
    @JSMethod
    public void setTiltGesturesEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setTiltGesturesEnabled(enable);
        }
    }

    /**
     * 设置缩放按钮是否可见，默认true
     */
    @JSMethod
    public void setZoomControlsEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setZoomControlsEnabled(enable);
        }
    }

    /**
     * 设置双指缩放手势是否可用，默认true
     */
    @JSMethod
    public void setZoomGesturesEnabled(boolean enable) {
        if (mMapManager != null) {
            mMapManager.setZoomGesturesEnabled(enable);
        }
    }

    /**
     * 设置缩放按钮的位置，默认右下, 右边界中部: 1; 右下: 2
     */
    @JSMethod
    public void setZoomPosition(int type) {
        if (mMapManager != null) {
            mMapManager.setZoomPosition(type);
        }
    }

    /**
     * 设置地图是否显示
     * @param isEnable 是否显示
     */
    @JSMethod
    public void isVisibleMap(boolean isEnable) {
        if (mMapManager != null) {
            mMapManager.isVisibleMap(isEnable);
        }

    }

    //--------------------------------------------------------------------------------

    /**
     * 设置地图缩放级别
     * @param zoomLevel 缩放级别（3-19)
     * @param animated  是否动画
     */
    @JSMethod
    public void setZoomLevel(int zoomLevel, boolean animated) {
        if (mMapManager != null) {
            mMapManager.setZoomLevel(zoomLevel, animated);
        }
    }

    /**
     * 改变地图的中心点
     * @param latitude  纬度
     * @param longitude 经度
     * @param animated  是否动画
     */
    @JSMethod
    public void setLatLng(String latitude, String longitude, boolean animated) {
        if (mMapManager != null) {
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(longitude);
            LatLng latLng = new LatLng(lat, lon);
            mMapManager.setLatLng(latLng, animated);
        }
    }

    /**
     * 设置地图中心点以及缩放级别
     * @param latitude  纬度
     * @param longitude 经度
     * @param zoomLevel 缩放级别（3-19)
     * @param animated  是否动画
     */
    @JSMethod
    public void setLatLngZoom(String latitude, String longitude, int zoomLevel, boolean animated) {
        if (mMapManager != null) {
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(longitude);
            LatLng latLng = new LatLng(lat, lon);
            mMapManager.setLatLngZoom(latLng, zoomLevel, animated);
        }
    }

    /**
     * 设置地图的旋转角度
     * @param bearing  地图旋转角度。以角度为单位，正北方向为0度，逆时针范围从0度到360度
     * @param animated 是否动画
     */
    @JSMethod
    public void changeBearing(float bearing, boolean animated) {
        if (mMapManager != null) {
            mMapManager.changeBearing(bearing, animated);
        }
    }

    /**
     * 设置地图倾斜度
     * @param tilt     地图倾斜度。以角度为单位，范围（0,60）
     * @param animated 是否动画
     */
    @JSMethod
    public void changeTilt(float tilt, boolean animated) {
        if (mMapManager != null) {
            mMapManager.changeTilt(tilt, animated);
        }
    }

    /**
     * 从地图上删除所有的overlay（marker，circle，polyline 等对象）
     */
    @JSMethod
    public void clear() {
        if (mMapManager != null) {
            mMapManager.clear();
        }
        if(mMapDrawManager != null){
            mMapDrawManager.clearAll();
        }
    }

    /**
     * 从地图上删除所有的覆盖物（marker，circle，polyline 等对象），但myLocationOverlay（内置定位覆盖物）除外
     * @param isKeepMyLocationOverlay
     */
    @JSMethod
    public void clearAll(boolean isKeepMyLocationOverlay) {
        if (mMapManager != null) {
            mMapManager.clear(isKeepMyLocationOverlay);
        }
        if(mMapDrawManager != null){
            mMapDrawManager.clearAll();
        }
    }

    /**
     * 删除地图缓存
     */
    @JSMethod
    public void removecache() {
        if (mMapManager != null) {
            mMapManager.removecache();
        }
    }

    /**
     * 地图截图
     * @param path 存放路径
     */
    @JSMethod
    public void screenShotMap(String path) {
        if (mMapManager != null) {
            mMapManager.screenShotMap(path);
        }
    }

    /**
     * 重新加载地图引擎，即调用此接口时会重新加载底图数据，覆盖物不受影响。
     */
    @JSMethod
    public void reloadMap() {
        if (mMapManager != null) {
            mMapManager.reloadMap();
        }
    }

    /**
     * 获取屏幕中心点的坐标
     * @return double 中心点经度
     */
    @JSMethod
    public void getMapCenterPoint(JSCallback callback) {
        if (mMapManager != null) {
            LatLng latLng = mMapManager.getMapCenterPoint();
            Map<String, Object> params = new HashMap<>();
            params.put("latitude", latLng.latitude);
            params.put("longitude", latLng.longitude);
            callback.invoke(params);
        }
    }

    /**
     * 获取当前缩放级别下，地图上1像素点对应的长度，单位米
     * @return Float
     */
    @JSMethod
    public void getScalePerPixel(JSCallback callback) {
        if (mMapManager != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("scalePerPixel", mMapManager.getScalePerPixel());
            callback.invoke(params);
        }
    }

    /**
     * 地图画点
     *
     * @param marker MarkerBean对象
     */
    //    @JSMethod
    //    public void addMarker(final MarkerBean marker) {
    //
    //        final MarkerOptions option = new MarkerOptions();
    //        if (marker.alpha != null) {
    //            option.alpha(marker.alpha);
    //        }
    //        if (marker.draggable != null) {
    //            option.draggable(marker.draggable);
    //        }
    //        if (marker.imgUrl != null) {
    //            Picasso.with(mContext).load(marker.imgUrl).placeholder(com.synway.map.R
    // .drawable.ic_marker).error(com.synway.map.R.drawable.ic_marker)
    //                    .into(new Target() {
    //                        @Override
    //                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
    //                            option.icon((BitmapDescriptorFactory.
    //                                    fromView(mMapDrawManager.getMarkerView(mContext, marker
    // .title, marker.titleColor, marker.titleBg,
    //                                            bitmap))));
    //                        }
    //
    //                        @Override
    //                        public void onBitmapFailed(Drawable errorDrawable) {
    //                            option.icon((BitmapDescriptorFactory.
    //                                    fromView(mMapDrawManager.getMarkerView(mContext, marker
    // .title, marker.titleColor, marker.titleBg,
    //                                            null))));
    //
    //                        }
    //
    //                        @Override
    //                        public void onPrepareLoad(Drawable placeHolderDrawable) {
    //
    //                        }
    //                    });
    //        }
    //        if (marker.infoWindowEnable != null) {
    //            option.infoWindowEnable(marker.infoWindowEnable);
    //        }
    //        if (marker.latitude != null && marker.longitude != null) {
    //            LatLng latLng = new LatLng(marker.latitude, marker.longitude);
    //            option.position(latLng);
    //        }
    //        if (marker.imgUrls != null) {
    //            ArrayList<BitmapDescriptor> bitmapDescriptorList = new ArrayList<>();
    //            for (int i = 0; i < marker.imgUrls.size(); i++) {
    //                bitmapDescriptorList.add(BitmapDescriptorFactory
    //                        .fromView(mMapDrawManager.getPolylineView(mContext,
    //                                marker.imgUrls.get(i))));
    //            }
    //            option.icons(bitmapDescriptorList);
    //        }
    //        if (marker.period != null) {
    //            option.period(marker.period);
    //        }
    //        if (marker.rotate != null) {
    //            option.rotateAngle(marker.rotate);
    //        }
    //        if (marker.flat != null) {
    //            option.setFlat(marker.flat);
    //        }
    //        if (marker.isGps != null) {
    //            option.setGps(marker.isGps);
    //        }
    //        if (marker.offsetX != null && marker.offsetY != null) {
    //            option.setInfoWindowOffset(marker.offsetX, marker.offsetY);
    //        }
    //        if (marker.zIndex != null) {
    //            option.zIndex(marker.zIndex);
    //        }
    ////            if(marker.latLng!=null){
    ////                option.position(marker.latLng);
    ////            }
    //        if (marker.infoContent != null) {
    //            option.snippet(marker.infoContent);
    //        }
    //        if (marker.infoTitle != null) {
    //            option.title(marker.infoTitle);
    //        }
    //        if (mMapDrawManager != null) {
    //            mMapDrawManager.addMarker(marker.id, option);
    //        }
    //    }

    /**
     * 地图上画点
     */
    @JSMethod
    public void addMarkers(final List<MarkerBean> markers, final boolean moveToCenter) {
        for (int t = 0; t < markers.size(); t++) {
            final int i = t;
            ThreadPool.instance().execute(new Runnable() {
                @Override
                public void run() {
                    final List<String> ids = new ArrayList<>();
                    final ArrayList<MarkerOptions> options = new ArrayList<>();
                    ids.add(markers.get(i).id);
                    final MarkerOptions option = new MarkerOptions();
                    if (markers.get(i).alpha != null) {
                        option.alpha(markers.get(i).alpha);
                    }
                    if (markers.get(i).draggable != null) {
                        option.draggable(markers.get(i).draggable);
                    }
                    if (markers.get(i).imgUrl != null) {
                        if (markers.get(i).imgUrl.equals("")) {
                            option.icon((BitmapDescriptorFactory.
                                    fromView(mMapDrawManager.getMarkerView(mContext, markers.get
                                                    (i).title, markers.get(i).titleColor, markers
                                                    .get(i).titleBg,
                                            null))));
                        } else {
                            try {
                                Bitmap bitmap = Picasso.with(mContext).load(markers.get(i)
                                        .imgUrl).get();
                                option.icon((BitmapDescriptorFactory.
                                        fromView(mMapDrawManager.getMarkerView(mContext, markers
                                                        .get(i).title, markers.get(i).titleColor,
                                                markers.get(i).titleBg,
                                                bitmap))));

                            } catch (IOException e) {
                                option.icon((BitmapDescriptorFactory.
                                        fromView(mMapDrawManager.getMarkerView(mContext, markers
                                                        .get(i).title, markers.get(i).titleColor,
                                                markers.get(i).titleBg,
                                                null))));
                                e.printStackTrace();
                            }
                        }

                    }
                    if (markers.get(i).infoWindowEnable != null) {
                        option.infoWindowEnable(markers.get(i).infoWindowEnable);
                    }
                    if (markers.get(i).latitude != null && markers.get(i).longitude != null) {
                        LatLng latLng = new LatLng(markers.get(i).latitude, markers.get(i)
                                .longitude);
                        option.position(latLng);
                    }
                    if (markers.get(i).imgUrls != null) {
                        ArrayList<BitmapDescriptor> bitmapDescriptorList = new ArrayList<>();
                        for (int k = 0; k < markers.get(i).imgUrls.size(); k++) {
                            if (markers.get(i).imgUrls.get(k).equals("")) {
                                bitmapDescriptorList.add(BitmapDescriptorFactory.
                                        fromView(mMapDrawManager.getPolylineView(mContext,
                                                null)));
                            } else {
                                try {
                                    Bitmap bitmap = Picasso.with(mContext).load(markers.get(i)
                                            .imgUrls.get(k)).get();
                                    bitmapDescriptorList.add(BitmapDescriptorFactory.
                                            fromView(mMapDrawManager.getPolylineView(mContext,
                                                    bitmap)));

                                } catch (IOException e) {
                                    bitmapDescriptorList.add(BitmapDescriptorFactory.
                                            fromView(mMapDrawManager.getPolylineView(mContext,
                                                    null)));
                                    e.printStackTrace();
                                }
                            }
                        }
                        option.icons(bitmapDescriptorList);
                    }
                    if (markers.get(i).period != null) {
                        option.period(markers.get(i).period);
                    }
                    if (markers.get(i).rotate != null) {
                        option.rotateAngle(markers.get(i).rotate);
                    }
                    if (markers.get(i).flat != null) {
                        option.setFlat(markers.get(i).flat);
                    }
                    if (markers.get(i).isGps != null) {
                        option.setGps(markers.get(i).isGps);
                    }
                    if (markers.get(i).offsetX != null && markers.get(i).offsetY != null) {
                        option.setInfoWindowOffset(markers.get(i).offsetX, markers.get(i).offsetY);
                    }
                    if (markers.get(i).zIndex != null) {
                        option.zIndex(markers.get(i).zIndex);
                    }
                    if (markers.get(i).infoContent != null) {
                        option.snippet(markers.get(i).infoContent);
                    }
                    if (markers.get(i).infoTitle != null) {
                        option.title(markers.get(i).infoTitle);
                    }
                    if (markers.get(i).isVisible != null) {
                        option.visible(markers.get(i).isVisible);
                    }
                    options.add(option);


                    // 调用runOnUiThread更新主线程ＵＩ
                    getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("WXBaseMap", "SIZE:" + options.size());
                            if (mMapDrawManager != null) {
                                mMapDrawManager.addMarkers(ids, options, moveToCenter);
                            }
                        }
                    });
                }
            });

        }
//        final List<String> ids = new ArrayList<>();
//        final ArrayList<MarkerOptions> options = new ArrayList<>();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < markers.size(); i++) {
//                    ids.add(markers.get(i).id);
//                    final MarkerOptions option = new MarkerOptions();
//                    if (markers.get(i).alpha != null) {
//                        option.alpha(markers.get(i).alpha);
//                    }
//                    if (markers.get(i).draggable != null) {
//                        option.draggable(markers.get(i).draggable);
//                    }
//                    if (markers.get(i).imgUrl != null) {
//                        if (markers.get(i).imgUrl.equals("")) {
//                            option.icon((BitmapDescriptorFactory.
//                                    fromView(mMapDrawManager.getMarkerView(mContext, markers.get
//                                                    (i).title, markers.get(i).titleColor, markers
//                                                    .get(i).titleBg,
//                                            null))));
//                        } else {
//                            try {
//                                Bitmap bitmap = Picasso.with(mContext).load(markers.get(i)
//                                        .imgUrl).get();
//                                option.icon((BitmapDescriptorFactory.
//                                        fromView(mMapDrawManager.getMarkerView(mContext, markers
//                                                        .get(i).title, markers.get(i).titleColor,
//                                                markers.get(i).titleBg,
//                                                bitmap))));
//
//                            } catch (IOException e) {
//                                option.icon((BitmapDescriptorFactory.
//                                        fromView(mMapDrawManager.getMarkerView(mContext, markers
//                                                        .get(i).title, markers.get(i).titleColor,
//                                                markers.get(i).titleBg,
//                                                null))));
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                    if (markers.get(i).infoWindowEnable != null) {
//                        option.infoWindowEnable(markers.get(i).infoWindowEnable);
//                    }
//                    if (markers.get(i).latitude != null && markers.get(i).longitude != null) {
//                        LatLng latLng = new LatLng(markers.get(i).latitude, markers.get(i)
//                                .longitude);
//                        option.position(latLng);
//                    }
//                    if (markers.get(i).imgUrls != null) {
//                        ArrayList<BitmapDescriptor> bitmapDescriptorList = new ArrayList<>();
//                        for (int k = 0; k < markers.get(i).imgUrls.size(); k++) {
//                            if (markers.get(i).imgUrls.get(k).equals("")) {
//                                bitmapDescriptorList.add(BitmapDescriptorFactory.
//                                        fromView(mMapDrawManager.getPolylineView(mContext,
//                                                null)));
//                            } else {
//                                try {
//                                    Bitmap bitmap = Picasso.with(mContext).load(markers.get(i)
//                                            .imgUrls.get(k)).get();
//                                    bitmapDescriptorList.add(BitmapDescriptorFactory.
//                                            fromView(mMapDrawManager.getPolylineView(mContext,
//                                                    bitmap)));
//
//                                } catch (IOException e) {
//                                    bitmapDescriptorList.add(BitmapDescriptorFactory.
//                                            fromView(mMapDrawManager.getPolylineView(mContext,
//                                                    null)));
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                        option.icons(bitmapDescriptorList);
//                    }
//                    if (markers.get(i).period != null) {
//                        option.period(markers.get(i).period);
//                    }
//                    if (markers.get(i).rotate != null) {
//                        option.rotateAngle(markers.get(i).rotate);
//                    }
//                    if (markers.get(i).flat != null) {
//                        option.setFlat(markers.get(i).flat);
//                    }
//                    if (markers.get(i).isGps != null) {
//                        option.setGps(markers.get(i).isGps);
//                    }
//                    if (markers.get(i).offsetX != null && markers.get(i).offsetY != null) {
//                        option.setInfoWindowOffset(markers.get(i).offsetX, markers.get(i).offsetY);
//                    }
//                    if (markers.get(i).zIndex != null) {
//                        option.zIndex(markers.get(i).zIndex);
//                    }
//                    if (markers.get(i).infoContent != null) {
//                        option.snippet(markers.get(i).infoContent);
//                    }
//                    if (markers.get(i).infoTitle != null) {
//                        option.title(markers.get(i).infoTitle);
//                    }
//                    if (markers.get(i).isVisible != null) {
//                        option.visible(markers.get(i).isVisible);
//                    }
//                    options.add(option);
//                }
//
//
//                // 调用runOnUiThread更新主线程ＵＩ
//                getInstance().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("WXBaseMap", "SIZE:" + options.size());
//                        if (mMapDrawManager != null) {
//                            mMapDrawManager.addMarkers(ids, options, moveToCenter);
//                        }
//                    }
//                });
//
//            }
//        }).start();

    }

    /**
     * 清除所有点
     */
    @JSMethod
    public void cleanAllMarkers() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllMarkers();
        }
    }

    /**
     * 清除ID= 1点
     * @param id 唯一的ID
     */
    @JSMethod
    public void cleanMarkerById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanMarkerById(id);
        }
    }

    /**
     * 地图上画线
     */
    @JSMethod(uiThread = false)
    public void initPolyline(final PolylineBean polylineBean) {
        try {
            final List<LatLng> points = new ArrayList<>();
            if (polylineBean.isAdd) {
                for (int i = 0; i < polylineBean.latList.size(); i++) {
                    LatLng latLng = new LatLng(polylineBean.latList.get(i), polylineBean.lonList
                            .get(i));

                    points.add(latLng);
                }
                if (mMapDrawManager != null) {
                    mMapDrawManager.addPolylineLatLngs(polylineBean.id, points);
                }

            } else {
                final PolylineOptions option = new PolylineOptions();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (polylineBean.isGeodesic != null) {
                            option.geodesic(polylineBean.isGeodesic);
                        }
                        if (polylineBean.colorValues != null) {
                            option.colorValues(polylineBean.colorValues);
                        }
                        if (polylineBean.useGradient != null) {
                            option.useGradient(polylineBean.useGradient);
                        }
                        if (polylineBean.lineCapType != null) {
                            switch (polylineBean.lineCapType) {
                                case 0:
                                    option.lineCapType(PolylineOptions.LineCapType.LineCapButt);
                                    break;
                                case 1:
                                    option.lineCapType(PolylineOptions.LineCapType.LineCapSquare);
                                    break;
                                case 2:
                                    option.lineCapType(PolylineOptions.LineCapType.LineCapArrow);
                                    break;
                                case 3:
                                    option.lineCapType(PolylineOptions.LineCapType.LineCapRound);
                                    break;
                            }

                        }
                        if (polylineBean.lineJoinType != null) {
                            switch (polylineBean.lineJoinType) {
                                case 0:
                                    option.lineJoinType(LineJoinBevel);
                                    break;
                                case 1:
                                    option.lineJoinType(LineJoinMiter);
                                    break;
                                case 2:
                                    option.lineJoinType(LineJoinRound);
                                    break;
                            }

                        }
                        if (polylineBean.transparency != null) {
                            option.transparency(polylineBean.transparency);
                        }
                        if (polylineBean.isVisible != null) {
                            option.visible(polylineBean.isVisible);
                        }
                        if (polylineBean.zIndex != null) {
                            option.zIndex(polylineBean.zIndex);
                        }
                        if (polylineBean.alpha != null) {
                            option.transparency(polylineBean.alpha);
                        }
                        if (polylineBean.lineImgs != null) {
                            ArrayList<BitmapDescriptor> bitmapDescriptorList = new ArrayList<>();
                            for (int k = 0; k < polylineBean.lineImgs.size(); k++) {
                                if (polylineBean.lineImgs.get(k).equals("")) {
                                    bitmapDescriptorList.add(BitmapDescriptorFactory.
                                            fromView(mMapDrawManager.getPolylineView(mContext,
                                                    null)));
                                } else {
                                    try {
                                        Bitmap bitmap = Picasso.with(mContext).load(polylineBean
                                                .lineImgs.get(k)).get();
                                        bitmapDescriptorList.add(BitmapDescriptorFactory.
                                                fromView(mMapDrawManager.getPolylineView(mContext,
                                                        bitmap)));

                                    } catch (IOException e) {
                                        bitmapDescriptorList.add(BitmapDescriptorFactory.
                                                fromView(mMapDrawManager.getPolylineView(mContext,
                                                        null)));
                                        e.printStackTrace();
                                    }
                                }
                            }
                            option.setCustomTextureList(bitmapDescriptorList);
                        }
                        if (polylineBean.lineIndexs != null) {
                            option.setCustomTextureIndex(polylineBean.lineIndexs);
                        }
                        if (polylineBean.useTexture != null) {
                            option.setUseTexture(polylineBean.useTexture);
                        }
                        if (!TextUtils.isEmpty(polylineBean.color)) {
                            option.color(Color.parseColor(polylineBean.color));
                        }
                        if (polylineBean.isDottedLine != null) {
                            option.setDottedLine(polylineBean.isDottedLine);
                        }
                        if (polylineBean.dottedLineType != null) {
                            option.setDottedLineType(polylineBean.dottedLineType);
                        }
                        if (polylineBean.width != null) {
                            option.width(polylineBean.width);
                        }
                        if (!TextUtils.isEmpty(polylineBean.lineImg)) {
                            try {
                                Bitmap bitmap = Picasso.with(mContext).load(polylineBean.lineImg)
                                        .get();
                                option.setCustomTexture((BitmapDescriptorFactory.
                                        fromView(mMapDrawManager.getPolylineView(mContext,
                                                bitmap))));

                            } catch (IOException e) {
                                option.setCustomTexture((BitmapDescriptorFactory.
                                        fromView(mMapDrawManager.getPolylineView(mContext,
                                                null))));
                                e.printStackTrace();
                            }

                        }
                        if (polylineBean.latList != null && polylineBean.lonList != null) {
                            for (int i = 0; i < polylineBean.latList.size(); i++) {
                                LatLng latLng = new LatLng(polylineBean.latList.get(i),
                                        polylineBean.lonList.get(i));
                                points.add(latLng);
                            }
                        }
                        option.setPoints(points);
                        if (mMapDrawManager != null) {
                            mMapDrawManager.initPolyline(polylineBean.id, option);
                        }
                        // 调用runOnUiThread更新主线程ＵＩ
                        //                    getInstance().runOnUiThread(new Runnable() {
                        //                        @Override
                        //                        public void run() {
                        //
                        //                        }
                        //                    });

                    }
                }).start();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 清除所有线
     */
    @JSMethod
    public void cleanAllPolylines() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllPolylines();
        }
    }

    /**
     * 清除ID= id线
     * @param id 唯一的ID
     */
    @JSMethod
    public void cleanPolylineById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanPolylineById(id);
        }
    }

    /**
     * 隐藏或显示ID= id点
     * @param id        唯一的ID
     * @param isVisible true显示，false隐藏
     */
    @JSMethod
    public void setVisibMarkerById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibMarkerById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的Markers
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllMarkers(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllMarkers(isVisible);
        }
    }

    /**
     * 隐藏或显示ID= id 折线
     * @param id        唯一KEY
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibPolylineById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibPolylineById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的Polylines
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllPolylines(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllPolylines(isVisible);
        }
    }

    /**
     * 添加文字覆盖物
     * @param bean Text
     */
    @JSMethod
    public void addText(TextBean bean) {
        if (mMapDrawManager != null && bean != null) {
            TextOptions options = new TextOptions();
            if (bean.latitude != null && bean.longitude != null) {
                options.position(new LatLng(bean.latitude, bean.longitude));
            }

            if (!TextUtils.isEmpty(bean.align)) {
                if (bean.align.equals("0")) {
                    // 默认居中
                    options.align(1, 1);
                } else if (bean.align.equals("1")) {
                    // 水平对齐
                    options.align(1, 0);
                } else if (bean.align.equals("2")) {
                    // 垂直对齐
                    options.align(0, 1);
                } else if (bean.align.equals("3")) {
                    // 不对齐
                    options.align(0, 0);
                }
            }
            if (!TextUtils.isEmpty(bean.bgColor)) {
                options.backgroundColor(Color.parseColor(bean.bgColor));
            }

            if (!TextUtils.isEmpty(bean.fontColor)) {
                options.fontColor(Color.parseColor(bean.fontColor));
            }

            if (bean.fontSize != null && bean.fontSize.intValue() > 0) {
                options.fontSize(bean.fontSize);
            }

            if (bean.rotate != null) {
                options.rotate(bean.rotate);
            }

            if (bean.object != null) {
                options.setObject(bean.object);
            }

            if (bean.content != null) {
                options.text(bean.content);
            }

            if (bean.zIndex != null) {
                options.zIndex(bean.zIndex);
            }

            if (bean.isVisible != null) {
                options.visible(bean.isVisible);
            }
            mMapDrawManager.addText(bean.id, options);
        }
    }

    /**
     * 显示或隐藏指定Id的Text,只针对有Id
     * @param id        唯一KEY
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibTextById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibTextById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的Texts
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllTexts(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllTexts(isVisible);
        }
    }

    /**
     * 删除指定Id的文字标记（Text）,只针对有Id
     * @param id 唯一KEY
     */
    @JSMethod
    public void cleanTextById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanTextById(id);
        }
    }

    /**
     * 清除地图上所有的文字标记（text）
     */
    @JSMethod
    public void cleanAllTexts() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllTexts();
        }
    }

    /**
     * 在地图上添加一个圆（circle）对象。
     * @param bean 一个circleOptions对象，它定义circle的属性信息
     */
    @JSMethod
    public void addCircle(CircleBean bean) {
        try {
            if (mMapDrawManager != null && bean != null) {
                CircleOptions options = new CircleOptions();

                if (bean.latitude != null && bean.longitude != null) {
                    options.center(new LatLng(bean.latitude, bean.longitude));
                }

                if (bean.fillColor != null) {
                    options.fillColor(Color.parseColor(bean.fillColor));
                }

                if (bean.radius != null) {
                    options.radius(bean.radius);
                }

                if (bean.strokeDottedLineType != null) {
                    options.setStrokeDottedLineType(bean.strokeDottedLineType);
                }

                if (bean.strokeColor != null) {
                    options.strokeColor(Color.parseColor(bean.strokeColor));
                }

                if (bean.strokeWidth != null) {
                    options.strokeWidth(bean.strokeWidth);
                }

                if (bean.isVisible != null) {
                    options.visible(bean.isVisible);
                }

                if (bean.zIndex != null) {
                    options.zIndex(bean.zIndex);
                }

                if (bean.holeLatList != null && bean.holeLatList.size() != 0
                        && bean.holeLonList != null && bean.holeLonList.size() != 0
                        && bean.holeRadiusList != null && bean.holeRadiusList.size() != 0) {
                    for (int i = 0; i < bean.holeLonList.size(); i++) {
                        CircleHoleOptions holeOptions = new CircleHoleOptions();
                        holeOptions.radius(bean.holeRadiusList.get(i));
                        holeOptions.center(new LatLng(bean.holeLatList.get(i),
                                bean.holeLonList.get(i)));
                        options.addHoles(holeOptions);
                    }
                }
                if (!TextUtils.isEmpty(bean.holePolygon)) {
                    JSONObject jsonObject = JSONObject.parseObject(bean.holePolygon);
                    String holePolygons = jsonObject.getString("hole");
                    List<HolePolygonBean> list = JSONObject
                            .parseArray(holePolygons, HolePolygonBean.class);
                    if (list != null && list.size() != 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).points != null && list.get(i).points.size() != 0) {
                                PolygonHoleOptions holeOptions = new PolygonHoleOptions();
                                List<LatLng> latLngs = new ArrayList<>();
                                for (int j = 0; j < list.get(i).points.size(); j++) {
                                    LatLng latLng = new LatLng(list.get(i).points.get(j)
                                            .latitude, list.get(i).points.get(j).longitude);
                                    latLngs.add(latLng);
                                }
                                holeOptions.addAll(latLngs);
                                options.addHoles(holeOptions);
                            }
                        }
                    }
                }
                mMapDrawManager.addCircle(bean.id, options);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 显示或隐藏指定Id的Circle,只针对有Id
     * @param id        唯一KEY
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibCircleById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibCircleById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的Circles
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllCircles(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllCircles(isVisible);
        }
    }

    /**
     * 删除指定Id的圆（circle）,只针对有Id
     * @param id 唯一KEY
     */
    @JSMethod
    public void cleanCircleById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanCircleById(id);
        }
    }

    /**
     * 清除地图上所有的Circles圆
     */
    @JSMethod
    public void cleanAllCircles() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllCircles();
        }
    }

    /**
     * 在地图上添加一个圆弧（arc）对象。
     */
    @JSMethod
    public void addArc(ArcBean bean) {
        if (mMapDrawManager != null && bean != null) {
            ArcOptions options = new ArcOptions();
            if (bean.startLatitude != null && bean.startLongitude != null
                    && bean.passedLatitude != null && bean.passedLongitude != null
                    && bean.endLatitude != null && bean.endLongitude != null) {
                options.point(new LatLng(bean.startLatitude, bean.startLongitude),
                        new LatLng(bean.passedLatitude, bean.passedLongitude),
                        new LatLng(bean.endLatitude, bean.endLongitude));
            }

            if (bean.strokeColor != null) {
                options.strokeColor(Color.parseColor(bean.strokeColor));
            }

            if (bean.strokeWidth != null) {
                options.strokeWidth(bean.strokeWidth);
            }

            if (bean.isVisible != null) {
                options.visible(bean.isVisible);
            }

            if (bean.zIndex != null) {
                options.zIndex(bean.zIndex);
            }

            mMapDrawManager.addArc(bean.id, options);
        }
    }


    /**
     * 显示或隐藏指定Id的Arc,只针对有Id
     * @param id        唯一KEY
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibArcById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibArcById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的Arcs
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllArcs(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllArcs(isVisible);
        }
    }

    /**
     * 删除指定Id的圆弧（circle）,只针对有Id
     * @param id 唯一KEY
     */
    @JSMethod
    public void cleanArcById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanArcById(id);
        }
    }

    /**
     * 清除地图上所有的Arcs圆弧
     */
    @JSMethod
    public void cleanAllArcs() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllArcs();
        }
    }

    /**
     * 在地图上添加一个多边形（polygon）对象
     * @param bean
     */
    @JSMethod
    public void addPolygon(PolygonBean bean) {
        try {
            if (mMapDrawManager != null && bean != null) {
                PolygonOptions options = new PolygonOptions();

                if (bean.latList != null && bean.lonList != null
                        && bean.latList.size() != 0 && bean.lonList.size() != 0) {
                    List<LatLng> latLngs = new ArrayList<>();
                    for (int i = 0; i < bean.latList.size(); i++) {
                        LatLng latLng = new LatLng(bean.latList.get(i), bean.lonList.get(i));
                        latLngs.add(latLng);
                    }
                    options.addAll(latLngs);
                }

                if (bean.fillColor != null) {
                    options.fillColor(Color.parseColor(bean.fillColor));
                }

                if (bean.strokeColor != null) {
                    options.strokeColor(Color.parseColor(bean.strokeColor));
                }

                if (bean.strokeWidth != null) {
                    options.strokeWidth(bean.strokeWidth);
                }

                if (bean.isVisible != null) {
                    options.visible(bean.isVisible);
                }

                if (bean.zIndex != null) {
                    options.zIndex(bean.zIndex);
                }

                if (bean.holeLatList != null && bean.holeLatList.size() != 0
                        && bean.holeLonList != null && bean.holeLonList.size() != 0
                        && bean.holeRadiusList != null && bean.holeRadiusList.size() != 0) {
                    for (int i = 0; i < bean.holeLonList.size(); i++) {
                        CircleHoleOptions holeOptions = new CircleHoleOptions();
                        holeOptions.radius(bean.holeRadiusList.get(i));
                        holeOptions.center(new LatLng(bean.holeLatList.get(i),
                                bean.holeLonList.get(i)));
                        options.addHoles(holeOptions);
                    }
                }
                if (!TextUtils.isEmpty(bean.holePolygon)) {
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(bean.holePolygon);
                        String holePolygons = jsonObject.getString("hole");
                        List<HolePolygonBean> list = JSONObject
                                .parseArray(holePolygons, HolePolygonBean.class);
                        if (list != null && list.size() != 0) {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).points != null && list.get(i).points.size() != 0) {
                                    PolygonHoleOptions holeOptions = new PolygonHoleOptions();
                                    List<LatLng> latLngs = new ArrayList<>();
                                    for (int j = 0; j < list.get(i).points.size(); j++) {
                                        LatLng latLng = new LatLng(list.get(i).points.get(j)
                                                .latitude, list.get(i).points.get(j).longitude);
                                        latLngs.add(latLng);
                                    }
                                    holeOptions.addAll(latLngs);
                                    options.addHoles(holeOptions);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                mMapDrawManager.addPolygon(bean.id, options);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 显示或隐藏指定Id的Polygon,只针对有Id
     * @param id        唯一KEY
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibPolygonById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibPolygonById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的Polygons
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllPolygons(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllPolygons(isVisible);
        }
    }

    /**
     * 删除指定Id的多边形,只针对有Id
     * @param id 唯一KEY
     */
    @JSMethod
    public void cleanPolygonById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanPolygonById(id);
        }
    }

    /**
     * 清除地图上所有的Polygons多边形
     */
    @JSMethod
    public void cleanAllPolygons() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllPolygons();
        }
    }

    /**
     * 在地图上添加一个海量点覆盖物（MultiPointOverlay）对象
     * @param bean 一个海量点覆盖物（MultiPointOverlay）对象
     */
    @JSMethod(uiThread = false)
    public void addMultiPointOverlay(final MultiPointBean bean) {
        try {
            if (mMapDrawManager != null && bean != null) {
                MultiPointOverlayOptions option = new MultiPointOverlayOptions();
                if (bean.anchorU != null && bean.anchorV != null) {
                    option.anchor(bean.anchorU, bean.anchorV);
                }
                if (!TextUtils.isEmpty(bean.imgUrl)) {
                    try {
                        Bitmap bitmap = Picasso.with(mContext).load(bean.imgUrl)
                                .error(R.drawable.ic_marker_blue).get();
                        option.icon((BitmapDescriptorFactory.fromBitmap(bitmap)));
                    } catch (Exception e) {
                        BitmapDescriptor bitmapDescriptor =
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue);

                        option.icon(bitmapDescriptor);
                        e.printStackTrace();
                    }
                } else {
                    BitmapDescriptor bitmapDescriptor =
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue);
                    option.icon(bitmapDescriptor);
                }

                List<MultiPointItem> pointItems = new ArrayList<>();
                if (bean.latList != null && bean.latList.size() != 0
                        && bean.lonList != null && bean.lonList.size() != 0) {
                    for (int i = 0; i < bean.latList.size(); i++) {
                        LatLng latLng = new LatLng(bean.latList.get(i), bean.lonList.get
                                (i));
                        MultiPointItem pointItem = new MultiPointItem(latLng);
                        pointItems.add(pointItem);
                    }
                }

                mMapDrawManager.addMultiPointOverlay(option, pointItems);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 显示或隐藏地图上所有的MultiPointOverlay
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibMultiPointOverlay(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibMultiPointOverlay(isVisible);
        }
    }

    /**
     * 清除地图上所有的MultiPointOverlay
     */
    @JSMethod
    public void cleanAllMultiPointOverlays() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllMultiPointOverlays();
        }
    }

    /**
     * 在地图上添加一个粒子系统对象 -- 在地图上动画，天气效果等
     * @param bean
     */
    @JSMethod(uiThread = false)
    public void addParticleOverlay(ParticleOverlayBean bean) {
        if (mMapDrawManager != null && bean != null) {
            ParticleOverlayOptions options = new ParticleOverlayOptions();
            if (bean.weatherType != null) {
                List<ParticleOverlayOptions> optionsList =
                        ParticleOverlayOptionsFactory.defaultOptions(bean.weatherType);
                options = optionsList.get(0);
            } else {
                if (!TextUtils.isEmpty(bean.imgUrl)) {
                    try {
                        Bitmap bitmap = Picasso.with(mContext).load(bean.imgUrl).get();
                        options.icon((BitmapDescriptorFactory.fromBitmap(bitmap)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    Toast.makeText(getContext(), "缺少粒子图片", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (bean.isLoop != null) {
                options.setLoop(bean.isLoop);
            }

            if (bean.isVisibile != null) {
                options.setVisible(bean.isVisibile);
            }

            if (bean.duration != null) {
                options.setDuration(bean.duration);
            }

            if (bean.maxParticles != null) {
                options.setMaxParticles(bean.maxParticles);
            }

            if (bean.particleLifeTime != null) {
                options.setParticleLifeTime(bean.particleLifeTime);
            }

            if (bean.rate != null && bean.rateTime != null) {
                options.setParticleEmissionModule(new ParticleEmissionModule(bean.rate,
                        bean.rateTime));
            }

            if (bean.shapeX != null && bean.shapeY != null) {
                options.setParticleShapeModule(new SinglePointParticleShape(bean.shapeX,
                        bean.shapeY, 0, true));
            } else {
                if (bean.shapeLeft != null && bean.shapeTop != null
                        && bean.shapeRight != null && bean.shapeBottom != null) {
                    options.setParticleShapeModule(new RectParticleShape(bean.shapeLeft,
                            bean.shapeTop, bean.shapeRight, bean.shapeBottom, true));
                }
            }

            if (bean.speedX1 != null && bean.speedX2 != null && bean.speedY1 != null
                    && bean.speedY2 != null) {
                options.setParticleStartSpeed(new RandomVelocityBetweenTwoConstants(bean.speedX1,
                        bean.speedY1, 0, bean.speedX2, bean.speedY2, 0));
            }

            if (bean.color1 != null && bean.color2 != null) {
                int c1 = Color.parseColor(bean.color1);
                int c2 = Color.parseColor(bean.color2);
                options.setParticleStartColor(new RandomColorBetWeenTwoConstants(
                        Color.red(c1), Color.green(c1), Color.blue(c1), Color.alpha(c1),
                        Color.red(c2), Color.green(c2), Color.blue(c2), Color.alpha(c2)));
            }

            if (bean.isChange != null && bean.isChange) {
                ParticleOverLifeModule particleOverLifeModule = new ParticleOverLifeModule();
                if (bean.changeRotation != null) {
                    particleOverLifeModule
                            .setRotateOverLife(new ConstantRotationOverLife(bean.changeRotation));
                }

                if (bean.changeSpeedX1 != null && bean.changeSpeedX2 != null
                        && bean.changeSpeedY1 != null && bean.changeSpeedY2 != null) {
                    particleOverLifeModule.setVelocityOverLife(new 
                            RandomVelocityBetweenTwoConstants(
                            bean.changeSpeedX1, bean.changeSpeedY1, 0,
                            bean.changeSpeedX2, bean.changeSpeedY2, 0));
                }

                if (bean.changeColor1 != null && bean.changeColor2 != null) {
                    int c1 = Color.parseColor(bean.changeColor1);
                    int c2 = Color.parseColor(bean.changeColor2);
                    particleOverLifeModule.setColorGenerate(new RandomColorBetWeenTwoConstants(
                            Color.red(c1), Color.green(c1), Color.blue(c1), Color.alpha(c1),
                            Color.red(c2), Color.green(c2), Color.blue(c2), Color.alpha(c2)));
                }

                if (bean.changeSizeX != null && bean.changeSizeY != null) {
                    particleOverLifeModule.setSizeOverLife(new CurveSizeOverLife(
                            bean.changeSizeX, bean.changeSizeY, 0));
                }
                options.setParticleOverLifeModule(particleOverLifeModule);
            }

            if (bean.particleW != null && bean.particleH != null) {
                options.setStartParticleSize(bean.particleW, bean.particleH);
            }

            mMapDrawManager.addParticleOverlay(options);
        }
    }

    /**
     * 显示或隐藏地图上所有的粒子系统对象
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibParticleOverlay(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibParticleOverlay(isVisible);
        }
    }

    /**
     * 清除地图上所有的粒子系统对象
     */
    @JSMethod
    public void cleanAllParticleOverlays() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllParticleOverlays();
        }
    }

    /**
     * 在地图上添加一个热力图
     * @param bean
     */
    @JSMethod
    public void addTileOverlay(TileOverlayBean bean) {
        try {
            if (mMapDrawManager != null && bean != null) {
                TileOverlayOptions option = new TileOverlayOptions();
                HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();

                if (bean.isPower != null && bean.isPower) {
                    // 使用权值
                    if (bean.latList != null && bean.latList.size() != 0
                            && bean.lonList != null && bean.lonList.size() != 0) {
                        List<WeightedLatLng> weightedLatLngs = new ArrayList<>();
                        if (bean.powerList != null && bean.powerList.size() != 0) {
                            for (int i = 0; i < bean.latList.size(); i++) {
                                LatLng latLng = new LatLng(bean.latList.get(i),
                                        bean.lonList.get(i));
                                WeightedLatLng weight =
                                        new WeightedLatLng(latLng, bean.powerList.get(i));
                                weightedLatLngs.add(weight);
                            }
                        } else {
                            for (int i = 0; i < bean.latList.size(); i++) {
                                LatLng latLng = new LatLng(bean.latList.get(i),
                                        bean.lonList.get(i));
                                WeightedLatLng weight = new WeightedLatLng(latLng);
                                weightedLatLngs.add(weight);
                            }
                        }
                        builder.weightedData(weightedLatLngs);
                    }
                } else {
                    // 不使用权值
                    if (bean.latList != null && bean.latList.size() != 0
                            && bean.lonList != null && bean.lonList.size() != 0) {
                        List<LatLng> latLngs = new ArrayList<>();
                        for (int i = 0; i < bean.latList.size(); i++) {
                            LatLng latLng = new LatLng(bean.latList.get(i), bean.lonList.get(i));
                            latLngs.add(latLng);
                        }
                        builder.data(latLngs);
                    }
                }

                if (bean.colors != null && bean.colors.size() != 0
                        && bean.startPoints != null && bean.startPoints.length != 0) {
                    int[] colors = new int[bean.colors.size() + 1];
                    for (int i = 0; i < bean.colors.size(); i++) {
                        colors[i] = Color.parseColor(bean.colors.get(i));
                    }
                    Gradient gradient = new Gradient(colors, bean.startPoints);
                    builder.gradient(gradient);
                }

                if (bean.radius != null) {
                    builder.radius(bean.radius);
                }

                if (bean.alpha != null) {
                    builder.transparency(bean.alpha);
                }

                // 构造热力图对象
                HeatmapTileProvider heatmapTileProvider = builder.build();
                option.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者

                if (bean.isVisible != null) {
                    option.visible(bean.isVisible);
                }

                if (bean.zIndex != null) {
                    option.zIndex(bean.zIndex);
                }

                mMapDrawManager.addTileOverlay(bean.id, option);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 显示或隐藏指定Id的TileOverlay,只针对有Id
     * @param id        唯一KEY
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibTileOverlayById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibTileOverlayById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的TileOverlays
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllTileOverlays(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllTileOverlays(isVisible);
        }
    }

    /**
     * 删除指定Id的热力图覆盖物（TileOverlay）,只针对有Id
     * @param id 唯一KEY
     */
    @JSMethod
    public void cleanTileOverlayById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanTileOverlayById(id);
        }
    }

    /**
     * 清除地图上所有的热力图覆盖物（text）
     */
    @JSMethod
    public void cleanAllTileOverlays() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllTileOverlays();
        }
    }

    /**
     * 在地图上添加一个Ground覆盖物（groundOverlay）对象-图片
     * @param bean 一个groundOverlayOptions 对象，它定义了groundOverlay 的属性信息
     */
    @JSMethod(uiThread = false)
    public void addGroundOverlay(GroundOverlayBean bean) {
        try {
            if (mMapDrawManager != null && bean != null) {
                GroundOverlayOptions option = new GroundOverlayOptions();
                if (bean.anchorU != null && bean.anchorV != null) {
                    option.anchor(bean.anchorU, bean.anchorV);
                }

                if (bean.bearing != null) {
                    option.bearing(bean.bearing);
                }

                if (!TextUtils.isEmpty(bean.imgUrl)) {
                    try {
                        Bitmap bitmap = Picasso.with(mContext).load(bean.imgUrl).get();
                        option.image((BitmapDescriptorFactory.fromBitmap(bitmap)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    Toast.makeText(getContext(), "缺少图片覆盖物", Toast.LENGTH_LONG).show();
                    return;
                }

                if (bean.latitude != null && bean.longitude != null
                        && bean.imgHeight != null && bean.imgWidth != null) {
                    option.position(new LatLng(bean.latitude, bean.longitude), bean.imgWidth,
                            bean.imgHeight);
                }

                if (bean.alpha != null) {
                    option.transparency(bean.alpha);
                }

                if (bean.isVisible != null) {
                    option.visible(bean.isVisible);
                }

                if (bean.zIndex != null) {
                    option.zIndex(bean.zIndex);
                }
                mMapDrawManager.addGroundOverlay(bean.id, option);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 显示或隐藏指定Id的GroundOverlay,只针对有Id
     * @param id        唯一KEY
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibGroundOverlayById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibGroundOverlayById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的GroundOverlays
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllGroundOverlays(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllGroundOverlays(isVisible);
        }
    }

    /**
     * 删除指定Id的图片覆盖物（,只针对有Id
     * @param id 唯一KEY
     */
    @JSMethod
    public void cleanGroundOverlayById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanGroundOverlayById(id);
        }
    }

    /**
     * 清除地图上所有的图片覆盖物
     */
    @JSMethod
    public void cleanAllGroundOverlays() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllGroundOverlays();
        }
    }

    /**
     * 在地图上添加一个导航指示箭头对象（navigateArrow）对象
     * @param bean 一个NavigateArrowOptions对象，它定义NavigateArrow的属性信息
     */
    @JSMethod
    public void addNavigateArrow(ArrowBean bean) {
        if (mMapDrawManager != null && bean != null) {
            NavigateArrowOptions option = new NavigateArrowOptions();

            List<LatLng> latLngs = new ArrayList<>();
            if (bean.latList != null && bean.latList.size() != 0 && bean.lonList != null
                    && bean.lonList.size() != 0) {
                //                List<LatLng> latLngs = new ArrayList<>();
                for (int i = 0; i < bean.latList.size(); i++) {
                    LatLng latLng = new LatLng(bean.latList.get(i), bean.lonList.get(i));
                    latLngs.add(latLng);
                }

            }

            if (bean.topColor != null) {
                option.topColor(Color.parseColor(bean.topColor));
            }

            if (bean.isVisible != null) {
                option.visible(bean.isVisible);
            }

            if (bean.width != null) {
                option.width(bean.width);
            }

            if (bean.zIndex != null) {
                option.zIndex(bean.zIndex);
            }

            mMapDrawManager.addNavigateArrow(bean.id, latLngs, option);
        }
    }

    /**
     * 显示或隐藏指定Id的NavigateArrow,只针对有Id
     * @param id        唯一KEY
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibArrowById(String id, Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibArrowById(id, isVisible);
        }
    }

    /**
     * 显示或隐藏地图上所有的NavigateArrows
     * @param isVisible 是否显示
     */
    @JSMethod
    public void setVisibAllArrows(Boolean isVisible) {
        if (mMapDrawManager != null) {
            mMapDrawManager.setVisibAllArrows(isVisible);
        }
    }

    /**
     * 删除指定Id的导航箭头对象（navigateArrow）,只针对有Id
     * @param id 唯一KEY
     */
    @JSMethod
    public void cleanArrowById(String id) {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanArrowById(id);
        }
    }

    /**
     * 清除地图上所有的导航箭头对象（navigateArrow）
     */
    @JSMethod
    public void cleanAllArrows() {
        if (mMapDrawManager != null) {
            mMapDrawManager.cleanAllArrows();
        }
    }

    /** 动画轨迹滑动 **/

    /**
     * 添加滑动
     */
    @JSMethod(uiThread = false)
    public void addMove(MoveBean bean) {
        if (mMapMoveManager != null && mMapDrawManager != null & bean != null) {
            List<LatLng> points = new ArrayList<>();
            if (bean.hasPolyline != null && bean.hasPolyline) {
                points = mMapDrawManager.getLatLngsById(bean.polylineId);
            } else {
                if (bean.latList != null && bean.latList.size() != 0 && bean.lonList != null
                        && bean.lonList.size() != 0) {
                    for (int i = 0; i < bean.latList.size(); i++) {
                        LatLng latLng = new LatLng(bean.latList.get(i), bean.lonList.get(i));
                        points.add(latLng);
                    }
                }
            }
            MarkerOptions option = new MarkerOptions();
            try {
                if (TextUtils.isEmpty(bean.imgUrl)) {
                    option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_move_car));
                } else {
                    Bitmap bitmap = Picasso.with(mContext).load(bean.imgUrl).get();
                    option.icon((BitmapDescriptorFactory.fromBitmap(bitmap)));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            if (bean.alpha != null) {
                option.alpha(bean.alpha);
            }

            if (bean.infoTitle != null) {
                option.title(bean.infoTitle);
            }
            if (bean.infoContent != null) {
                option.snippet(bean.infoContent);
            }


            if (bean.infoWindowEnable != null) {
                option.infoWindowEnable(bean.infoWindowEnable);
            }

            mMapMoveManager.addMove(points, option, bean.time, bean.isStart);
        }
    }


    /**
     * 开启滑动
     */
    @JSMethod
    public void startMove() {
        if (mMapMoveManager != null) {
            mMapMoveManager.startMove();
        }
    }

    /**
     * 暂停滑动
     */
    @JSMethod
    public void stopMove() {
        if (mMapMoveManager != null) {
            mMapMoveManager.stopMove();
        }
    }

    /**
     * 重启滑动
     */
    @JSMethod
    public void reStartMove() {
        if (mMapMoveManager != null) {
            mMapMoveManager.reStartMove();
        }
    }

    /**
     * 销毁滑动
     */
    @JSMethod
    public void destoryMove() {
        if (mMapMoveManager != null) {
            mMapMoveManager.destoryMove();
        }
    }
}
