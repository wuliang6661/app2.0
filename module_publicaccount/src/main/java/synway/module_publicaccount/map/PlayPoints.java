package synway.module_publicaccount.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import java.util.List;

import qyc.synjob.SynJobTask;
import synway.module_publicaccount.R;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.push.DownLoadPic;
import synway.module_publicaccount.until.ConfigUtil;
import synway.module_publicaccount.until.StringUtil;

/**
 * Created by ysm on 2017/3/21.
 * 删除一个轨迹点
 */

public class PlayPoints extends SynJobTask {
    private AMap mAMap = null;
    private Polyline mPolyline=null;
    private List<LatLng> points;
    private Context context;
    private UserPointBean userPointBean;
    public PlayPoints(AMap aMap, Polyline polyline, List<LatLng> points, UserPointBean userPointBean,Context context){
        this.mAMap = aMap;
        this.mPolyline=polyline;
        this.points=points;
        this.userPointBean=userPointBean;
        this.context=context;
    }
    @Override
    public boolean inUIThread() {
        return false;
    }

    @Override
    public void onStart(Object... objects) {
        if (mPolyline == null) {//没有轨迹
            return;
        }
        // 构建 轨迹的显示区域
        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        // 实例 SmoothMoveMarker 对象
        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(mAMap);
        // 设置 平滑移动的 图标
        if(StringUtil.isNotEmpty(userPointBean.picurl)) {
            String path = ConfigUtil.getPath(DownLoadPic.getImgName(userPointBean.picurl));
            Drawable drawable = Drawable.createFromPath(path);
            smoothMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) drawable).getBitmap()));
        }
        else {
            smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.public_push_location));
        }
        // 取轨迹点的第一个点 作为 平滑移动的启动
        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // 设置轨迹点
        smoothMarker.setPoints(subList);
        // 设置平滑移动的总时间  单位  秒
        smoothMarker.setTotalDuration(40);

        // 设置  自定义的InfoWindow 适配器
        mAMap.setInfoWindowAdapter(infoWindowAdapter);
        // 显示 infowindow
        smoothMarker.getMarker().showInfoWindow();

        // 设置移动的监听事件  返回 距终点的距离  单位 米
        smoothMarker.setMoveListener(new SmoothMoveMarker.MoveListener() {
            @Override
            public void move(final double distance) {

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (infoWindowLayout != null && title != null) {

                            title.setText("距离终点还有： " + (int)distance + "米");
                        }
                    }
                });

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
            infoWindowLayout = new LinearLayout(context);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(context);
            snippet = new TextView(context);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);
            infoWindowLayout.setBackgroundResource(R.drawable.infowindow_bg);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }
}
