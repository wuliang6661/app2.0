package com.synway.util;

import android.util.LongSparseArray;

import com.amap.api.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

/**
 * @author XuKaiyin
 * @class synway.synwaylocating.main.location
 * @name KeyMarkerManager
 * @describe 有对应ID的marker管理类
 * @time 2018/6/19 15:46
 */
public class KeyMarkerManager {

    // KeyMarkerManager
    public HashMap<String, Marker> mHashMap = new HashMap<>();
    private static KeyMarkerManager sKeyMarkerManager;

    public static KeyMarkerManager getInstance() {
        synchronized (KeyMarkerManager.class) {
            if (sKeyMarkerManager == null) {
                sKeyMarkerManager = new KeyMarkerManager();
            }
        }
        return sKeyMarkerManager;
    }

    /**
     * 添加一个点
     * @param id     key唯一Id
     * @param marker
     */
    public void addMarker(String id, Marker marker) {
        // 去掉相同Id 的 Marker
        if (mHashMap.containsKey(id)) {
            removeMarker(mHashMap.get(id));
        }
        mHashMap.put(id, marker);
    }

    /**
     * 添加一组点，
     * @param ids     一组唯一的keys
     * @param markers
     */
    public void addMarkers(List<String> ids, List<Marker> markers) {
        for (int i = 0; i < ids.size(); i++) {
            // 去掉相同Id 的 Marker
            if (mHashMap.containsKey(ids.get(i))) {
                removeMarker(mHashMap.get(ids.get(i)));
            }
            mHashMap.put(ids.get(i), markers.get(i));
        }
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的marker
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removeMarker(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(Marker marker : mHashMap.values())
            {
                removeMarker(marker);
            }
            mHashMap.clear();
        }
    }

    /**
     * marker删除
     * @param marker
     */
    private void removeMarker(Marker marker) {
        if (marker != null) {
            marker.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定点
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * marker隐藏或显示
     * @param marker
     * @param isVisible
     */
    private void setVisibleOrGone(Marker marker, Boolean isVisible) {
        if (marker != null) {
            marker.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有点
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(Marker marker : mHashMap.values())
            {
                setVisibleOrGone(marker, isVisible);
            }

        }
    }
}
