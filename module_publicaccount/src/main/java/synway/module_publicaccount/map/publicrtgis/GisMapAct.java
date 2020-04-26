package synway.module_publicaccount.map.publicrtgis;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
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
import qyc.synjob.SynJobLoader;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.map.AddMapPoints;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.map.SelectAllDynamicLocation;
import synway.module_publicaccount.map.view.DynamicLocationList;
import synway.module_publicaccount.public_chat.SyncGpsToMars;
import synway.module_publicaccount.push.PushUtil;

/**
 * 实时轨迹地图
 */
public class GisMapAct extends Activity implements View.OnClickListener{

    private AMap aMap = null;
    private MapView mapView = null;

    private double mLat = 0, mLon = 0;
//	//测试数据
//	private double mLat = 30.182795, mLon = 120.152144;

    //纠偏
    private SyncGpsToMars syncGpsToMars = null;

    private Marker mMarker = null;

    private LayoutInflater inflater = null;
    private String publicGUID=null;
    private Obj_PulibcMsgDynamicLocation obj_pulibcMsgDynamicLocation;
    private SynJobLoader synJobLoader;
    public Button locationSearch;
    // ================广播
    private NewMsgReceiver onNewMsgReceive = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        publicGUID=getIntent().getStringExtra("publicGUID");
        obj_pulibcMsgDynamicLocation=(Obj_PulibcMsgDynamicLocation)getIntent().getSerializableExtra("Obj_PulibcMsgDynamicLocation");
        setContentView(R.layout.model_public_account_gis_map_act);
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
        inflater = LayoutInflater.from(this);
        init();
        NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(this);
        onNewMsgReceive = new NewMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushUtil.PublicNewMsg.getGisAction(publicGUID));
        registerReceiver(onNewMsgReceive, filter);
        //初始化任务加载器
        synJobLoader = new SynJobLoader(this, null);
        synJobLoader.loadJobLast("loadDynamicLocationDB",new SelectAllDynamicLocation(0,obj_pulibcMsgDynamicLocation));
        synJobLoader.loadJobLast("addMapPoints",new AddMapPoints(this,aMap,netConfig,inflater,1));
        synJobLoader.synJobContext().startJob("loadDynamicLocationDB", aMap);

    }
    public void init(){
        locationSearch= findViewById(R.id.locationSearch);
        locationSearch.setVisibility(View.VISIBLE);
        locationSearch.setOnClickListener(this);
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
        if (null != onNewMsgReceive) {
            unregisterReceiver(onNewMsgReceive);
        }
        synJobLoader.removeAll();
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        //跳转到实时轨迹列表
        if (v.getId() == R.id.locationSearch) {
            Intent intent=new Intent();
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("Obj_PulibcMsgDynamicLocation",obj_pulibcMsgDynamicLocation);
            mBundle.putString("publicid",publicGUID);
            intent.putExtras(mBundle);
            intent.setClass(this, DynamicLocationList.class);
            startActivityForResult(intent,1);
//            startActivity(intent);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == 3) {
            ArrayList<UserPointBean> allPointBeens=new ArrayList<UserPointBean>();
            ArrayList<UserPointBean> friendPointBeens=( ArrayList<UserPointBean>)data.getSerializableExtra("friendPointBeens");
            ArrayList<UserPointBean> animyuserPointBeens=( ArrayList<UserPointBean>)data.getSerializableExtra("animyuserPointBeens");
            ArrayList<UserPointBean> equipuserPointBeens=( ArrayList<UserPointBean>)data.getSerializableExtra("equipuserPointBeens");
            allPointBeens=friendPointBeens;
            if(allPointBeens.size()>0){
                for(UserPointBean userPointBean:animyuserPointBeens){
                    allPointBeens.add(userPointBean);
                }
            }else{
                allPointBeens=animyuserPointBeens;
            }
            if(allPointBeens.size()>0){
               for(UserPointBean userPointBean:equipuserPointBeens){
                   allPointBeens.add(userPointBean);
               }
            }else{
                allPointBeens=equipuserPointBeens;
            }
            if (aMap != null) {
                aMap.clear();
            }
            synJobLoader.synJobContext().startJob("addMapPoints",allPointBeens);//添加点
        }
    }
    /**
     * 新的公众推送消息
     */
    private class NewMsgReceiver extends BroadcastReceiver {
        long receiveLong;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("testy","收到了广播");
//            receiveLong = System.currentTimeMillis();
//            Obj_PulibcMsgDynamicLocation obj = (Obj_PulibcMsgDynamicLocation) intent.getSerializableExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_SOBJ);
//            int msgType = intent.getIntExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSGTYPE, 0);
//            obj.MsgType=msgType;
            synJobLoader.synJobContext().startJob("loadDynamicLocationDB", aMap);

        }

    }
}
