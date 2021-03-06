//package synway.module_publicaccount.rt_gis_msg.show;
//
//import android.app.Activity;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.AMap.OnInfoWindowClickListener;
//import com.amap.api.maps.CameraUpdateFactory;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.MapsInitializer;
//import com.amap.api.maps.model.BitmapDescriptorFactory;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.Marker;
//import com.amap.api.maps.model.MarkerOptions;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//
//import synway.module_publicaccount.R;
//import synway.module_publicaccount.rt_gis_msg.show.RTGISViewI;
//import synway.module_interface.config.BaseUtil;
//import synway.module_publicaccount.location.show.GridAdapter;
//import synway.module_publicaccount.location.show.GridHeadView;
//import synway.module_publicaccount.location.show.SpsLastHeadHeight;
//import synway.module_publicaccount.rt_gis_msg.Obj_RTGis_Point;
//import synway.module_publicaccount.rt_gis_msg.presenter.RTGISPresenterI;
//import synway.module_publicaccount.rt_gis_msg.presenter.RTGISPresenterImp;
//import synway.module_publicaccount.until.StringUtil;
//
//public class RTGISAct extends Activity implements RTGISViewI {
//    private RTGISPresenterI rtgisPresenterI=null;
//    private String publicGUID=null;
//    /*** 地图*/
//    private MapView mapView = null;
//
//    /**地图操作类,由地图控件生成*/
//    private AMap aMap = null;
//
//    /**最后一次接口移到的中心点*/
//    private double[] lastCenter = null;
//
//    /**保存所有地图上的点对象*/
//    private HashMap<String, Marker> markerMap = new HashMap<>();
//
//    private GridHeadView ghv;
//
//    private FloatingActionButton fab = null;
//
//    private ImageButton button;
//
//    private List<Obj_RTGis_Point> pointList=new ArrayList<>();
//
//    private HashMap<String, GridAdapter.PicItem> headViewMap = new HashMap<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//		/* 设置布局 */
//        setContentView(R.layout.model_location_gaodemap_cv);
//        // 设置离线地图文件路径
//        MapsInitializer.sdcardDir = BaseUtil.OFF_LINE_MAP_PATH;
//
//        publicGUID=getIntent().getStringExtra("publicGUID");
//
//		/* 初始化地图 */
//        mapView = (MapView) findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//        aMap = mapView.getMap();
//
//        fab = (FloatingActionButton)findViewById(R.id.fab);
//        fab.setVisibility(View.GONE);
//
//        button= (ImageButton) findViewById(R.id.imageView5);
//        button.setVisibility(View.GONE);
//
//        // 实时位置界面顶部的状态栏，最先只是一个LinearLayout，现在把这些控件封装在了一起
//        ghv = (GridHeadView) findViewById(R.id.picHeadView);
//        ghv.setOnGridItemClickListener(onItemClickListener);
//        int maxHeight = getResources().getDisplayMetrics().heightPixels * 3 / 4;
//        ghv.setMaxHeight(maxHeight);
//
//        aMap.getUiSettings().setRotateGesturesEnabled(false);
//        // 开启比例尺
//        aMap.getUiSettings().setScaleControlsEnabled(true);
//        aMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
//        aMap.setOnMarkerClickListener(onMarkerClickListener);
//
//		/* 获取配置中的地图缩放等级+中心点 */
//        double[] lastLocationAndZoom = _SaveLocation.getLastLocation(this);
//        /* 设置中心点和缩放等级 */
//        if (lastCenter != null) {
//            lastLocationAndZoom[0] = lastCenter[0];
//            lastLocationAndZoom[1] = lastCenter[1];
//        }
//
//		/* 移动地图 */
//        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
//                        lastLocationAndZoom[0], lastLocationAndZoom[1]),
//                (float) lastLocationAndZoom[2]));
//        rtgisPresenterI=new RTGISPresenterImp(RTGISAct.this,this) ;
//        rtgisPresenterI.start(publicGUID);
//    }
//
//    @Override
//    protected void onDestroy() {
//        rtgisPresenterI.stop();
////        MsgReadSwitch.close(this);
////        Sps_NotifyAgain.clear(this);
//        mapView.onDestroy();
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        // 暂停地图
//        mapView.onPause();
//        SpsLastHeadHeight.saveLastHeadHeight(this, ghv.getCurrentHeight());
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // 继续地图
//        ghv.initHeight(SpsLastHeadHeight.getLastHeadHeight(this));
//        mapView.onResume();
//    }
//
//    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//            GridAdapter.PicItem item = ghv.getPicItem(position);
//            String pointID=item.tag.toString();
//            LatLng latLng = markerMap.get(pointID).getPosition();
//            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
//                    aMap.getCameraPosition().zoom));
//        }
//    };
//
//    private OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {
//
//        @Override
//        public void onInfoWindowClick(Marker marker) {
//            marker.hideInfoWindow();
//        }
//    };
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//
//    @Override
//    public void setPoint(List<Obj_RTGis_Point> pointList) {
//        ghv.removeAll();
//        markerMap.clear();
//        for(Obj_RTGis_Point obj_rtGis_point:pointList){
//            addHead(obj_rtGis_point.ID,obj_rtGis_point.name);
//            LatLng latLng=new LatLng( Double.valueOf(obj_rtGis_point.Y),Double.valueOf(obj_rtGis_point.X));
//            addMapPoint(obj_rtGis_point.ID,latLng, Long.parseLong(obj_rtGis_point.time),obj_rtGis_point.name);
//        }
//
//    }
//
//    @Override
//    public void addPoint(Obj_RTGis_Point point) {
//        Marker marker=markerMap.get(point.ID);
//        removeHead(point.ID);
//        addHead(point.ID,point.name);
//        if(marker!=null){
//            LatLng latLng=new LatLng(Double.valueOf(point.Y),Double.valueOf(point.X));
//            moveMapPoint(point.ID,latLng);
//        }else{
//            LatLng latLng=new LatLng(Double.valueOf(point.Y),Double.valueOf(point.X));
//            addMapPoint(point.ID,latLng, Long.parseLong(point.time),point.name);
//        }
//    }
//
//    @Override
//    public void removePoint(Obj_RTGis_Point point) {
//        markerMap.remove(point.ID);
//        removeHead(point.ID);
//    }
//
//    /*** 添加头像*/
//    private void addHead(String userID, String userName) {
//        GridAdapter.PicItem item = new GridAdapter.PicItem();
//        item.tag = userID;
//        item.picName = StringUtil.isNotEmpty(userName) ? userName : "未知姓名";
//        Drawable bigDrawable;
//        bigDrawable = Drawable.createFromPath(BaseUtil.FILE_HEAD_IMG_THU + "/"
//                + userID);
//        if (bigDrawable == null) {
//            bigDrawable = getResources().getDrawable(
//                    R.drawable.public_head);
//        }
//        item.bigPic = bigDrawable;
//        headViewMap.put(userID, item);
//
//        ghv.addPicItem(item);
//        ghv.setBottomDescription(headViewMap.size() + "人正在共享位置");
//        ghv.updateView();
//    }
//
//    private AMap.OnMarkerClickListener onMarkerClickListener = new AMap.OnMarkerClickListener() {
//
//        @Override
//        public boolean onMarkerClick(Marker marker) {
//            if (marker.isInfoWindowShown()) {
//                marker.hideInfoWindow();
//            } else {
//                marker.showInfoWindow();
//            }
//            return true;
//        }
//    };
//
//    /*** 移除头像*/
//    private void removeHead(String ID) {
//        GridAdapter.PicItem item = headViewMap.remove(ID);
//        if (item != null) {
//            ghv.removePicItem(item);
//            ghv.updateView();
//        }
//        if (headViewMap.size() == 0) {
//            ghv.setBottomDescription("所有人都已经退出位置上传");
//        } else {
//            ghv.setBottomDescription(headViewMap.size() + "人正在共享位置");
//        }
//    }
//
//    /** 向地图上添加一个点 */
//    private Marker addMapPoint(String pointID, LatLng latLng, long uploadTime, String title) {
//        if (latLng == null) {
//            return null;
//        }
//        Marker marker = aMap.addMarker(new MarkerOptions()
//                .anchor(0.5f, 0.5f).position(latLng).title(title).icon(BitmapDescriptorFactory.fromView(getTargetView())));
//        markerMap.put(pointID, marker);
//        marker.setObject(uploadTime);
//        return marker;
//    }
//
//    /*** 移动一个点*/
//    private void moveMapPoint(String pointID, LatLng latLng) {
//        Marker marker = markerMap.get(pointID);
//        if (marker == null || latLng == null) {
//            return;
//        }
//        marker.setPosition(latLng);
//    }
//
//    private View getTargetView() {
//        View view = LayoutInflater.from(this).inflate(R.layout.model_public_account_location_marker, null);
//        ImageView iv = (ImageView) view.findViewById(R.id.imageView1);
//        iv.setImageResource(R.drawable.public_head);
//        return view;
//    }
//}
