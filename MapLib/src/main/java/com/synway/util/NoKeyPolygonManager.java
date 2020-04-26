package com.synway.util;

import com.amap.api.maps.model.Polygon;

import java.util.LinkedList;
import java.util.List;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name  NoKeyPolygonManager
 * @describe 无对应ID的polygon管理类 (多边形)
 * @time 2019/1/24 13:54
 */
public class NoKeyPolygonManager {

    // PolygonManager
    public LinkedList<Polygon> polygonList = new LinkedList<Polygon>();
    private static NoKeyPolygonManager polygonManager;

    public static NoKeyPolygonManager getInstance() {
        synchronized (NoKeyPolygonManager.class) {
            if (polygonManager == null) {
                polygonManager = new NoKeyPolygonManager();
            }
        }
        return polygonManager;
    }

    /**
     * 添加一个多边形
     * @param polygon
     */
    public void addPolygon(Polygon polygon) {
        polygonList.add(polygon);
    }

    public boolean isEmpty() {
        return polygonList.isEmpty();
    }

    /**
     * 清除所有多边形
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有Polygon
            for (int i = 0; i < polygonList.size(); i++) {
                Polygon polygon = polygonList.get(i);
                removePolygon(polygon);
            }
            polygonList.clear();
        }

    }

    private void removePolygon(Polygon polygon){
        if (polygon != null) {
            polygon.remove();
        }
    }

    /**
     * 设置隐藏或显示所有多边形
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< polygonList.size(); i++) {
                polygonList.get(i).setVisible(isVisible);
            }
        }
    }
}
