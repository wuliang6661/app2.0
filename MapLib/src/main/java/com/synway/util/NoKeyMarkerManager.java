package com.synway.util;

import com.amap.api.maps.model.Marker;

import java.util.LinkedList;
import java.util.List;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name NoKeyMarkerManager
 * @describe 无对应ID的marker管理类
 * @time 2018/12/26 10:57
 */
public class NoKeyMarkerManager {

    // MarkerManager
    public LinkedList<Marker> markerList = new LinkedList<Marker>();
    private static NoKeyMarkerManager markerManager;

    public static NoKeyMarkerManager getInstance() {
        synchronized (NoKeyMarkerManager.class) {
            if (markerManager == null) {
                markerManager = new NoKeyMarkerManager();
            }
        }
        return markerManager;
    }

    /**
     * 添加一个点
     * @param marker
     */
    public void addMarker(Marker marker) {
        markerList.add(marker);
    }

    /**
     * 添加一组点
     * @param markers
     */
    public void addMarkers(List<Marker> markers) {
        markerList.addAll(markers);
    }

    public boolean isEmpty() {
        return markerList.isEmpty();
    }

    /**
     * 清除所有点
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有Marker
            for (int i = 0; i < markerList.size(); i++) {
                Marker marker = markerList.get(i);
                removeMarker(marker);
            }
            markerList.clear();
        }

    }

    private void removeMarker(Marker marker){
        if (marker != null) {
            marker.remove();
        }
    }

    /**
     * 设置隐藏或显示所有点
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< markerList.size(); i++) {
                markerList.get(i).setVisible(isVisible);
            }
        }
    }
}
