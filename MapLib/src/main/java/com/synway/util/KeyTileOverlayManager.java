package com.synway.util;

import com.amap.api.maps.model.TileOverlay;

import java.util.HashMap;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name KeyTileOverlayManager
 * @describe 有对应ID的TileOverlay管理类 (瓦片图层覆盖物)
 * @time 2019/1/25 10:12
 */
public class KeyTileOverlayManager {

    // KeyTileOverlayManager
    public HashMap<String, TileOverlay> mHashMap = new HashMap<>();
    private static KeyTileOverlayManager sKeyTileOverlayManager;

    public static KeyTileOverlayManager getInstance() {
        synchronized (KeyTileOverlayManager.class) {
            if (sKeyTileOverlayManager == null) {
                sKeyTileOverlayManager = new KeyTileOverlayManager();
            }
        }
        return sKeyTileOverlayManager;
    }

    /**
     * 添加瓦片图层覆盖物
     * @param id     key唯一Id
     * @param tileOverlay
     */
    public void addTileOverlay(String id, TileOverlay tileOverlay) {
        // 去掉相同Id 的 TileOverlay
        if (mHashMap.containsKey(id)) {
            removeTileOverlay(mHashMap.get(id));
        }
        mHashMap.put(id, tileOverlay);
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的TileOverlay
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removeTileOverlay(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有瓦片图层覆盖物
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(TileOverlay tileOverlay : mHashMap.values())
            {
                removeTileOverlay(tileOverlay);
            }
            mHashMap.clear();
        }
    }

    /**
     * TileOverlay删除
     * @param tileOverlay
     */
    private void removeTileOverlay(TileOverlay tileOverlay) {
        if (tileOverlay != null) {
            tileOverlay.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定瓦片图层覆盖物
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * tileOverlay隐藏或显示
     * @param tileOverlay
     * @param isVisible
     */
    private void setVisibleOrGone(TileOverlay tileOverlay, Boolean isVisible) {
        if (tileOverlay != null) {
            tileOverlay.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有瓦片图层覆盖物
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(TileOverlay tileOverlay : mHashMap.values())
            {
                setVisibleOrGone(tileOverlay, isVisible);
            }
        }
    }
}
