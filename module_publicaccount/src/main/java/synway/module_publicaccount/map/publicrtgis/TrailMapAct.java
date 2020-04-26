package synway.module_publicaccount.map.publicrtgis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.map.until.sortClass;
import synway.module_publicaccount.publiclist.SyncGetHeadThu;
import synway.module_publicaccount.push.DownLoadPic;
import synway.module_publicaccount.until.ConfigUtil;
import synway.module_publicaccount.until.StringUtil;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail.Point;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.friendTag;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.enemyTag;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.Picture;
import synway.module_publicaccount.until.TimeToDate;
import synway.module_interface.config.netConfig.Sps_NetConfig;
//import com.amap.api.services.core.LatLonPoint;


/**
 * Created by ysm on 2017/3/27.
 * 历史轨迹地图
 */

public class TrailMapAct extends Activity implements View.OnClickListener{

    private AMap aMap = null;
    private MapView mapView = null;

    private double mLat = 0, mLon = 0;
//	//测试数据
//	private double mLat = 30.182795, mLon = 120.152144;


    private Marker mMarker = null;

    private LayoutInflater inflater = null;
    private String publicGUID=null;
    private Obj_PublicMsgTrail obj_publicMsgTrail;
    public Button locationSearch,playmove,stopmove;
    //    private SynJobLoader synJobLoader;
    private Polyline mPolyline;
    private   List<LatLng> latLngs;
    private     UserPointBean userTrailPointBean;
    private   SmoothMoveMarker smoothMarker;
    private int mPauseIndex;
    private double mDistanceRemain;
    /**
     * 模拟速度
     */
    private double mSpeed = 300;
    /**
     * 路径总距离
     */
    private float mAllDistance;
    private LinearLayout playlinearyout=null;
    private SeekBar seekBar;
    private TextView speedNumber;
    private int time=20;
    private int changetime=20;
    private LinearLayout seeklayout;
    private SyncGetHeadThu syncGetHeadThu;
    private NetConfig netConfig = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        String info = getIntent().getStringExtra("INFO");
        publicGUID=getIntent().getStringExtra("publicGUID");
        obj_publicMsgTrail=(Obj_PublicMsgTrail)getIntent().getSerializableExtra("obj_publicMsgTrail");
        setContentView(R.layout.model_public_account_gis_map_act);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        // 设置离线地图文件路径
        MapsInitializer.sdcardDir = BaseUtil.OFF_LINE_MAP_PATH;
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        inflater = LayoutInflater.from(this);
        init();
        NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(this);
        ArrayList<Point> points=obj_publicMsgTrail.Points;
//        synJobLoader = new SynJobLoader(this, null);
        userTrailPointBean=getUserPointBean();
        playline(userTrailPointBean);
//        synJobLoader.loadJobLast("addMapPoints",new AddMapPoints(this,aMap,netConfig,inflater,2));
//        ArrayList<UserPointBean> pushUserPointBeens = new ArrayList<UserPointBean>();
//        pushUserPointBeens.add(userTrailPointBean);
//        synJobLoader.synJobContext().startJob("addMapPoints",pushUserPointBeens);//操作地图，在地图上画轨迹线和点
    }

    public UserPointBean getUserPointBean(){
        UserPointBean userPointBean=new UserPointBean();
        if(obj_publicMsgTrail.Points.size()>0) {
            if(obj_publicMsgTrail.Type==1){//敌方
                enemyTag enemyTag=(enemyTag)obj_publicMsgTrail.Points.get(0).tag;
                userPointBean.username = enemyTag.publicObject;
            }else if(obj_publicMsgTrail.Type==2){//友方
                friendTag friendTag = (friendTag) obj_publicMsgTrail.Points.get(0).tag;
                if (StringUtil.isEmpty(friendTag.policeNum)) {//警号为空，用equip中locId判断,是設備類型
                    userPointBean.username = friendTag.equip.equipName;
                }else{
                    userPointBean.username = friendTag.name;
                }
            }
            if(obj_publicMsgTrail.Points.get(0).pType==2){//如果有头像的话
                Picture picture = (Picture) obj_publicMsgTrail.Points.get(0).pointInfo;
                userPointBean.picurl = picture.picURL;
            }
            userPointBean.points=obj_publicMsgTrail.Points;
        }
        return  userPointBean;
    }
    public void init(){
//        locationSearch=(Button)findViewById(R.id.locationSearch);
//        locationSearch.setText("播放");
//        locationSearch.setOnClickListener(this);
        netConfig = Sps_NetConfig.getNetConfigFromSpf(this);
        syncGetHeadThu=new SyncGetHeadThu(netConfig.httpIP,netConfig.httpPort,this);
        playlinearyout= findViewById(R.id.playpoint);
        speedNumber= findViewById(R.id.speedNumber);
        speedNumber.setText(time+"秒");
        seeklayout= findViewById(R.id.seeklayout);
        seekBar= findViewById(R.id.speedSeekBar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        playmove= findViewById(R.id.playmove);
        stopmove= findViewById(R.id.stopmove);
        playmove.setOnClickListener(this);
        stopmove.setOnClickListener(this);
        if(obj_publicMsgTrail.Points.size()>1){
            playlinearyout.setVisibility(View.VISIBLE);
            seeklayout.setVisibility(View.VISIBLE);
        }else{
            playlinearyout.setVisibility(View.GONE);
            seeklayout.setVisibility(View.GONE);
        }
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






    public BitmapDescriptor getMarketBmp(boolean isMars, String picurl,String username,int pointType) {
        View view = inflater.inflate(R.layout.model_myloc_gis_marker_view, null);
        ImageView imageView = view.findViewById(R.id.imageView1);
        TextView textView = view.findViewById(R.id.username);
        if (isMars) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
            imageView.setLayoutParams(layoutParams);
//            if (StringUtil.isNotEmpty(picurl)) {
//                String path = getPath(DownLoadPic.getImgName(picurl));
//                Drawable drawable = Drawable.createFromPath(path);
//                imageView.setImageDrawable(drawable);
//            } else {
//                imageView.setImageResource(R.drawable.public_push_location);
//            }
            if(pointType==0){//起点
                imageView.setImageResource(R.drawable.syngis_start);
            }else if(pointType==1){//终点
                imageView.setImageResource(R.drawable.syngis_end);
            }
            textView.setVisibility(View.GONE);
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
    private String getPath(String id) {
        return BaseUtil.FILE_HEAD_IMG_THU + "/" + id;
    }

    @Override
    public void onClick(View v) {
        //轨迹播放
//        if (v.getId() == R.id.locationSearch) {
//            if(locationSearch.getText().equals("播放")){
////                locationSearch.setText("暂停");
//////                 if(smoothMarker!=null){
//////                    continueRun();
//////                 }else{
////                     startPlayLines();
//////                 }
////            }else  if(locationSearch.getText().equals("暂停")){
////                locationSearch.setText("播放");
////                if(smoothMarker!=null){
////                 pauseRun();
////                }
////            }
//        }
        if(v.getId()==R.id.playmove){
            startPlayLines();
        }else if(v.getId()==R.id.stopmove){
            if(smoothMarker!=null){
                smoothMarker.destroy();
                smoothMarker=null;
            }
        }

    }

    /****
     * 画轨迹
     * @param userPointBean
     */
//    LatLonPoint startPoint,endPoint;
    public void playline( UserPointBean userPointBean){
        sortClass sort = new sortClass();
        ArrayList<MarkerOptions> markerOptionses = new ArrayList<MarkerOptions>();
        LatLngBounds.Builder b = LatLngBounds.builder();
        List<LatLng> points = new ArrayList<LatLng>();
        int rgb=10;
        rgb=rgb+30;
        ArrayList<Point> userpoint = new ArrayList<Point>();//一个人的点，用来连接轨迹
        userpoint.clear();
        for (Point point : userPointBean.points) {
            point.datetime = TimeToDate.StringToDate(point.time);
            userpoint.add(point);
        }
        Collections.sort(userpoint, sort);//按照时间排序，正序
        latLngs= new ArrayList<LatLng>();
        latLngs.clear();
        for (int i = 0; i < userpoint.size(); i++) {
            String lon = userpoint.get(i).x;
            String lat = userpoint.get(i).y;
            if (lat == null || lon == null) {
                Toast.makeText(this, "经纬度解析失败Lat=" + lat + ",Lon=" + lon, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                mLat = Double.valueOf(lat);
                mLon = Double.valueOf(lon);
//                    if(i==0){
//                        startPoint=new LatLonPoint(mLat,mLon);
//                    }else if(i==userpoint.size()-1){
//                        endPoint=new LatLonPoint(mLat,mLon);
//                    }
                LatLng latLng = getOffset(this, mLat, mLon);
                latLngs.add(latLng);
                b.include(latLng);
                points.add(latLng);
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18, 30, 30)));
                MarkerOptions markerOption=null;
                if(i==0){
                    MarkerOptions markerOptionfirst= new MarkerOptions().anchor(0.5f, 1.0f)
                            .draggable(true)
                            .position(latLng)
                            .title(userPointBean.username)
                            .icon(getMarketBmp(true, userPointBean.picurl,userPointBean.username,0));//起点
                    aMap.addMarker(markerOptionfirst);
                }else if(i== userpoint.size()-1){
                    MarkerOptions markerOptionlast= new MarkerOptions().anchor(0.5f, 1.0f)
                            .draggable(true)
                            .position(latLng)
                            .title(userPointBean.username)
                            .icon(getMarketBmp(true, userPointBean.picurl,userPointBean.username,1))
                            ;//终点
                    aMap.addMarker(markerOptionlast);
                }
                else{
                    LatLng firstlatLng = getOffset(this,  Double.valueOf(userpoint.get(0).y),   Double.valueOf(userpoint.get(0).x));
                    if(!firstlatLng.equals(latLng)){
                        markerOption= new MarkerOptions().anchor(0.5f, 1.0f)
                                .draggable(true)
                                .position(latLng)
                                .icon(getMarketBmp(false, "","",2));//普通点
                    }
                }

                markerOptionses.add(markerOption);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "经纬度解析失败Lat=" + lat + ",Lon=" + lon, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mPolyline=aMap.addPolyline((new PolylineOptions()).addAll(latLngs).geodesic(true).setDottedLine(true).color(Color.argb(rgb, 1, 1, 1)));//添加轨迹线

        if(markerOptionses.size()>0) {
            aMap.addMarkers(markerOptionses,true);//添加点}
        }
        if(points.size()==1){
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(points.get(0), 18, 30, 30)));
        }else if(points.size()>1) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 50));
        }
//        getRoute(startPoint,endPoint);
    }

    /***
     * 播放轨迹动画
     */
    public  void startPlayLines(){
        if(smoothMarker!=null) {
            smoothMarker.destroy();
            smoothMarker = null;
        }
        if (mPolyline == null) {//没有轨迹
            return;
        }
        // 构建 轨迹的显示区域
//      LatLngBounds bounds = new LatLngBounds(latLngs.get(0), latLngs.get(latLngs.size() - 2));
//      aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        // 实例 SmoothMoveMarker 对象
        smoothMarker  = new SmoothMoveMarker(aMap);
        // 设置 平滑移动的 图标
        if(StringUtil.isNotEmpty(userTrailPointBean.picurl)) {
            String path = ConfigUtil.getPath(DownLoadPic.getImgName(userTrailPointBean.picurl));
            Drawable drawable = Drawable.createFromPath(path);
            if(drawable==null){
                smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.public_push_location3));
            }else {
                smoothMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) drawable).getBitmap()));
            }
        }
        else {
            smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.public_push_location3));
        }
        LatLng latLng = latLngs.get(latLngs.size()-1);
        latLngs.add(latLng);
        // 取轨迹点的第一个点 作为 平滑移动的启动
        LatLng drivePoint = latLngs.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(latLngs, drivePoint);
        latLngs.set(pair.first, drivePoint);
        List<LatLng> subList = latLngs.subList(pair.first, latLngs.size());
        // 设置轨迹点
        smoothMarker.setPoints(subList);
        // 设置平滑移动的总时间  单位  秒
        smoothMarker.setTotalDuration(changetime);
        smoothMarker.setMoveListener(new SmoothMoveMarker.MoveListener() {
            @Override
            public void move(double v) {
                /**
                 * 最后一次取到的值即为暂停时的数据，getIndex()方法取到的是当前list的下标,v为剩余距离
                 */
                mPauseIndex = smoothMarker.getIndex();
                mDistanceRemain = v;
                Log.i("testy","路径的剩余距离"+mDistanceRemain);
//              if(v==0.0){
//                  runOnUiThread(new Runnable() {
//                      @Override
//                      public void run() {
//                          locationSearch.setText("播放");
//                      }
//                  });
//
//                  smoothMarker.destroy();
//              }
            }
        });
        // 开始移动
        smoothMarker.startSmoothMove();
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
    TextView title;
    TextView snippet;
    /**
     * 自定义View并且绑定数据方法
     * @param marker 点击的Marker对象
     * @return  返回自定义窗口的视图
     */
    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(this);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(this);
            snippet = new TextView(this);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);
            infoWindowLayout.setBackgroundResource(R.drawable.infowindow_bg);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }
    /**
     * 根据起点和重点，调用高德api规划路径
     */
//    private void getRoute( LatLonPoint startPoint, LatLonPoint endPoint) {
//        RouteSearch routeSearch = new RouteSearch(this);
//        routeSearch.setRouteSearchListener(this);
//        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
//        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
//        routeSearch.calculateWalkRouteAsyn(query);
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void continueRun() {
        LatLng drivePoint = latLngs.get(mPauseIndex + 1);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(latLngs, drivePoint);
        latLngs.set(pair.first, drivePoint);
        latLngs = latLngs.subList(pair.first, latLngs.size());
        /**
         * 重新设置剩余的轨迹点
         */
        smoothMarker.setPoints(latLngs);
        /**
         * 重新设置滑动时间
         */
        smoothMarker.setTotalDuration(20);
//        smoothMarker.setTotalDuration((int) (mDistanceRemain / mSpeed));
        smoothMarker.startSmoothMove();
    }
    private void pauseRun() {
        smoothMarker.stopMove();
    }
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int seekProgress = seekBar.getProgress();
            if(seekProgress<13){
                seekBar.setProgress(0);
                changetime =  time;
                speedNumber.setText(changetime + "秒");
            }else if(seekProgress>=13 && seekProgress<38){
                seekBar.setProgress(25);
                changetime = time/4*3;
                speedNumber.setText(changetime + "秒");
            }else if(seekProgress>=38 && seekProgress<63){
                seekBar.setProgress(50);
                changetime = time/2;
                speedNumber.setText(changetime + "秒");
            }else if(seekProgress>=63 && seekProgress<88){
                seekBar.setProgress(75);
                changetime = time/4;
                speedNumber.setText(changetime + "秒");
            }else if(seekProgress>=88){
                seekBar.setProgress(100);
                changetime = 1;
                speedNumber.setText(changetime + "秒");
            }

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//            Log.i("testy", "得到的进度条进度是" + (double)progress/100);
//            changetime=(double)((progress / 100) * time)+1;
//            speedNumber.setText(((double)progress / 100)*time+"秒");
//            if(smoothMarker!=null) {
//                smoothMarker.setTotalDuration((progress / 100) * time);
//            }
        }
    };
//
//    @Override
//    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
//
//    }
//
//    @Override
//    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
//
//    }
//
//    @Override
//    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
//
//    }
//
//    @Override
//    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
//
//    }

}

