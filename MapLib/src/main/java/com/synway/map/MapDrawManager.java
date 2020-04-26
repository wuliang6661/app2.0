package com.synway.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Arc;
import com.amap.api.maps.model.ArcOptions;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MultiPointItem;
import com.amap.api.maps.model.MultiPointOverlay;
import com.amap.api.maps.model.MultiPointOverlayOptions;
import com.amap.api.maps.model.NavigateArrow;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.particle.ParticleOverlay;
import com.amap.api.maps.model.particle.ParticleOverlayOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.synway.adapter.InfoWinAdapter;
import com.synway.util.KeyGroundOverlayManager;
import com.synway.util.NoKeyGroundOverlayManager;
import com.synway.util.KeyArcManager;
import com.synway.util.KeyCircleManager;
import com.synway.util.KeyMarkerManager;
import com.synway.util.KeyNavigateManager;
import com.synway.util.KeyPolygonManager;
import com.synway.util.KeyPolylineManager;
import com.synway.util.KeyTextManager;
import com.synway.util.KeyTileOverlayManager;
import com.synway.util.MultiPointOverlayManager;
import com.synway.util.NoKeyArcManager;
import com.synway.util.NoKeyCircleManager;
import com.synway.util.NoKeyMarkerManager;
import com.synway.util.NoKeyNavigateManager;
import com.synway.util.NoKeyPolygonManager;
import com.synway.util.NoKeyPolylineManager;
import com.synway.util.NoKeyTextManager;
import com.synway.util.NoKeyTileOverlayManager;
import com.synway.util.ParticleOverlayManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author XuKaiyin
 * @class com.synway.map
 * @name 地图绘画管理, 轨迹纠偏
 * @describe
 * @time 2018/12/20 13:27
 */
public class MapDrawManager implements TraceListener {
    private MapView mMapView = null;
    private AMap mAMap = null;
    private LBSTraceClient mTraceClient = null;
    private Context mContext;
    private Marker clickMarker = null;

    public MapDrawManager(MapView mapView, Context context) {
        mContext = context;
        mMapView = mapView;
        mMapView = mapView;
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        setOnMapClick();
        setMarkerClick();
        setInfoWindow1(context);
    }

    /**
     * 对地图的点击事件
     */
    public void setOnMapClick() {
        if (mAMap == null) {
            return;
        }
        // 对Map点击事件，为了点除点以外，隐藏弹出窗
        mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //点击地图上没marker 的地方，隐藏inforwindow
                if (clickMarker != null) {
                    if (clickMarker.isInfoWindowShown()) {
                        clickMarker.hideInfoWindow();
                    }
                    clickMarker = null;
                }
            }
        });
    }

    /**
     * 获取当前地图可视区域范围所有marker对象。
     *
     * @return List<Marker> 当前地图可视区域范围的所有marker对象。
     */
    public List<Marker> getMapScreenMarkers() {
        if (mAMap == null) {
            return null;
        }
        return mAMap.getMapScreenMarkers();
    }

    /**
     * 在地图上添加一个圆弧（arc）对象。
     * @param id      Id 可能null
     * @param options 圆弧（arc）对象.它定义arc的属性信息
     */
    public void addArc(String id, ArcOptions options) {
        if (mAMap == null || options == null) {
            return;
        }
        Arc arc = mAMap.addArc(options);
        if (id != null) {
            // 对有keyId的圆集中管理
            KeyArcManager.getInstance().addArc(id, arc);
        } else {
            // 对无keyId的圆集中管理
            NoKeyArcManager.getInstance().addArc(arc);
        }
    }

    /**
     * 显示或隐藏指定Id的Arc,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibArcById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyArcManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的Arcs
     * @param isVisible 是否显示
     */
    public void setVisibAllArcs(Boolean isVisible) {
        KeyArcManager.getInstance().setAllVisib(isVisible);
        NoKeyArcManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 删除指定Id的圆弧（circle）,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanArcById(String id) {
        if (id == null) {
            return;
        }
        KeyArcManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的Arcs圆弧
     */
    public void cleanAllArcs() {
        KeyArcManager.getInstance().clearAll();
        NoKeyArcManager.getInstance().clearAll();
    }

    /**
     * 在地图上添加一个圆（circle）对象。
     * @param id      Id 可能null
     * @param options 一个circleOptions对象，它定义circle的属性信息
     */
    public void addCircle(String id, CircleOptions options) {
        if (mAMap == null || options == null) {
            return;
        }
        Circle circle = mAMap.addCircle(options);
        if (id != null) {
            // 对有keyId的圆集中管理
            KeyCircleManager.getInstance().addCircle(id, circle);
        } else {
            // 对无keyId的圆集中管理
            NoKeyCircleManager.getInstance().addCircle(circle);
        }
    }

    /**
     * 显示或隐藏指定Id的Circle,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibCircleById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyCircleManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的Circles
     * @param isVisible 是否显示
     */
    public void setVisibAllCircles(Boolean isVisible) {
        KeyCircleManager.getInstance().setAllVisib(isVisible);
        NoKeyCircleManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 删除指定Id的圆（circle）,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanCircleById(String id) {
        if (id == null) {
            return;
        }
        KeyCircleManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的Circles圆
     */
    public void cleanAllCircles() {
        KeyCircleManager.getInstance().clearAll();
        NoKeyCircleManager.getInstance().clearAll();
    }

    /**
     * 在地图上添一个点标记（marker）对象
     *
     * @param options 一个markerOptions 对象，它定义了marker 的属性信息
     * @param id      Id 可能null
     * @return Marker 点
     */
    public Marker addMarker(String id, MarkerOptions options) {
        if (mAMap == null || options == null) {
            return null;
        }
        Marker marker = mAMap.addMarker(options);
        if (id != null) {
            // 对有keyId,点集中管理
            KeyMarkerManager.getInstance().addMarker(id, marker);
        } else {
            // 对无keyId，点集中管理
            NoKeyMarkerManager.getInstance().addMarker(marker);
        }
        return marker;
    }

    /**
     * 在地图上添一组图片标记（marker）对象，
     * 并设置是否改变地图状态以至于所有的marker对象都在当前地图可视区域范围内显示。
     *
     * @param options      多个markerOptions对象，它们分别定义了对应marker的属性信息。
     * @param moveToCenter 是否改变地图状态，默认为false。
     * @param ids          Ids列表，可为null
     */
    public void addMarkers(List<String> ids, ArrayList<MarkerOptions> options, boolean moveToCenter) {
        if (mAMap == null || options == null) {
            return;
        }
        if (ids != null) {
            // 当id和markers的size一致，管理
            if (ids.size() == options.size()) {
                List<Marker> markers = mAMap.addMarkers(options, moveToCenter);
                // 对有keyId,点集中管理
                KeyMarkerManager.getInstance().addMarkers(ids, markers);
            }
        } else {
            List<Marker> markers = mAMap.addMarkers(options, moveToCenter);
            // 对无keyId，点集中管理
            NoKeyMarkerManager.getInstance().addMarkers(markers);
        }
    }

    /**
     * 移动一组点到中心
     * @param markers
     */
    public void moveMarkers(List<Marker> markers) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        for(int i=0;i<markers.size();i++){
            boundsBuilder.include(markers.get(i).getPosition());//把所有点都include进去（LatLng类型）
        }

        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15));//第二个参数为四周留空宽度
    }

    /**
     * 删除指定Id的Marker,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanMarkerById(String id) {
        if (id == null) {
            return;
        }
        KeyMarkerManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的Markers
     */
    public void cleanAllMarkers() {
        KeyMarkerManager.getInstance().clearAll();
        NoKeyMarkerManager.getInstance().clearAll();
    }

    /**
     * 显示或隐藏指定Id的Marker,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibMarkerById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyMarkerManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的Markers
     * @param isVisible 是否显示
     */
    public void setVisibAllMarkers(Boolean isVisible) {
        KeyMarkerManager.getInstance().setAllVisib(isVisible);
        NoKeyMarkerManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 设置marker点击事件监听接口。
     */
    public void setMarkerClick() {
        if (mAMap == null) {
            return;
        }
        // 点击Marker点击事件，点击marker,显示弹出窗，并保存点击Marker
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickMarker = marker;
                clickMarker.showInfoWindow();
                return true;
            }
        });
    }

    /**
     * 设置自定义InfoWindow1
     */
    public void setInfoWindow1(Context context) {
        if (mAMap == null) {
            return;
        }
        InfoWinAdapter adapter = new InfoWinAdapter(context);
        mAMap.setInfoWindowAdapter(adapter);
    }

    /**
     * 得到画点的显示的view
     *
     * @param context
     * @param marker  文字
     * @param color   文字颜色
     * @param bgColor 文字背景颜色
     * @param img     图片
     * @return
     */
    public View getMarkerView(final Context context, String marker, String color, String bgColor,
                              Bitmap img) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_marker, null);
        TextView mTvMarker = view.findViewById(R.id.tv_marker);
        final ImageView mIvMarker = view.findViewById(R.id.iv_marker);
        mTvMarker.setText(TextUtils.isEmpty(marker) ? "" : marker);
        if (!TextUtils.isEmpty(color)) {
            mTvMarker.setTextColor(Color.parseColor(color));
        }
        if (!TextUtils.isEmpty(bgColor)) {
            mTvMarker.setBackgroundColor(Color.parseColor(bgColor));
        }
        if (img==null) {
            mIvMarker.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_marker));
        } else {
            mIvMarker.setImageBitmap(img);
        }

        return view;
    }

    /**
     * 在地图上添加一个折线对象（polyline）对象
     *
     * @param id
     * @param option 一个polylineOptions对象，它定义polyline的属性信息
     */
    public void initPolyline(String id, PolylineOptions option) {
        if (mAMap == null || option == null) {
            return;
        }
        Polyline polyline = mAMap.addPolyline(option);
        if (id != null) {
            // 对有keyId,折线集中管理
            KeyPolylineManager.getInstance().addPolyline(id, polyline);
        } else {
            // 对无keyId，折线集中管理
            NoKeyPolylineManager.getInstance().addPolyline(polyline);
        }
    }

    /**
     * 在地图上延长折线到另一个点
     *
     * @param id      折线Id
     * @param latLngs 一组点
     */
    public void addPolylineLatLngs(String id, List<LatLng> latLngs) {
        if (mAMap == null || id == null || latLngs == null || latLngs.size() == 0) {
            return;
        }
        KeyPolylineManager.getInstance().addPolylineLatLngs(id, latLngs);
    }

    /**
     * 显示或隐藏指定Id的折线,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibPolylineById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyPolylineManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的Polylines
     * @param isVisible 是否显示
     */
    public void setVisibAllPolylines(Boolean isVisible) {
        KeyPolylineManager.getInstance().setAllVisib(isVisible);
        NoKeyPolylineManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 删除指定Id的Polyline,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanPolylineById(String id) {
        if (id == null) {
            return;
        }
        KeyPolylineManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的Polylines
     */
    public void cleanAllPolylines() {
        KeyPolylineManager.getInstance().clearAll();
        NoKeyPolylineManager.getInstance().clearAll();
    }

    /**
     * 显根据Id，获得折线上的点列表
     * @param id 唯一KEY
     * @return List<LatLng>
     */
    public List<LatLng> getLatLngsById(String id) {
        if (id == null) {
            return null;
        }
        return KeyPolylineManager.getInstance().getLatLngs(id);
    }

    /**
     * 得到线显示的view
     *
     * @param context
     * @param img     图片
     * @return
     */
    public View getPolylineView(final Context context, Bitmap img) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_polyline, null);
        final ImageView mIvPolyline = view.findViewById(R.id.iv_Polyline);
        if (img==null) {
            mIvPolyline.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_texture));
        } else {
            mIvPolyline.setImageBitmap(img);
        }
        return view;
    }

    /**
     * 在地图上添加一个多边形（polygon）对象
     * @param id      Id 可能null
     * @param options
     */
    public void addPolygon(String id, PolygonOptions options) {
        if (mAMap == null || options == null) {
            return;
        }
        Polygon polygon = mAMap.addPolygon(options);
        if (id != null) {
            // 对有keyId的多边形集中管理
            KeyPolygonManager.getInstance().addPolygon(id, polygon);
        } else {
            // 对无keyId的多边形集中管理
            NoKeyPolygonManager.getInstance().addPolygon(polygon);
        }
    }

    /**
     * 显示或隐藏指定Id的Polygon,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibPolygonById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyPolygonManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的Polygons
     * @param isVisible 是否显示
     */
    public void setVisibAllPolygons(Boolean isVisible) {
        KeyPolygonManager.getInstance().setAllVisib(isVisible);
        NoKeyPolygonManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 删除指定Id的多边形,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanPolygonById(String id) {
        if (id == null) {
            return;
        }
        KeyPolygonManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的Polygons多边形
     */
    public void cleanAllPolygons() {
        KeyPolygonManager.getInstance().clearAll();
        NoKeyPolygonManager.getInstance().clearAll();
    }

    /**
     * 在地图上添加一个导航指示箭头对象（navigateArrow）对象
     * @param id      Id 可能null
     * @param options 一个NavigateArrowOptions对象，它定义NavigateArrow的属性信息
     */
    public void addNavigateArrow(String id, List<LatLng> latLngs, NavigateArrowOptions options) {
        if (mAMap == null || options == null) {
            return;
        }
        NavigateArrow navigateArrow = mAMap.addNavigateArrow(options);
        navigateArrow.setPoints(latLngs);
        if (id != null) {
            // 对有keyId的导航指示箭头对象集中管理
            KeyNavigateManager.getInstance().addNavigateArrow(id, navigateArrow);
        } else {
            // 对无keyId的导航指示箭头对象集中管理
            NoKeyNavigateManager.getInstance().addNavigateArrow(navigateArrow);
        }

    }

    /**
     * 显示或隐藏指定Id的NavigateArrow,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibArrowById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyNavigateManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的NavigateArrows
     * @param isVisible 是否显示
     */
    public void setVisibAllArrows(Boolean isVisible) {
        KeyNavigateManager.getInstance().setAllVisib(isVisible);
        NoKeyNavigateManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 删除指定Id的导航箭头对象（navigateArrow）,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanArrowById(String id) {
        if (id == null) {
            return;
        }
        KeyNavigateManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的导航箭头对象（navigateArrow）
     */
    public void cleanAllArrows() {
        KeyNavigateManager.getInstance().clearAll();
        NoKeyNavigateManager.getInstance().clearAll();
    }


    /**
     * 在地图上添一个文字标记（text）对象
     * @param id      Id 可能null
     * @param options 一个textOptions 对象，它定义了text 的属性信息。
     */
    public void addText(String id, TextOptions options) {
        if (mAMap == null || options == null) {
            return;
        }
        options.align(0,1);
        Text text = mAMap.addText(options);
        if (id != null) {
            // 对有keyId的文字标记集中管理
            KeyTextManager.getInstance().addText(id, text);
        } else {
            // 对无keyId的文字标记集中管理
            NoKeyTextManager.getInstance().addText(text);
        }
    }

    /**
     * 显示或隐藏指定Id的Text,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibTextById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyTextManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的Texts
     * @param isVisible 是否显示
     */
    public void setVisibAllTexts(Boolean isVisible) {
        KeyTextManager.getInstance().setAllVisib(isVisible);
        NoKeyTextManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 删除指定Id的文字标记（Text）,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanTextById(String id) {
        if (id == null) {
            return;
        }
        KeyTextManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的文字标记（text）
     */
    public void cleanAllTexts() {
        KeyTextManager.getInstance().clearAll();
        NoKeyTextManager.getInstance().clearAll();
    }

    /**
     * 在地图上添加一个瓦片图层覆盖物（tileOverlay）对象 -- 热力图
     * @param id 唯一KEY
     * @param options 一个tileOverlayOptions 对象，它定义了tileOverlay的属性信息。
     */
    public void addTileOverlay(String id, TileOverlayOptions options) {
        if (mAMap == null || options == null) {
            return;
        }
        TileOverlay tileOverlay = mAMap.addTileOverlay(options);
        if (id != null) {
            // 对有keyId的瓦片图层覆盖物集中管理
            KeyTileOverlayManager.getInstance().addTileOverlay(id, tileOverlay);
        } else {
            // 对无keyId的瓦片图层覆盖物集中管理
            NoKeyTileOverlayManager.getInstance().addTileOverlay(tileOverlay);
        }
    }

    /**
     * 显示或隐藏指定Id的TileOverlay,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibTileOverlayById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyTileOverlayManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的TileOverlays
     * @param isVisible 是否显示
     */
    public void setVisibAllTileOverlays(Boolean isVisible) {
        KeyTileOverlayManager.getInstance().setAllVisib(isVisible);
        NoKeyTileOverlayManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 删除指定Id的瓦片图层覆盖物（TileOverlay）,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanTileOverlayById(String id) {
        if (id == null) {
            return;
        }
        KeyTileOverlayManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的瓦片图层覆盖物（text）
     */
    public void cleanAllTileOverlays() {
        KeyTileOverlayManager.getInstance().clearAll();
        NoKeyTileOverlayManager.getInstance().clearAll();
    }

    /**
     * 在地图上添加一个海量点覆盖物（MultiPointOverlay）对象
     *
     * @param options 一个海量点覆盖物（MultiPointOverlay）对象
     */
    public void addMultiPointOverlay(MultiPointOverlayOptions options, List<MultiPointItem> points) {
        if (mAMap == null || options == null) {
            return;
        }
        MultiPointOverlay multiPointOverlay = mAMap.addMultiPointOverlay(options);

        multiPointOverlay.setItems(points);
        MultiPointOverlayManager.getInstance().addMultiPointOverlay(multiPointOverlay);
    }

    /**
     * 显示或隐藏地图上所有的MultiPointOverlay
     * @param isVisible 是否显示
     */
    public void setVisibMultiPointOverlay(Boolean isVisible) {
        MultiPointOverlayManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 清除地图上所有的MultiPointOverlay
     */
    public void cleanAllMultiPointOverlays() {
        MultiPointOverlayManager.getInstance().clearAll();
    }


    /**
     * 在地图上添加一个粒子系统对象 -- 在地图上动画，天气效果等
     *
     * @param options
     */
    public void addParticleOverlay(ParticleOverlayOptions options) {
        if (mAMap == null || options == null) {
            return;
        }
        ParticleOverlay particleOverlay = mAMap.addParticleOverlay(options);
        ParticleOverlayManager.getInstance().addParticleOverlay(particleOverlay);
    }

    /**
     * 显示或隐藏地图上所有的粒子系统对象
     * @param isVisible 是否显示
     */
    public void setVisibParticleOverlay(Boolean isVisible) {
        ParticleOverlayManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 清除地图上所有的粒子系统对象
     */
    public void cleanAllParticleOverlays() {
        ParticleOverlayManager.getInstance().clearAll();
    }

    /**
     * 在地图上添加一个Ground覆盖物（groundOverlay）对象-图片
     * @param id 唯一KEY
     * @param options 一个groundOverlayOptions 对象，它定义了groundOverlay 的属性信息
     */
    public void addGroundOverlay(String id, GroundOverlayOptions options) {
        if (mAMap == null) {
            return;
        }
        GroundOverlay groundOverlay = mAMap.addGroundOverlay(options);
        if (id != null) {
            // 对有keyId的图片覆盖物集中管理
            KeyGroundOverlayManager.getInstance().addGroundOverlay(id, groundOverlay);
        } else {
            // 对无keyId的图片覆盖物集中管理
            NoKeyGroundOverlayManager.getInstance().addGroundOverlay(groundOverlay);
        }
    }

    /**
     * 显示或隐藏指定Id的GroundOverlay,只针对有Id
     * @param id 唯一KEY
     * @param isVisible 是否显示
     */
    public void setVisibGroundOverlayById(String id, Boolean isVisible) {
        if (id == null || isVisible == null) {
            return;
        }
        KeyGroundOverlayManager.getInstance().setVisibility(id, isVisible);
    }

    /**
     * 显示或隐藏地图上所有的GroundOverlays
     * @param isVisible 是否显示
     */
    public void setVisibAllGroundOverlays(Boolean isVisible) {
        KeyGroundOverlayManager.getInstance().setAllVisib(isVisible);
        NoKeyGroundOverlayManager.getInstance().setAllVisib(isVisible);
    }

    /**
     * 删除指定Id的图片覆盖物（,只针对有Id
     *
     * @param id 唯一KEY
     */
    public void cleanGroundOverlayById(String id) {
        if (id == null) {
            return;
        }
        KeyGroundOverlayManager.getInstance().remove(id);
    }

    /**
     * 清除地图上所有的图片覆盖物
     */
    public void cleanAllGroundOverlays() {
        KeyGroundOverlayManager.getInstance().clearAll();
        NoKeyGroundOverlayManager.getInstance().clearAll();
    }

    /**
     * 添加建筑物图层，默认图层的区域为全世界
     *
     * @describe 添加建筑物图层，默认图层的区域为全世界。
     * 建议一张地图上只添加一个BuildingOverlay，不要添加多个BuildingOverlay
     */
    public void addBuildingOverlay() {
        if (mAMap == null) {
            return;
        }
        mAMap.addBuildingOverlay();
    }

    /**
     * 轨迹纠偏 start
     */

    private ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap<Integer,
            TraceOverlay>();

    /**
     * 初始化 LBSTraceClient
     *
     * @param context   context
     * @param lineId    路线ID
     * @param traceList 路线 list
     */
    public void initLBSTraceClient(Context context, int lineId, List<TraceLocation> traceList) {

        if (mOverlayList.containsKey(lineId)) {
            TraceOverlay overlay = mOverlayList.get(lineId);
            overlay.zoopToSpan();
            int status = overlay.getTraceStatus();
            String tipString = "";
            if (status == TraceOverlay.TRACE_STATUS_PROCESSING) {
                tipString = "该线路轨迹纠偏进行中...";
            } else if (status == TraceOverlay.TRACE_STATUS_FINISH) {
                tipString = "该线路轨迹已完成";
            } else if (status == TraceOverlay.TRACE_STATUS_FAILURE) {
                tipString = "该线路轨迹失败";
            } else if (status == TraceOverlay.TRACE_STATUS_PREPARE) {
                tipString = "该线路轨迹纠偏已经开始";
            }
            return;
        }
        TraceOverlay mTraceOverlay = new TraceOverlay(mAMap);
        mOverlayList.put(lineId, mTraceOverlay);
        List<LatLng> mapList = traceLocationToMap(traceList);
        mTraceOverlay.setProperCamera(mapList);
        mTraceClient = new LBSTraceClient(context);
    }

    /**
     * 轨迹纠偏
     *
     * @param lineID    用于标示一条轨迹，支持多轨迹纠偏，如果多条轨迹调起纠偏接口，则lineID需不同
     * @param locations 一条轨迹的点集合，目前支持该点集合为一条行车GPS高精度定位轨迹
     * @param type      轨迹坐标系，目前支持高德 LBSTraceClient.TYPE_AMAP; 1
     *                  GPS LBSTraceClient.TYPE_GPS; 2
     *                  百度 LBSTraceClient.TYPE_BAIDU 3
     */
    public void queryProcessedTrace(int lineID, List<TraceLocation> locations, int type) {
        if (mTraceClient == null) {
            return;
        }
        mTraceClient.queryProcessedTrace(lineID, locations, type, this);
    }

    /**
     * 轨迹纠偏失败回调
     */
    @Override
    public void onRequestFailed(int lineID, String errorInfo) {
        Toast.makeText(mContext, errorInfo, Toast.LENGTH_SHORT).show();
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
        }
    }

    /**
     * 轨迹纠偏过程回调
     */
    @Override
    public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {
        if (segments == null) {
            return;
        }
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_PROCESSING);
            overlay.add(segments);
        }
    }

    /**
     * 轨迹纠偏结束回调
     */
    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance, int waitTime) {
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
            overlay.setDistance(distance);
            overlay.setWaitTime(waitTime);
        }
    }

    /**
     * 清除地图所有已完成或出错的轨迹
     */
    private void cleanAllTrace() {
        if (mOverlayList == null || mOverlayList.size() == 0) {
            return;
        }
        Iterator iter = mOverlayList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Integer key = (Integer) entry.getKey();
            TraceOverlay overlay = (TraceOverlay) entry.getValue();
            if (overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FINISH
                    || overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FAILURE) {
                overlay.remove();
                mOverlayList.remove(key);
            }
        }
    }

    /**
     * 清除地图所有已完成或出错的轨迹
     */
    private void cleanTraceById(Integer id) {
        if (id == null || mOverlayList == null && mOverlayList.size() == 0) {
            return;
        }
        Iterator iter = mOverlayList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Integer key = (Integer) entry.getKey();
            TraceOverlay overlay = (TraceOverlay) entry.getValue();
            if (key == id) {
                overlay.remove();
                mOverlayList.remove(key);
            }
        }
    }

    /**
     * 轨迹纠偏点转换为地图LatLng
     *
     * @param traceLocationList
     * @return List<LatLng>
     */
    public List<LatLng> traceLocationToMap(List<TraceLocation> traceLocationList) {
        List<LatLng> mapList = new ArrayList<LatLng>();
        for (TraceLocation location : traceLocationList) {
            LatLng latlng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mapList.add(latlng);
        }
        return mapList;
    }

    Polyline tracedPolyline = null;

    /**
     * 展示纠偏后的点
     *
     * @param rectifications
     */
    public void showTracedLocations(List<LatLng> rectifications) {
        if (tracedPolyline == null) {
            tracedPolyline = mAMap.addPolyline(new PolylineOptions()
                    .setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.ic_texture))
                    .width(20).zIndex(0));
        }
        if (rectifications == null) {
            return;
        }
        tracedPolyline.setPoints(rectifications);
    }


    /** 轨迹纠偏 end */

    public void clearAll() {
        cleanAllArcs();
        cleanAllCircles();
        cleanAllMarkers();
        cleanAllPolylines();
        cleanAllPolygons();
        cleanAllArrows();
        cleanAllTexts();
        cleanAllTileOverlays();
        cleanAllTileOverlays();
        cleanAllMultiPointOverlays();
        cleanAllParticleOverlays();
        cleanAllGroundOverlays();
    }
}
