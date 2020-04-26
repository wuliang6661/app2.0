package synway.module_publicaccount.public_chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;


import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.SyncGpsToMars.IOnGspToMarsLsn;

public class LBSMapAct extends Activity {

    private AMap aMap = null;
    private MapView mapView = null;

    private double mLat = 0, mLon = 0;
//	//测试数据
//	private double mLat = 30.182795, mLon = 120.152144;

    //纠偏
    private SyncGpsToMars syncGpsToMars = null;

    private Marker mMarker = null;

    private LayoutInflater inflater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        String lat = getIntent().getStringExtra("LAT");
        String lon = getIntent().getStringExtra("LON");
        String info = getIntent().getStringExtra("INFO");

        if (lat == null || lon == null) {
            Toast.makeText(this, "经纬度解析失败Lat=" + lat + ",Lon=" + lon, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            mLat = Double.valueOf(lat);
            mLon = Double.valueOf(lon);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "经纬度解析失败Lat=" + lat + ",Lon=" + lon, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        setContentView(R.layout.model_public_account_map_act);

        // 设置离线地图文件路径
        MapsInitializer.sdcardDir = BaseUtil.OFF_LINE_MAP_PATH;

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        inflater = LayoutInflater.from(this);

        LatLng latLng = getOffset(this,mLat, mLon);
        mMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 1.0f)
                .draggable(true)
                .position(latLng)
                //.title("目标位置")
                .icon(getMarketBmp(true)));
        if (info != null && !info.equals("")) {
            mMarker.setTitle(info);
            mMarker.showInfoWindow();
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        syncGpsToMars = new SyncGpsToMars();
        syncGpsToMars.setLsn(onGspToMarsResult);
        NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(this);

//        mMarker = aMap.addMarker(new MarkerOptions()
//                .anchor(0.5f, 0.5f)
//                .draggable(true)
//                .position(latLng)
//                .title("目标位置")
//                .icon(getMarketBmp(true)));
//        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//        syncGpsToMars.start(netConfig.httpIP, netConfig.httpPort, mLon, mLat,"");

    }

    /**
     * 将GPS坐标转为高德坐标
     */
    public final LatLng getOffset(
            Context context,double lat, double lng) {
        // 只支持从其他坐标转到高德坐标
        ArrayList<int[]> returnList = new ArrayList<int[]>();
        CoordinateConverter converter = new CoordinateConverter(context);
        converter.from(CoordinateConverter.CoordType.GPS);

        LatLng latLng = new LatLng(lat, lng);
        converter.coord(latLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    private IOnGspToMarsLsn onGspToMarsResult = new IOnGspToMarsLsn() {
        @Override
        public void onResult(double marsLon, double marsLat,String picurl) {
            LatLng latLng = new LatLng(marsLat, marsLon);
            if (null != mMarker) {
                mMarker.setPosition(latLng);
                mMarker.setIcon(getMarketBmp(true));
            } else {
                mMarker = aMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .draggable(true)
                        .position(latLng)
                        .title("目标位置")
                        .icon(getMarketBmp(true)));

                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
    };

    private BitmapDescriptor getMarketBmp(boolean isMars) {
        View view = inflater.inflate(R.layout.model_myloc_lbs_marker_view, null);
        ImageView imageView = view.findViewById(R.id.imageView1);
        if (isMars) {
            imageView.setImageResource(R.drawable.myloc_icon_green);
        } else {
            imageView.setImageResource(R.drawable.myloc_icon_gray);
        }

        return BitmapDescriptorFactory.fromView(view);
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
        if (null != syncGpsToMars) {
            syncGpsToMars.stop();
            syncGpsToMars = null;
        }
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }
}
