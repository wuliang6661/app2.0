package com.synway.util;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;

import java.util.HashMap;
import java.util.List;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name KeyPolygonManager
 * @describe 有对应ID的polygon管理类 (多边形)
 * @time 2019/1/24 13:53
 */
public class KeyPolygonManager {
    // KeyPolygonManager
    public HashMap<String, Polygon> mHashMap = new HashMap<>();
    private static KeyPolygonManager sKeyPolygonManager;

    public static KeyPolygonManager getInstance() {
        synchronized (KeyPolygonManager.class) {
            if (sKeyPolygonManager == null) {
                sKeyPolygonManager = new KeyPolygonManager();
            }
        }
        return sKeyPolygonManager;
    }

    /**
     * 添加多边形
     * @param id     key唯一Id
     * @param polygon
     */
    public void addPolygon(String id, Polygon polygon) {
        // 去掉相同Id 的 Polygon
        if (mHashMap.containsKey(id)) {
            removePolygon(mHashMap.get(id));
        }
        mHashMap.put(id, polygon);
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的Polygon
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removePolygon(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有多边形
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(Polygon polygon : mHashMap.values())
            {
                removePolygon(polygon);
            }
            mHashMap.clear();
        }
    }

    /**
     * Polygon删除
     * @param polygon
     */
    private void removePolygon(Polygon polygon) {
        if (polygon != null) {
            polygon.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定多边形
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * polygon隐藏或显示
     * @param polygon
     * @param isVisible
     */
    private void setVisibleOrGone(Polygon polygon, Boolean isVisible) {
        if (polygon != null) {
            polygon.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有多边形
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(Polygon polygon : mHashMap.values())
            {
                setVisibleOrGone(polygon, isVisible);
            }
        }
    }
}
