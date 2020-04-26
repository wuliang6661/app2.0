package com.synway.util;

import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlay;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name KeyTileOverlayManager
 * @describe 无ID的TileOverlay管理类 (瓦片图层覆盖物)
 * @time 2019/1/25 10:12
 */
public class NoKeyTileOverlayManager {

    // TileOverlayManager
    public LinkedList<TileOverlay> tileOverlayList = new LinkedList<TileOverlay>();
    private static NoKeyTileOverlayManager tileOverlayManager;

    public static NoKeyTileOverlayManager getInstance() {
        synchronized (NoKeyTileOverlayManager.class) {
            if (tileOverlayManager == null) {
                tileOverlayManager = new NoKeyTileOverlayManager();
            }
        }
        return tileOverlayManager;
    }

    /**
     * 添加一个瓦片图层覆盖物
     * @param tileOverlay
     */
    public void addTileOverlay(TileOverlay tileOverlay) {
        tileOverlayList.add(tileOverlay);
    }

    public boolean isEmpty() {
        return tileOverlayList.isEmpty();
    }

    /**
     * 清除所有瓦片图层覆盖物
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有TileOverlay
            for (int i = 0; i < tileOverlayList.size(); i++) {
                TileOverlay tileOverlay = tileOverlayList.get(i);
                removeTileOverlay(tileOverlay);
            }
            tileOverlayList.clear();
        }

    }

    private void removeTileOverlay(TileOverlay tileOverlay){
        if (tileOverlay != null) {
            tileOverlay.remove();
        }
    }

    /**
     * 设置隐藏或显示所有瓦片图层覆盖物
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< tileOverlayList.size(); i++) {
                tileOverlayList.get(i).setVisible(isVisible);
            }
        }
    }
}
