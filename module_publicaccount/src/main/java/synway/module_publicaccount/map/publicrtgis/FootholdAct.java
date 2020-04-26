package synway.module_publicaccount.map.publicrtgis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.Gradient;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.TileOverlayOptions;

import java.util.ArrayList;

import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.util.bean.FootHoldPointBen;

import static synway.module_publicaccount.public_chat.util.ConfigUtil.FOOTHOLD_POINT_LIST;

/**
 * 落脚点地图
 */
public class FootholdAct extends Activity {

    private AMap aMap = null;
    private MapView mapView = null;
    private ArrayList<FootHoldPointBen> latLngs;
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),
            Color.argb(255 / 3 * 2, 0, 255, 0),
            Color.rgb(125, 191, 0),
            Color.rgb(185, 71, 0),
            Color.rgb(255, 0, 0)
    };

    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {0.0f,
            0.10f, 0.20f, 0.60f, 1.0f};
    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(
            ALT_HEATMAP_GRADIENT_COLORS, ALT_HEATMAP_GRADIENT_START_POINTS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        latLngs=createRectangle( new LatLng(39.90403, 116.407525),1,1);
        latLngs = (ArrayList<FootHoldPointBen>) getIntent().getSerializableExtra(FOOTHOLD_POINT_LIST);
        for (FootHoldPointBen footHoldPointBen : latLngs) {
            Log.i("testy", "经纬度范围" + footHoldPointBen.getMinlng() + "," + footHoldPointBen.getMaxlng() + "," + footHoldPointBen.getMinlat() + "," +
                    footHoldPointBen.getMaxlat() + ",");
        }
        setContentView(R.layout.model_public_account_foothold_map_act);
        init(savedInstanceState);
        createHeatMap(latLngs);
//        startPlayPoints();
    }

    public void init(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        // 设置离线地图文件路径
        MapsInitializer.sdcardDir = BaseUtil.OFF_LINE_MAP_PATH;
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        // 开启比例尺
        aMap.getUiSettings().setScaleControlsEnabled(true);
    }

    public void createHeatMap(ArrayList<FootHoldPointBen> footHoldPointBens) {
        ArrayList<LatLng> latLnglist = new ArrayList<>();
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        for (FootHoldPointBen footHoldPointBen : footHoldPointBens) {
            LatLng first = getOffset(this, footHoldPointBen.getMinlat(), footHoldPointBen.getMinlng());
            LatLng second = getOffset(this, footHoldPointBen.getMinlat(), footHoldPointBen.getMaxlng());
            LatLng three = getOffset(this, footHoldPointBen.getMaxlat(), footHoldPointBen.getMinlng());
            LatLng four = getOffset(this, footHoldPointBen.getMaxlat(), footHoldPointBen.getMaxlng());
            bounds.include(first);
            bounds.include(second);
            bounds.include(three);
            bounds.include(four);
            latLnglist.add(first);
            latLnglist.add(second);
            latLnglist.add(three);
            latLnglist.add(four);
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150));
        // 第二步： 构建热力图 TileProvider
        HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
        builder.data(latLnglist) // 设置热力图绘制的数据
                .gradient(ALT_HEATMAP_GRADIENT); // 设置热力图渐变，有默认值 DEFAULT_GRADIENT，可不设置该接口
        // Gradient 的设置可见参考手册
        // 构造热力图对象
        HeatmapTileProvider heatmapTileProvider = builder.build();
        // 第三步： 构建热力图参数对象
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
        tileOverlayOptions.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者

        // 第四步： 添加热力图
        aMap.addTileOverlay(tileOverlayOptions);
    }


    private void startPlayPoints(ArrayList<LatLng> latLngs) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getLatLngBounds(latLngs), 50));// 设置指定的可视区域地图
        aMap.addPolygon(new PolygonOptions()
                .addAll(latLngs)
                .fillColor(Color.LTGRAY).strokeColor(Color.RED).strokeWidth(1));
    }

    @Override
    protected void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    private LatLngBounds getLatLngBounds(ArrayList<LatLng> latLngs) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < latLngs.size(); i++) {
            b.include(latLngs.get(i));
        }
        return b.build();
    }

    private ArrayList<LatLng> createRectangle(LatLng center, double halfWidth,
                                              double halfHeight) {
        ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight * 2, center.longitude - halfWidth * 2));
        latLngs.add(new LatLng(center.latitude + halfHeight * 3, center.longitude - halfWidth * 3));
        return latLngs;
    }

    /**
     * 将GPS坐标转为高德坐标
     * 使用新的高德api 2017-03-14
     */
    public static LatLng getOffset(Context context, double wgLat, double wgLon) {
        // 只支持从其他坐标转到高德坐标
        CoordinateConverter converter = new CoordinateConverter(context);
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(wgLat, wgLon));
        // 执行转换操作
        return converter.convert();
    }
}
