package synway.module_publicaccount.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import qyc.synjob.SynJobTask;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.map.publicrtgis.GisMapAct;
import synway.module_publicaccount.map.until.sortClass;
import synway.module_publicaccount.public_chat.SyncGpsToMars;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail.Point;
import synway.module_publicaccount.push.DownLoadPic;
import synway.module_publicaccount.until.StringUtil;
import synway.module_publicaccount.until.TimeToDate;

/**
 * Created by ysm on 2017/3/23.
 * 在地图上添加点
 */

public class AddMapPoints extends SynJobTask {
    //纠偏
    private SyncGpsToMars syncGpsToMars = null;
    private Context mapcontext;
    private double mLat = 0, mLon = 0;
    private Marker mMarker = null;
    private AMap aMap = null;
    private NetConfig netConfig;
    private LayoutInflater inflater = null;
    private int type;
    private Polyline mPolyline;

    public AddMapPoints(Context context, AMap aMap, NetConfig netConfig, LayoutInflater inflater, int type) {
        this.mapcontext = context;
        this.aMap = aMap;
        this.netConfig = netConfig;
        this.inflater = inflater;
        this.type = type;//1为实时轨迹，2为历史轨迹
    }

    @Override
    public boolean inUIThread() {
        return true;
    }

    @Override
    public void onStart(Object... objects) {
        ArrayList<UserPointBean> pushUserPointBeens = (ArrayList<UserPointBean>) objects[0];
//        ArrayList<UserPointBean> trypushUserPointBeen=getTryPushUserPointBeen();//测试经纬度数据
        if (pushUserPointBeens.size() == 0) {
            Toast.makeText(mapcontext, "您没有选择任何人的轨迹点", Toast.LENGTH_SHORT).show();
            return;
        }
        sortClass sort = new sortClass();
        ArrayList<MarkerOptions> markerOptionses = new ArrayList<MarkerOptions>();
        List<LatLng> points = new ArrayList<LatLng>();
        LatLngBounds.Builder b = LatLngBounds.builder();
        int rgb = 10;
        for (UserPointBean userPointBean : pushUserPointBeens) {
            rgb = rgb + 30;
            ArrayList<Point> userpoint = new ArrayList<Point>();//一个人的点，用来连接轨迹
            userpoint.clear();
            for (Point point : userPointBean.points) {
                point.datetime = TimeToDate.StringToDate(point.time);
                userpoint.add(point);
            }
            Collections.sort(userpoint, sort);//按照时间排序，正序
            List<LatLng> latLngs = new ArrayList<LatLng>();
            latLngs.clear();
            for (int i = 0; i < userpoint.size(); i++) {
                String lon = userpoint.get(i).x;
                String lat = userpoint.get(i).y;
                if (lat == null || lon == null) {
                    Toast.makeText(mapcontext, "经纬度解析失败Lat=" + lat + ",Lon=" + lon, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    mLat = Double.valueOf(lat);
                    mLon = Double.valueOf(lon);
                    LatLng latLng = getOffset(mapcontext, mLat, mLon);
                    latLngs.add(latLng);
                    points.add(latLng);
                    b.include(latLng);
                    MarkerOptions markerOption = null;
                    if (i == 0) {
                        MarkerOptions markerOptionfirst = new MarkerOptions().anchor(0.5f, 1.0f)
                                .draggable(true)
                                .position(latLng)
                                .title(userPointBean.username)
                                .icon(getMarketBmp(true, userPointBean.picurl, userPointBean.username,userPointBean.DataType,userPointBean.type,userPointBean.EquipType));
                        aMap.addMarker(markerOptionfirst);
//                      ArrayList<MarkerOptions> markerOptions=new ArrayList<MarkerOptions>();
//                      markerOptions.add(markerOptionfirst);
//                      aMap.addMarkers(markerOptions,true);
                    } else {
                        LatLng firstlatLng = getOffset(mapcontext, Double.valueOf(userpoint.get(0).y), Double.valueOf(userpoint.get(0).x));
                        if (!firstlatLng.equals(latLng)) {
                            markerOption = new MarkerOptions().anchor(0.5f, 1.0f)
                                    .draggable(true)
                                    .position(latLng)
                                    .icon(getMarketBmp(false, "", "",0,0,""));
                        }
                    }

                    markerOptionses.add(markerOption);
                } catch (NumberFormatException e) {
                    Toast.makeText(context(), "经纬度解析失败Lat=" + lat + ",Lon=" + lon, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mPolyline = aMap.addPolyline((new PolylineOptions()).addAll(latLngs).geodesic(true).setDottedLine(true).color(Color.argb(rgb, 1, 1, 1)));//添加轨迹线
//            loadJobBefore("playpoints",new PlayPoints(aMap,mPolyline,latLngs,userPointBean,mapcontext),SelectAllDynamicLocation.class);
        }
        if (markerOptionses.size() > 0) {
            aMap.addMarkers(markerOptionses, true);//添加点}
        }
        if (points.size() == 1) {
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(points.get(0), 18, 30, 30)));
        } else if (points.size() > 1) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 150));
        }

//        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 50));
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


    public BitmapDescriptor getMarketBmp(boolean isMars, String picurl, String username,int DataType,int type,String EquipType) {
        View view = inflater.inflate(R.layout.model_myloc_gis_marker_view, null);
        ImageView imageView = view.findViewById(R.id.imageView1);
        TextView textView = view.findViewById(R.id.username);
        if (isMars) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
            imageView.setLayoutParams(layoutParams);
            if(StringUtil.isNotEmpty(picurl)){
               if(DataType==1){
                   if(type==1){
                       imageView.setImageResource(R.drawable.public_enemy);
                   }else if(type==2){
                       imageView.setImageResource(R.drawable.public_friend);
                   }
               }else if(DataType==2){//设备
                   if(EquipType.equals("人")){
                       if(type==1){
                           imageView.setImageResource(R.drawable.public_equip_enemy);
                       }else if(type==2){
                           imageView.setImageResource(R.drawable.public_equip_people);
                       }
                   }else if(EquipType.equals("设备")){
                       imageView.setImageResource(R.drawable.public_equip_equip);
                   }else if(EquipType.equals("车")){
                       imageView.setImageResource(R.drawable.public_equip_car);
                   }
               }
            }
            else {
                imageView.setImageResource(R.drawable.public_push_location);
            }
            textView.setVisibility(View.VISIBLE);
            textView.setText(username + "(起)");
//            imageView.setImageResource(R.drawable.myloc_icon_green);
        } else {
//            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(20,20);
//            imageView.setLayoutParams( layoutParams);
            textView.setVisibility(View.GONE);
//            imageView.setImageResource(R.drawable.myloc_icon_gray);
//            imageView.setImageResource(R.drawable.mappoint);
        }

        return BitmapDescriptorFactory.fromView(view);
    }

    public ArrayList<UserPointBean> getTryPushUserPointBeen() {
        ArrayList<UserPointBean> trypushUserPointBeen = new ArrayList<>();
        double y = 30.269861;
        double x = 120.196538;
        for (int i = 0; i < 3; i++) {
            UserPointBean userPointBean = new UserPointBean();
            userPointBean.username = "ysm" + i;
            ArrayList<Point> points = new ArrayList<Point>();
            x = x + 0.1;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int j = 0; j < 5; j++) {
                x = x + 0.1;
                Point point = new Point();
                point.x = String.valueOf(x);
                point.y = String.valueOf(y);
                point.time = df.format(new Date());
                points.add(point);
            }
            userPointBean.points = points;
            trypushUserPointBeen.add(userPointBean);
        }
        return trypushUserPointBeen;
    }

    private String getPath(String id) {
        return BaseUtil.FILE_HEAD_IMG_THU + "/" + id;
    }
}
