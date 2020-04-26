package com.synway.util;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;

/**
 * @author XuKaiyin
 * @class synway.synwaylocating.main.location
 * @name KeyPolylineManager
 * @describe 有对应ID的Polyline管理类 （折线）
 * @time 2018/6/19 15:46
 */
public class KeyPolylineManager {

    // KeyPolylineManager
    public HashMap<String, Polyline> mHashMap = new HashMap<>();
    private static KeyPolylineManager sKeyPolylineManager;

    public static KeyPolylineManager getInstance() {
        synchronized (KeyPolylineManager.class) {
            if (sKeyPolylineManager == null) {
                sKeyPolylineManager = new KeyPolylineManager();
            }
        }
        return sKeyPolylineManager;
    }

    /**
     * 添加折线
     * @param id     key唯一Id
     * @param polyline
     */
    public void addPolyline(String id, Polyline polyline) {
        // 去掉相同Id 的 Polyline
        if (mHashMap.containsKey(id)) {
            removePolyline(mHashMap.get(id));
        }
        mHashMap.put(id, polyline);
    }

    /**
     * 延长指定Id 的折线
     * @param id     key唯一Id
     * @param latLngs 一组点
     */
    public void addPolylineLatLngs(String id, List<LatLng> latLngs) {
        if (mHashMap.containsKey(id) && mHashMap.get(id) != null) {
            PolylineOptions options = mHashMap.get(id).getOptions();
            options.addAll(latLngs);
            mHashMap.get(id).setOptions(options);
        }
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的Polyline
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removePolyline(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有线
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(Polyline polyline : mHashMap.values())
            {
                removePolyline(polyline);
            }
            mHashMap.clear();
        }
    }

    /**
     * Polyline删除
     * @param polyline
     */
    private void removePolyline(Polyline polyline) {
        if (polyline != null) {
            polyline.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定线
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * polyline隐藏或显示
     * @param polyline
     * @param isVisible
     */
    private void setVisibleOrGone(Polyline polyline, Boolean isVisible) {
        if (polyline != null) {
            polyline.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有线
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(Polyline polyline : mHashMap.values())
            {
                setVisibleOrGone(polyline, isVisible);
            }

        }
    }

    /**
     * 获得折线
     * @param id
     */
    public List<LatLng> getLatLngs(String id) {
        if (mHashMap.containsKey(id)) {
            return mHashMap.get(id).getPoints();
        } else {
            return null;
        }
    }
}
